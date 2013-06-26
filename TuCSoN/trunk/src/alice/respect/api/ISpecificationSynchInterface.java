package alice.respect.api;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT specification primitives
 * using a synchronous semantics.
 * 
 * @author aricci
 * 
 */
public interface ISpecificationSynchInterface {

    LogicTuple inpS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple inS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple nopS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple noS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    void outS(AgentId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple rdpS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple rdS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

}