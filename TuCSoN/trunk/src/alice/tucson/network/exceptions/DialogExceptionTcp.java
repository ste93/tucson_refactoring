package alice.tucson.network.exceptions;

/**
 * 
 * @author Saverio Cicora
 * 
 */
public class DialogExceptionTcp extends DialogException {
    private static final long serialVersionUID = 1L;
    
    public DialogExceptionTcp(){
    	super();
    }
    
    public DialogExceptionTcp(final Throwable cause){
    	super(cause);
    }
}
