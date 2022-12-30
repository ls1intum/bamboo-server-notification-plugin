package model;

import java.util.Objects;

public class CustomFeedback {

    private String name;

    private boolean successful;

    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomFeedback that = (CustomFeedback) o;
        return successful == that.successful && Objects.equals(name, that.name) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, successful, message);
    }
}
