package de.tum.in.www1.bamboo.server.parser.strategy;

import nu.xom.Element;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Utility class providing shared functionality for report parsing
 */
public class ParserUtils {

    /**
     * Extracts and parses an attribute to an int. Defaults to 0 if parsing fails.
     *
     * @param element Element with attributes
     * @param attribute Attribute  to extract
     * @return extracted number
     */
    public static int extractInt(Element element, String attribute) {
        try {
            return Integer.parseInt(element.getAttributeValue(attribute));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Shorten the path to start after the source directory, which is assumed to be '/assignment/src/'.
     * Returns the path using unix file separators.
     * If '/assignment/src/' is not found, the whole path will be returned.
     *
     * @param path Absolute path like C:\BambooTest\assignment\src\com\abc\staticCodeAnalysis\App.java
     * @return path like com/abc/staticCodeAnalysis/App.java]
     */
    public static String shortenAndTransformToUnixPath(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        String packageDelimiter = File.separator + "assignment" + File.separator + "src" + File.separator;
        int indexOfSrc = path.indexOf(packageDelimiter);

        // Fallback: Return the whole path if no assignment/src segment exists
        if (indexOfSrc == -1) {
            return transformToUnixPath(path);
        }
        return transformToUnixPath(path.substring(indexOfSrc + packageDelimiter.length()));
    }

    public static String transformToUnixPath(String path) {
        String fileDelimiterRegex = Pattern.quote(File.separator);
        return path.replace(File.separator, "/");
    }

    public static String stripNewLinesAndWhitespace(String text) {
        return text.replaceAll("(\\r|\\n)", "").trim();
    }
}
