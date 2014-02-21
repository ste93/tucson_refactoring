/**
 * IJVal.java
 */
package alice.tuples.javatuples;

import alice.tuplecentre.api.exceptions.InvalidOperationException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public interface IJVal extends IJArg {

    boolean isDouble();

    boolean isFloat();

    boolean isInt();

    boolean isLiteral();

    boolean isLong();

    double toDouble() throws InvalidOperationException;

    float toFloat() throws InvalidOperationException;

    int toInt() throws InvalidOperationException;

    String toLiteral() throws InvalidOperationException;

    long toLong() throws InvalidOperationException;

}