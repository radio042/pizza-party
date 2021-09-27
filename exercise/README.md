# Scenario to be implemented

### Requirements
- recent Docker version
- recent Maven version
- recent Java version

### Try it out
1. Set up the infrastructure. For each of the steps below open a **new** terminal session in the pizza-party/ folder and run: 
   1. `docker compose up -d` to start all containers. To do this the Docker daemon must be running. To verify that the containers are up, and the 4 expected topics are created, wait for a few seconds and run `docker exec pizza-party_kafka_1 kafka-topics --list --bootstrap-server localhost:29092`.
   2. `docker exec -it pizza-party_kafka_1 kafka-console-producer --topic in --bootstrap-server localhost:29092` to start a Kafka console producer for the input topic. Here you write Kafka messages.
         - If you are using GitBash with MINGW64, the following error might pop up: "_the input device is not a TTY.  If you are using mintty, try prefixing the command with 'winpty'_".
         In that case just prefix the command with 'winpty': `winpty docker exec -it pizza-party_kafka_1 kafka-console-producer --topic in --bootstrap-server localhost:29092`.
   3. to read the messages in the Kafka topics:
      1. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic in --bootstrap-server localhost:29092 --from-beginning` to start a Kafka console consumer for the input topic. Here you see the messages in that topic.
      4. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic suggestions --bootstrap-server localhost:29092 --from-beginning` to start a Kafka console consumer for the internal "suggestions" topic. Here you see the messages in that topic.
      5. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic responses --bootstrap-server localhost:29092 --from-beginning` to start a Kafka console consumer for the internal "responses" topic. Here you see the messages in that topic.
      6. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic out --bootstrap-server localhost:29092 --from-beginning` to start a Kafka console consumer for the output topic. Here you see the messages in that topic.
2. Start the Quarkus application
   1. Open a new terminal session and `cd` into the `pizza-party/exercise/` folder
   1. Start the application: `mvn clean compile quarkus:dev`
3. Suggest a pizza for the party
   1. Nothing has been implemented here, but you can verify the system is set up properly:
      1. Write "**Hawaii**" in the console producer
      2. Verify that the following message was produced in the output topic: "**Keine Ahnung, ob die Pizza für alle passt, die Implementierung fehlt.**"
4. Clean up
   1. Run `docker compose down` in a terminal session in the pizza-party/ folder. This exits the console producers and consumers, so you don't need to exit each of them with ctrl+c
   2. Stop the Quarkus application with ctrl+c in the terminal session where it runs
   
### Tip
To implement the system you would mostly need Camel-related code, but also some:
- boring Camel-unrelated Java code
- interface-related code, for example to create messages in an _agreed upon message format_

You can avoid implementing Camel-unrelated stuff by using the methods in 
[PizzaPartyHelper](src/main/java/util/PizzaPartyHelper.java) in various parts of the application.