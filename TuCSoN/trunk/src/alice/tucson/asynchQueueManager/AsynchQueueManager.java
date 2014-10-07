package alice.tucson.asynchQueueManager;

import it.unibo.sd.jade.operations.AbstractTucsonAction;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Inp;
import it.unibo.sd.jade.operations.ordinary.No;
import it.unibo.sd.jade.operations.ordinary.Nop;
import it.unibo.sd.jade.operations.ordinary.Rd;
import it.unibo.sd.jade.operations.ordinary.Rdp;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * Manager for Asynchronous operation.
 *
 * @author Consalici Drudi
 *
 */
public class AsynchQueueManager extends AbstractTucsonAgent {
    /**
     * context for execute operation.
     */
    private EnhancedAsynchACC acc;
    /**
     * queue containing operation to execute.
     */
    private FilterQueue waitingQueue;
    /**
     * queue containing operation to execute.
     */
    //private FilterQueue pendingQueue;
    /**
     * queue containing operation to execute.
     */
    private CompletedFilterQueue completedQueue;
    /**
     * to stop the aqm immediately.
     */
    private boolean stopAbrout = false;
    /**
     * to stop the aqm gracefully.
     */
    private boolean stopGraceful = false;
    /**
     * Semaphore containing the number of pending operation.
     */
    private Semaphore operationCount;
    /**
     * Semaphore used for synchronization after shutdown.
     */
    private Semaphore waitOperationCount;
    /**
     * polling time for queue.
     */
    private static final int POLLINGQUEUETIME = 1000;
    /**
     * @return the operation queue.
     */
    public final FilterQueue
    getWaitingQueue() {
        return waitingQueue;
    }
    /**
     * @return the operation queue.
     */
    /*public final FilterQueue
    getPendingQueue() {
        return pendingQueue;
    }*/
    /**
     * @return the operation queue.
     */
    public final CompletedFilterQueue
    getCompletedQueue() {
        return completedQueue;
    }
    /**
     * @return flag for shutdownNow.
     */
    public final boolean getStopAbrout() {
        return stopAbrout;
    }
    /**
     * @return flag for shutdown.
     */
    public final boolean getStopGraceful() {
        return stopGraceful;
    }
    /**
     * @return semaphore for number of pending operation.
     */
    public final Semaphore getOperationCount() {
        return operationCount;
    }
    /**
     * @return semaphore for synchronization after shutdown.
     */
    public final Semaphore getWaitOperationCount() {
        return waitOperationCount;
    }
    /**
     * @param id
     *              the aqm agent's id
     * @throws TucsonInvalidAgentIdException
     *              if the given String does not represent a valid TuCSoN agent
     *              identifier
     */
    public AsynchQueueManager(final String id)
            throws TucsonInvalidAgentIdException {
        super(id);
        waitingQueue = new FilterQueue();
        //pendingQueue = new FilterQueue();
        completedQueue = new CompletedFilterQueue();
        operationCount = new Semaphore(0);
        waitOperationCount = new Semaphore(0);
        this.go();
	}
	/*public static AsynchQueueManager getAQM(String idC) throws TucsonInvalidAgentIdException{
		idCount++;
		id=idC+""+idCount;
		return new AsynchQueueManager(id);
	}*/
	@Override
	public void operationCompleted(final AbstractTupleCentreOperation op) {
	}
	@Override
	public void operationCompleted(final ITucsonOperation op) {
	}
	/**
	 * @param msg
	 *           message to print.
	 */
	public final void log(final String msg) {
		System.out.println(msg);
	}
	/**
	 * Add a operation in the queue of pending operation,
	 * only if the shutdown operation wasn't called before.
	 * @param action
	 *               The AbstractTucsonAction to execute.
	 * @param clientListener
	 *               Action to do after the execution of operation.
	 * @return
	 *               True if the action is added.
	 */
     public final boolean add(final AbstractTucsonAction action,
    		 final TucsonOperationCompletionListener clientListener) {
		if (stopGraceful || stopAbrout) {
			return false;
		}
		//acc = this.getContext();
		TucsonOperationForAsynchManager elem = null;
		
		WrapperOperationListener wol =
				new WrapperOperationListener(
						clientListener, this);
		elem = new TucsonOperationForAsynchManager(
				acc, action, wol);
		wol.setTucsonOperationForAsynchManager(elem);
		log("operazione aggiunta");
		try {
			return waitingQueue.add(elem);
		} catch (IllegalStateException ex) {
			return false;
		}
	}
    /**
     * Add a operation in the queue of pending operation,
	 * only if the shutdown operation wasn't called before.
	 * @param action
	 *               The AbstractTucsonAction to execute.
	 * @param clientListener
	 *               Action to do after the execution of operation.
	 * @param timeOut
	 *               Max time to wait the operation's
	 *               positive result
	 * @return
	 *               True if the action is added.
     */
	public final boolean add(final AbstractTucsonAction action,
			final TucsonOperationCompletionListener clientListener,
			final long timeOut) {
		if (stopGraceful || stopAbrout) {
			return false;
		}
		TucsonOperationForAsynchManager elem = null;
		WrapperOperationListener wol =
				new AsynchTimedWaitingOperationListener(
						clientListener, this, timeOut);
		elem = new TucsonOperationForAsynchManager(
				acc, action, wol, timeOut);
		wol.setTucsonOperationForAsynchManager(elem);
		log("[" + this.getTucsonAgentId() + "] "
				+ "Operazione aggiunta con timeout");
		try {
			return waitingQueue.add(elem);
		} catch (IllegalStateException ex) {
			return false;
		}
	}
	/**
	 * Shut down immediately the aqm.
	 */
	public final void shutdownNow() {
		stopAbrout = true;
	}
	/**
	 * complete the pending operation and shut down the aqm.
	 */
	public final void shutdown() {
		stopGraceful = true;
	}
	@Override
	protected final void main() {
		acc = this.getContext();
		TucsonOperationForAsynchManager elem = null;
		log("[aqm]parte il main");
		while (!stopAbrout && !stopGraceful) {
			try {
					elem = waitingQueue.poll(
						POLLINGQUEUETIME,
						TimeUnit.MILLISECONDS);
					if (elem != null) {
						if (elem.getAcc() == null) {
							log("["
						+ this.getTucsonAgentId()
						+ "]ci passo e il mio acc="
						+ acc);
							elem.setAcc(acc);
						}
						operationCount.release();
					    elem.execute();
					    //pendingQueue.add(elem);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (
					TucsonOperationNotPossibleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnreachableNodeException e) {
					e.printStackTrace();
				}
		}
		if (stopAbrout) {
			//while(operationCount.availablePermits()!=0){}
			try {
				log("[aqm] queueLength:" + waitingQueue.size());
				if (operationCount.availablePermits() != 0) {
					log("[aqm]:stopAbrout="
					+ stopAbrout
					+ " AvaiblePermits:"
					+ operationCount.availablePermits());
					//
					waitOperationCount.acquire();
					//
				} else {
					log("[aqm]: avaible permits = 0");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			while (operationCount.availablePermits() != 0
					|| !waitingQueue.isEmpty()) {
				      try {
							elem = waitingQueue.poll(
									POLLINGQUEUETIME,
									TimeUnit.MILLISECONDS);
							if (elem != null) {
								if (elem.getAcc() == null) {
									elem.setAcc(acc);
								}
								operationCount.release();
							    elem.execute();
							    //pendingQueue.add(elem);
							}
						} catch (
						InterruptedException e) {
							e.printStackTrace();
						} catch (
						TucsonOperationNotPossibleException e) {
							e.printStackTrace();
						} catch (
						UnreachableNodeException e) {
							e.printStackTrace();
						}
			}
		}
		log("[" + this.getTucsonAgentId() + "] Dead!");
	}
}
