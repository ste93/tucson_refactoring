package alice.tucson.asynchSupport;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.asynchSupport.operations.AbstractTucsonAction;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * Manager for Asynchronous operation.
 *
 * @author Consalici Drudi
 *
 */
public class AsynchQueueManager extends AbstractTucsonAgent {

    /**
     * polling time for queue.
     */
    private static final int POLLINGQUEUETIME = 1000;

    /**
     * @param msg
     *            message to print.
     */
    private final static void log(final String msg) {
        System.out.println(msg);
    }

    /**
     * context for execute operation.
     */
    private EnhancedAsynchACC acc;
    /**
     * queue containing operation to execute.
     */
    private final CompletedFilterQueue completedQueue;
    /**
     * Semaphore containing the number of pending operation.
     */
    private final Semaphore operationCount;
    /**
     * to stop the aqm immediately.
     */
    private boolean stopAbrout = false;
    /**
     * to stop the aqm gracefully.
     */
    private boolean stopGraceful = false;
    /**
     * queue containing operation to execute.
     */
    private final FilterQueue waitingQueue;

    /**
     * Semaphore used for synchronization after shutdown.
     */
    private final Semaphore waitOperationCount;

    /**
     * @param id
     *            the aqm agent's id
     * @throws TucsonInvalidAgentIdException
     *             if the given String does not represent a valid TuCSoN agent
     *             identifier
     */
    public AsynchQueueManager(final String id)
            throws TucsonInvalidAgentIdException {
        super(id);
        this.waitingQueue = new FilterQueue();
        this.completedQueue = new CompletedFilterQueue();
        this.operationCount = new Semaphore(0);
        this.waitOperationCount = new Semaphore(0);
        this.go();
    }

    /**
     * Add a operation in the queue of pending operation, only if the shutdown
     * operation wasn't called before.
     *
     * @param action
     *            The AbstractTucsonAction to execute.
     * @param clientListener
     *            Action to do after the execution of operation.
     * @return True if the action is added.
     */
    public final boolean add(final AbstractTucsonAction action,
            final TucsonOperationCompletionListener clientListener) {
        if (this.stopGraceful || this.stopAbrout) {
            return false;
        }
        TucsonOperationForAsynchManager elem = null;
        final WrapperOperationListener wol = new WrapperOperationListener(
                clientListener, this);
        elem = new TucsonOperationForAsynchManager(this.acc, action, wol);
        wol.setTucsonOperationForAsynchManager(elem);
        try {
            return this.waitingQueue.add(elem);
        } catch (final IllegalStateException ex) {
            return false;
        }
    }

    /**
     * @return the operation queue.
     */
    public final CompletedFilterQueue getCompletedQueue() {
        return this.completedQueue;
    }

    /**
     * @return semaphore for number of pending operation.
     */
    public final Semaphore getOperationCount() {
        return this.operationCount;
    }

    /**
     * @return flag for shutdownNow.
     */
    public final boolean getStopAbrout() {
        return this.stopAbrout;
    }

    /**
     * @return flag for shutdown.
     */
    public final boolean getStopGraceful() {
        return this.stopGraceful;
    }

    /**
     * @return the operation queue.
     */
    public final FilterQueue getWaitingQueue() {
        return this.waitingQueue;
    }

    /**
     * @return semaphore for synchronization after shutdown.
     */
    public final Semaphore getWaitOperationCount() {
        return this.waitOperationCount;
    }

    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        /*
         * Not used atm
         */
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * Not used atm
         */
    }

    /**
     * complete the pending operation and shut down the aqm.
     */
    public final void shutdown() {
        this.stopGraceful = true;
    }

    /**
     * Shut down immediately the aqm.
     */
    public final void shutdownNow() {
        this.stopAbrout = true;
    }

    @Override
    protected final void main() {
        this.acc = this.getContext();
        TucsonOperationForAsynchManager elem = null;
        AsynchQueueManager.log("[" + this.getTucsonAgentId() + "]start work");
        // this is the loop that execute the operation until user stop command
        while (!this.stopAbrout && !this.stopGraceful) {
            try {
                elem = this.waitingQueue.poll(
                        AsynchQueueManager.POLLINGQUEUETIME,
                        TimeUnit.MILLISECONDS);
                if (elem != null) {
                    this.operationCount.release();
                    elem.execute();
                }
            } catch (final InterruptedException e) {
                e.printStackTrace();
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final UnreachableNodeException e) {
                e.printStackTrace();
            }
        }
        // Case stopAbrout: do not execute waitingQueue ops
        if (this.stopAbrout) {
            try {
                if (this.operationCount.availablePermits() != 0) {
                    AsynchQueueManager.log("[aqm]:stopAbrout="
                            + this.stopAbrout + " AvaiblePermits:"
                            + this.operationCount.availablePermits());
                    this.waitOperationCount.acquire();
                } else {
                    /*
                     * log something
                     */
                }
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // case StopGracefull: execute all added operations
            while (this.operationCount.availablePermits() != 0
                    || !this.waitingQueue.isEmpty()) {
                try {
                    elem = this.waitingQueue.poll(
                            AsynchQueueManager.POLLINGQUEUETIME,
                            TimeUnit.MILLISECONDS);
                    if (elem != null) {
                        this.operationCount.release();
                        elem.execute();
                    }
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                } catch (final TucsonOperationNotPossibleException e) {
                    e.printStackTrace();
                } catch (final UnreachableNodeException e) {
                    e.printStackTrace();
                }
            }
        }
        AsynchQueueManager.log("[" + this.getTucsonAgentId() + "] End work");
    }
}
