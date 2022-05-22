package main

import (
	"context"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"lecture5/producer/client"
	"lecture5/producer/common"
	"lecture5/producer/common/serializer"
	"net/http"
)

func main() {
	brokers := []string{"localhost:9092"}
	kafkaClient := client.NewKafkaClient(brokers, "tests")

	jsonSerializer := serializer.NewJsonSerializer()

	http.HandleFunc("/entryList", func(writer http.ResponseWriter, request *http.Request) {
		if request.Method == "POST" {
			body, err := ioutil.ReadAll(request.Body)
			if err != nil {
				log.Error(err)
				writer.WriteHeader(500)
				_, err := writer.Write([]byte("Internal error"))
				if err != nil {
					log.Error(err)
				}
				return
			}

			var entries []common.Entry
			err = jsonSerializer.Deserialize(body, &entries)
			if err != nil {
				log.Error(err)
				writer.WriteHeader(500)
				_, err := writer.Write([]byte("Internal error"))
				if err != nil {
					log.Error(err)
				}
				return
			}

			go func() {
				for _, entry := range entries {
					log.Info("entry")
					data, err := jsonSerializer.Serialize(entry)
					err = kafkaClient.SendMessage(context.Background(), data)
					if err != nil {
						log.Error(err)
					}
				}

			}()

			_, err = writer.Write([]byte("Record sent"))
			if err != nil {
				log.Error(err)
			}
		}
	})

	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		log.Error(err)
	}
}
