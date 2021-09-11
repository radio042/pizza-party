package friend1;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.builder.RouteBuilder;

public class Friend1ResponseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:suggestions?brokers=localhost:29092")
                .routeId("friend1")
                .setHeader("pizza-type")
                .jsonpath("$.pizza-type", String.class)
                .pollEnrich()
                .simple("file:classes?noop=true&fileName=friend-1-pizza-preferences.txt")
                .aggregationStrategy(comparePizzaTypeAndRenderResponse())
                .to("kafka:responses?brokers=localhost:29092");
    }

    private AggregationStrategy comparePizzaTypeAndRenderResponse() {
        return (oldExchange, newExchange) -> {
            String pizzaType = oldExchange.getIn().getHeader("pizza-type", String.class);
            String preferences = newExchange.getIn().getBody(String.class);
            String preference = preferences.contains(pizzaType) ? "yep" : "nope";
            String response = String.format("{\"pizza-type\": \"%s\", \"response\": \"%s\"}", pizzaType, preference);
            newExchange.getIn().setBody(response);
            return newExchange;
        };
    }

}
