package alice.tucson.asynchQueueManager;

import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import it.unibo.sd.jade.operations.AbstractTucsonAction;

/**
 * Represent an AbstractTucsonOperation with listener and TimeOut.
 * 
 * @author Consalici Drudi
 *
 */
public class TucsonOperationForAsynchManager {
    /**
     * Operation to do.
     */
    private AbstractTucsonAction action;
    /**
     * Action to do after the completion of operation.
     */
    private TucsonOperationCompletionListener listener;
    /**
     * time adding operation.
     */
    private long timeExecution;
    /**
     * Context to communicate with tucson.
     */
    private EnhancedAsynchACC acc;
    /**
     * operation on completed.
     */
    private AbstractTupleCentreOperation op;
    /**
     * identified a removed operation from filterQueue
     */
    private boolean isDeleted = false;

    /**
     * Create TucsonOperationForAsynchManager object.
     * 
     * @param acc
     *            Context of operation
     * @param action
     *            operation to do
     * @param listener
     *            action to do after the completion of operation
     */
    public TucsonOperationForAsynchManager(final EnhancedAsynchACC acc,
            final AbstractTucsonAction action,
            final TucsonOperationCompletionListener listener) {
        this.action = action;
        this.listener = listener;
        this.acc = acc;
        timeExecution = System.currentTimeMillis();
    }

    /**
     * Create TucsonOperationForAsynchManager object.
     * 
     * @param acc
     *            Context of operation
     * @param action
     *            operation to do
     * @param listener
     *            action to do after the completion of operation
     * @param timeout
     *            set the max time of lifecycle of operation
     */
    public TucsonOperationForAsynchManager(final EnhancedAsynchACC acc,
            final AbstractTucsonAction action,
            final TucsonOperationCompletionListener listener, final long timeout) {
        this.action = action;
        this.listener = listener;
        this.acc = acc;
    }

    /**
     * @return the action
     */
    public final AbstractTucsonAction getAction() {
        return action;
    }

    /**
     * Execute operation.
     * 
     * @return ITucsonOperation is returned
     * @throws TucsonOperationNotPossibleException
     *             if i can't do the operation
     * @throws UnreachableNodeException
     *             if i can't find Node
     */
    public final ITucsonOperation execute()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return action.executeAsynch(acc, listener);
    }

    public void setOp(AbstractTupleCentreOperation op) {
        this.op = op;
    }

    public AbstractTupleCentreOperation getOp() {
        return op;
    }

    public void setDeleted(boolean delete) {
        isDeleted = delete;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
