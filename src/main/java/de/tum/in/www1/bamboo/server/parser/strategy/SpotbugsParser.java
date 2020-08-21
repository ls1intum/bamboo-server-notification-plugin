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

        // Iterate over <file> elements
        for (Element fileElement : root.getChildElements(FILE_TAG)) {
            String className = fileElement.getAttributeValue(FILE_ATT_CLASSNAME);

            // Iterate over <bugInstance> elements
            for (Element bugInstanceElement : fileElement.getChildElements()) {
                Issue issue = new Issue(className);

                issue.setType(bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_TYPE));
                issue.setPriority(bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_PRIORITY));
                issue.setCategory(bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_CATEGORY));
                issue.setMessage(bugInstanceElement.getAttributeValue(BUGINSTANCE_ATT_MESSAGE));

                issues.add(issue);
            }
        }
        report.setIssues(issues);
        return report;
    }

}
