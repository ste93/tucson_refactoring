package alice.tucson.asynchQueueManager;

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
     * tofam.
     */
    private TucsonOperationForAsynchManager tofam;
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
    public void operationCompleted(
             final AbstractTupleCentreOperation op) {
        try {
            aqm.getOperationCount().acquire();
            if (aqm.getStopAbrout() && aqm.getOperationCount().availablePermits()==0) {
                aqm.getWaitOperationCount().release();
            }
        	if(!aqm.getStopAbrout()){
        		//aqm.getPendingQueue().remove(tofam);
        		tofam.setOp(op);
        		aqm.getCompletedQueue().add(tofam);
        		clientListener.operationCompleted(op);
        	}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Override
    public void operationCompleted(final ITucsonOperation op) {

    }
    
    public final void setTucsonOperationForAsynchManager(
			final TucsonOperationForAsynchManager tofam) {
		this.tofam = tofam;
	}
    public TucsonOperationForAsynchManager getTucsonOperationForAsynchManager(){
    	return tofam;
    }
    public AsynchQueueManager getAsynchQueueManager(){
    	return aqm;
    }
    public TucsonOperationCompletionListener getTucsonOperationCompletionListener(){
    	return clientListener;
    }
}