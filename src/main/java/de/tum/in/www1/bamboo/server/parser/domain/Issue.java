package de.tum.in.www1.bamboo.server.parser.domain;

public class Issue {

    // Path of to the source file using unix file separators
    private String file;

    private String rule;

    // TODO: This is currently not used in Artemis but could be useful for further filtering.
    // Map tool specific codes to a common format
    private String priority;

    private String category;

    private String message;

    private Integer startLine;

    private Integer endLine;

    private Integer startColumn;

    private Integer endColumn;

    public Issue() {
    }

    public Issue(String file) {
        this.file = file;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public void setStartLine(Integer startLine) {
        this.startLine = startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public void setEndLine(Integer endLine) {
        this.endLine = endLine;
    }

    public Integer getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(Integer startColumn) {
        this.startColumn = startColumn;
    }

    public Integer getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(Integer endColumn) {
        this.endColumn = endColumn;
    }
}
