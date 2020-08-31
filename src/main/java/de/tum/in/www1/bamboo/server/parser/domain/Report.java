package de.tum.in.www1.bamboo.server.parser.domain;

import java.util.List;

public class Report {

    private StaticCodeAnalysisTool tool;

    private List<Issue> issues;

    public Report(StaticCodeAnalysisTool tool) {
        this.tool = tool;;
    }

    public StaticCodeAnalysisTool getTool() {
        return tool;
    }

    public void setTool(StaticCodeAnalysisTool tool) {
        this.tool = tool;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}
