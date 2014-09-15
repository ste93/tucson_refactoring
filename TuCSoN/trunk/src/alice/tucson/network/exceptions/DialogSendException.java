package alice.tucson.network.exceptions;

public class DialogSendException extends DialogException {

	/**
	 * Exception thrown when a TuCSoN sending operation goes wrong
	 */
	private static final long serialVersionUID = 1L;

	public DialogSendException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DialogSendException(final String message) {
		super(message);
	}

	public DialogSendException(final Throwable cause) {
		super(cause);
	}
	
	

}
