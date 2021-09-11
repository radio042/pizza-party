package scattergather;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class GatherRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("kafka:responses?brokers=localhost:29092")
                .routeId("gather")
                .setHeader("pizza-type").jsonpath("$.pizza-type", String.class)
                .setHeader("approval").jsonpath("$.approval", Boolean.class)
                .aggregate(header("pizza-type"), newConsensusStrategy())
                .completionSize(2) // because we expect a response from 2 friends
                .process(toResponse())
                .to("kafka:out?brokers=localhost:29092");
    }

    private AggregationStrategy newConsensusStrategy() {
        return (oldExchange, newExchange) -> {
            boolean approvalInNewResponse = newExchange.getIn().getHeader("approval", Boolean.class);
            if (oldExchange == null) {
                newExchange.getIn().setBody(approvalInNewResponse);
            } else {
                boolean approvalInOldResponse = oldExchange.getIn().getHeader("approval", Boolean.class);
                boolean bothApprove = approvalInNewResponse && approvalInOldResponse;
                newExchange.getIn().setBody(bothApprove);
            }
            return newExchange;
        };
    }

    private Processor toResponse() {
        return exchange -> {
            boolean consensus = exchange.getIn().getBody(Boolean.class);
            String pizzaType = exchange.getIn().getHeader("pizza-type", String.class);
            String transformedMessage = String.format("%s sind mit %s einverstanden.",
                    consensus ? "Alle" : "Nicht alle",
                    pizzaType);
            exchange.getIn().setBody(transformedMessage);
        };
    }
}
