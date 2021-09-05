package suggestion;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class ScatterGatherRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:suggestions?brokers=localhost:29092")
                .routeId("scatter")
                .multicast()
                .to("kafka:arthur?brokers=localhost:29092")
                .to("kafka:ford?brokers=localhost:29092")
                .to("kafka:zaphod?brokers=localhost:29092");

        from("kafka:responses?brokers=localhost:29092")
                .routeId("gather")
                .aggregate(newConsensusStrategy())
                .body()
                .completionSize(3)
                .to("mock:result");
    }

    private AggregationStrategy newConsensusStrategy() {

        return new AggregationStrategy() {
            @Override
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                // todo
                return null;
            }
        };
    }
}
