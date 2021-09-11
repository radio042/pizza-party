package scattergather;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class ScatterRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:in?brokers=localhost:29092")
                .routeId("scatter")
                .process(this::renderResponse)
                .to("kafka:suggestions?brokers=localhost:29092");
    }

    private void renderResponse(Exchange exchange) {
        String suggestion = exchange.getIn().getBody(String.class);
        String transformedMessage = String.format("{\"pizza-type\": \"%s\"}", suggestion);
        exchange.getIn().setBody(transformedMessage);
    }
}
