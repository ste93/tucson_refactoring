package alice.tuplecentre.api;

import java.util.List;
import alice.tuplecentre.core.TCCycleResult.Outcome;

public interface ITCCycleResult {
	
	public boolean isResultDefined();
	
	public boolean isResultSuccess();
	
	public boolean isResultFailure();
	
	public void setOpResult(Outcome o);
	
	public Outcome getOpResult();
	
	public void setTupleResult(Tuple res);
	
	public Tuple getTupleResult();
	
	public void setTupleListResult(List<Tuple> resList);
	
	public List<Tuple> getTupleListResult();

	public void setEndTime(long time);
	
	public long getEndTime();

	public long getStartTime(); 
	
}
