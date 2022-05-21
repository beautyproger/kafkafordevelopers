package main

import (
	"context"
	"fmt"
	log "github.com/sirupsen/logrus"
	"lecture5/consumer/client"
	"time"
)

func main() {
	kafkaClient := client.NewKafkaClient([]string{"localhost:9092"}, "tests")
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		cancel()
	}()
	batch, err := kafkaClient.ReadBatch(ctx, 10*time.Second)
	if err != nil {
		log.Fatal(err)
	}

	log.Info(batch)

	//TODO обработать батч в разных потоках и вывести result в том же порядке
	
	fmt.Scanln()
}
