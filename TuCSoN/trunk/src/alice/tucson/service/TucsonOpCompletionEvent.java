package alice.tucson.service;

import alice.logictuple.*;
import alice.tucson.api.TucsonOpId;

import java.util.List;

/* MODIFIED BY <s.mariani@unibo.it> */

/**
 * Completion of a TuCSoN operation: such event stores the corresponding operation ID, its success
 * state, its result and other useful info.
 */
public class TucsonOpCompletionEvent{
	
	private LogicTuple tuple;
	private List<LogicTuple> tupleList;
	private boolean allowed;
	private TucsonOpId opId;
	private String spec;
	private boolean success;

	public TucsonOpCompletionEvent(TucsonOpId opId, boolean allowed, boolean success){
		this.opId = opId;
		this.allowed = allowed;
		this.success = success;
	}

	public TucsonOpCompletionEvent(TucsonOpId opId, boolean allowed, boolean success, LogicTuple tuple){
		this.opId = opId;
		this.allowed = allowed;
		this.success = success;
		this.tuple = tuple;
	}

	public TucsonOpCompletionEvent(TucsonOpId opId, boolean allowed, boolean success, String spec){
		this.opId = opId;
		this.allowed = allowed;
		this.success = success;
		this.spec = spec;
	}
	
	public TucsonOpCompletionEvent(TucsonOpId opId, boolean allowed, boolean success, List<LogicTuple> tupleList){
		this.opId = opId;
		this.allowed = allowed;
		this.success = success;
		this.tupleList = tupleList;
	}

	public boolean operationAllowed(){
		return allowed;
	}

	public boolean operationSucceeded(){
		return success;
	}

	public TucsonOpId getOpId(){
		return opId;
	}

	public LogicTuple getTuple(){
		return tuple;
	}
	
	public List<LogicTuple> getTupleList(){
		return tupleList;
	}

	public String getSpec(){
		return spec;
	}
	
}
