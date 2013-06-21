package alice.tuplecentre.core;

import java.util.LinkedList;
import java.util.List;

import alice.tuplecentre.api.ITCCycleResult;
import alice.tuplecentre.api.Tuple;

public class TCCycleResult implements ITCCycleResult {

    public enum Outcome {
        FAILURE, SUCCESS, UNDEFINED
    }

    private long endTime;
    private Outcome opResult;
    private final long startTime;
    private List<Tuple> tupleListResult;
    private Tuple tupleResult;

    public TCCycleResult() {
        this.opResult = Outcome.UNDEFINED;
        this.tupleResult = null;
        this.tupleListResult = new LinkedList<Tuple>();
        this.startTime = System.currentTimeMillis();
    }

    public long getEndTime() {
        return this.endTime;
    }

    public Outcome getOpResult() {
        return this.opResult;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public List<Tuple> getTupleListResult() {
        return this.tupleListResult;
    }

    public Tuple getTupleResult() {
        return this.tupleResult;
    }

    public boolean isResultDefined() {
        return this.opResult != Outcome.UNDEFINED;
    }

    public boolean isResultFailure() {
        return this.opResult == Outcome.FAILURE;
    }

    public boolean isResultSuccess() {
        return this.opResult == Outcome.SUCCESS;
    }

    public void setEndTime(final long time) {
        this.endTime = time;
    }

    public void setOpResult(final Outcome o) {
        this.opResult = o;
    }

    public void setTupleListResult(final List<Tuple> res) {
        this.tupleListResult = res;
    }

    public void setTupleResult(final Tuple res) {
        this.tupleResult = res;
    }

}
