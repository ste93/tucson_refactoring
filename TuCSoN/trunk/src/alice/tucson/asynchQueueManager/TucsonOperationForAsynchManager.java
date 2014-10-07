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
     * max time for the lifecycle of operation.
     */
    private long timeout;
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
     * 
     */
    private boolean isDeleted=false;
    /**
     * Create TucsonOperationForAsynchManager object.
     * @param acc
     *           Context of operation
     * @param action
     *           operation to do
     * @param listener
     *           action to do after the completion of operation
     */
    public TucsonOperationForAsynchManager(
    		final EnhancedAsynchACC acc,
    		final AbstractTucsonAction action,
    		final TucsonOperationCompletionListener listener) {
		this.action = action;
		this.listener = listener;
		this.acc = acc;
		timeExecution = System.currentTimeMillis();
	}
    /**
     * Create TucsonOperationForAsynchManager object.
     * @param acc
     *           Context of operation
     * @param action
     *           operation to do
     * @param listener
     *           action to do after the completion of operation
     * @param timeout
     *           set the max time of lifecycle of operation
     */
	public TucsonOperationForAsynchManager(
			final EnhancedAsynchACC acc,
			final AbstractTucsonAction action,
			final TucsonOperationCompletionListener listener,
			final long timeout) {
		this.action = action;
		this.listener = listener;
		this.timeout = timeout;
		this.acc = acc;
	}
	/**
	 * @return the timeOut of operation
	 */
	public final long getTimeOut() {
		return timeout;
	}
	/**
	 * @return the action
	 */
	public final AbstractTucsonAction  getAction() {
		return action;
	}
	/**
	 * Execute operation.
	 * @return ITucsonOperation is returned
	 * @throws TucsonOperationNotPossibleException
	 * 			if i can't do the operation
	 * @throws UnreachableNodeException
	 * 			if i can't find Node
	 */
	public final ITucsonOperation execute()
			throws TucsonOperationNotPossibleException,
				UnreachableNodeException {
		return action.executeAsynch(acc, listener);
	}
	/**
	 * @return the listener of action
	 */
	public final TucsonOperationCompletionListener getListener() {
		return listener;
	}
	/**
	 * @return the object creation time
	 */
	public final long getTimeExecution() {
		return timeExecution;
	}
	/**
	 * @return the object's context
	 */
	public final EnhancedAsynchACC getAcc(){
		return acc;
	}
	/**
	 * @param acc
	 *        the context of object
	 */
	public final void setAcc(final EnhancedAsynchACC acc){
		this.acc = acc;
	}
	public final void setOp(AbstractTupleCentreOperation op){
		this.op = op;
	}
	
	public final AbstractTupleCentreOperation getOp(){
		return op;
	}
	public void setDeleted(boolean delete){
		isDeleted=delete;
	}
	
	public boolean isDeleted(){
		return isDeleted;
	}
}
