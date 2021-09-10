package scattergather;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class ScatterGatherRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:in?brokers=localhost:29092")
                .routeId("scatter")
                .process(toFriendFormat())
                .to("kafka:suggestions?brokers=localhost:29092");

        from("kafka:responses?brokers=localhost:29092")
                .routeId("gather")
                .process(addPizzaTypeHeaders())
                .aggregate(header("pizza-type"), newConsensusStrategy())
                .completionSize(2) // because we expect a response from 2 friends
                .process(toResponse())
                .to("kafka:out?brokers=localhost:29092");
    }

    private AggregationStrategy newConsensusStrategy() {
        return new AggregationStrategy() {
            @Override
            public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                String firstResponse = oldExchange.getIn().getBody(String.class);
                String secondResponse = newExchange.getIn().getBody(String.class);
                boolean newExchangeBody = firstResponse != null
                        && firstResponse.equals(secondResponse)
                        && firstResponse.equals("yep");
                newExchange.getIn().setBody(newExchangeBody);
                return newExchange;
            }
        };
    }

    private Processor toFriendFormat() {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String suggestion = exchange.getIn().getBody(String.class);
                String transformedMessage = String.format("{\"pizza-type\": \"%s\"}", suggestion);
                exchange.getIn().setBody(transformedMessage);
            }
        };
    }

    private Processor addPizzaTypeHeaders() {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader("pizza-type", simple("${in.body.pizza-type}"));
                exchange.getIn().setHeader("response", simple("${in.body.response}"));
            }
        };
    }

    private Processor toResponse() {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Boolean consensus = exchange.getIn().getBody(Boolean.class);
                String pizzaType = exchange.getIn().getHeader("pizza-type", String.class);
                String transformedMessage = String.format("%s sind mit %s einverstanden",
                        consensus ? "Alle" : "Nicht alle",
                        pizzaType);
                exchange.getIn().setBody(transformedMessage);
            }
        };
    }
}
