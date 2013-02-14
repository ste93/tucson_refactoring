package alice.tuplecentre.api;

import java.util.List;

import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Basic interface for tuple centre operations.
 * 
 * @author aricci
 */
public interface ITupleCentreOperation{

	boolean isOut();
	
	boolean isIn();

	boolean isRd();

	boolean isInp();

	boolean isRdp();
	
	boolean isNo();
	
	boolean isNop();
	
	boolean isGet();
	
	boolean isSet();		

	boolean isOutAll();
	
	boolean isInAll();

	boolean isRdAll();
	
	boolean isUrd();
	
	boolean isUin();
	
	boolean isUinp();
	
	boolean isUrdp();
	
	boolean isSet_s();
	
	boolean isGet_s();
	
	/**
	 * Tests if the operation is completed
	 * 
	 * @return true if the operation is completed
	 */
	boolean isOperationCompleted();
	
	boolean isResultSuccess();
	
	boolean isResultFailure();

	/**
	 * Wait for operation completion
	 * 
	 * Current execution flow is blocked until the operation is completed
	 */
	void waitForOperationCompletion();

	/**
	 * Wait for operation completion, with time out
	 * 
	 * Current execution flow is blocked until the operation is completed
	 * or a maximum waiting time is elapsed
	 * 
	 * @param ms maximum waiting time
	 * @throws OperationTimeOutException
	 */
	void waitForOperationCompletion(long ms) throws OperationTimeOutException;

	/**
	 * Get operation identifier
	 * 
	 * @return Operation identifier
	 */
	long getId();

	Tuple getTupleResult();

	List<Tuple> getTupleListResult();

	List<Tuple> getTupleListArgument();

	Tuple getTupleArgument();

	TupleTemplate getTemplateArgument();

}