package alice.tucson.asynchQueueManager;

import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
/**
 * Listener used for Asynchronous execution of blocking operation (In, Out, Rd)
 * @author Consalici Drudi
 *
 */
public class AsynchWaitingOperationListener
    implements TucsonOperationCompletionListener {
	/**
	 * element of queue.
	 */
    private TucsonOperationForAsynchManager tofam;
    /**
     * action to do on success execution of operation.
     */
    private TucsonOperationCompletionListener clientListener;
    /**
     * link to Asynch Queue Manager.
     */
    private AsynchQueueManager aqm;
    /**
     * Create listener.
     * @param clientListener
     *              action to do on success execution of operation
     * @param aqm
     *              link to Asynch Queue Manager
     */
    public AsynchWaitingOperationListener(
    		final TucsonOperationCompletionListener clientListener,
    		final AsynchQueueManager aqm) {
		this.aqm = aqm;
		this.clientListener = clientListener;
	}
    /**
     * Set the element of the queue.
     * @param tofam
     * 			Element of the queue
     */
	public final void setTucsonOperationForAsynchManager(
			final TucsonOperationForAsynchManager tofam) {
		this.tofam = tofam;
	}
	@Override
	public final void operationCompleted(
			final AbstractTupleCentreOperation op) {
		try {
			aqm.getOperationCount().acquire();
			log(" AvaiblePermits: "
			+ aqm.getOperationCount().availablePermits()
			+ " StopAbrout:" + aqm.getStopAbrout());
			if (aqm.getStopAbrout() && aqm.getOperationCount().availablePermits() == 0) {
				aqm.getWaitOperationCount().release();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (op.isResultSuccess()) {
			clientListener.operationCompleted(op);
		} else {
			if (!aqm.getStopAbrout()) {
				try {
					aqm.getWaitingQueue().put(tofam);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Print message on Console.
	 * @param string
	 * 			message to print
	 */
	private void log(final String string) {
		// TODO Auto-generated method stub
		System.out.println(string);
	}
	@Override
	public void operationCompleted(final ITucsonOperation op) {

	}
}