package client

import (
	"context"
	"fmt"
)

type KafkaClient struct {
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}

	return client
}

func (client *KafkaClient) SendMessage(ctx context.Context, message []byte) error {

	fmt.Println(string(message))
	//TODO todo
	return nil
}
