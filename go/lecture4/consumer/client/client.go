package client

import (
	"context"
)

type KafkaClient struct {
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}

	return client
}

func (client *KafkaClient) Read(ctx context.Context) ([]byte, error) {
	//TODO todo

	return nil, nil
}
