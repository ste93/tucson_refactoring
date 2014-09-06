package alice.tucson.network.exceptions;

public class DialogReceiveException extends DialogException {

	/**
	 * Exception thrown when a TuCSoN receiving operation goes wrong 
	 */
	private static final long serialVersionUID = 1L;

	public DialogReceiveException(String message, Throwable cause) {
		super(message, cause);
	}

	public DialogReceiveException(String message) {
		super(message);
	}

	public DialogReceiveException(Throwable cause) {
		super(cause);
	}

	
}
