package client

import (
	"context"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"strings"
)

type KafkaClient struct {
	p     *kafka.Producer
	topic string
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}
	var err error
	client.p, err = kafka.NewProducer(&kafka.ConfigMap{
		"bootstrap.servers": strings.Join(brokers, ","),
	})
	client.topic = topic
	if err != nil {
		panic(err)
	}

	return client
}

func (client *KafkaClient) SendMessage(ctx context.Context, message []byte) error {
	go func() {
		for e := range client.p.Events() {
			switch ev := e.(type) {
			case *kafka.Message:
				if ev.TopicPartition.Error != nil {
					fmt.Printf("Delivery failed: %v\n", ev.TopicPartition)
				} else {
					fmt.Printf("Delivered message to %v\n", ev.TopicPartition)
				}
			}
		}
	}()

	err := client.p.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &client.topic, Partition: kafka.PartitionAny},
		Value:          message,
	}, nil)

	if err != nil {
		return err
	}

	return nil
}

func (client *KafkaClient) Close() {
	client.p.Flush(15 * 1000)
	client.p.Close()
}
