package alice.tucson.asynchSupport;

import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * listener for non blocking operation.
 * 
 * @author Consalici Drudi
 *
 */
public class WrapperOperationListener implements
        TucsonOperationCompletionListener {

    /**
     * link to the Asynch Queue Manager.
     */
    protected AsynchQueueManager aqm;
    /**
     * action to do after operation.
     */
    protected TucsonOperationCompletionListener clientListener;
    /**
     * tofam.
     */
    protected TucsonOperationForAsynchManager tofam;

    /**
     * @param clientListener
     *            action to do after the operation
     * @param aqm
     *            link to the Asynch Queue Manager
     */
    public WrapperOperationListener(
            final TucsonOperationCompletionListener clientListener,
            final AsynchQueueManager aqm) {
        this.aqm = aqm;
        this.clientListener = clientListener;
    }

    public AsynchQueueManager getAsynchQueueManager() {
        return this.aqm;
    }

    public TucsonOperationCompletionListener getTucsonOperationCompletionListener() {
        return this.clientListener;
    }

    public TucsonOperationForAsynchManager getTucsonOperationForAsynchManager() {
        return this.tofam;
    }

    /**
     * manage the result of operations executed from asynchQueueManager
     */
    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        try {
            //
            // semaphore for stop managing
            //
            this.aqm.getOperationCount().acquire();
            if (this.aqm.getStopAbrout()
                    && this.aqm.getOperationCount().availablePermits() == 0) {
                this.aqm.getWaitOperationCount().release();
            }
            if (!this.aqm.getStopAbrout()) {
                this.tofam.setOp(op);
                this.aqm.getCompletedQueue().add(this.tofam);
                if (this.clientListener != null) {
                    // Execution of the user listener
                    this.clientListener.operationCompleted(op);
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * Not used atm
         */
    }

    public final void setTucsonOperationForAsynchManager(
            final TucsonOperationForAsynchManager tofam) {
        this.tofam = tofam;
    }
}
