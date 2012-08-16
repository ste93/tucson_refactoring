package alice.respect.api;

import java.util.List;

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT ordinary primitives using a synchronous
 * semantics.
 * 
 * @author aricci
 *
 */
public interface IOrdinarySynchInterface {
    
	void out(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple in(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
		
	LogicTuple rd(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple inp(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple rdp(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple no(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple nop(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	void out_all(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple in_all(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple rd_all(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple no_all(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple urd(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple uin(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple uno(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple urdp(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple uinp(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple unop(AgentId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	List<LogicTuple> get(AgentId aid) throws OperationNotPossibleException;
	
	List<LogicTuple> set(AgentId aid, LogicTuple tupleList) throws OperationNotPossibleException, InvalidLogicTupleException;
	
}