package alice.respect.api;

import java.util.List;

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT ordinary primitives using a synchronous
 * semantics.
 * 
 * @author aricci
 *
 */
public interface IOrdinarySynchInterface {
    
	void out(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple in(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
		
	LogicTuple rd(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple inp(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple rdp(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple no(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple nop(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	void out_all(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple in_all(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple rd_all(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple no_all(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple urd(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	LogicTuple uin(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple uno(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple urdp(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple uinp(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	LogicTuple unop(IId aid, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	List<LogicTuple> get(IId aid) throws OperationNotPossibleException;
	
	List<LogicTuple> set(IId aid, LogicTuple tupleList) throws OperationNotPossibleException, InvalidLogicTupleException;
	
}