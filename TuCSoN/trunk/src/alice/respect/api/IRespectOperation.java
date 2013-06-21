package alice.respect.api;

import java.util.List;

import alice.logictuple.LogicTuple;

/**
 * ReSpecT Operation Interface.
 * 
 * @author aricci
 * 
 */
public interface IRespectOperation extends
        alice.tuplecentre.api.ITupleCentreOperation {

    /**
     * Gets the results of a get operation
     */
    public List<LogicTuple> getLogicTupleListResult();

    /**
     * Gets the argument of the operation
     * 
     * @return
     */
    LogicTuple getLogicTupleArgument();

    /**
     * Gets the result of the operation,
     * 
     * @return
     */
    LogicTuple getLogicTupleResult();

    boolean isEnv();

    boolean isGetEnv();

    boolean isSetEnv();

    boolean isTime();

}
