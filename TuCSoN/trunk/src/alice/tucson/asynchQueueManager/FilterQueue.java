package alice.tucson.asynchQueueManager;

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
     * 
     * @author Consalici Drudi
     *
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
	
	public final void log(final String msg) {
		System.out.println(msg);
	}
	
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
