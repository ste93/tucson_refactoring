package alice.respect.api;

import alice.logictuple.*;
import alice.logictuple.exception.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT specification primitives using a
 * synchronous semantics.
 * 
 * @author aricci
 *
 */
public interface ISpecificationSynchInterface {
    
	void out_s(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple in_s(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
		
	LogicTuple rd_s(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple inp_s(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple rdp_s(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple no_s(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple nop_s(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

}