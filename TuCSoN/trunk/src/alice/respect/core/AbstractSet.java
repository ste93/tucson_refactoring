package alice.respect.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 22/lug/2013
 * 
 * @param <E>
 *            the type of the set
 */
public abstract class AbstractSet<E> {

    /**
     * 
     */
    protected List<E> elAdded;
    /**
     * 
     */
    protected List<E> elements;
    /**
     * 
     */
    protected List<E> elRemoved;
    private boolean transaction;

    /**
     * 
     */
    public AbstractSet() {
        this.elements = new LinkedList<E>();
        this.elAdded = new LinkedList<E>();
        this.elRemoved = new LinkedList<E>();
        this.transaction = false;
    }

    /**
     * 
     * @param e
     *            the element to be added
     */
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

    /**
     * 
     */
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

    /**
     * 
     * @return the element taken
     */
    public E get() {
        final E e = this.elements.remove(0);
        if (this.transaction) {
            this.elRemoved.add(e);
        }
        return e;
    }

    /**
     * 
     * @return an Iterator through this set
     */
    public Iterator<E> getIterator() {
        return this.elements.listIterator();
    }

    /**
     * 
     * @return wether this Set is empty or not
     */
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /**
     * 
     * @return wether this set has any operation waiting to be served
     */
    public boolean operationsPending() {
        if (this.elAdded.isEmpty() && this.elRemoved.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @param e
     *            the element to be removed from this Set
     */
    public void remove(final E e) {
        this.elements.remove(e);
        if (this.transaction) {
            this.elRemoved.add(e);
        }
    }

    /**
     * 
     * @return the size of this Set
     */
    public int size() {
        return this.elements.size();
    }

    /**
     * 
     * @return the Array representation of this Set
     */
    public abstract E[] toArray();

}
