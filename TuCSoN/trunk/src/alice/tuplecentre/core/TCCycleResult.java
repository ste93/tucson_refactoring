package alice.tuplecentre.core;

import java.util.LinkedList;
import java.util.List;

import alice.tuplecentre.api.ITCCycleResult;
import alice.tuplecentre.api.Tuple;

public class TCCycleResult implements ITCCycleResult {
	
	public enum Outcome{SUCCESS, FAILURE, UNDEFINED};

	private Outcome opResult;
	private Tuple tupleResult;
	private List<Tuple> tupleListResult;
	private long startTime, endTime;
	
	public TCCycleResult(){
		this.opResult = Outcome.UNDEFINED;
		this.tupleResult = null;
//		oppure null?
		this.tupleListResult = new LinkedList<Tuple>();
		this.startTime = System.currentTimeMillis();
	}
	
	public boolean isResultDefined() {
		return this.opResult != Outcome.UNDEFINED;
	}
	
	public boolean isResultSuccess() {
		return this.opResult == Outcome.SUCCESS;
	}
	
	public boolean isResultFailure() {
		return this.opResult == Outcome.FAILURE;
	}
	
	@Override
	public void setOpResult(Outcome o) {
		opResult = o;
	}

	@Override
	public Outcome getOpResult() {
		return opResult;
	}
	
	@Override
	public void setTupleResult(Tuple res) {
		this.tupleResult = res;
	}
	
	@Override
	public Tuple getTupleResult() {
		return this.tupleResult;
	}
	
	@Override
	public void setTupleListResult(List<Tuple> res) {
		this.tupleListResult = res;
	}

	@Override
	public List<Tuple> getTupleListResult() {
		return this.tupleListResult;
	}
	
	public void setEndTime(long time) {
		this.endTime = time;
	}
	
	public long getEndTime() {
		return this.endTime;
	}

	public long getStartTime() {
		return this.startTime;
	}

}
