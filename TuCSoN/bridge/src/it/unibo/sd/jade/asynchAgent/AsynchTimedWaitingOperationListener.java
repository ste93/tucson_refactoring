package it.unibo.sd.jade.asynchAgent;


import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 *
 *
 *
 * @author Consalici Drudi
 *
 */
public class AsynchTimedWaitingOperationListener
               implements TucsonOperationCompletionListener{
    /**
     * Link to the client operation.
     */
    private TucsonOperationForAsynchManager tofam;
    /**
     * Link to the clientListener.
     */
    private TucsonOperationCompletionListener clientListener;
    /**
     * Max time to get the positive response.
     */
    private long timeOut;
    /**
     * Link to AsynchQueueManager.
     */
    private AsynchQueueManager aqm;
    /**
     * AsynchTimedWaitingOperationListener constructor.
     * @param clientListener
     *             clientListener with the code to execute
     *             when a response arrive.
     * @param aqm
     *          The AsynchQueueManager who create this object.
     * @param timeOut
     *          Max time to get a positive response from the operation.
     */
    public AsynchTimedWaitingOperationListener(
            final TucsonOperationCompletionListener clientListener,
            final AsynchQueueManager aqm, long timeOut){
            this.clientListener = clientListener;
            this.aqm  = aqm;
            this.timeOut = timeOut;
    }
    /**
     * Set the TucsonOperationForAsynchManager who
     * represent the operation to execute.
     * @param tofam
     *         The object with the operation to execute
     */
    public final void setTucsonOperationForAsynchManager(
    		TucsonOperationForAsynchManager tofam){
        this.tofam = tofam;
    }
    /**
     * When a operation is completed we control if it succeed and
     * if not we control if the timeout isn't expired
     * (if it's expired we don't rerun the operation).
     * 
     * @param op
     *         The operation returned at completion
     */
	@Override
	public final void operationCompleted(
			final AbstractTupleCentreOperation op) {
        try {
            aqm.getOperationCount().acquire();
            if (aqm.getStopAbrout() && aqm.getOperationCount().availablePermits() == 0){
                aqm.getWaitOperationCount().release();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
			if (op.isResultSuccess()) {
				clientListener.operationCompleted(op);
			} else {
				if (!aqm.getStopAbrout()){
					if (System.currentTimeMillis()- tofam.getTimeExecution()< timeOut){
						try {
							aqm.getQueue().put(tofam);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						clientListener.operationCompleted(op);
					}
				}
			}
		}
		@Override
		public void operationCompleted(final ITucsonOperation op) {

		}
	}