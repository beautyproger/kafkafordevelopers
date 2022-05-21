package client

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	log "github.com/sirupsen/logrus"
	"strings"
)

type KafkaClient struct {
	c *kafka.Consumer
}

func NewKafkaClient(brokers []string, topic string) *KafkaClient {
	client := &KafkaClient{}
	var err error
	client.c, err = kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers":     strings.Join(brokers, ","),
		"broker.address.family": "v4",
		"group.id":              "test-consumer",
		"session.timeout.ms":    6000,
		"auto.offset.reset":     "earliest"})

	if err != nil {
		panic(err)
	}

	err = client.c.SubscribeTopics([]string{topic}, nil)
	if err != nil {
		panic(err)
	}

	return client
}
func (client *KafkaClient) Poll(timeout int) (*kafka.Message, error) {
	ev := client.c.Poll(timeout)
	if ev == nil {
		return nil, nil
	}
	switch e := ev.(type) {
	case *kafka.Message:
		log.Info("%% Message on %s:\n%s\n",
			e.TopicPartition, string(e.Value))
		return ev.(*kafka.Message), nil
	case kafka.Error:
		log.Info("%% Error: %v: %v\n", e.Code(), e)
		if e.Code() == kafka.ErrAllBrokersDown {
			return nil, e
		}
	default:
		log.Info("Ignored", e)
	}
	return nil, nil
}

func (client *KafkaClient) Close() {
	err := client.c.Close()
	if err != nil {
		log.Error(err)
	}
}
