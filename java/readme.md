На скорости 100 000 рпс с краткосрочными пиками до 2 000 000 рпс мы получаем информацию о показе рекламмы пользователю. Отдел ML хочет получить оповещение, если в течении 10 минут, мы показали банеры пользователю пять раз, для подготовки нового пакета рекламмы. Сообщение об этом должны размещатся в соотведстующем топике kafka. Система должна горизотально маштабироватся.

Вы должны помнить о следующих оледуюших вещах:

1) Кафка способна выступать в роли примитива синхронизации. Мы можем писать однопоточный код, и маштабироватся за счет добавления consumer.
2) Вы можете использовать акторную модель в максимально пирмитивном виде.

```
git clone git@gitlab.slurm.io:kafkafordevelopers/slurm.git kafkafordevelopers
```

Если ключ не добавляли, то такой командой - но вы не сможите пушать код для ревью.
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
