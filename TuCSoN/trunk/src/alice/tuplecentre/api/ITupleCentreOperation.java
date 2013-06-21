package alice.tuplecentre.api;

import java.util.List;

import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Basic interface for tuple centre operations.
 * 
 * @author aricci
 */
public interface ITupleCentreOperation {

    /**
     * Get operation identifier
     * 
     * @return Operation identifier
     */
    long getId();

    TupleTemplate getTemplateArgument();

    Tuple getTupleArgument();

    List<Tuple> getTupleListArgument();

    List<Tuple> getTupleListResult();

    Tuple getTupleResult();

    boolean isGet();

    boolean isGet_s();

    boolean isIn();

    boolean isInAll();

    boolean isInp();

    boolean isNo();

    boolean isNop();

    /**
     * Tests if the operation is completed
     * 
     * @return true if the operation is completed
     */
    boolean isOperationCompleted();

    boolean isOut();

    boolean isOutAll();

    boolean isRd();

    boolean isRdAll();

    boolean isRdp();

    boolean isResultFailure();

    boolean isResultSuccess();

    boolean isSet();

    boolean isSet_s();

    boolean isUin();

    boolean isUinp();

    boolean isUrd();

    boolean isUrdp();

    /**
     * Wait for operation completion
     * 
     * Current execution flow is blocked until the operation is completed
     */
    void waitForOperationCompletion();

    /**
     * Wait for operation completion, with time out
     * 
     * Current execution flow is blocked until the operation is completed or a
     * maximum waiting time is elapsed
     * 
     * @param ms
     *            maximum waiting time
     * @throws OperationTimeOutException
     */
    void waitForOperationCompletion(long ms) throws OperationTimeOutException;

}