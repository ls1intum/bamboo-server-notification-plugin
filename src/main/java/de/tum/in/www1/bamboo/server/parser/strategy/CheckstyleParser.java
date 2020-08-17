package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Issue;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import de.tum.in.www1.bamboo.server.parser.domain.StaticAssessmentTool;
import nu.xom.Document;
import nu.xom.Element;

import java.util.ArrayList;
import java.util.List;

public class CheckstyleParser implements ParserStrategy {

    private static final String FILE_TAG = "file";
    private static final String FILE_ATT_NAME = "name";
    /**
     * The source attribute of error elements looks like com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocPackageCheck
     * where the first segment after 'checks' denotes the category and the segment after denotes rule. The are rules
     * which do not belong to a category e.g. com.puppycrawl.tools.checkstyle.checks.NewlineAtEndOfFileCheck. In this
     * case we will use the rule name as the category.
     */
    private static final String ERROR_ATT_SOURCE = "source";
    private static final String ERROR_ATT_SEVERITY = "severity";
    private static final String ERROR_ATT_MESSAGE = "message";
    private static final String ERROR_ATT_LINENUMBER = "line";
    private static final String ERROR_ATT_COLUMN = "column";
    private static final String CATEGORY_DELIMITER = "checks";

    @Override
    public Report parse(Document doc) {
        Report report = new Report(StaticAssessmentTool.CHECKSYTLE);
        List<Issue> issues = new ArrayList<>();
        Element root = doc.getRootElement();

        // Iterate over all <file> elements
        for (Element fileElement : root.getChildElements(FILE_TAG)) {
            String classname = fileElement.getAttributeValue(FILE_ATT_NAME);

            // Iterate over all <error> elements
            for (Element errorElement : fileElement.getChildElements()) {
                String errorSource = errorElement.getAttributeValue(ERROR_ATT_SOURCE);
                String[] errorSegments = errorSource.split("\\.");
                String type = errorSegments[errorSegments.length - 1];
                String category = errorSegments[errorSegments.length - 2];
                if (category.equals(CATEGORY_DELIMITER)) {
                    category = type;
                }
                String priority = errorElement.getAttributeValue(ERROR_ATT_SEVERITY);
                String message = errorElement.getAttributeValue(ERROR_ATT_MESSAGE);
                Integer line;
                try {
                    line = Integer.parseInt(errorElement.getAttributeValue(ERROR_ATT_LINENUMBER));
                } catch (NumberFormatException e) {
                    // If for some reason the line number can't be parsed, we default to line 0
                    line = 0;
                }
                Integer column;
                try {
                    column = Integer.parseInt(errorElement.getAttributeValue(ERROR_ATT_COLUMN));
                } catch (NumberFormatException e) {
                    // If for some reason the column number can't be parsed, we default to column 0
                    column = 0;
                }
                issues.add(new Issue(classname, type, priority, category, message, line, column));
            }
        }
        report.setIssues(issues);
        return report;
    }
}
