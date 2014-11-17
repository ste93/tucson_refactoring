package alice.tucson.asynchQueueManager;

import java.util.Iterator;
/**
 * A specialization of FilterQueue who manage the completed operations  
 * @author Consalici Drudi
 *
 */
public class CompletedFilterQueue extends FilterQueue{
	/**
	 * return a list of all success operations
	 * @return
	 */
	public CompletedFilterQueue getAllSuccessOp(){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		CompletedFilterQueue typedOp=new CompletedFilterQueue();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getOp().isResultSuccess()){
				typedOp.add(elem);
			}
		}
		return typedOp;
	}
	/**
	 * return a list of all unsuccess operations
	 * @return
	 */
	public CompletedFilterQueue getAllUnsuccessOp(){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		CompletedFilterQueue typedOp=new CompletedFilterQueue();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (!elem.getOp().isResultSuccess()){
				typedOp.add(elem);
			}
		}
		return typedOp;
	}
	/**
	 * remove from the completedQueue the success operations
	 */
	public void removeAllSuccessOperation(){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getOp().isResultSuccess()){
				this.remove(elem);
			}
		}
	}
	/**
         * remove from the completedQueue the unsuccess operations
         */
	public void removeAllUnsuccessOperation(){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (!elem.getOp().isResultSuccess()){
				this.remove(elem);
			}
		}
	}
	/**
         * return a list of all operations who match the arguments
         */
	public CompletedFilterQueue getAllTypedOperation(Class optype){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		CompletedFilterQueue typedOp=new CompletedFilterQueue();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getAction().getClass().equals(optype)){
				typedOp.add(elem);
			}
		}
		return typedOp;
	}
	
}
