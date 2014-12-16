package alice.respect.api;

import java.util.List;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;

/**
 * A ReSpecT Tuple Centre Interface to issue ReSpecT specification primitives
 * using a synchronous semantics.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public interface ISpecificationSynchInterface {
    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @return the result of the operation
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    List<LogicTuple> getS(final IId aid) throws OperationNotPossibleException;

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
    LogicTuple inpS(AgentId aid, LogicTuple t)
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
    LogicTuple inS(AgentId aid, LogicTuple t)
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
    LogicTuple nopS(AgentId aid, LogicTuple t)
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
    LogicTuple noS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

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
    void outS(AgentId aid, LogicTuple t) throws InvalidLogicTupleException,
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
    LogicTuple rdpS(AgentId aid, LogicTuple t)
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
    LogicTuple rdS(AgentId aid, LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param t
     *            the tuple representation of the ReSpecT specification to set
     * @return the result of the operation
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     */
    List<LogicTuple> setS(final IId aid, final LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invokin the operation
     * @param spec
     *            the String representation of the ReSpecT specification to set
     * @return the result of the operation
     * @throws OperationNotPossibleException
     *             if the operation requested cannot be carried out
     * @throws InvalidSpecificationException
     *             if the given String does not represent a valid ReSpecT
     *             specification
     */
    List<LogicTuple> setS(final IId aid, final RespectSpecification spec)
            throws OperationNotPossibleException, InvalidSpecificationException;
}
