package client

import (
	"context"
	"github.com/segmentio/kafka-go"
	log "github.com/sirupsen/logrus"
	"time"
)

type KafkaClient struct {
	w kafka.Writer
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}
	client.w = kafka.Writer{
		Addr:         kafka.TCP(brokers...),
		Topic:        topic,
		MaxAttempts:  2,                //скока раз повторить перед тем как отметить пакет как неотправленный
		BatchSize:    5,                //размер пакета
		BatchBytes:   2176000,          //размер пакета в байтах
		BatchTimeout: 10 * time.Second, //таймаут
		RequiredAcks: 1,                // количество подтверждений перед ответом
	}

	return client
}

func (client *KafkaClient) SendMessage(ctx context.Context, message []byte) error {
	err := client.w.WriteMessages(ctx, kafka.Message{
		Value: message,
	})

	if err != nil {
		log.Error(err)
		return err
	}

	return nil
}
