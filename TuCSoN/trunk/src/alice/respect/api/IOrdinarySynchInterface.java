package alice.respect.api;

import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT ordinary primitives using a
 * synchronous semantics.
 * 
 * @author aricci
 * 
 */
public interface IOrdinarySynchInterface {

    List<LogicTuple> get(IId aid) throws OperationNotPossibleException;

    LogicTuple in(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    List<LogicTuple> in_all(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple inp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple no(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    List<LogicTuple> no_all(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple nop(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    void out(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    List<LogicTuple> out_all(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple rd(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    List<LogicTuple> rd_all(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    LogicTuple rdp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    List<LogicTuple> set(IId aid, LogicTuple tupleList)
            throws OperationNotPossibleException, InvalidLogicTupleException;

    LogicTuple spawn(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple uin(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple uinp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple uno(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple unop(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple urd(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    LogicTuple urdp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

}