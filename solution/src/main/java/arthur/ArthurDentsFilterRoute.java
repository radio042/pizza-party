package arthur;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class ArthurDentsFilterRoute extends RouteBuilder {
    public static final String AGREED_UPON_AGGREGATION_HEADER = "pizza-type";

    @Override
    public void configure() throws Exception {
        from("kafka:arthur?brokers=localhost:29092")
                .routeId("arthur")
                .setHeader(AGREED_UPON_AGGREGATION_HEADER, simple("${in.body}"))
                .pollEnrich("file:resources?filename=arthur-pizza-preferences.txt")
                .process(responseProcessor())
                .to("kafka:responses?brokers=localhost:29092");
    }

    private Processor responseProcessor() {
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String pizzaType = exchange.getIn().getHeader(AGREED_UPON_AGGREGATION_HEADER, String.class);
                String preference = String.valueOf(exchange.getIn().getBody(String.class).contains(pizzaType));
                String response = String.format("{\"pizza-type\": %s, \"response\": %s}", pizzaType, preference);
                exchange.getIn().setBody(response);
            }
        };
    }

}
