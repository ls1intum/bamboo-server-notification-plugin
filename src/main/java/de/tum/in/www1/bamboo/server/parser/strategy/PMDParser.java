package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Issue;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import de.tum.in.www1.bamboo.server.parser.domain.StaticAssessmentTool;
import nu.xom.Document;
import nu.xom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PMDParser implements ParserStrategy {

    // XSD for PMD XML reports: https://github.com/pmd/pmd/blob/master/pmd-core/src/main/resources/report_2_0_0.xsd
    private static final String FILE_TAG = "file";
    private static final String FILE_ATT_NAME = "name";
    private static final String VIOLATION_ATT_CLASS = "class";
    private static final String VIOLATION_ATT_PACKAGE = "package";
    private static final String VIOLATION_ATT_RULE = "rule";
    private static final String VIOLATION_ATT_RULESET = "ruleset";
    private static final String VIOLATION_ATT_PRIORITY = "priority";
    private static final String VIOLATION_ATT_BEGINLINE = "beginline";
    private static final String VIOLATION_ATT_ENDLINE = "endline";
    private static final String VIOLATION_ATT_BEGINCOLUMN = "begincolumn";
    private static final String VIOLATION_ATT_ENDCOLUMN = "endcolumn";

    @Override
    public Report parse(Document doc) {
        Report report = new Report(StaticAssessmentTool.PMD);
        List<Issue> issues = new ArrayList<>();
        Element root = doc.getRootElement();

        // Iterate over all <file> elements
        for (Element fileElement : root.getChildElements(FILE_TAG)) {
            // Extract the file path but use this only as a fallback more specific information is available
            String fileName = fileElement.getAttributeValue(FILE_ATT_NAME);
            String packageFromFileName = ParserUtils.transformPathToFullyQualifiedClassName(fileName);

            // Iterate over all <violation> elements
            for (Element violationElement : fileElement.getChildElements()) {
                Issue issue = new Issue();

                issue.setClassname(buildFullyQualifiedClassName(violationElement).orElse(packageFromFileName));
                issue.setType(violationElement.getAttributeValue(VIOLATION_ATT_RULE));
                issue.setCategory(violationElement.getAttributeValue(VIOLATION_ATT_RULESET));
                issue.setPriority(violationElement.getAttributeValue(VIOLATION_ATT_PRIORITY));
                issue.setStartLine(ParserUtils.extractInt(violationElement, VIOLATION_ATT_BEGINLINE));
                issue.setEndLine(ParserUtils.extractInt(violationElement, VIOLATION_ATT_ENDLINE));
                issue.setStartColumn(ParserUtils.extractInt(violationElement, VIOLATION_ATT_BEGINCOLUMN));
                issue.setEndColumn(ParserUtils.extractInt(violationElement, VIOLATION_ATT_ENDCOLUMN));

                issues.add(issue);
            }
        }
        report.setIssues(issues);
        return report;
    }

    /**
     * Build the fully qualified class name using the package and class attribute values.
     * As the XSD defines those attributes as optional, an empty optional is returned if one attribute does not exist.
     *
     * @param violationElement Violation element of the PMD report
     * @return Optional containing the fully qualified class name. Empty if package or class attribute doesn't exist
     */
    private Optional<String> buildFullyQualifiedClassName(Element violationElement) {
        String className = violationElement.getAttributeValue(VIOLATION_ATT_CLASS);
        String packageName = violationElement.getAttributeValue(VIOLATION_ATT_PACKAGE);
        if (className == null || packageName == null) {
            return Optional.empty();
        } else {
            return Optional.of(packageName + "." + className);
        }
    }
}
