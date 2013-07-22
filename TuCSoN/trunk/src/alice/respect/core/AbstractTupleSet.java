package alice.respect.core;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.respect.core.collection.BucketMap;
import alice.tuprolog.Var;

public abstract class AbstractTupleSet implements ITupleSet {

    /**
     * FIXME questa classe dovrebbe stare nella BucketMap
     * 
     * 
     * This class represents a key-value pair, where the key is a String and the
     * value is an instance of the class LogicTuple. These
     * <tt>LogicTupleEntry</tt> objects are valid <i>only</i> for the duration
     * of the iteration; more formally, the behavior of a map entry is undefined
     * if the backing map has been modified after the entry was returned by the
     * iterator, except through the <tt>setValue</tt> operation on the map
     * entry.
     * 
     */
    protected class LogicTupleEntry implements Map.Entry<String, LogicTuple> {

        private final String key;
        private final LogicTuple value;

        LogicTupleEntry(final String k, final LogicTuple v) {
            this.key = k;
            this.value = v;
        }

        public String getKey() {
            return this.key;
        }

        /**
         * {@inheritDoc}
         */
        public LogicTuple getValue() {
            return this.value;
        }

        /**
         * <p>
         * NOTE: This method is not implemented, the value of this class can not
         * be changed after initialization.
         * </p>
         * {@inheritDoc}
         * 
         * @throws UnsupportedOperationException
         *             on each invocation of method.
         */
        public LogicTuple setValue(final LogicTuple v) {
            throw new UnsupportedOperationException();
        }

    }

    protected List<LogicTupleEntry> tAdded;
    protected boolean transaction;
    protected List<LogicTupleEntry> tRemoved;

    protected BucketMap<String, LogicTuple> tuples;

    public void add(final LogicTuple t) {
        // TODO CICORA: in questo caso l'eccezione viene gestita nella getKey
        // Cosa fare in caso di problemi?
        this.tuples.put(this.createEntry(t));
        if (this.transaction) {
            this.tAdded.add(this.createEntry(t));
        }
    }

    public void beginTransaction() {
        this.transaction = true;
        this.tAdded.clear();
        this.tRemoved.clear();
    }

    public void empty() {
        this.tuples.clear();
    }

    public void endTransaction(final boolean commit) {
        if (!commit) {
            Iterator<LogicTupleEntry> it = this.tAdded.listIterator();
            while (it.hasNext()) {
                this.tuples.remove(it.next());
            }
            it = this.tRemoved.listIterator();
            while (it.hasNext()) {
                this.tuples.put(it.next());
            }
        }
        this.transaction = false;
        this.tAdded.clear();
        this.tRemoved.clear();
    }

    public Iterator<LogicTuple> getIterator() {
        return this.tuples.iterator();
    }

    public LogicTuple getMatchingTuple(final LogicTuple templ) {
        if (templ == null) {
            return null;
        }
        final Iterator<LogicTuple> l = this.tuples.iterator();
        while (l.hasNext()) {
            final LogicTuple tu = l.next();
            if (templ.match(tu)) {
                l.remove();
                if (this.transaction) {
                    this.tRemoved.add(this.createEntry(tu));
                }
                final AbstractMap<Var, Var> v = new LinkedHashMap<Var, Var>();
                return new LogicTuple(tu.toTerm().copyGoal(v, 0));
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this.tuples.isEmpty();
    }

    public boolean operationsPending() {
        if (this.tAdded.isEmpty() && this.tRemoved.isEmpty()) {
            return false;
        }
        return true;
    }

    public LogicTuple readMatchingTuple(final LogicTuple templ) {
        if (templ == null) {
            return null;
        }
        final Iterator<LogicTuple> l = this.tuples.iterator();
        while (l.hasNext()) {
            final LogicTuple tu = l.next();
            if (templ.match(tu)) {
                final AbstractMap<Var, Var> v = new LinkedHashMap<Var, Var>();
                return new LogicTuple(tu.toTerm().copyGoal(v, 0));
            }
        }
        return null;
    }

    public void remove(final LogicTuple t) {
        // TODO CICORA: in questo caso l'eccezione viene gestita nella getKey
        // Cosa fare in caso di problemi?
        this.tuples.remove(this.createEntry(t));
        if (this.transaction) {
            this.tRemoved.add(this.createEntry(t));
        }
    }

    public int size() {
        return this.tuples.size();
    }

    public LogicTuple[] toArray() {
        return this.tuples.toArray(new LogicTuple[this.tuples.size()]);
    }

    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer();
        for (final LogicTuple t : this.tuples) {
            str.append(t.toString()).append(".\n");
        }
        return str.toString();
    }

    protected abstract String getKey(LogicTuple t);

    private LogicTupleEntry createEntry(final LogicTuple t) {
        return new LogicTupleEntry(this.getKey(t), t);
    }

}
