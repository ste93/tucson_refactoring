package alice.tuplecentre.api;

import java.util.List;
import alice.tuplecentre.core.TCCycleResult.Outcome;

/**
 *
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 *
 */
public interface ITCCycleResult {

    /**
     *
     * @return the time the operation was completed
     */
    long getEndTime();

    /**
     *
     * @return the outcome of the operation
     */
    Outcome getOpResult();

    /**
     *
     * @return the time the operation was requested
     */
    long getStartTime();

    /**
     *
     * @return the list of tuples result of the operation
     */
    List<Tuple> getTupleListResult();

    /**
     *
     * @return the tuple result of the operation
     */
    Tuple getTupleResult();

    /**
     *
     * @return wether the result of the operation is defined
     */
    boolean isResultDefined();

    /**
     *
     * @return wether the result of the operation is a failure
     */
    boolean isResultFailure();

    /**
     *
     * @return wether the result of the operation is a success
     */
    boolean isResultSuccess();

    /**
     *
     * @param time
     *            the time at which the operation completed
     */
    void setEndTime(long time);

    /**
     *
     * @param o
     *            the outcome of the operation
     */
    void setOpResult(Outcome o);

    /**
     *
     * @param resList
     *            the list of tuples result of the operation
     */
    void setTupleListResult(List<Tuple> resList);

    /**
     *
     * @param res
     *            the tuple result of the operation
     */
    void setTupleResult(Tuple res);
}
