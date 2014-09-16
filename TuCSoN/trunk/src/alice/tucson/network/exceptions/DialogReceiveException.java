package alice.tucson.network.exceptions;

public class DialogReceiveException extends DialogException {
    /**
     * Exception thrown when a TuCSoN receiving operation goes wrong
     */
    private static final long serialVersionUID = 1L;

    public DialogReceiveException(final String message) {
        super(message);
    }

    public DialogReceiveException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DialogReceiveException(final Throwable cause) {
        super(cause);
    }
}
