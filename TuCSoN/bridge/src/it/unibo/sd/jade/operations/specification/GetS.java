package it.unibo.sd.jade.operations.specification;

import it.unibo.sd.jade.operations.AbstractTucsonSpecificationAction;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * <code>get_s</code> TuCSoN primitive.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class GetS extends AbstractTucsonSpecificationAction {
    /**
     * 
     * @param t
     *            the TuCSoN tuple centre id target of the coordination
     *            operation
     */
    public GetS(final TucsonTupleCentreId t) {
        super(t, null, null, null);
        // TODO Auto-generated constructor stub
    }

    @Override
    public ITucsonOperation executeAsynch(final EnhancedAsynchACC acc,
            final TucsonOperationCompletionListener listener)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return acc.getS(this.tcid, listener);
    }

    @Override
    public ITucsonOperation executeSynch(final EnhancedSynchACC acc,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return acc.getS(this.tcid, timeout);
    }

    /*
     * (non-Javadoc)
     * @see
     * it.unibo.sd.jade.operations.AbstractTucsonSpecificationAction#toString()
     */
    @Override
    public String toString() {
        return "get_s" + super.toString();
    }
}
