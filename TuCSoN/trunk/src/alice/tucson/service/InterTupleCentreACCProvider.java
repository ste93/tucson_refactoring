package alice.tucson.service;

import alice.respect.api.*;
import alice.respect.api.exceptions.OperationNotPossibleException;

import alice.respect.core.RespectOperation;

import alice.tucson.api.InterTupleCentreACC;
import alice.tucson.api.TucsonOpId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.api.TucsonTupleCentreId;


import alice.tuplecentre.core.TupleCentreOperation;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 */
public class InterTupleCentreACCProvider implements ILinkContext{
	
	private final static int threadsNumber = Runtime.getRuntime().availableProcessors()+1;
	private static HashMap<String, InterTupleCentreACC> helpList;
	private static ExecutorService exec;
	private alice.tuplecentre.api.TupleCentreId idTo;

	public InterTupleCentreACCProvider(alice.tuplecentre.api.TupleCentreId id){
		idTo = id;
		if(helpList == null)
			helpList = new HashMap<String, InterTupleCentreACC>();
		if (exec == null)
			exec = Executors.newFixedThreadPool(threadsNumber);
	}

	public synchronized void doOperation(TupleCentreId id, TupleCentreOperation op) throws OperationNotPossibleException{
		// id il tuplecentre source
		Executor ex = new Executor(idTo, id, op, helpList);
		exec.execute(ex);
	}

	@SuppressWarnings("unused")
	private void log(String msg){
		System.out.println("[RespectInterTupleCentreContextProxy]: " + msg);
	}

	class Executor extends Thread{

		private alice.tuplecentre.api.TupleCentreId toId;
		private TupleCentreId fromId;
		private TupleCentreOperation op;
		private HashMap<String, InterTupleCentreACC> helpList;
		private InterTupleCentreACC helper;

		public Executor(alice.tuplecentre.api.TupleCentreId toId, TupleCentreId fromId, TupleCentreOperation op, HashMap<String, InterTupleCentreACC> helpList){
			this.toId = toId;
			this.fromId = fromId;
			this.op = op;
			this.helpList = helpList;
		}

		public void run(){
			
			helper = helpList.get(fromId.getNode());
			if(helper == null){
				try {
					helper = new InterTupleCentreACCProxy(new TucsonTupleCentreId(fromId));
				} catch (TucsonInvalidTupleCentreIdException e) {
					System.err.println("[RespectInterTupleCentreContextProxy] Executor: " + e);
					e.printStackTrace();
				}
				helpList.put(fromId.getNode(), helper);
			}

			TucsonOpId opId = null;
			try {
				opId = helper.doOperation(toId, op.getType(), ((RespectOperation) op).getLogicTupleArgument());
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[RespectInterTupleCentreContextProxy] Executor: " + e);
				e.printStackTrace();
			} catch (UnreachableNodeException e) {
				System.err.println("[RespectInterTupleCentreContextProxy] Executor: " + e);
				e.printStackTrace();
			}
			TucsonOpCompletionEvent ev = helper.waitForCompletion(opId);
			op.setTupleResult(ev.getTuple());
		
		}

	}
	
}
