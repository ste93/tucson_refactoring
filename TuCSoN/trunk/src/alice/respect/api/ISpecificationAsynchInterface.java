package alice.respect.api;

import alice.logictuple.*;
import alice.logictuple.exception.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT specification primitives using an
 * asynchronous semantics.
 * 
 * @author aricci
 *
 */
public interface ISpecificationAsynchInterface {
    
	IRespectOperation no_s(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation nop_s(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation out_s(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation in_s(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
		
	IRespectOperation rd_s(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation inp_s(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

	IRespectOperation rdp_s(IId aid, LogicTuple t, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation get_s(IId aid, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	IRespectOperation set_s(IId aid, RespectSpecification spec, OperationCompletionListener l) throws InvalidLogicTupleException, OperationNotPossibleException;

}