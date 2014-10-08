package it.unibo.sd.jade.operations;

import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Root of the hierarchy of TuCSoN actions (aka coordination operations).
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public abstract class AbstractTucsonAction {
    /**
     * the id of tuple centre target of the operation
     */
    protected TucsonTupleCentreId tcid;
    /**
     * the name of the tuple centre target of the operation
     */
    protected String tupleCentreName;

    /**
     * 
     * @param tc
     *            the TuCSoN tuple centre id target of the coordination
     *            operation
     */
    public AbstractTucsonAction(final TucsonTupleCentreId tc) {
        this.tcid = tc;
    }

    /**
     * 
     * @param acc
     *            the TuCSoN ACC ultimately in charge of action execution
     * @param listener
     *            the TuCSoN listener responsible for handling completion
     *            notifications
     * @return the TuCSoN operation requested
     * @throws TucsonOperationNotPossibleException
     *             if the coordination operation request cannot be carried out
     * @throws UnreachableNodeException
     *             if the target TuCSoN node is not available on the network
     */
    public abstract ITucsonOperation executeAsynch(EnhancedAsynchACC acc,
            TucsonOperationCompletionListener listener)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException;

    /**
     * 
     * @param acc
     *            the TuCSoN ACC ultimately in charge of action execution
     * @param timeout
     *            the maximum timeout the caller is willing to wait
     * @return the TuCSoN operation requested
     * @throws TucsonOperationNotPossibleException
     *             if the coordination operation request cannot be carried out
     * @throws UnreachableNodeException
     *             if the target TuCSoN node is not available on the network
     * @throws OperationTimeOutException
     *             if the chosen timeout elapses prior to completion
     *             notification
     */
    public abstract ITucsonOperation executeSynch(EnhancedSynchACC acc,
            Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;
}
