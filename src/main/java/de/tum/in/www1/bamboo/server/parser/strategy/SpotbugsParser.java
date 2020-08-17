package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Issue;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import de.tum.in.www1.bamboo.server.parser.domain.StaticAssessmentTool;
import nu.xom.Document;
import nu.xom.Element;

import java.util.ArrayList;
import java.util.List;

public class SpotbugsParser implements ParserStrategy {

    private static final String FILE_TAG = "file";
    private static final String FILE_ATT_CLASSNAME = "classname";
    private static final String BUGINSTANCE_ATT_TYPE = "type";
    private static final String BUGINSTANCE_ATT_PRIORITY = "priority";
    private static final String BUGINSTANCE_ATT_CATEGORY = "category";
    private static final String BUGINSTANCE_ATT_MESSAGE = "message";
    private static final String BUGINSTANCE_ATT_LINENUMBER = "lineNumber";

    @Override
    public Report parse(Document doc) {
        Report report = new Report(StaticAssessmentTool.SPOTBUGS);
        List<Issue> issues = new ArrayList<>();
        Element root = doc.getRootElement();

        for (Element fileElement : root.getChildElements(FILE_TAG)) {
            String classname = fileElement.getAttributeValue(FILE_ATT_CLASSNAME);

            for (Element bugInstanceElement : fileElement.getChildElements()) {
                String type = bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_TYPE);
                String priority = bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_PRIORITY);
                String category = bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_CATEGORY);
                String message = bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_MESSAGE);
                Integer line;
                try {
                    line = Integer.parseInt(bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_LINENUMBER));
                } catch (NumberFormatException e) {
                    // If for some reason the line number can't be parsed, we default to line 0
                    line = 0;
                }
                issues.add(new Issue(classname, type, priority, category, message, line, null));
            }
        }
        report.setIssues(issues);
        return report;
    }

}
