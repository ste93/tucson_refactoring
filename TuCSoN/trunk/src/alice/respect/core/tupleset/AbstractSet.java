package alice.respect.core.tupleset;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class AbstractSet<E> {

	protected LinkedList<E> elements;
	protected LinkedList<E> elAdded;
	protected LinkedList<E> elRemoved;
	private boolean transaction;

	public AbstractSet() {
		elements = new LinkedList<E>();
		elAdded = new LinkedList<E>();
		elRemoved = new LinkedList<E>();
		transaction = false;
	}

	public void add(E e) {
		elements.add(e);
		if (transaction) {
			elAdded.add(e);
		}
	}

	public void remove(E e) {
		elements.remove(e);
		if (transaction) {
			elRemoved.add(e);
		}
	}

	public void empty() {
		elements.clear();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public E get() {
		E e = elements.removeFirst();
		if (transaction) {
			elRemoved.add(e);
		}
		return e;
	}

	public Iterator<E> getIterator() {
		return elements.listIterator();
	}

	public int size() {
		return elements.size();
	}

	/**
	 * Begins a transaction section
	 * 
	 * Every operation on multiset can be undone
	 */
	public void beginTransaction() {
		transaction = true;
		elAdded.clear();
		elRemoved.clear();
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
	public void endTransaction(boolean commit) {
		if (!commit) {
			Iterator<E> it = elAdded.listIterator();
			while (it.hasNext()) {
				elements.remove(it.next());
			}
			it = elRemoved.listIterator();
			while (it.hasNext()) {
				elements.add(it.next());
			}
		}
		transaction = false;
		elAdded.clear();
		elRemoved.clear();
	}

	public boolean operationsPending() {
		if (elAdded.isEmpty() && elRemoved.isEmpty())
			return false;
		else
			return true;
	}

	public abstract E[] toArray();

}
