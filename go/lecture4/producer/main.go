package main

import (
	"context"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"net/http"
	"slurm.io/lecture4/producer/client"
	"slurm.io/lecture4/producer/config"
	"slurm.io/lecture4/producer/model"
	"slurm.io/lecture4/producer/serializer"
)

func main() {
	cfg := config.LoadAndStoreConfig()
	brokers := []string{cfg.KafkaBroker}
	kafkaClient := client.NewKafkaClient(brokers, cfg.TopicName)
	jsonSerializer := serializer.NewJsonSerializer()
	http.HandleFunc("/userAction", func(writer http.ResponseWriter, request *http.Request) {
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

			var entry = &model.UserAction{}
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

	err := http.ListenAndServe(cfg.Port, nil)
	if err != nil {
		log.Error(err)
	}
}
