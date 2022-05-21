package main

import (
	"context"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"net/http"
	"serialisation/consumer/common"
	"serialisation/consumer/common/serializer"
	"serialisation/producer/client"
)

func main() {
	brokers := []string{"localhost:29092", "localhost:39092"}
	kafkaClient := client.NewKafkaClient(brokers, "tests")
	kafkaClient.CreateTopic("localhost:29092", 4, 1)

	jsonSerializer := serializer.NewJsonSerializer()

	http.HandleFunc("/send", func(writer http.ResponseWriter, request *http.Request) {
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

			var entry = &common.Entry{}
			err = jsonSerializer.Deserialize(body, entry)
			if err != nil {
				log.Error(err)
				writer.WriteHeader(500)
				_, err := writer.Write([]byte("Internal error"))
				if err != nil {
					log.Error(err)
				}
				return
			}
			log.Info(body)
			go func() {
				data, err := jsonSerializer.Serialize(entry)
				err = kafkaClient.SendMessage(context.Background(), data)
				if err != nil {
					log.Error(err)
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
