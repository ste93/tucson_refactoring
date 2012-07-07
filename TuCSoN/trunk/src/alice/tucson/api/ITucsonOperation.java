package alice.tucson.api;

import alice.logictuple.InvalidTupleOperationException;
import alice.logictuple.LogicTuple;

import alice.tuplecentre.api.*;

import java.util.List;

/**
 * 
 */
public interface ITucsonOperation extends ITupleCentreOperation{

	LogicTuple getLogicTupleResult();

	LogicTuple getLogicTupleArgument();
	
	void setLogicTupleListResult(List<LogicTuple> tl);
	
	List<LogicTuple> getLogicTupleListResult();
	
	String getSpecResult() throws InvalidTupleOperationException;
	
	boolean isResultSuccess();
	
	boolean isSuccessed();
	
	boolean isAllowed();

}
