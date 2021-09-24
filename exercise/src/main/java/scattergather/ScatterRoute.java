package scattergather;

import org.apache.camel.builder.RouteBuilder;

public class ScatterRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:in?brokers=localhost:29092")
                .process(exchange -> exchange.getMessage().setBody("Keine Ahnung, ob die Pizza für alle passt, die Implementierung fehlt."))
                .to("kafka:out?brokers=localhost:29092");
    }
}
