package main

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	log "github.com/sirupsen/logrus"
	"serialisation/consumer/actor"
	"serialisation/consumer/client"
	"serialisation/consumer/service"
	"time"
)

func wrapJson(values []string) []byte {
	res, err := json.Marshal(values)
	if err != nil {
		return nil
	}

	return res
}

func main() {
	brokers := []string{"localhost:9092"}
	actorConfig := &actor.Config{
		CountThreshold: 5,
		WindowPeriod:   30 * time.Second,
		CleanupPeriod:  15 * time.Second,
	}
	rootActor := actor.NewRootActor(actorConfig)
	entrySender := service.NewEntrySender(brokers, "limits_reached")
	rebalancingCB := func(consumer *kafka.Consumer, event kafka.Event) error {
		log.Info("rebalancing event")
		switch ev := event.(type) {
		case kafka.AssignedPartitions:
			//TODO перематываем оффсеты

			for _, offset := range times {
				if err != nil {
					log.Error(err.Error())
				}
				rootActor.OnPartitionAssigned(offset) //TODO ставим перемотанные оффсеты
			}

			break
		case kafka.RevokedPartitions:
			for _, partition := range ev.Partitions {
				rootActor.OnPartitionRevoked(partition)
			}
			break
		}
		return nil
	}
	kafkaClient := client.NewKafkaClient(brokers, "tests", rebalancingCB)
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		kafkaClient.Close()
		cancel()
	}()

	go func(ctx context.Context) {
		for {
			select {
			default:
				msg, err := kafkaClient.Poll(ctx, 400)
				if err != nil {
					log.Error("Couldn't poll a message", err)
				}
				if msg != nil {
					//TODO вызываем нужный актор с помощью RootActor и при вызове команды активации мы пушим сообщение ReachedLimit
				}
			case <-ctx.Done():
				fmt.Println("Done listening")
			}
		}
	}(ctx)

	fmt.Scanln()
}
