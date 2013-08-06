package alice.respect.core.tupleset;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.core.collection.DoubleKeyMVMap;
import alice.respect.core.collection.MVMap;
import alice.tuprolog.Var;

public abstract class AbstractTupleSet implements ITupleSet {

    protected class LTEntry {

        final String key1;
        final String key2;
        final LogicTuple value;

        LTEntry(final String keyOuter, final String keyInner, final LogicTuple v) {
            this.key1 = keyOuter;
            this.key2 = keyInner;
            this.value = v;
        }

        public String getKey1() {
            return this.key1;
        }

        public String getKey2() {
            return this.key2;
        }

        public LogicTuple getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return this.key1 + "\t" + this.key2 + "\t" + this.value.toString();
        }

    }

    protected LinkedList<LTEntry> tAdded;
    protected boolean transaction;
    protected LinkedList<LTEntry> tRemoved;

    protected DoubleKeyMVMap<String, String, LogicTuple> tuples;

    public AbstractTupleSet() {
        super();
    }

    public void add(final LogicTuple t) {
        try {
            final LTEntry e = this.createEntry(t);
            this.tuples.put(e.getKey1(), e.getKey2(), e.getValue());
            if (this.transaction) {
                this.tAdded.add(e);
            }
        } catch (final InvalidLogicTupleException e1) {
            e1.printStackTrace();
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
            Iterator<LTEntry> it = this.tAdded.listIterator();
            while (it.hasNext()) {
                final LTEntry e = it.next();
                this.tuples.remove(e.getKey1(), e.getKey2(), e.getValue());
            }
            it = this.tRemoved.listIterator();
            while (it.hasNext()) {
                final LTEntry e = it.next();
                this.tuples.put(e.getKey1(), e.getKey2(), e.getValue());
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

        Iterator<LogicTuple> l;
        try {
            final String key2 = this.getTupleKey2(templ);
            if (key2.equals("VAR")) {
                l = this.tuples.get(this.getTupleKey1(templ)).iterator();
            } else {
                final MVMap<String, LogicTuple> map =
                        this.tuples.get(this.getTupleKey1(templ));
                if (map.get("VAR").size() > 0) {
                    l = map.iterator();
                } else {
                    l = map.get(key2).iterator();
                }
            }

            while (l.hasNext()) {
                final LogicTuple tu = l.next();
                if (templ.match(tu)) {
                    l.remove();
                    if (this.transaction) {
                        this.tRemoved.add(this.createEntry(tu));
                    }
                    final AbstractMap<Var, Var> v =
                            new LinkedHashMap<Var, Var>();
                    return new LogicTuple(tu.toTerm().copyGoal(v, 0));
                }
            }
        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
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

        Iterator<LogicTuple> l;
        try {
            final String key2 = this.getTupleKey2(templ);
            if (key2.equals("VAR")) {
                l = this.tuples.get(this.getTupleKey1(templ)).iterator();
            } else {
                final MVMap<String, LogicTuple> map =
                        this.tuples.get(this.getTupleKey1(templ));
                if (map.get("VAR").size() > 0) {
                    l = map.iterator();
                } else {
                    l = map.get(key2).iterator();
                }
            }

            while (l.hasNext()) {
                final LogicTuple tu = l.next();
                if (templ.match(tu)) {
                    final AbstractMap<Var, Var> v =
                            new LinkedHashMap<Var, Var>();
                    return new LogicTuple(tu.toTerm().copyGoal(v, 0));
                }
            }
        } catch (final InvalidLogicTupleException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public void remove(final LogicTuple t) {
        try {
            final LTEntry e = this.createEntry(t);
            final boolean res =
                    this.tuples.remove(e.getKey1(), e.getKey2(), e.getValue());
            if (res) {
                if (this.transaction) {
                    this.tRemoved.add(this.createEntry(t));
                }
            }
        } catch (final InvalidLogicTupleException e1) {
            System.out.println(t.toString());
            e1.printStackTrace();
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
        return this.tuples.toString();
    }

    abstract protected String getTupleKey1(LogicTuple t)
            throws alice.logictuple.exceptions.InvalidLogicTupleException;

    abstract protected String getTupleKey2(LogicTuple t)
            throws alice.logictuple.exceptions.InvalidLogicTupleException;

    private LTEntry createEntry(final LogicTuple t)
            throws InvalidLogicTupleException {
        return new LTEntry(this.getTupleKey1(t), this.getTupleKey2(t), t);
    }

}
