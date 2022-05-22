package actor

import (
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	log "github.com/sirupsen/logrus"
	"serialisation/consumer/model"
)

type RootActor struct {
	config       *Config
	partitionMap map[string]*PartitionActor
}

func NewRootActor(config *Config) *RootActor {
	actor := &RootActor{}
	actor.config = config
	actor.partitionMap = make(map[string]*PartitionActor)

	return actor
}

func (root *RootActor) OnPartitionAssigned(topicPartition kafka.TopicPartition) {
	if _, ok := root.partitionMap[formatPartition(topicPartition)]; !ok {
		root.partitionMap[formatPartition(topicPartition)] = NewPartitionActor(root.config)
	}
}

func (root *RootActor) OnPartitionRevoked(topicPartition kafka.TopicPartition) {
	delete(root.partitionMap, formatPartition(topicPartition))
}

func (root *RootActor) AddEvent(partition kafka.TopicPartition, event *model.Entry) int {
	actor := root.partitionMap[formatPartition(partition)]
	if actor == nil {
		log.Error("Event on partition", partition, " which is not assigned")
		return NONE
	} else {
		return actor.AddEvent(event)
	}
}

func formatPartition(partition kafka.TopicPartition) string {
	return fmt.Sprintf("%s%d", *partition.Topic, partition.Partition)
}

/*
public class RootActor {

    public Map<String, Long> queryState() {
        return partitionMap.values().stream()
                .flatMap(partitionActor -> partitionActor.queryState().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
*/
