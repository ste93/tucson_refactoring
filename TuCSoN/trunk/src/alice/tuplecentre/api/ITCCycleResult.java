package alice.tuplecentre.api;

import java.util.List;

import alice.tuplecentre.core.TCCycleResult.Outcome;

public interface ITCCycleResult {

    long getEndTime();

    Outcome getOpResult();

    long getStartTime();

    List<Tuple> getTupleListResult();

    Tuple getTupleResult();

    boolean isResultDefined();

    boolean isResultFailure();

    boolean isResultSuccess();

    void setEndTime(long time);

    void setOpResult(Outcome o);

    void setTupleListResult(List<Tuple> resList);

    void setTupleResult(Tuple res);

}
