package scattergather;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static util.PizzaPartyHelper.createSuggestionMessage;

public class ScatterRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:in?brokers=localhost:29092")
                .routeId("solution-scatter")
                .process(this::renderResponse)
                .to("kafka:suggestions?brokers=localhost:29092");
    }

    private void renderResponse(Exchange exchange) {
        String suggestion = exchange.getMessage().getBody(String.class);
        String transformedMessage = createSuggestionMessage(suggestion);
        exchange.getMessage().setBody(transformedMessage);
    }
}
