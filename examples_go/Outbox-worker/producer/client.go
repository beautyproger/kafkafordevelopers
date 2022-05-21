package main

import (
	"context"
	"github.com/segmentio/kafka-go"
	log "github.com/sirupsen/logrus"
	"net"
	"strconv"
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

func (client *KafkaClient) CreateTopic(LeaderAddr string, NumPartitions int, ReplicationFactor int) {
	conn, err := kafka.Dial("tcp", LeaderAddr)
	if err != nil {
		panic(err.Error())
	}
	defer conn.Close()

	controller, err := conn.Controller()
	if err != nil {
		panic(err.Error())
	}
	controllerConn, err := kafka.Dial("tcp", net.JoinHostPort(controller.Host, strconv.Itoa(controller.Port)))
	if err != nil {
		panic(err.Error())
	}
	defer controllerConn.Close()

	topicConfigs := []kafka.TopicConfig{{Topic: client.w.Topic,
		NumPartitions:     NumPartitions,
		ReplicationFactor: ReplicationFactor}}

	err = controllerConn.CreateTopics(topicConfigs...)
	if err != nil {
		panic(err.Error())
	}
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
