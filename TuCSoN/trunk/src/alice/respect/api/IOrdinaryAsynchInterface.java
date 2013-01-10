package alice.respect.api;

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT ordinary primitives using an
 * asynchronous semantics.
 * 
 * @author aricci
 *
 */
public interface IOrdinaryAsynchInterface {
	
	IRespectOperation spawn(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation out(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation in(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
		
	IRespectOperation rd(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation inp(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation rdp(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation no(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation nop(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation get(IId aid, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation set(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation out_all(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation in_all(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation rd_all(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation no_all(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation urd(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation uin(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation uno(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation urdp(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation uinp(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation unop(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
}