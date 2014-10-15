package alice.respect.api;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT ordinary primitives using
 * an asynchronous semantics.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public interface IOrdinaryAsynchInterface {
    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple if
     *             the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out if the
     *             operation requested cannot be carried out
     */
    IRespectOperation get(IId aid, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation the tuple argument of the
     *            operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation in(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation inAll(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation inp(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation no(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation noAll(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation nop(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation out(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation outAll(IId aid, LogicTuple t,
            OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation rd(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation rdAll(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation rdp(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation set(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation spawn(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation uin(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation uinp(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation uno(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation unop(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation urd(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation urdp(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;
}
