package scattergather;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static util.PizzaPartyHelper.createAggregatedResponseMessage;
import static util.PizzaPartyHelper.obtainExchangeWhereBodyTellsWhetherAllExchangesHaveTheirHeaderSetToTrue;

public class GatherRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:responses?brokers=localhost:29092")
                .routeId("solution-gather")
                .setHeader("pizza-type").jsonpath("$.pizza-type", String.class)
                .setHeader("approval").jsonpath("$.approval", Boolean.class)
                .aggregate(header("pizza-type"), checkConsensus())
                .completionSize(2) // because we expect a response from 2 friends
                .process(this::renderResponse)
                .to("kafka:out?brokers=localhost:29092");
    }

    private AggregationStrategy checkConsensus() {
        return (oldExchange, newExchange)
                -> obtainExchangeWhereBodyTellsWhetherAllExchangesHaveTheirHeaderSetToTrue(
                        oldExchange, newExchange, "approval");
    }

    private void renderResponse(Exchange exchange) {
        boolean consensus = exchange.getMessage().getBody(Boolean.class);
        String pizzaType = exchange.getMessage().getHeader("pizza-type", String.class);
        String transformedMessage = createAggregatedResponseMessage(consensus, pizzaType);
        exchange.getMessage().setBody(transformedMessage);
    }
}
