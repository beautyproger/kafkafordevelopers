package main

import (
	"context"
	"encoding/json"
	"fmt"
	log "github.com/sirupsen/logrus"
	"pipelines/kafka"
	"pipelines/model"
)

func main() {
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		cancel()
	}()
	kafkaClient := kafka.NewKafkaClient([]string{"localhost:29092"}, "metrics", "output-measures")

	go func(ctx context.Context) {
		for {
			log.Println("Run")
			msg, err := kafkaClient.Read(ctx)
			if err != nil {
				log.Println("Couldn't read from kafka " + err.Error())
			}
			measure := model.Measure{}
			err = json.Unmarshal(msg, &measure)
			if err != nil {
				log.Println("Failed to convert " + err.Error())
			}

			if measure.Device == "dev42" {
				message, err := json.Marshal(measure)
				if err != nil {
					log.Println("Failed to convert " + err.Error())
				}
				err = kafkaClient.Write(ctx, message)
				if err != nil {
					log.Println("Failed to send a message " + err.Error())
				}
			}
		}
	}(ctx)

	fmt.Scanln()
}
