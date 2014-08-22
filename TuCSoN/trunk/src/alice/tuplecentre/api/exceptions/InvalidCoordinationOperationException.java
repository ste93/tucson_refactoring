package alice.tuplecentre.api.exceptions;

/**
 * @author Mattia Balducci
 * @author Alessia Papini
 * 
 */
public class InvalidCoordinationOperationException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidCoordinationOperationException(){
    	super();
    }
    
    public InvalidCoordinationOperationException(final String message){
    	super(message);
    }
}