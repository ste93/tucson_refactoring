package it.unibo.sd.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;

/**
 * Ordinary actions are those involving ordinary tuples (not specification
 * tuples).
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public abstract class AbstractTucsonOrdinaryAction extends AbstractTucsonAction {
    /**
     * the tuple argument of the operation
     */
    protected LogicTuple tuple;

    /**
     * 
     * @param tc
     *            the TuCSoN tuple centre id target of the coordination
     *            operation
     * @param t
     *            the logic tuple argument of the coordination operation
     */
    public AbstractTucsonOrdinaryAction(final TucsonTupleCentreId tc,
            final LogicTuple t) {
        super(tc);
        this.tuple = t;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(" + this.tuple + ") to " + this.tcid;
    }
}
