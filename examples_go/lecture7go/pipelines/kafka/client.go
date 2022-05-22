package kafka

import (
	"context"
	"github.com/segmentio/kafka-go"
	"time"
)

type Client struct {
	r *kafka.Reader
	w *kafka.Writer
}

func NewKafkaClient(brokers []string, readTopic string, writeTopic string) *Client {
	client := &Client{}
	client.r = kafka.NewReader(kafka.ReaderConfig{
		Brokers: brokers,
		GroupID: "measure",
		Topic:   readTopic,
	})

	client.w = &kafka.Writer{
		Addr:         kafka.TCP(brokers...),
		Topic:        writeTopic,
		MaxAttempts:  2,                //скока раз повторить перед тем как отметить пакет как неотправленный
		BatchSize:    5,                //размер пакета
		BatchBytes:   2176000,          //размер пакета в байтах
		BatchTimeout: 10 * time.Second, //таймаут
		RequiredAcks: 1,                // количество подтверждений перед ответом
	}

	return client
}

func (client *Client) Read(ctx context.Context) ([]byte, error) {
	msg, err := client.r.ReadMessage(ctx)
	if err != nil {
		return nil, err
	}

	return msg.Value, nil
}

func (client *Client) Write(ctx context.Context, message []byte) error {
	err := client.w.WriteMessages(ctx, kafka.Message{
		Value: message,
	})

	if err != nil {
		return err
	}
	return nil
}
