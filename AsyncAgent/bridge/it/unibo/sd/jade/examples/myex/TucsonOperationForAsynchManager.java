package it.unibo.sd.jade.examples.myex;

import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import it.unibo.sd.jade.operations.AbstractTucsonAction;
import it.unibo.sd.jade.operations.AbstractTucsonOrdinaryAction;

public class TucsonOperationForAsynchManager {
	AbstractTucsonAction action;
	TucsonOperationCompletionListener listener;
	long timeout;
	long timeExecution;
	EnhancedAsynchACC acc;
	boolean isGoing=false;
	public TucsonOperationForAsynchManager(EnhancedAsynchACC acc,AbstractTucsonAction action,TucsonOperationCompletionListener listener) {
		this.action=action;
		this.listener=listener;
		this.acc=acc;
		timeExecution=System.currentTimeMillis();
	}
	public TucsonOperationForAsynchManager(EnhancedAsynchACC acc,AbstractTucsonAction action,TucsonOperationCompletionListener listener,long timeout) {
		this.action=action;
		this.listener=listener;
		this.timeout=timeout;
		this.acc=acc;
	}
	public long getTimeOut(){
		return timeout;
	}
	public AbstractTucsonAction  getAction(){
		return action;
	}
	public ITucsonOperation execute() throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return action.executeAsynch(acc, listener);
	}
	public TucsonOperationCompletionListener getListener(){
		return listener;
	}
	public long getTimeExecution(){
		return timeExecution;
	}
	public EnhancedAsynchACC getAcc(){
		return acc;
	}
	public void setAcc(EnhancedAsynchACC acc){
		this.acc=acc;
	}
}
