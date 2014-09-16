package alice.respect.api;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT specification primitives
 * using an asynchronous semantics.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public interface ISpecificationAsynchInterface {
    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation getS(IId aid, OperationCompletionListener l)
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
     * @return the operation requested
     * @throws InvalidLogicTupleException
     *             if the tuple given as argument is not a valid Prolog tuple
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation inpS(IId aid, LogicTuple t, OperationCompletionListener l)
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
    IRespectOperation inS(IId aid, LogicTuple t, OperationCompletionListener l)
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
    IRespectOperation nopS(IId aid, LogicTuple t, OperationCompletionListener l)
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
    IRespectOperation noS(IId aid, LogicTuple t, OperationCompletionListener l)
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
    IRespectOperation outS(IId aid, LogicTuple t, OperationCompletionListener l)
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
    IRespectOperation rdpS(IId aid, LogicTuple t, OperationCompletionListener l)
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
    IRespectOperation rdS(IId aid, LogicTuple t, OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param spec
     *            the ReSpecT specification given as argument
     * @param l
     *            the listener of the operation completion
     * @return the operation requested
     * @throws InvalidSpecificationException
     *             if the given String does not represent a valid ReSpecT
     *             specification
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    IRespectOperation setS(IId aid, RespectSpecification spec,
            OperationCompletionListener l)
            throws InvalidSpecificationException, OperationNotPossibleException;
}
