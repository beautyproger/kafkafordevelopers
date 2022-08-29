На скорости 100 000 рпс с краткосрочными пиками до 2 000 000 рпс мы получаем информацию о показе рекламы пользователю. Отдел ML хочет получить оповещение, если в течении 10 минут, мы показали банеры пользователю пять раз, для подготовки нового пакета рекламы. Сообщение об этом должны размещаться в соответствующем топике kafka. Система должна горизонтально масштабироватся.

Вы должны помнить о следующих вещах:

1) Кафка способна выступать в роли примитива синхронизации. Мы можем писать однопоточный код, и масштабироватся за счет добавления consumer.
2) Вы можете использовать акторную модель в максимально примитивном виде.

```
git clone git@gitlab.slurm.io:kafkafordevelopers/slurm.git kafkafordevelopers
```

Если ключ не добавляли, то такой командой - но вы не сможете пушать код для ревью.
```
git clone https://gitlab.slurm.io/kafkafordevelopers/slurm.git kafkafordevelopers
```

Скачать актуальный main.
```
git fetch --progress --prune --tags origin &&  git checkout --track -B main remotes/origin/main
```

Для того чтобы запустить Kafka, перейдите в директорию проекта java/go и выполните

```
docker-compose up -d
```


Чтобы отправить сообщение в kafka используйте команду.
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

Остановить кафку и удалить все данные можно выполнив эти команды:
```
docker-compose down
docker rm -f zookeeper broker
```
