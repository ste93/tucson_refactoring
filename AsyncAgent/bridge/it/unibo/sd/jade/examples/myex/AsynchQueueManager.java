package it.unibo.sd.jade.examples.myex;

import it.unibo.sd.jade.operations.AbstractTucsonAction;
import it.unibo.sd.jade.operations.bulk.InAll;
import it.unibo.sd.jade.operations.bulk.OutAll;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Inp;
import it.unibo.sd.jade.operations.ordinary.No;
import it.unibo.sd.jade.operations.ordinary.Nop;
import it.unibo.sd.jade.operations.ordinary.Rd;
import it.unibo.sd.jade.operations.ordinary.Rdp;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.exceptions.DialogExceptionTcp;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class AsynchQueueManager extends AbstractTucsonAgent {
	private EnhancedAsynchACC acc;
	private LinkedBlockingQueue<TucsonOperationForAsynchManager> queue;
	private static int idCount=0;
	private  String id;
	private boolean stopAbrout=false;
	private boolean stopGraceful=false;
	private Semaphore operationCount;
	private Semaphore waitOperationCount;
	//static int pos=0;
	public LinkedBlockingQueue<TucsonOperationForAsynchManager> getQueue(){
		return queue;
	}
	
	public boolean getStopAbrout(){
		return stopAbrout;
	}
	
	public boolean getStopGraceful(){
		return stopGraceful;
	}
	
	public Semaphore getOperationCount(){
		return operationCount;
	}
	
	public Semaphore getWaitOperationCount(){
		return waitOperationCount;
	}
	
	public AsynchQueueManager(String id) throws TucsonInvalidAgentIdException {
		super(id);
		queue=new LinkedBlockingQueue<TucsonOperationForAsynchManager>();
		log("Parte "+id);
		operationCount=new Semaphore(0);
		waitOperationCount=new Semaphore(0);
		this.go();
	}
	/*public static AsynchQueueManager getAQM(String idC) throws TucsonInvalidAgentIdException{
		idCount++;
		id=idC+""+idCount;
		return new AsynchQueueManager(id);
	}*/
	@Override
	public void operationCompleted(AbstractTupleCentreOperation op) {
		
	}
	
	@Override
	public void operationCompleted(ITucsonOperation op) {

		
	}
	public void log(String msg){
		System.out.println(msg);
	}
	public boolean add(AbstractTucsonAction action,TucsonOperationCompletionListener clientListener){
		if(stopGraceful||stopAbrout){
			return false;
		}
		//acc = this.getContext();
		TucsonOperationForAsynchManager elem=null;
		if(action instanceof In){
			Inp inp=new Inp(action.getTupleCentreId(),((In) action).getTuple());
			//AsynchWaitingOperationListener aol=new AsynchWaitingOperationListener(queue,clientListener,operationCount,stopAbrout, waitOperationCount);
			AsynchWaitingOperationListener aol=new AsynchWaitingOperationListener(clientListener,this);
			elem=new TucsonOperationForAsynchManager(acc,inp,aol);
			aol.setTucsonOperationForAsynchManager(elem);
		}else if(action instanceof Rd){
			Rdp rdp=new Rdp(action.getTupleCentreId(),((Rd) action).getTuple());
			//AsynchWaitingOperationListener aol=new AsynchWaitingOperationListener(queue,clientListener,operationCount,stopAbrout, waitOperationCount);
			AsynchWaitingOperationListener aol=new AsynchWaitingOperationListener(clientListener,this);
			elem=new TucsonOperationForAsynchManager(acc,rdp,aol);
			aol.setTucsonOperationForAsynchManager(elem);
		}else if(action instanceof No){
			Nop nop=new Nop(action.getTupleCentreId(),((No) action).getTuple());
			//AsynchWaitingOperationListener aol=new AsynchWaitingOperationListener(queue,clientListener,operationCount,stopAbrout, waitOperationCount);
			AsynchWaitingOperationListener aol=new AsynchWaitingOperationListener(clientListener,this);
			elem=new TucsonOperationForAsynchManager(acc,nop,aol);
			aol.setTucsonOperationForAsynchManager(elem);
		}else{
			//WrapperOperationListener wol=new WrapperOperationListener(clientListener,operationCount, stopAbrout, waitOperationCount);
			WrapperOperationListener wol=new WrapperOperationListener(clientListener,this);
			elem=new TucsonOperationForAsynchManager(acc, action,wol);
		}
		log("operazione aggiunta");
		try{
			return queue.add(elem);
		}catch(IllegalStateException ex){
			return false;
		}
	}
	public boolean add(AbstractTucsonAction action,TucsonOperationCompletionListener clientListener,long timeOut){
		if(stopGraceful||stopAbrout){
			return false;
		}
		TucsonOperationForAsynchManager elem=null;
		if(action instanceof In){
			Inp inp=new Inp(action.getTupleCentreId(),((In) action).getTuple());
			AsynchTimedWaitingOperationListener aol=new AsynchTimedWaitingOperationListener(clientListener,this,timeOut);
			elem=new TucsonOperationForAsynchManager(acc,inp,aol,timeOut);
			aol.setTucsonOperationForAsynchManager(elem);
		}else if(action instanceof Rd){
			Rdp rdp=new Rdp(action.getTupleCentreId(),((Rd) action).getTuple());
			AsynchTimedWaitingOperationListener aol=new AsynchTimedWaitingOperationListener(clientListener,this,timeOut);
			elem=new TucsonOperationForAsynchManager(acc,rdp,aol,timeOut);
			aol.setTucsonOperationForAsynchManager(elem);
		}else if(action instanceof No){
			Nop nop=new Nop(action.getTupleCentreId(),((No) action).getTuple());
			AsynchTimedWaitingOperationListener aol=new AsynchTimedWaitingOperationListener(clientListener,this,timeOut);
			elem=new TucsonOperationForAsynchManager(acc,nop,aol,timeOut);
			aol.setTucsonOperationForAsynchManager(elem);
		}else{
			//WrapperOperationListener wol=new WrapperOperationListener(clientListener,operationCount, stopAbrout, waitOperationCount);
			WrapperOperationListener wol=new WrapperOperationListener(clientListener,this);
			elem=new TucsonOperationForAsynchManager(acc, action, wol,timeOut);
		}
		log("["+this.getTucsonAgentId()+"] Operazione aggiunta con timeout");
		try{
			return queue.add(elem);
		}catch(IllegalStateException ex){
			return false;
		}
		
	}
	public void shutdownNow(){
		stopAbrout=true;
	}
	public void shutdown(){
		
		stopGraceful=true;
	}
	@Override
	protected void main() {
		acc = this.getContext();
		TucsonOperationForAsynchManager elem = null;
		log("[aqm]parte il main");
		while(!stopAbrout&&!stopGraceful){
			/*if(stopGraceful&&queue.isEmpty()){
				log("["+this.getTucsonAgentId()+"]stopgraceFOUL  e queue="+queue.isEmpty());
			}*/
			try {
					elem=queue.poll(1000,TimeUnit.MILLISECONDS);
					if(elem!=null){
						if(elem.getAcc()==null){
							log("["+this.getTucsonAgentId()+"]ci passo e il mio acc="+acc);
							elem.setAcc(acc);
						}
						operationCount.release();
						/*log("releaseAqm: "+pos);
						pos++;*/
					    elem.execute();
					    
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TucsonOperationNotPossibleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnreachableNodeException e) {
					e.printStackTrace();
					log("elem="+elem+" acc="+elem.getAcc()+" listener"+elem.getListener());
				} 
			
		}
		log("["+this.getTucsonAgentId()+"]Fine primo WHILEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
		if(stopAbrout){
			//while(operationCount.availablePermits()!=0){}
			try {
				log("[aqm] queueLength:"+queue.size());
				if(operationCount.availablePermits()!=0){
					log("[aqm]:stopAbrout="+stopAbrout+" AvaiblePermits:"+operationCount.availablePermits());
					waitOperationCount.acquire();
				}else{
					log("[aqm]: avaible permits = 0");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			log("operationCount.availablePermits()::::"+operationCount.availablePermits()+" queue empty="+(queue.isEmpty()));
			while(operationCount.availablePermits()!=0||!queue.isEmpty()){
			      log("["+this.getTucsonAgentId()+"]lo scrivo milioni di volte.Ne restano:"+operationCount.availablePermits());
				      try {
							elem=queue.poll(1000,TimeUnit.MILLISECONDS);
							if(elem!=null){
								if(elem.getAcc()==null){
									log("["+this.getTucsonAgentId()+"]ci passo e il mio acc="+acc);
									elem.setAcc(acc);
								}
								operationCount.release();
								log("["+this.getTucsonAgentId()+"]aggiungo elemento alla queue nel secondo while");
							    elem.execute();
							    
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TucsonOperationNotPossibleException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnreachableNodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			}
		}
		log("["+this.getTucsonAgentId()+" vado a morire");

		
	}
	

}
