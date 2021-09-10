# pizza-party
Which pizza type do all services agree on? An Apache Camel Project for pizza selection among micro services using the scatter-gather EIP.

## Starting, trying out and stopping Kafka

1. Start containers and create topics
```
$ docker compose up -d

[+] Running 4/4
 - Network pizza-party_default        Created                                                                                                                                                                                      0.0s
 - Container pizza-party_zookeeper_1  Started                                                                                                                                                                                      0.6s
 - Container pizza-party_kafka_1      Started                                                                                                                                                                                      1.4s
 - Container kafka-create-topics      Started  
```

2. After a few seconds the topics schould be created:
```
$ docker exec pizza-party_kafka_1 kafka-topics --list --bootstrap-server localhost:29092

in
out
responses
suggestions
```

3. Producing messages

To produce a message in the topic "in":
- open the console producer for this topic
- enter a message, for example "hello", and press enter
- exit the console producer with ctrl+c
```
$ docker exec -it pizza-party_kafka_1 kafka-console-producer --topic in --bootstrap-server localhost:29092

>hello
>^C
```

4. Reading Messages

To read a message from the topic "in", where the message "hello" was produced in the previous step:
- open the console consumer for this topic
- see the messages
- exit the console conszmer with ctrl+c
```
$ docker exec pizza-party_kafka_1 kafka-console-consumer --topic in --bootstrap-server localhost:29092 --from-beginning

hello

```

5. Shut the containers down
```
$ docker compose down

[+] Running 4/4
 - Container kafka-create-topics      Removed                                                                                                                                                                                     11.0s
 - Container pizza-party_kafka_1      Removed                                                                                                                                                                                      5.4s
 - Container pizza-party_zookeeper_1  Removed                                                                                                                                                                                      1.7s
 - Network pizza-party_default        Removed
```