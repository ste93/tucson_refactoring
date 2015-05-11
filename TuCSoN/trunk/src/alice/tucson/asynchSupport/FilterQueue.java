package alice.tucson.asynchSupport;

import it.unibo.sd.jade.operations.AbstractTucsonAction;
import it.unibo.sd.jade.operations.ordinary.In;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queue for all operation added in AsynchQueueManager.
 * @author Consalici Drudi
 *
 */
public class FilterQueue extends
     LinkedBlockingQueue<TucsonOperationForAsynchManager> {
        /**
         * return a list of all operations who match the arguments
         */
	public FilterQueue getAllTypedOperation(Class optype){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		FilterQueue typedOp=new FilterQueue();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getAction().getClass().equals(optype)){
				typedOp.add(elem);
			}
		}
		return typedOp;
	}
	/**
	 * remove the specific action to list
	 * @param action
	 * @return
	 */
	public boolean removeAbstractTucsonAction(AbstractTucsonAction action){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getAction().equals(action)){
				elem.setDeleted(true);
				return this.remove(elem);				
			}
		}
		return false;
	}
	/**
	 * remove the operation identified by ID to list
	 * @param id
	 * @return
	 */
	public boolean removeOperationById(long id){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getOp().getId()==id){
				elem.setDeleted(true);
				return this.remove(elem);
			}
		}
		return false;
	}
	/**
	 * remove all the operations match with argument
	 * @param optype
	 */
	public void removeAllTypedOperation(Class optype){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getAction().getClass().equals(optype)){
				elem.setDeleted(true);
				this.remove(elem);
			}
		}
	}
	
}
