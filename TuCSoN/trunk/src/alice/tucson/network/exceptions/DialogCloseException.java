package alice.tucson.network.exceptions;

public class DialogCloseException extends DialogException {

	/**
	 * Exception thrown when something goes wrong while
	 *  closing a dialog connection
	 */
	private static final long serialVersionUID = 1L;

	public DialogCloseException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DialogCloseException(final String message) {
		super(message);
	}

	public DialogCloseException(final Throwable cause) {
		super(cause);
	}

}
