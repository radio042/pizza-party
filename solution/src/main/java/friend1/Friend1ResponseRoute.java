package friend1;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class Friend1ResponseRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:suggestions?brokers=localhost:29092")
                .routeId("friend1")
                .process(addPizzaTypeHeader())
                .pollEnrich("file:resources?filename=friend-1-pizza-preferences.txt")
                .process(createResponse())
                .to("kafka:responses?brokers=localhost:29092");
    }

    private Processor createResponse() {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String pizzaType = exchange.getIn().getHeader("pizza-type", String.class);
                String preference = exchange.getIn().getBody(String.class).contains(pizzaType) ? "yep" : "nope";
                String response = String.format("{\"pizza-type\": %s, \"response\": %s}", pizzaType, preference);
                exchange.getIn().setBody(response);
            }
        };
    }

    private Processor addPizzaTypeHeader() {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader("pizza-type", simple("${in.body.pizza-type}"));
            }
        };
    }

}
