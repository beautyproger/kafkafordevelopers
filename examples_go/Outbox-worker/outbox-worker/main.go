package main

import (
	"context"
	"fmt"
	log "github.com/sirupsen/logrus"
	"time"
)

func main() {
	brokers := []string{"localhost:29092", "localhost:39092"}
	kafkaClient := NewKafkaClient(brokers, "tests")
	workerRepo := NewWorkerRepo(&RepoConfig{
		host:     "localhost",
		port:     "5432",
		user:     "postgres",
		password: "example",
		dbname:   "postgres",
	})

	ticker := time.NewTicker(30 * time.Second)
	quit := make(chan struct{})

	defer ticker.Stop()

	go func() {
		for {
			select {
			case <-ticker.C:
				err := SendBatch(context.Background(), kafkaClient, workerRepo)
				if err != nil {
					log.Error(err)
					return
				}
				log.Info("Batch sent successfully")
			case <-quit:
				ticker.Stop()
				return
			}
		}
	}()

	fmt.Scanln()
	fmt.Println("done")
}

func SendBatch(ctx context.Context, kafkaClient *KafkaClient, repo *WorkerRepo) error {
	transaction, err := repo.BeginTransaction(ctx)
	if err != nil {
		return err
	}
	messages, err := repo.GetMessagesToSend(ctx, 100, transaction)
	if err != nil {
		return err
	}
	for _, message := range messages {
		err := kafkaClient.SendMessage(ctx, []byte(message.value))
		if err != nil {
			transaction.Rollback(ctx)
			return err
		}

		err = repo.MarkedMessageAsSent(ctx, message, transaction)

		if err != nil {
			transaction.Rollback(ctx)
			return err
		}
	}
	err = transaction.Commit(ctx)
	if err != nil {
		return err
	}
	return nil
}
