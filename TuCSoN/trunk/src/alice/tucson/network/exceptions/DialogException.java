package alice.tucson.network.exceptions;

/**
 * 
 * @author Saverio Cicora
 * 
 */
public class DialogException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public DialogException(){
    	super();
    }
    
    public DialogException(final Throwable cause){
    	super(cause);
    }

	public DialogException(String message, Throwable cause) {
		super(message, cause);
	}

	public DialogException(String message) {
		super(message);
	}
}
