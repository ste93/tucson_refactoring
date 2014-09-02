/**
 * InvalidJValException.java
 */
package alice.tuples.javatuples.exceptions;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public class InvalidJValException extends Exception {
    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

	public InvalidJValException() {
		super();
	}

	public InvalidJValException(String message) {
		super(message);
	}
}
