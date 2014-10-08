package it.unibo.sd.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;

/**
 * Specification actions are those involving specification tuples, that is,
 * ReSpecT reactions specifications.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public abstract class AbstractTucsonSpecificationAction extends
        AbstractTucsonAction {
    /**
     * the tuple representing the event, guards and body of a ReSpecT
     * specification tuple
     */
    protected LogicTuple event, guards, reaction;

    /**
     * 
     * @param t
     *            the TuCSoN tuple centre id target of the coordination
     *            operation
     * @param e
     *            the logic tuple representing the triggering event of the
     *            ReSpecT specification tuple
     * @param g
     *            the logic tuple representing the guards of the ReSpecT
     *            specification tuple
     * @param r
     *            the logic tuple representing the reaction body of the ReSpecT
     *            specification tuple
     */
    public AbstractTucsonSpecificationAction(final TucsonTupleCentreId t,
            final LogicTuple e, final LogicTuple g, final LogicTuple r) {
        super(t);
        this.event = e;
        this.guards = g;
        this.reaction = r;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(reaction(" + this.event + ", " + this.guards + ", "
                + this.reaction + ")) to " + this.tcid;
    }
}
