package alice.tucson.asynchQueueManager;


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
               extends WrapperOperationListener{
    /**
     * Max time to get the positive response.
     */
    private long timeOut;
    
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
    	super(clientListener, aqm);
        this.timeOut = timeOut;
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
							aqm.getWaitingQueue().put(tofam);
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