package main

import (
	"context"
	"encoding/json"
	"fmt"
	log "github.com/sirupsen/logrus"
	"net/http"
	"serialisation/consumer/client"
	"serialisation/consumer/common"
	"serialisation/consumer/common/serializer"
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
	//TODO подключимся к БД
	serializer := serializer.NewJsonSerializer()
	//TODO топик настраивается через конфиг
	kafkaClient := client.NewKafkaClient([]string{"localhost:29092"}, "tests")
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		cancel()
	}()
	go func(ctx context.Context) {
		for {
			select {
			case <-time.After(500 * time.Millisecond):
				msg, err := kafkaClient.ReadFromOffset(ctx, 0)
				if err != nil {
					fmt.Println("Couldn't read from kafka " + err.Error())
				}
				entry := common.Entry{}
				err = serializer.Deserialize(msg, &entry)
				if err != nil {
					return
				}
				fmt.Println(entry)
				//TODO сохраняем оффсеты в БД

			case <-ctx.Done():
				fmt.Println("Done listening")
			}
		}
	}(ctx)

	err := http.ListenAndServe(":9090", nil)
	if err != nil {
		log.Error(err)
	}

}
