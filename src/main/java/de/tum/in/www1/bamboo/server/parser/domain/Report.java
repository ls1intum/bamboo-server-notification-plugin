package de.tum.in.www1.bamboo.server.parser.domain;

import java.util.List;

public class Report {

    private StaticAssessmentTool tool;

    private List<Issue> issues;

    public Report(StaticAssessmentTool tool) {
        this.tool = tool;;
    }

    public StaticAssessmentTool getTool() {
        return tool;
    }

    public void setTool(StaticAssessmentTool tool) {
        this.tool = tool;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}
