Задание для 4 лекции:
Нужно асинхронно передавать в UserActionProcessor сообщения которые мы получаем в 
rest метод 127.0.0.1:8080/userAction

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
