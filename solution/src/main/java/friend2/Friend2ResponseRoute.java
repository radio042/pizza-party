package friend2;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static util.PizzaPartyHelper.checkApproval;
import static util.PizzaPartyHelper.createFriendResponseMessage;

public class Friend2ResponseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:suggestions?brokers=localhost:29092")
                .routeId("friend2")
                .setHeader("pizza-type").jsonpath("$.pizza-type", String.class)
                .pollEnrich()
                .simple("file:classes?noop=true&idempotent=false&fileName=friend-2-pizza-preferences.txt")
                .aggregationStrategy(this::comparePizzaType)
                .process(this::renderResponse)
                .to("kafka:responses?brokers=localhost:29092");
    }

    private AggregationStrategy comparePizzaType() {
        return (oldExchange, newExchange) -> {
            String pizzaType = oldExchange.getMessage().getHeader("pizza-type", String.class);
            String preferences = newExchange.getMessage().getBody(String.class);
            Boolean approval = checkApproval(preferences, pizzaType);
            oldExchange.getMessage().setBody(approval);
            return oldExchange;
        };
    }

    private void renderResponse(Exchange exchange) {
        String pizzaType = exchange.getMessage().getHeader("pizza-type", String.class);
        boolean preference = exchange.getMessage().getBody(Boolean.class);
        String response = createFriendResponseMessage(pizzaType, preference);
        exchange.getMessage().setBody(response);
    }

}
