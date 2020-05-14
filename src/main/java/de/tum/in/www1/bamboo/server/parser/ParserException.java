package de.tum.in.www1.bamboo.server.parser;

public class ParserException extends Exception {

    public ParserException() {
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
