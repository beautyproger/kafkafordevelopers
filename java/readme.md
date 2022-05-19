```
docker-compose up -d
```

```
docker exec --interactive --tty broker \
    kafka-console-producer --bootstrap-server broker:9092 \
        --topic test
```

```
docker exec --interactive --tty broker \
    kafka-console-consumer --bootstrap-server broker:9092 \
        --topic test \
        --from-beginning
```

```
docker exec broker \
    kafka-topics --bootstrap-server broker:9092 \
             --create \
             --replication-factor 1 \
             --partitions 3 \
             --topic KafkaCourse-UserAction-ClickStream
```

```
docker-compose down
```

```
docker rm -f zookeeper broker
```
