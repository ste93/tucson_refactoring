package alice.tucson.network.exceptions;

public class DialogCloseException extends DialogException {

	/**
	 * Exception thrown when something goes wrong while
	 *  closing a dialog connection
	 */
	private static final long serialVersionUID = 1L;

	public DialogCloseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DialogCloseException(String message) {
		super(message);
	}

	public DialogCloseException(Throwable cause) {
		super(cause);
	}

}
