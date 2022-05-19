package main

import (
	"encoding/json"
	"fmt"
)

var (
	kafkaConnection = ""
)

func wrapJson(values []string) []byte {
	res, err := json.Marshal(values)
	if err != nil {
		return nil
	}

	return res
}

func main() {
	//kafkaClient := NewKafkaClient([]string{"localhost:29092"}, "tests")
	//ctx, cancel := context.WithCancel(context.Background())
	//TODO прочитать сообщения из кафки и  использовать log для вывода

	fmt.Scanln()
	//останавливаем приложение от выключения
}
