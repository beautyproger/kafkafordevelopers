package main

import (
	"context"
	"encoding/json"
	"fmt"
	log "github.com/sirupsen/logrus"
	"net/http"
	"slurm.io/lecture3/consumer/client"
	"slurm.io/lecture3/consumer/repo"
	"time"
)

var (
	kafkaConnection = ""
)

func wrapJson(values []string) []byte {
	res, err := json.Marshal(values)
	if err != nil {
		return nil
	}

	return res
}

func main() {
	saveRepo := repo.NewRepo(repo.RepoConfig{
		Host:     "localhost",
		Port:     "5432",
		User:     "postgres",
		Password: "example",
		Dbname:   "postgres",
	})

	kafkaClient := client.NewKafkaClient([]string{"localhost:29092"}, "tests")
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		cancel()
		kafkaClient.Close()
	}()
	go func(ctx context.Context) {
		for {
			select {
			case <-time.After(500 * time.Millisecond):
				msg, err := kafkaClient.Poll(400)
				log.Info("poll")
				if err != nil {
					log.Error(err)
				}
				if msg != nil {
					log.Info("Received a message")
					if err != nil {
						log.Info("Couldn't read from kafka " + err.Error())
					}

					err = saveRepo.AddValue(ctx, string(msg.Value))
					if err != nil {
						log.Info("Couldn't write kafka msg to DB " + err.Error())
					}
				}
			case <-ctx.Done():
				fmt.Println("Done listening")
			}
		}
	}(ctx)

	http.HandleFunc("/list", func(writer http.ResponseWriter, request *http.Request) {
		values, err := saveRepo.ReadValues(request.Context())
		if err != nil {
			writer.WriteHeader(500)
			_, err := writer.Write([]byte("Internal server error"))
			if err != nil {
				return
			}
		}

		writer.WriteHeader(200)
		_, err = writer.Write(wrapJson(values))
		if err != nil {
			return
		}
	})

	err := http.ListenAndServe(":9090", nil)
	if err != nil {
		log.Error(err)
	}
}
