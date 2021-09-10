import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class FileTest extends CamelTestSupport {

    @Test
    public void testPrintFile() throws Exception {
        getMockEndpoint("mock:end").expectedMessageCount(1);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:data?noop=true&fileName=friend-1-pizza-preferences.txt")
                        .to("mock:end");
            }
        };
    }

}
