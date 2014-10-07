package alice.tucson.persistency;

import java.util.List;
import alice.respect.core.TupleSet;
import alice.respect.core.tupleset.ITupleSet;

public class PersistencyData {
    private long initCreationPersistencyData;
    private List<String> predicates;
    private TupleSet prologPredicates;
    private List<String> specTuples;
    private long terminateCreationPersistencyData;
    private List<String> tuples;
    private ITupleSet tupleSet;
    private ITupleSet tupleSpecSet;
    private List<String> updates;

    public PersistencyData() {
    }

    public PersistencyData(final ITupleSet tSet, final ITupleSet tSpecSet,
            final TupleSet pPrologPred, final List<String> tUpdates) {
        this.initCreationPersistencyData = System.nanoTime();
        this.setTupleSet(tSet);
        this.setTupleSpecSet(tSpecSet);
        this.setPrologPredicates(pPrologPred);
        this.setUpdates(tUpdates);
        this.terminateCreationPersistencyData = System.nanoTime();
        this.log("Time elapsed from creation of PersistencyData: "
                + (this.terminateCreationPersistencyData - this.initCreationPersistencyData)
                / 1000000 + " milliseconds.");
    }

    public PersistencyData(final List<String> tuples,
            final List<String> specTuples, final List<String> pPredicates,
            final List<String> updates) {
        this.initCreationPersistencyData = System.nanoTime();
        this.setTuples(tuples);
        this.setSpecTuples(specTuples);
        this.setPredicates(pPredicates);
        this.setUpdates(updates);
        this.terminateCreationPersistencyData = System.nanoTime();
        this.log("Time elapsed from creation of PersistencyData: "
                + (this.terminateCreationPersistencyData - this.initCreationPersistencyData)
                / 10000000 + " milliseconds.");
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

    private void log(final String s) {
        System.out.println("[" + this.getClass().getName() + "] " + s);
    }
}
