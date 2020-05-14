package de.tum.in.www1.bamboo.server.parser.domain;

public class Finding {

    private String type;

    private String priority;

    private String category;

    private String message;

    private int line;

    public Finding(String type, String priority, String category, String message, int line) {
        this.type = type;
        this.priority = priority;
        this.category = category;
        this.message = message;
        this.line = line;
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

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
