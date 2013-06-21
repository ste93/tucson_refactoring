package alice.tucson.service;

import java.util.List;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonOpId;

/* MODIFIED BY <s.mariani@unibo.it> */

/**
 * Completion of a TuCSoN operation: such event stores the corresponding
 * operation ID, its success state, its result and other useful info.
 */
public class TucsonOpCompletionEvent {

    private final boolean allowed;
    private final TucsonOpId opId;
    private String spec;
    private final boolean success;
    private LogicTuple tuple;
    private List<LogicTuple> tupleList;

    public TucsonOpCompletionEvent(final TucsonOpId id, final boolean a,
            final boolean s) {
        this.opId = id;
        this.allowed = a;
        this.success = s;
    }

    public TucsonOpCompletionEvent(final TucsonOpId id, final boolean a,
            final boolean s, final List<LogicTuple> tl) {
        this.opId = id;
        this.allowed = a;
        this.success = s;
        this.tupleList = tl;
    }

    public TucsonOpCompletionEvent(final TucsonOpId id, final boolean a,
            final boolean s, final LogicTuple t) {
        this.opId = id;
        this.allowed = a;
        this.success = s;
        this.tuple = t;
    }

    public TucsonOpCompletionEvent(final TucsonOpId id, final boolean a,
            final boolean s, final String sp) {
        this.opId = id;
        this.allowed = a;
        this.success = s;
        this.spec = sp;
    }

    public TucsonOpId getOpId() {
        return this.opId;
    }

    public String getSpec() {
        return this.spec;
    }

    public LogicTuple getTuple() {
        return this.tuple;
    }

    public List<LogicTuple> getTupleList() {
        return this.tupleList;
    }

    public boolean operationAllowed() {
        return this.allowed;
    }

    public boolean operationSucceeded() {
        return this.success;
    }

}
