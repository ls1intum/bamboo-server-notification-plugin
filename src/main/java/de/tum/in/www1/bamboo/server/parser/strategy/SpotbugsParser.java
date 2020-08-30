package de.tum.in.www1.bamboo.server.parser.strategy;

import de.tum.in.www1.bamboo.server.parser.domain.Issue;
import de.tum.in.www1.bamboo.server.parser.domain.Report;
import de.tum.in.www1.bamboo.server.parser.domain.StaticCodeAnalysisTool;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import java.util.ArrayList;
import java.util.List;

public class SpotbugsParser implements ParserStrategy {

    private static final String BUGINSTANCE_ELEMENT = "BugInstance";
    private static final String BUGINSTANCE_ATT_TYPE = "type";
    private static final String BUGINSTANCE_ATT_CATEGORY = "category";
    private static final String BUGINSTANCE_ATT_PRIORITY = "priority";
    private static final String SOURCELINE_ELEMENT = "SourceLine";
    private static final String SOURCELINE_ATT_SOURCEPATH = "sourcepath";
    private static final String SOURCELINE_ATT_START = "start";
    private static final String LONGMESSAGE_ELEMENT = "LongMessage";

    @Override
    public Report parse(Document doc) {
        Report report = new Report(StaticCodeAnalysisTool.SPOTBUGS);
        List<Issue> issues = new ArrayList<>();
        // Element BugCollection
        Element root = doc.getRootElement();

        // Iterate over <BugInstance> elements
        for (Element bugInstance : root.getChildElements(BUGINSTANCE_ELEMENT)) {
            Issue issue = new Issue();

            // Extract bugInstance attributes
            issue.setRule(bugInstance.getAttributeValue(BUGINSTANCE_ATT_TYPE));
            issue.setCategory(bugInstance.getAttributeValue(BUGINSTANCE_ATT_CATEGORY));
            issue.setPriority(bugInstance.getAttributeValue(BUGINSTANCE_ATT_PRIORITY));

            // Extract information out of <SourceLine>
            Elements sourceLines = bugInstance.getChildElements(SOURCELINE_ELEMENT);
            if (sourceLines.size() > 0) {
                Element sourceLine = sourceLines.get(0);
                // The sourcePath begins with the package name so we don't need to shorten it
                String unixPath = ParserUtils.transformToUnixPath(sourceLine.getAttributeValue(SOURCELINE_ATT_SOURCEPATH));
                issue.setFilePath(unixPath);
                // Set endLine by duplicating the startLine. Spotbugs does not support a endLine
                int startLine = ParserUtils.extractInt(sourceLine, SOURCELINE_ATT_START);
                issue.setStartLine(startLine);
                issue.setEndLine(startLine);
            }

            // Extract message
            Elements longMessages = bugInstance.getChildElements(LONGMESSAGE_ELEMENT);
            if (longMessages.size() > 0) {
                Element longMessage = longMessages.get(0);
                issue.setMessage(ParserUtils.stripNewLinesAndWhitespace(longMessage.getValue()));
            }
            issues.add(issue);
        }
        report.setIssues(issues);
        return report;
    }
}
