package main

import (
	"context"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"net/http"
	"outbox-worker/producer/common"
	"outbox-worker/producer/common/serializer"
)

func main() {
	jsonSerializer := serializer.NewJsonSerializer()

	workerRepo := NewWorkerRepo(&RepoConfig{
		host:     "localhost",
		port:     "5432",
		user:     "postgres",
		password: "example",
		dbname:   "postgres",
	})

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
				if err != nil {
					log.Error(err)
				}
				err = workerRepo.AddMessage(context.Background(), string(data))
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
