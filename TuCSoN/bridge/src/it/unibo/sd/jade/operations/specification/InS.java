package it.unibo.sd.jade.operations.specification;

import it.unibo.sd.jade.operations.AbstractTucsonSpecificationAction;
import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * 
 */
public class InS extends AbstractTucsonSpecificationAction {
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
    public InS(final TucsonTupleCentreId t, final LogicTuple e,
            final LogicTuple g, final LogicTuple r) {
        super(t, e, g, r);
    }

    @Override
    public ITucsonOperation executeAsynch(final EnhancedAsynchACC acc,
            final TucsonOperationCompletionListener listener)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return acc.inS(this.tcid, this.event, this.guards, this.reaction,
                listener);
    }

    @Override
    public ITucsonOperation executeSynch(final EnhancedSynchACC acc,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return acc.inS(this.tcid, this.event, this.guards, this.reaction,
                timeout);
    }
}
