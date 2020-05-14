package de.tum.in.www1.bamboo.server.parser.domain;

import org.json.JSONObject;

import java.util.List;

public class Report {

    private String tool;

    private String threshold;

    private String effort;

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

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getEffort() {
        return effort;
    }

    public void setEffort(String effort) {
        this.effort = effort;
    }

    public List<Finding> getFindings() {
        return findings;
    }

    public void setFindings(List<Finding> findings) {
        this.findings = findings;
    }
}
