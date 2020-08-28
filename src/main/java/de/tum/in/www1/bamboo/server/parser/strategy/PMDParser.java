package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Issue;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import de.tum.in.www1.bamboo.server.parser.domain.StaticAssessmentTool;
import nu.xom.Document;
import nu.xom.Element;

import java.util.ArrayList;
import java.util.List;

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
        for (Element fileElement : root.getChildElements(FILE_TAG, root.getNamespaceURI())) {
            // Extract the file path
            String filePath = fileElement.getAttributeValue(FILE_ATT_NAME);
            String shortenedPath = ParserUtils.shortenAndTransformToUnixPath(filePath);

            // Iterate over all <violation> elements
            for (Element violationElement : fileElement.getChildElements()) {
                Issue issue = new Issue(shortenedPath);

                issue.setRule(violationElement.getAttributeValue(VIOLATION_ATT_RULE));
                issue.setCategory(violationElement.getAttributeValue(VIOLATION_ATT_RULESET));
                issue.setPriority(violationElement.getAttributeValue(VIOLATION_ATT_PRIORITY));
                issue.setStartLine(ParserUtils.extractInt(violationElement, VIOLATION_ATT_BEGINLINE));
                issue.setEndLine(ParserUtils.extractInt(violationElement, VIOLATION_ATT_ENDLINE));
                issue.setStartColumn(ParserUtils.extractInt(violationElement, VIOLATION_ATT_BEGINCOLUMN));
                issue.setEndColumn(ParserUtils.extractInt(violationElement, VIOLATION_ATT_ENDCOLUMN));
                issue.setMessage(ParserUtils.stripNewLinesAndWhitespace(violationElement.getValue()));

                issues.add(issue);
            }
        }
        report.setIssues(issues);
        return report;
    }
}
