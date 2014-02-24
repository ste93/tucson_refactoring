/**
 * LogicMatchingEngine.java
 */
package alice.logictuple;

import alice.tuprolog.Prolog;
import alice.tuprolog.Term;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 07/gen/2014
 * 
 */
public class LogicMatchingEngine {

    private static final Prolog MATCHING_ENGINE = new Prolog();

    static public boolean
            match(final LogicTuple tupleA, final LogicTuple tupleB) {
        final Term a = tupleA.toTerm().getTerm();
        final Term b = tupleB.toTerm().getTerm();
        return a.match(b);
    }

    static public boolean propagate(final LogicTuple tupleA,
            final LogicTuple tupleB) {
        final Term a = tupleA.toTerm().getTerm();
        final Term b = tupleB.toTerm().getTerm();
        return a.unify(LogicMatchingEngine.MATCHING_ENGINE, b);
    }

    private LogicMatchingEngine() {
    }

}
