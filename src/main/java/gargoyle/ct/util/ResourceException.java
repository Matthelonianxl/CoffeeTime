package gargoyle.ct.util;

public class ResourceException extends RuntimeException {
    private static final long serialVersionUID = -695314450946813395L;

    public ResourceException(String message) {
        super(message);
    }

    public ResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
