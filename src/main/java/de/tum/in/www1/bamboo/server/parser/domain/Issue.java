package de.tum.in.www1.bamboo.server.parser.domain;

public class Issue {

    private String classname;

    private String type;

    private String priority;

    private String category;

    private String message;

    private Integer line;

    private Integer column;

    public Issue(String classname, String type, String priority, String category, String message, Integer line, Integer column) {
        this.classname = classname;
        this.type = type;
        this.priority = priority;
        this.category = category;
        this.message = message;
        this.line = line;
        this.column = column;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }
}
