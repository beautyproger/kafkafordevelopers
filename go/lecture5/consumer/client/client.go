package client

import (
	"context"
	"github.com/segmentio/kafka-go"
	"time"
)

type KafkaClient struct {
	r *kafka.Reader
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}
	client.r = kafka.NewReader(kafka.ReaderConfig{
		Brokers: brokers,
		GroupID: "test-reader",
		Topic:   topic,
	})

	return client
}

func (client *KafkaClient) Read(ctx context.Context) ([]byte, error) {
	msg, err := client.r.ReadMessage(ctx)
	if err != nil {
		return nil, err
	}
	return msg.Value, nil
}

func (client *KafkaClient) ReadBatch(ctx context.Context, timeout time.Duration) ([][]byte, error) {
	//TODO прочитать батч с ограничением по времени
	return nil, nil
}
