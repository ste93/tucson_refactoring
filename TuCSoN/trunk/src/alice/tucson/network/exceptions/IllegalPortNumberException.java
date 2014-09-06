package alice.tucson.network.exceptions;

public class IllegalPortNumberException extends RuntimeException {

	/**
	 * Exception thrown when an invalid port number is used
	 */
	private static final long serialVersionUID = 1L;

	public IllegalPortNumberException(String arg0) {
		super(arg0);
	}

	
}
