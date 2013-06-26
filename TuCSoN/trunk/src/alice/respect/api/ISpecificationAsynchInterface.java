package alice.respect.api;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT specification primitives
 * using an asynchronous semantics.
 * 
 * @author aricci
 * 
 */
public interface ISpecificationAsynchInterface {

    IRespectOperation getS(IId aid, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    IRespectOperation
            inpS(IId aid, LogicTuple t, OperationCompletionListener l)
                    throws InvalidLogicTupleException,
                    OperationNotPossibleException;

    IRespectOperation inS(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    IRespectOperation
            nopS(IId aid, LogicTuple t, OperationCompletionListener l)
                    throws InvalidLogicTupleException,
                    OperationNotPossibleException;

    IRespectOperation noS(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    IRespectOperation
            outS(IId aid, LogicTuple t, OperationCompletionListener l)
                    throws InvalidLogicTupleException,
                    OperationNotPossibleException;

    IRespectOperation
            rdpS(IId aid, LogicTuple t, OperationCompletionListener l)
                    throws InvalidLogicTupleException,
                    OperationNotPossibleException;

    IRespectOperation rdS(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    IRespectOperation setS(IId aid, RespectSpecification spec,
            OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException;

}