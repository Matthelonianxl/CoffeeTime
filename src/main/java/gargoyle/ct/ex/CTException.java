package gargoyle.ct.ex;

public class CTException extends RuntimeException {
    private static final long serialVersionUID = -4669219915320326863L;

    public CTException(String message) {
        super(message);
    }

    public CTException(String message, Throwable cause) {
        super(message, cause);
    }
}
