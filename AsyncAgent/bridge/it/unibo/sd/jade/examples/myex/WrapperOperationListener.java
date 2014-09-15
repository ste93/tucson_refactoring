package it.unibo.sd.jade.examples.myex;

import java.util.concurrent.Semaphore;

import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class WrapperOperationListener implements TucsonOperationCompletionListener{
	TucsonOperationCompletionListener clientListener;
	AsynchQueueManager aqm;
	public WrapperOperationListener(TucsonOperationCompletionListener clientListener,AsynchQueueManager aqm){
		this.aqm=aqm;
		this.clientListener=clientListener;
	}
	@Override
	public void operationCompleted(AbstractTupleCentreOperation op) {
		try {
			aqm.getOperationCount().acquire();
			if(aqm.getStopAbrout()&&aqm.getOperationCount().availablePermits()==0){
				aqm.getWaitOperationCount().release();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try{
			clientListener.operationCompleted(op);
		}catch(Exception ex){
			ex.printStackTrace();
		}
			
	}
	@Override
	public void operationCompleted(ITucsonOperation op) {

	}
	
}