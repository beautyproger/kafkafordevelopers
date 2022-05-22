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
	/*repo := NewRepo(repo2.Config{
		host:     "localhost",
		port:     "5432",
		user:     "postgres",
		password: "example",
		dbname:   "postgres",
	})*/

	serializer := serializer.NewJsonSerializer()

	kafkaClient := client.NewKafkaClient([]string{"localhost:9092"}, "tests")
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
				/*
					err = repo.AddValue(ctx, string(msg))
					if err != nil {
						fmt.Println("Couldn't write kafka msg to DB " + err.Error())
					}*/

			case <-ctx.Done():
				fmt.Println("Done listening")
			}
		}
	}(ctx)

	http.HandleFunc("/list", func(writer http.ResponseWriter, request *http.Request) {
		/*values, err := repo.ReadValues(request.Context())
		if err != nil {
			writer.WriteHeader(500)
			_, err := writer.Write([]byte("Internal server error"))
			if err != nil {
				return
			}
		}*/

		writer.WriteHeader(200)
		//_, err = writer.Write(wrapJson())
		//if err != nil {
		//	return
		//}
	})

	err := http.ListenAndServe(":9090", nil)
	if err != nil {
		log.Error(err)
	}

}
