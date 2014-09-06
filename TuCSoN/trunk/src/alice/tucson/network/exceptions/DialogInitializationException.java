package alice.tucson.network.exceptions;


public class DialogInitializationException extends DialogException {
    private static final long serialVersionUID = 1L;

    /**
	 * Exception thrown when something goes wrong while
	 *  creating a new ServerSocket
	 */
	public DialogInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public DialogInitializationException(String message) {
		super(message);
	}

	public DialogInitializationException(Throwable cause) {
		super(cause);
	}


}
