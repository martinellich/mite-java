package ch.martinelli.mite4java;

public class MiteApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public MiteApiException(int statusCode, String responseBody) {
        super("mite API error: HTTP %d — %s".formatted(statusCode, responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public MiteApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
    }

    public int statusCode() {
        return statusCode;
    }

    public String responseBody() {
        return responseBody;
    }
}
