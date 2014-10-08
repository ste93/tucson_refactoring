package it.unibo.sd.jade.exceptions;

/**
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * 
 */
public class CannotAcquireACCException extends Exception {
    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param msg
     *            the message describing the exception
     */
    public CannotAcquireACCException(final String msg) {
        super(msg);
    }
}
