package alice.tucson.asynchSupport;

import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.asynchSupport.operations.AbstractTucsonAction;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * Represent an AbstractTucsonOperation with listener and TimeOut.
 *
 * @author Consalici Drudi
 *
 */
public class TucsonOperationForAsynchManager {

    /**
     * Context to communicate with tucson.
     */
    private final EnhancedAsynchACC acc;
    /**
     * Operation to do.
     */
    private final AbstractTucsonAction action;
    /**
     * identified a removed operation from filterQueue
     */
    private boolean isDeleted = false;
    /**
     * Action to do after the completion of operation.
     */
    private final TucsonOperationCompletionListener listener;
    /**
     * operation on completed.
     */
    private AbstractTupleCentreOperation op;

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
        System.currentTimeMillis();
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
        return this.action.executeAsynch(this.acc, this.listener);
    }

    /**
     * @return the action
     */
    public final AbstractTucsonAction getAction() {
        return this.action;
    }

    public AbstractTupleCentreOperation getOp() {
        return this.op;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void setDeleted(final boolean delete) {
        this.isDeleted = delete;
    }

    public void setOp(final AbstractTupleCentreOperation op) {
        this.op = op;
    }
}
