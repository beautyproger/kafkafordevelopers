package main

import (
	"context"
	"encoding/json"
	"fmt"
	log "github.com/sirupsen/logrus"
	"lecture5/consumer/client"
	"lecture5/consumer/common"
	"lecture5/consumer/processor"
	"sync"
	"time"
)

func main() {
	kafkaClient := client.NewKafkaClient([]string{"localhost:9092"}, "tests")
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		cancel()
	}()
	batch, err := kafkaClient.ReadBatch(ctx, 10*time.Second)
	if err != nil {
		log.Fatal(err)
	}

	var wg sync.WaitGroup

	log.Info(len(batch))

	var result []common.Entry

	for idx, rawEntry := range batch {
		var entry common.Entry
		json.Unmarshal(rawEntry, &entry)
		result = append(result, entry)

		wg.Add(1)

		index := idx
		go func(index int, input *common.Entry) {
			defer wg.Done()
			processor.ProcessEntry(input)
		}(index, &result[index])
	}

	wg.Wait()

	log.Info(result)

	fmt.Scanln()
}
