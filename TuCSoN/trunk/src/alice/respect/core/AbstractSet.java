package alice.respect.core;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractSet<E> {

    protected LinkedList<E> elAdded;
    protected LinkedList<E> elements;
    protected LinkedList<E> elRemoved;
    private boolean transaction;

    public AbstractSet() {
        this.elements = new LinkedList<E>();
        this.elAdded = new LinkedList<E>();
        this.elRemoved = new LinkedList<E>();
        this.transaction = false;
    }

    public void add(final E e) {
        this.elements.add(e);
        if (this.transaction) {
            this.elAdded.add(e);
        }
    }

    /**
     * Begins a transaction section
     * 
     * Every operation on multiset can be undone
     */
    public void beginTransaction() {
        this.transaction = true;
        this.elAdded.clear();
        this.elRemoved.clear();
    }

    public void empty() {
        this.elements.clear();
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
            Iterator<E> it = this.elAdded.listIterator();
            while (it.hasNext()) {
                this.elements.remove(it.next());
            }
            it = this.elRemoved.listIterator();
            while (it.hasNext()) {
                this.elements.add(it.next());
            }
        }
        this.transaction = false;
        this.elAdded.clear();
        this.elRemoved.clear();
    }

    public E get() {
        final E e = this.elements.removeFirst();
        if (this.transaction) {
            this.elRemoved.add(e);
        }
        return e;
    }

    public Iterator<E> getIterator() {
        return this.elements.listIterator();
    }

    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    public boolean operationsPending() {
        if (this.elAdded.isEmpty() && this.elRemoved.isEmpty()) {
            return false;
        }
        return true;
    }

    public void remove(final E e) {
        this.elements.remove(e);
        if (this.transaction) {
            this.elRemoved.add(e);
        }
    }

    public int size() {
        return this.elements.size();
    }

    public abstract E[] toArray();

}
