package client

import (
	"context"
	"github.com/segmentio/kafka-go"
)

type KafkaClient struct {
	r *kafka.Reader
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}
	//TODO клиент
	return client
}

func (client *KafkaClient) ReadFromOffset(ctx context.Context, offset int64) ([]byte, error) {
	//TODO читаем с оффсетом без коммитов

	return nil, nil
}
