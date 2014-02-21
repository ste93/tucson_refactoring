/**
 * IJTuple.java
 */
package alice.tuples.javatuples;

import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public interface IJTuple extends Tuple {

    void addArg(IJVal arg) throws InvalidTupleException;

    IJVal getArg(int i) throws InvalidOperationException;

    int getNArgs();

}