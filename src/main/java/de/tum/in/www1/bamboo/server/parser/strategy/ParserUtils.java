package de.tum.in.www1.bamboo.server.parser.strategy;

import nu.xom.Element;

/**
 * Utility class providing shared functionality for report parsing
 */
public class ParserUtils {

    /**
     * Extracts and parses an attribute to an int. Defaults to 0 if parsing fails
     *
     * @param element Element with attributes
     * @param attribute Attribute  to extract
     * @return extracted number
     */
    public static int extractInt(Element element, String attribute) {
        try {
            return Integer.parseInt(element.getAttributeValue(attribute));
        } catch (NumberFormatException e) {
            // If for some reason the line number can't be parsed, we default to line 0
            return 0;
        }
    }
}
