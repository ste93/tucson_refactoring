package alice.tucson.network.exceptions;

public class DialogAcceptException extends DialogException {

	/**
	 * Exception thrown when something goes wrong while
	 *  accepting a new dialog connection
	 */
	private static final long serialVersionUID = 1L;

	public DialogAcceptException(String message, Throwable cause) {
		super(message, cause);
	}

	public DialogAcceptException(String message) {
		super(message);
	}

	public DialogAcceptException(Throwable cause) {
		super(cause);
	}
	
	

}
