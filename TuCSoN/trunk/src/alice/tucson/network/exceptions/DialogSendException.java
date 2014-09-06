package alice.tucson.network.exceptions;

public class DialogSendException extends DialogException {

	/**
	 * Exception thrown when a TuCSoN sending operation goes wrong
	 */
	private static final long serialVersionUID = 1L;

	public DialogSendException(String message, Throwable cause) {
		super(message, cause);
	}

	public DialogSendException(String message) {
		super(message);
	}

	public DialogSendException(Throwable cause) {
		super(cause);
	}
	
	

}
