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

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation the identifier
     *            of who is invokin the operation
     * @return the result of the operation the result of the operation
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out if the
     *             operation requested cannot be carried out
     */
    List<LogicTuple> get(IId aid) throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation the tuple argument of the
     *            operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple if
     *             the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple in(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    List<LogicTuple> inAll(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple inp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple no(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    List<LogicTuple> noAll(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple nop(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    void out(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    List<LogicTuple> outAll(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple rd(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    List<LogicTuple> rdAll(IId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple rdp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param tupleList
     *            the list of tuples argument of the operation
     * @return the result of the operation
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     */
    List<LogicTuple> set(IId aid, LogicTuple tupleList)
            throws OperationNotPossibleException, InvalidLogicTupleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple spawn(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple uin(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple uinp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple uno(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple unop(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple urd(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    LogicTuple urdp(IId aid, LogicTuple t) throws InvalidLogicTupleException,
            OperationNotPossibleException;

}
