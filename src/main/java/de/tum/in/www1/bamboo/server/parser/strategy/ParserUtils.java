package de.tum.in.www1.bamboo.server.parser.strategy;

import nu.xom.Element;

import java.io.File;

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
     * Transform the path to a fully qualified class name starting at the src directory.
     * If src directory is not part of the path, the whole path will be transformed to a dotted notation and returned.
     *
     * @param path Absolute path like C:/BambooTest/src/com/abc/staticCodeAnalysis/App.java
     * @return Package name like com.abc.staticCodeAnalysis.App
     */
    public static String transformPathToFullyQualifiedClassName(String path) {
        if (path == null || path.isEmpty()) {
            return path;
        }
        String packageDelimiter = File.separator + "src" + File.separator;
        int indexOfSrc = path.indexOf(packageDelimiter);
        // Fallback: Return the whole path if no src folder exists
        if (indexOfSrc == -1) {
            return path.replace(File.separator, ".");
        }
        int javaExtensionIndex = path.endsWith(".java") ? path.length() - 5 : path.length();
        return path.substring(indexOfSrc + packageDelimiter.length(), javaExtensionIndex).replace(File.separator, ".");
    }
}
