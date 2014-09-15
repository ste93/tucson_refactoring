package it.unibo.sd.jade.examples.myex;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class AsynchWaitingOperationListener implements TucsonOperationCompletionListener{
	TucsonOperationForAsynchManager tofam;
	TucsonOperationCompletionListener clientListener;
	AsynchQueueManager aqm;
	public AsynchWaitingOperationListener(TucsonOperationCompletionListener clientListener, AsynchQueueManager aqm){
		this.aqm=aqm;
		this.clientListener=clientListener;
	}
	public void setTucsonOperationForAsynchManager(TucsonOperationForAsynchManager tofam){
		this.tofam=tofam;
	}
	@Override
	public void operationCompleted(AbstractTupleCentreOperation op) {
		try {
			aqm.getOperationCount().acquire();
			log(" AvaiblePermits: "+aqm.getOperationCount().availablePermits()+" StopAbrout:"+aqm.getStopAbrout());
			
			if(aqm.getStopAbrout()&&aqm.getOperationCount().availablePermits()==0){
				aqm.getWaitOperationCount().release();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if(op.isResultSuccess()){
			clientListener.operationCompleted(op);
			System.out.println("[aqm listener]op ha avuto successo");
		}else{
			if(!aqm.getStopAbrout()){
				try {
					aqm.getQueue().put(tofam);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void log(String string) {
		// TODO Auto-generated method stub
		System.out.println(string);
	}
	@Override
	public void operationCompleted(ITucsonOperation op) {

	}
	
}