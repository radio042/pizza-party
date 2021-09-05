package suggestion;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class ScatterGatherRoutes extends RouteBuilder {

    // todo: expect {"pizza-type": string, "response": boolean}

    public static final String AGREED_UPON_AGGREGATION_HEADER = "pizza-type";
    public static final String AGREED_UPON_ACCEPT = "yep";
    public static final String AGREED_UPON_DECLINE = "nope";

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
                .setHeader(AGREED_UPON_AGGREGATION_HEADER, simple("${in.body.pizza-type}"))
                .aggregate(header(AGREED_UPON_AGGREGATION_HEADER), newConsensusStrategy())
                .completionSize(3) // because we expect a response from 3 services
                .log("Do we all agree on ordering ${header.pizza-type} - ${in.body}");
    }

    private AggregationStrategy newConsensusStrategy() {
        return new AggregationStrategy() {
            @Override
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                String firstResponse = oldExchange.getIn().getBody(String.class);
                String secondResponse = newExchange.getIn().getBody(String.class);
                String newExchangeBody = firstResponse != null
                        && firstResponse.equals(secondResponse)
                        && firstResponse.equals(AGREED_UPON_ACCEPT)
                        ? AGREED_UPON_ACCEPT
                        : AGREED_UPON_DECLINE;
                newExchange.getIn().setBody(newExchangeBody);
                return newExchange;
            }
        };
    }
}
