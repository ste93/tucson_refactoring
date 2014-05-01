package it.unibo.sd.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;

/**
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * 
 */
public abstract class AbstractTucsonOrdinaryAction extends AbstractTucsonAction {
    /**
     * 
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

    // /**
    // *
    // * @return the Logic Tuple argument of the coordination operation
    // */
    // public LogicTuple getTuple() {
    // return this.tuple;
    // }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(" + this.tuple + ") to " + this.tcid;
    }
}
