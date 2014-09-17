package it.unibo.sd.jade.examples.myex;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;


public class AsynchTimedWaitingOperationListener implements TucsonOperationCompletionListener{

		TucsonOperationForAsynchManager tofam;
		TucsonOperationCompletionListener clientListener;
		long timeOut;
		 AsynchQueueManager aqm;
		public AsynchTimedWaitingOperationListener(TucsonOperationCompletionListener clientListener, AsynchQueueManager aqm,long timeOut){
			this.clientListener=clientListener;
			this.aqm=aqm;
			this.timeOut=timeOut;
		}
		public void setTucsonOperationForAsynchManager(TucsonOperationForAsynchManager tofam){
			this.tofam=tofam;
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
			if(op.isResultSuccess()){
				clientListener.operationCompleted(op);
				System.out.println("[aqm listener]op ha avuto successo");
			}else{
				if(!aqm.getStopAbrout()){
					if(System.currentTimeMillis()-tofam.getTimeExecution()<timeOut){
						try {
							aqm.getQueue().put(tofam);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else{
						clientListener.operationCompleted(op);
					}
				}
			}
		}
		@Override
		public void operationCompleted(ITucsonOperation op) {

		}
		
	}