Для корректной обработки запроса нужно вызвать медленную бизнес-логику. В 90% случаев бизнес логика обрабатывает запрос за 100 миллисекунд. В 10% за 400 миллисекунд. Мы получаем сообщения со скоростью 100 rps. Для каждого пользователя, мы должны обрабатывать сообщение именно в том порадке в котором они поступили.
Помните что:
1) Партиционирование сообщений по ключу.
2) Обрабатывать сообщения нужно паралельно.
3) Что при паралельной обработке возможен reorder

Если вы добавли свой ключ в гитлаб, скачать репозиторий можно коммандой
```
git clone git@gitlab.slurm.io:kafkafordevelopers/slurm.git kafkafordevelopers
```

Если ключ не добавляли, то такой командой - но вы не сможите пушать код для ревью.
```
git clone https://gitlab.slurm.io/kafkafordevelopers/slurm.git kafkafordevelopers
```

Скачать актуальный main.
```
git fetch --progress --prune --tags origin &&  git checkout --track -B master remotes/origin/master
```

Для того чтобы запустить Kafka, перейдите в директорию проекта java/go и выполните

```
docker-compose up -d
```


Чтобы отправить сообщеие в kafka используйте команду.
```
docker exec --interactive --tty broker \
    kafka-console-producer --bootstrap-server broker:9092 \
        --topic test
```

Считать все данные из топика можно командой:
```
docker exec --interactive --tty broker \
    kafka-console-consumer --bootstrap-server broker:9092 \
        --topic test \
        --from-beginning
```

Топик будет создан автоматически.
Но для тренировки следует создавать топик вручную:
```
docker exec broker \
    kafka-topics --bootstrap-server broker:9092 \
             --create \
             --replication-factor 1 \
             --partitions 3 \
             --topic KafkaCourse-UserAction-ClickStream
```

Остановить кафку и удалить все данные можно выполнив эти комманды:
```
docker-compose down
docker rm -f zookeeper broker
```
