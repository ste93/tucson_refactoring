/**
 * IJTuple.java
 */
package alice.tuples.javatuples.api;

import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public interface IJTuple extends Tuple {

    /**
     * 
     * @param arg
     *            the JVal to add to this JTuple
     * @throws InvalidTupleException
     *             if the given JVal is invalid (e.g. null)
     */
    void addArg(IJVal arg) throws InvalidTupleException;

    /**
     * 
     * @param i
     *            the index of the JVal to retrieve (starting from 0)
     * @return the JVal retrieved
     * @throws InvalidOperationException
     *             if the given index is invalid (e.g. > JTuple.getNArgs())
     */
    IJVal getArg(int i) throws InvalidOperationException;

    /**
     * 
     * @return the number of JVal in this JTuple
     */
    int getNArgs();

}
