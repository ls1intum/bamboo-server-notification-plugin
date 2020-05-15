package de.tum.in.www1.bamboo.server.parser.domain;

import java.util.List;

public class Report {

    private String tool;

    List<Finding> findings;

    public Report(String tool) {
        this.tool = tool;;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public List<Finding> getFindings() {
        return findings;
    }

    public void setFindings(List<Finding> findings) {
        this.findings = findings;
    }
}
