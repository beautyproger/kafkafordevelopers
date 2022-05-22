package main

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	log "github.com/sirupsen/logrus"
	"serialisation/consumer/actor"
	"serialisation/consumer/client"
	"serialisation/consumer/model"
	"serialisation/consumer/service"
	"time"
)

func wrapJson(values []string) []byte {
	res, err := json.Marshal(values)
	if err != nil {
		return nil
	}

	return res
}

func main() {
	brokers := []string{"localhost:9092"}
	actorConfig := &actor.Config{
		CountThreshold: 5,
		WindowPeriod:   30 * time.Second,
		CleanupPeriod:  15 * time.Second,
	}
	rootActor := actor.NewRootActor(actorConfig)
	entrySender := service.NewEntrySender(brokers, "limits_reached")
	rebalancingCB := func(consumer *kafka.Consumer, event kafka.Event) error {
		log.Info("rebalancing event")
		switch ev := event.(type) {
		case kafka.AssignedPartitions:
			log.Info("assigned the partition")
			log.Info(ev.Partitions)
			rewindTime := time.Now().Add(-(actorConfig.WindowPeriod * 2))
			var offsetParts []kafka.TopicPartition
			for _, partition := range ev.Partitions {
				var err error
				partition.Offset = kafka.Offset(rewindTime.UnixMilli())
				if err != nil {
					log.Error(err)
				}
				offsetParts = append(offsetParts, partition)
			}
			log.Info(offsetParts)
			times, err := consumer.OffsetsForTimes(offsetParts, 1000)
			if err != nil {
				log.Error(err.Error())
			}
			log.Info(times)
			err = consumer.Assign(times)
			if err != nil {
				log.Error(err.Error())
			}

			for _, offset := range times {
				if err != nil {
					log.Error(err.Error())
				}
				rootActor.OnPartitionAssigned(offset)
			}

			break
		case kafka.RevokedPartitions:
			for _, partition := range ev.Partitions {
				rootActor.OnPartitionRevoked(partition)
			}
			break
		}
		return nil
	}
	kafkaClient := client.NewKafkaClient(brokers, "tests", rebalancingCB)
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		kafkaClient.Close()
		cancel()
	}()

	go func(ctx context.Context) {
		for {
			select {
			default:
				msg, err := kafkaClient.Poll(ctx, 400)
				if err != nil {
					log.Error("Couldn't poll a message", err)
				}
				if msg != nil {
					var entry model.Entry
					err = json.Unmarshal(msg.Value, &entry)
					if err != nil {
						log.Error("Couldn't parse a message", err)
					} else {
						entry.Timestamp = msg.Timestamp
						if rootActor.AddEvent(msg.TopicPartition, &entry) == actor.ACTIVATION {
							limitReached := model.LimitReached{
								Country:   entry.Country,
								ReachTime: entry.Timestamp.String(),
							}
							err := entrySender.SendUserAction(limitReached)
							if err != nil {
								log.Error("send user action error ", err)
							}
						}
					}
				}
			case <-ctx.Done():
				fmt.Println("Done listening")
			}
		}
	}(ctx)

	fmt.Scanln()
}
