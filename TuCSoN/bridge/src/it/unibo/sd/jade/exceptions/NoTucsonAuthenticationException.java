package it.unibo.sd.jade.exceptions;

/**
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * 
 */
public class NoTucsonAuthenticationException extends Exception {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param msg
     *            the message describign the exception
     */
    public NoTucsonAuthenticationException(final String msg) {
        super(msg);
    }
}
