package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;

import java.util.Arrays;
import java.util.Map;

public class PizzaPartyHelper {

    /**
     * Is the pizza type among the pizza preferences?
     * @param pizzaPreferences pizza preferences, the format does not matter
     * @param pizzaType pizza type to check
     * @return whether the pizza type is one of the preferred pizza types
     */
    public static Boolean checkApproval(String pizzaPreferences, String pizzaType) {
        return Arrays.asList(pizzaPreferences.replace(", ", ",").split(",")).contains(pizzaType);
    }

    public static String createSuggestionMessage(String pizzaType) {
        return toJson(Map.of("pizza-type", pizzaType));
    }

    public static String createFriendResponseMessage(String pizzaType, Boolean approval) {
        return toJson(Map.of("pizza-type", pizzaType, "approval", approval));
    }

    public static String toJson(Map<String, ?> input) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "error, see logs";
        }
    }

    public static String createAggregatedResponseMessage(Boolean consensus, String pizzaType) {
        return String.format("%s Freunde sind mit %s einverstanden.",
                consensus ? "Alle" : "Nicht alle",
                pizzaType);
    }

    /**
     * Check if two exchanges that have a specific boolean header both have the header value set to true.
     * An Exchange must have that header.
     * One of the exchanges can be null, but not both.
     * Return an exchange with a body of type boolean, where the body is the result of that check.
     *
     * @param exchangeOne an exchange that is either null or has the specified header
     * @param exchangeTwo an exchange that is either null or has the specified header
     * @param header the header that will be compared
     * @return an exchange with a body of type boolean that tells if both headers are true
     */
    public static Exchange obtainExchangeWhereBodyTellsWhetherAllExchangesHaveTheirHeaderSetToTrue(
            Exchange exchangeOne, Exchange exchangeTwo, String header) {
        if (exchangeOne == null) {
            boolean headerValueInExchangeTwo = exchangeTwo.getMessage().getHeader(header, Boolean.class);
            exchangeTwo.getMessage().setBody(headerValueInExchangeTwo);
            return exchangeTwo;
        } else if (exchangeTwo == null) {
            boolean headerValueInExchangeOne = exchangeOne.getMessage().getHeader(header, Boolean.class);
            exchangeOne.getMessage().setBody(headerValueInExchangeOne);
            return exchangeOne;
        } else {
            boolean headerValueInExchangeTwo = exchangeTwo.getMessage().getHeader(header, Boolean.class);
            boolean headerValueInExchangeOne = exchangeOne.getMessage().getHeader(header, Boolean.class);
            boolean bothApprove = headerValueInExchangeTwo && headerValueInExchangeOne;
            exchangeTwo.getMessage().setBody(bothApprove);
            return exchangeTwo;
        }
    }
}
