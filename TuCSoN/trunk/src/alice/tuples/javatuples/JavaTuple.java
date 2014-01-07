/**
 * JavaTuple.java
 */
package alice.tuples.javatuples;

import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 07/gen/2014
 * 
 */
public class JavaTuple implements TupleTemplate {

    /*
     * (non-Javadoc)
     * @see
     * alice.tuplecentre.api.TupleTemplate#match(alice.tuplecentre.api.Tuple)
     */
    @Override
    public boolean match(Tuple t) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see alice.tuplecentre.api.TupleTemplate#propagate(alice.tuprolog.Prolog,
     * alice.tuplecentre.api.Tuple)
     */
    @Override
    public boolean propagate(Tuple t) {
        // TODO Auto-generated method stub
        return false;
    }

}
