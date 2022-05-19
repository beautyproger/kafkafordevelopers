CONSUMER_NAME=consumer
PRODUCER_NAME=producer

build_producer:
 GOARCH=amd64 GOOS=darwin go build -o ${PRODUCER_NAME}-darwin producer/main.go producer/client.go
 GOARCH=amd64 GOOS=linux go build -o ${PRODUCER_NAME}-linux producer/main.go producer/client.go
 GOARCH=amd64 GOOS=window go build -o ${PRODUCER_NAME}-windows producer/main.go producer/client.go

build_consumer:
 GOARCH=amd64 GOOS=darwin go build -o ${CONSUMER_NAME}-darwin consumer/main.go consumer/client.go consumer/repo.go
 GOARCH=amd64 GOOS=linux go build -o ${CONSUMER_NAME}-linux consumer/main.go consumer/client.go consumer/repo.go
 GOARCH=amd64 GOOS=window go build -o ${CONSUMER_NAME}-windows consumer/main.go consumer/client.go consumer/repo.go

run_producer:
 ./${PRODUCER_NAME}

run_consumer:
 ./${CONSUMER_NAME}

build_and_run_producer: build_producer run_producer
build_and_run_consumer: build_consumer run_consumer

clean:
 go clean
 rm ${BINARY_NAME}-darwin
 rm ${BINARY_NAME}-linux
 rm ${BINARY_NAME}-windows