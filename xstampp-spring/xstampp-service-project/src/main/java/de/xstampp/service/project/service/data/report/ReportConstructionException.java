package de.xstampp.service.project.service.data.report;

import org.springframework.http.HttpStatus;

public class ReportConstructionException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private String userMessage = "Report construction failed due to an internal server error.";

    public ReportConstructionException() {
    }

    public ReportConstructionException(String message) {
        super(message);
    }

    public ReportConstructionException(String userMessage, String logMessage, HttpStatus httpStatus) {
        super(userMessage + " --- " + logMessage);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }

    public ReportConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportConstructionException(Throwable cause) {
        super(cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
