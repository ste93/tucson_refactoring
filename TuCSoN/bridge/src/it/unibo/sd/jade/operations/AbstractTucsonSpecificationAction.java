package it.unibo.sd.jade.operations;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;

/**
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * 
 */
public abstract class AbstractTucsonSpecificationAction extends
        AbstractTucsonAction {

    /**
     * 
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
}
