package alice.tucson.network.exceptions;

public class DialogAcceptException extends DialogException {

	/**
	 * Exception thrown when something goes wrong while
	 *  accepting a new dialog connection
	 */
	private static final long serialVersionUID = 1L;

	public DialogAcceptException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DialogAcceptException(final String message) {
		super(message);
	}

	public DialogAcceptException(final Throwable cause) {
		super(cause);
	}
	
	

}
