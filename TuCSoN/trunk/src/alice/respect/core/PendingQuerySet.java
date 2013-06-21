/*
 * TupleSetImpl.java Copyright 2000-2001-2002 aliCE team at deis.unibo.it This
 * software is the proprietary information of deis.unibo.it Use is subject to
 * license terms.
 */
package alice.respect.core;

import java.util.Iterator;
import java.util.LinkedList;

import alice.tuplecentre.core.Event;

/**
 * Pending Query Set.
 * 
 * @author aricci
 */
public class PendingQuerySet {

    private final LinkedList<Event> evAdded;
    private final LinkedList<Event> events;
    private final LinkedList<Event> evRemoved;
    private boolean transaction;

    public PendingQuerySet() {
        this.events = new LinkedList<Event>();
        this.evAdded = new LinkedList<Event>();
        this.evRemoved = new LinkedList<Event>();
        this.transaction = false;
    }

    public void add(final alice.tuplecentre.core.Event t) {
        this.events.add(t);
        if (this.transaction) {
            this.evAdded.add(t);
        }
    }

    /**
     * Begins a transaction section
     * 
     * Every operation on multiset can be undone
     */
    public void beginTransaction() {
        this.transaction = true;
        this.evAdded.clear();
        this.evRemoved.clear();
    }

    public void empty() {
        this.events.clear();
    }

    /**
     * Ends a transaction section specifying if operations must be committed or
     * undone
     * 
     * @param commit
     *            if <code>true</code> the operations are committed, else they
     *            are undone and the multiset is rolled back to the state before
     *            the <code>beginTransaction</code> invocation
     */
    public void endTransaction(final boolean commit) {
        if (!commit) {
            Iterator<? extends Event> it = this.evAdded.listIterator();
            while (it.hasNext()) {
                this.events.remove(it.next());
            }
            it = this.evRemoved.listIterator();
            while (it.hasNext()) {
                this.events.add(it.next());
            }
        }
        this.transaction = false;
        this.evAdded.clear();
        this.evRemoved.clear();
    }

    public alice.tuplecentre.core.Event get() {
        final alice.tuplecentre.core.Event ev = this.events.removeFirst();
        if (this.transaction) {
            this.evRemoved.add(ev);
        }
        return ev;
    }

    public Iterator<? extends Event> getIterator() {
        return this.events.listIterator();
    }

    public boolean isEmpty() {
        return this.events.isEmpty();
    }

    public void remove(final alice.tuplecentre.core.Event t) {
        this.events.remove(t);
        if (this.transaction) {
            this.evRemoved.add(t);
        }
    }

    public boolean removeEventOfOperation(final long opId) {
        final Iterator<? extends Event> it = this.events.listIterator();
        while (it.hasNext()) {
            final alice.tuplecentre.core.Event ev = it.next();
            if (ev.getSimpleTCEvent().getId() == opId) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void removeEventsOf(final alice.tuplecentre.api.AgentId id) {
        final Iterator<? extends Event> it = this.events.listIterator();
        while (it.hasNext()) {
            final alice.tuplecentre.core.Event ev = it.next();
            if (ev.getSource().toString().equals(id.toString())) {
                it.remove();
            }
        }
    }

    public int size() {
        return this.events.size();
    }

    public alice.tuplecentre.core.Event[] toArray() {
        final int size = this.events.size();
        final alice.tuplecentre.core.Event[] evArray =
                new alice.tuplecentre.core.Event[size];
        for (int i = 0; i < size; i++) {
            evArray[i] = this.events.get(i);
        }
        return evArray;
    }

}
