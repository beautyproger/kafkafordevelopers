package client

import (
	"context"
	"github.com/segmentio/kafka-go"
	log "github.com/sirupsen/logrus"
)

type KafkaClient struct {
	r *kafka.Reader
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}
	client.r = kafka.NewReader(kafka.ReaderConfig{
		Brokers:   brokers,
		Topic:     topic,
		Partition: 0,
	})

	return client
}

func (client *KafkaClient) ReadFromOffset(ctx context.Context, offset int64) ([]byte, error) {
	err := client.r.SetOffset(offset)
	if err != nil {
		return nil, err
	}
	msg, err := client.r.FetchMessage(ctx)
	log.Info("message fetched topic: ", msg.Topic, " offset: ", msg.Offset, " partition: ", msg.Partition)
	if err != nil {
		return nil, err
	}

	return msg.Value, nil
}
