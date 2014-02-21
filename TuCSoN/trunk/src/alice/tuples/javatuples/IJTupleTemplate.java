/**
 * IJTupleTemplate.java
 */
package alice.tuples.javatuples;

import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public interface IJTupleTemplate extends TupleTemplate {

    void addArg(IJArg arg) throws InvalidTupleException;

    IJArg getArg(int i) throws InvalidOperationException;

    int getNArgs();

}