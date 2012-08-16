package alice.tucson.api;

import java.util.LinkedList;

import alice.logictuple.LogicTuple;

public abstract class SpawnActivity {
	
	private LinkedList<LogicTuple> params;
	private LinkedList<LogicTuple> results;
	
	public SpawnActivity(){
		params = new LinkedList<LogicTuple>();
		params = new LinkedList<LogicTuple>();
	}
	
	protected final LogicTuple[] readParams(LogicTuple[] templates){
		return templates;
	}
	
	protected final LogicTuple[] consumeParams(LogicTuple[] templates){
		return templates;
	}
	
	protected final void shareResults(LogicTuple[] tuples){
		
	}
	
	abstract public void doActivity();
	
}
