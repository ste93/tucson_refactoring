package alice.tucson.network.exceptions;

public class DialogInitializationException extends DialogException {
    /**
     * Exception thrown when something goes wrong while creating a new
     * ServerSocket
     */
    private static final long serialVersionUID = 1L;

    public DialogInitializationException(final String message) {
        super(message);
    }

    public DialogInitializationException(final String message,
            final Throwable cause) {
        super(message, cause);
    }

    public DialogInitializationException(final Throwable cause) {
        super(cause);
    }
}
