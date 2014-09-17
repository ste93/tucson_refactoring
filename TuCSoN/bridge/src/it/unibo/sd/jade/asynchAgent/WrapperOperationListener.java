package it.unibo.sd.jade.asynchAgent;

import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
/**
 * listener for non blocking operation.
 * @author Consalici Drudi
 *
 */
public class WrapperOperationListener
        implements TucsonOperationCompletionListener {
    /**
     * action to do after operation.
     */
    private TucsonOperationCompletionListener clientListener;
    /**
     * link to the Asynch Queue Manager.
     */
    private AsynchQueueManager aqm;
    /**
     * @param clientListener
     *          action to do after the operation
     * @param aqm
     *          link to the Asynch Queue Manager
     */
    public WrapperOperationListener(
            final TucsonOperationCompletionListener clientListener,
            final AsynchQueueManager aqm){
        this.aqm = aqm;
        this.clientListener = clientListener;
    }
    @Override
    public final void operationCompleted(
             final AbstractTupleCentreOperation op) {
        try {
            aqm.getOperationCount().acquire();
            if (aqm.getStopAbrout() && aqm.getOperationCount().availablePermits()==0) {
                aqm.getWaitOperationCount().release();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            clientListener.operationCompleted(op);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void operationCompleted(final ITucsonOperation op) {

    }
}