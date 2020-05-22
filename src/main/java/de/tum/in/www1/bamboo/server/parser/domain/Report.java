package de.tum.in.www1.bamboo.server.parser.domain;

import java.util.List;

public class Report {

    private String tool;

    private List<Issue> issues;

    public Report(String tool) {
        this.tool = tool;;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}
