/**
 * IJVal.java
 */
package alice.tuples.javatuples.api;

import alice.tuplecentre.api.exceptions.InvalidOperationException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public interface IJVal extends IJArg {
    /**
     * 
     * @return wether the JVal is a double
     */
    boolean isDouble();

    /**
     * 
     * @return wether the JVal is a float
     */
    boolean isFloat();

    /**
     * 
     * @return wether the JVal is a int
     */
    boolean isInt();

    /**
     * 
     * @return wether the JVal is a literal
     */
    boolean isLiteral();

    /**
     * 
     * @return wether the JVal is a long
     */
    boolean isLong();

    /**
     * 
     * @return the double value of the JVal
     * @throws InvalidOperationException
     *             if the JVal is not a double
     */
    double toDouble() throws InvalidOperationException;

    /**
     * 
     * @return the float value of the JVal
     * @throws InvalidOperationException
     *             if the JVal is not a float
     */
    float toFloat() throws InvalidOperationException;

    /**
     * 
     * @return the int value of the JVal
     * @throws InvalidOperationException
     *             if the JVal is not a int
     */
    int toInt() throws InvalidOperationException;

    /**
     * 
     * @return the literal (Java String) value of the JVal
     * @throws InvalidOperationException
     *             if the JVal is not a literal (Java String)
     */
    String toLiteral() throws InvalidOperationException;

    /**
     * 
     * @return the long value of the JVal
     * @throws InvalidOperationException
     *             if the JVal is not a long
     */
    long toLong() throws InvalidOperationException;
}
