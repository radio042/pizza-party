# Fully implemented scenario

### Requirements
- recent Docker version
- recent Maven version
- recent Java version

### Try it out
1. Set up the infrastructure. For each of the steps below open a **new** terminal session in the pizza-party/ folder and run: 
   1. `docker compose up -d` to start all containers. To verify that the containers are up and the 4 expected topics are created, wait for a few seconds and run `docker exec pizza-party_kafka_1 kafka-topics --list --bootstrap-server localhost:29092`.
   2. `docker exec -it pizza-party_kafka_1 kafka-console-producer --topic in --bootstrap-server localhost:29092` to start a kafka console producer for the input topic. Here you write Kafka messages.
   3. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic in --bootstrap-server localhost:29092 --from-beginning` to start a kafka console consumer for the input topic. Here you see the messages in that topic.
   4. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic suggestions --bootstrap-server localhost:29092 --from-beginning` to start a kafka console consumer for the internal "suggestions" topic. Here you see the messages in that topic.
   5. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic responses --bootstrap-server localhost:29092 --from-beginning` to start a kafka console consumer for the internal "responses" topic. Here you see the messages in that topic.
   6. `docker exec pizza-party_kafka_1 kafka-console-consumer --topic out --bootstrap-server localhost:29092 --from-beginning` to start a kafka console consumer for the output topic. Here you see the messages in that topic.
2. Start the service
   1. Open a new terminal session in the pizza-party/exercise/ folder and run `mvn clean compile quarkus:dev`
3. Suggest a pizza for the party
   1. Nothing has been implemented here, but you can verify the system is set up properly:
      1. Write "**4 Käse**" in the console producer
      2. Verify that the following message was produced in the output topic: "**Keine Ahnung, ob die Pizza für alle passt, die Implementerung fehlt.**"
4. Clean up
   1. Run `docker compose down` in a terminal session in the pizza-party/ folder
   2. Stop the Quarkus service with ctrl+c in the terminal session where it runs