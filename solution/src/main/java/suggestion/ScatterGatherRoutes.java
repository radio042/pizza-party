package suggestion;

import org.apache.camel.builder.RouteBuilder;

public class ScatterGatherRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // todo implement scatter route
        from("todo").to("todo");

        // todo implement gather route
        from("todo").to("todo");
    }
}
