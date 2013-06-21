package alice.respect.api;

import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.respect.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.api.IId;

/**
 * Interface to a ReSpecT Tuple Centre with timing functionalities.
 * 
 * @author aricci
 * 
 */
public interface ITimedContext {

    List<LogicTuple> get(IId id, long ms) throws OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple in(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            alice.respect.api.exceptions.OperationTimeOutException;

    LogicTuple in_all(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            alice.respect.api.exceptions.OperationTimeOutException;

    LogicTuple inp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple no(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple no_all(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple nop(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    void out(IId id, LogicTuple t, long ms) throws InvalidLogicTupleException,
            OperationNotPossibleException, OperationTimeOutException;

    void out_all(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple rd(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple rd_all(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple rdp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    List<LogicTuple> set(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple spawn(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple uin(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            alice.respect.api.exceptions.OperationTimeOutException;

    LogicTuple uinp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple uno(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple unop(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple urd(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    LogicTuple urdp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

}