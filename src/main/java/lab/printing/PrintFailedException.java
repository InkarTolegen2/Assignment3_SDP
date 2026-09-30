package lab.printing;

public class PrintFailedException extends RuntimeException {

    public enum Reason {
        DEVICE_UNAVAILABLE,
        OUT_OF_MEDIA,
        INVALID_LABEL,
        UNSUPPORTED_DESTINATION,
        UNKNOWN
    }

    private final Reason reason;

    public PrintFailedException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public PrintFailedException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
