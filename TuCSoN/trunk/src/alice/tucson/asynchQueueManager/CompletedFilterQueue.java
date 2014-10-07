package alice.tucson.asynchQueueManager;

import it.unibo.sd.jade.operations.AbstractTucsonAction;

import java.util.Iterator;

public class CompletedFilterQueue extends FilterQueue{
	
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
	
	public void removeAllSuccessOperation(){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (elem.getOp().isResultSuccess()){
				this.remove(elem);
			}
		}
	}
	
	public void removeAllUnsuccessOperation(){
		Iterator<TucsonOperationForAsynchManager> it = this.iterator();
		while(it.hasNext()){
			TucsonOperationForAsynchManager elem = it.next();
			if (!elem.getOp().isResultSuccess()){
				this.remove(elem);
			}
		}
	}
	
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
