package friend1;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class Friend1ResponseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:suggestions?brokers=localhost:29092")
                .routeId("friend1")
                .setHeader("pizza-type").jsonpath("$.pizza-type", String.class)
                .pollEnrich()
                .simple("file:classes?noop=true&idempotent=false&fileName=friend-1-pizza-preferences.txt")
                .aggregationStrategy(this::comparePizzaType)
                .process(this::renderResponse)
                .to("kafka:responses?brokers=localhost:29092");
    }

    private AggregationStrategy comparePizzaType() {
        return (oldExchange, newExchange) -> {
            String pizzaType = oldExchange.getIn().getHeader("pizza-type", String.class);
            String preferences = newExchange.getIn().getBody(String.class);
            Boolean preference = preferences.contains(pizzaType);
            oldExchange.getIn().setBody(preference);
            return oldExchange;
        };
    }

    private void renderResponse(Exchange exchange) {
        String pizzaType = exchange.getIn().getHeader("pizza-type", String.class);
        boolean preference = exchange.getIn().getBody(Boolean.class);
        String response = String.format("{\"pizza-type\": \"%s\", \"approval\": %s}", pizzaType, preference);
        exchange.getIn().setBody(response);
    }

}
