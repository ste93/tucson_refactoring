package alice.tucson.network.exceptions;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 22/lug/2013
 * 
 */
public class DialogException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public DialogException() {
        super();
    }

    /**
     * 
     * @param message
     *            the message to store
     */
    public DialogException(final String message) {
        super(message);
    }
}
