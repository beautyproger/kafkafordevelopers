package main

import (
	"context"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"net/http"
	"slurm.io/lecture3/producer/client"
)

func main() {
	brokers := []string{"localhost:29092"}
	kafkaClient := client.NewKafkaClient(brokers, "tests")

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
			log.Info(body)
			go func() {
				err = kafkaClient.SendMessage(context.Background(), body)
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
