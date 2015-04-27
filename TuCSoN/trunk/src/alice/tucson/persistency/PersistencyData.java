package alice.tucson.persistency;

import java.util.List;
import alice.respect.core.TupleSet;
import alice.respect.core.tupleset.ITupleSet;

public class PersistencyData {

    private List<String> predicates;
    private TupleSet prologPredicates;
    private List<String> specTuples;
    private List<String> tuples;
    private ITupleSet tupleSet;
    private ITupleSet tupleSpecSet;
    private List<String> updates;

    public PersistencyData() {
    }

    public PersistencyData(final ITupleSet tSet, final ITupleSet tSpecSet,
            final TupleSet pPrologPred, final List<String> tUpdates) {
        this.setTupleSet(tSet);
        this.setTupleSpecSet(tSpecSet);
        this.setPrologPredicates(pPrologPred);
        this.setUpdates(tUpdates);
    }

    public PersistencyData(final List<String> tuples,
            final List<String> specTuples, final List<String> pPredicates,
            final List<String> updates) {
        this.setTuples(tuples);
        this.setSpecTuples(specTuples);
        this.setPredicates(pPredicates);
        this.setUpdates(updates);
    }

    public List<String> getPredicates() {
        return this.predicates;
    }

    public TupleSet getPrologPredicates() {
        return this.prologPredicates;
    }

    public List<String> getSpecTuples() {
        return this.specTuples;
    }

    public List<String> getTuples() {
        return this.tuples;
    }

    public ITupleSet getTupleSet() {
        return this.tupleSet;
    }

    public ITupleSet getTupleSpecSet() {
        return this.tupleSpecSet;
    }

    public List<String> getUpdates() {
        return this.updates;
    }

    public void setPredicates(final List<String> pPredicates) {
        this.predicates = pPredicates;
    }

    public void setPrologPredicates(final TupleSet prologPredicates) {
        this.prologPredicates = prologPredicates;
    }

    public void setSpecTuples(final List<String> specTuples) {
        this.specTuples = specTuples;
    }

    public void setTuples(final List<String> tuples) {
        this.tuples = tuples;
    }

    public void setTupleSet(final ITupleSet tupleSet) {
        this.tupleSet = tupleSet;
    }

    public void setTupleSpecSet(final ITupleSet tupleSpecSet) {
        this.tupleSpecSet = tupleSpecSet;
    }

    public void setUpdates(final List<String> updates) {
        this.updates = updates;
    }
}
