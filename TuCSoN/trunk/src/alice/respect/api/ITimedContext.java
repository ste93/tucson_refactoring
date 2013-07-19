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

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    List<LogicTuple> get(IId id, long ms) throws OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple in(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple inAll(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple inp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple no(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple noAll(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple nop(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    void out(IId id, LogicTuple t, long ms) throws InvalidLogicTupleException,
            OperationNotPossibleException, OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    void outAll(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple rd(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple rdAll(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple rdp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    List<LogicTuple> set(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple spawn(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple uin(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple uinp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple uno(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple unop(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple urd(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

    /**
     * 
     * @param id
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param ms
     *            the timeout for operation completion
     * @return the result of the operation
     * @throws InvalidLogicTupleException
     *             if the given argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws OperationTimeOutException
     *             if the given timeout expired prior to operation completion
     */
    LogicTuple urdp(IId id, LogicTuple t, long ms)
            throws InvalidLogicTupleException, OperationNotPossibleException,
            OperationTimeOutException;

}
