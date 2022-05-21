package main

import (
	"context"
	"encoding/json"
	"fmt"
	log "github.com/sirupsen/logrus"
	"net/http"
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
	repo := NewRepo(RepoConfig{
		host:     "localhost",
		port:     "5432",
		user:     "postgres",
		password: "example",
		dbname:   "postgres",
	})

	kafkaClient := NewKafkaClient([]string{"localhost:29092", "localhost:39092"}, "tests")
	ctx, cancel := context.WithCancel(context.Background())
	defer func() {
		cancel()
	}()
	go func(ctx context.Context) {
		for {
			select {
			case <-time.After(500 * time.Millisecond):
				msg, err := kafkaClient.Read(ctx)
				if err != nil {
					fmt.Println("Couldn't read from kafka " + err.Error())
				}

				err = repo.AddValue(ctx, string(msg))
				if err != nil {
					fmt.Println("Couldn't write kafka msg to DB " + err.Error())
				}
			case <-ctx.Done():
				fmt.Println("Done listening")
			}
		}
	}(ctx)

	http.HandleFunc("/list", func(writer http.ResponseWriter, request *http.Request) {
		values, err := repo.ReadValues(request.Context())
		if err != nil {
			writer.WriteHeader(500)
			_, err := writer.Write([]byte("Internal server error"))
			if err != nil {
				return
			}
		}

		writer.WriteHeader(200)
		_, err = writer.Write(wrapJson(values))
		if err != nil {
			return
		}
	})

	err := http.ListenAndServe(":9090", nil)
	if err != nil {
		log.Error(err)
	}
}
