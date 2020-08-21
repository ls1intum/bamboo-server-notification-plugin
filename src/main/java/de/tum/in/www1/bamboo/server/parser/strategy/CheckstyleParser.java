package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Issue;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import de.tum.in.www1.bamboo.server.parser.domain.StaticAssessmentTool;
import nu.xom.Document;
import nu.xom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CheckstyleParser implements ParserStrategy {

    private static final String FILE_TAG = "file";
    private static final String FILE_ATT_NAME = "name";
    private static final String ERROR_ATT_SOURCE = "source";
    private static final String ERROR_ATT_SEVERITY = "severity";
    private static final String ERROR_ATT_MESSAGE = "message";
    private static final String ERROR_ATT_LINENUMBER = "line";
    private static final String ERROR_ATT_COLUMN = "column";
    private static final String CATEGORY_DELIMITER = "checks";
    private static final String CATEGORY_MISCELLANEOUS = "miscellaneous";

    @Override
    public Report parse(Document doc) {
        Report report = new Report(StaticAssessmentTool.CHECKSYTLE);
        List<Issue> issues = new ArrayList<>();
        Element root = doc.getRootElement();

        // Iterate over all <file> elements
        for (Element fileElement : root.getChildElements(FILE_TAG)) {
            String classPath = transformToFullyQualifiedClassName(fileElement.getAttributeValue(FILE_ATT_NAME));

            // Iterate over all <error> elements
            for (Element errorElement : fileElement.getChildElements()) {
                Issue issue = new Issue(classPath);

                String errorSource = errorElement.getAttributeValue(ERROR_ATT_SOURCE);
                extractRuleAndCategory(issue, errorSource);

                issue.setPriority(errorElement.getAttributeValue(ERROR_ATT_SEVERITY));
                issue.setMessage(errorElement.getAttributeValue(ERROR_ATT_MESSAGE));
                issue.setStartLine(ParserUtils.extractInt(errorElement, ERROR_ATT_LINENUMBER));
                issue.setEndColumn(ParserUtils.extractInt(errorElement, ERROR_ATT_COLUMN));

                issues.add(issue);
            }
        }
        report.setIssues(issues);
        return report;
    }

    /**
     * Transform the path to a fully qualified class name starting at the src directory.
     * If src directory is not part of the path, the whole path will be transformed to a dotted notation and returned.
     *
     * @param path absolute path like C:/BambooTest/src/com/abc/staticCodeAnalysis/App.java
     * @return the package name like com.abc.staticCodeAnalysis.App
     */
    private String transformToFullyQualifiedClassName(String path) {
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

    /**
     * Extracts and sets the rule and the category given the check's package name.
     *
     * @param issue issue under construction
     * @param errorSource package like com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocPackageCheck. The first
     *                    segment after '.checks.' denotes the category and the segment after the rule. Some rules do
     *                    not belong to a category e.g. com.puppycrawl.tools.checkstyle.checks.NewlineAtEndOfFileCheck.
     *                    Such rule will be grouped under {@link #CATEGORY_MISCELLANEOUS}.
     */
    private void extractRuleAndCategory(Issue issue, String errorSource) {
        String[] errorSourceSegments = errorSource.split("\\.");
        int noOfSegments = errorSourceSegments.length;

        // Should never happen but check for robustness
        if (noOfSegments < 2) {
            issue.setCategory(errorSource);
            return;
        }
        String type = errorSourceSegments[noOfSegments - 1];
        String category = errorSourceSegments[noOfSegments - 2];

        // Check if the rule has a category
        if (category.equals(CATEGORY_DELIMITER)) {
            category = CATEGORY_MISCELLANEOUS;
        }
        issue.setType(type);
        issue.setCategory(category);
    }
}
