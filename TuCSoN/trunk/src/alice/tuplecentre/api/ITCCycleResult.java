package alice.tuplecentre.api;

import java.util.List;

import alice.tuplecentre.core.TCCycleResult.Outcome;

public interface ITCCycleResult {

    public long getEndTime();

    public Outcome getOpResult();

    public long getStartTime();

    public List<Tuple> getTupleListResult();

    public Tuple getTupleResult();

    public boolean isResultDefined();

    public boolean isResultFailure();

    public boolean isResultSuccess();

    public void setEndTime(long time);

    public void setOpResult(Outcome o);

    public void setTupleListResult(List<Tuple> resList);

    public void setTupleResult(Tuple res);

}
