package Common;

public class InvalidIPException extends Exception {
    public final String reason;

    public InvalidIPException(String reason) {
        this.reason = reason;
    }
}