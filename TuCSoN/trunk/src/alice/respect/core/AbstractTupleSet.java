package alice.respect.core;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import alice.logictuple.LogicTuple;
import alice.respect.core.collection.BucketMap;
import alice.tuprolog.Var;

public abstract class AbstractTupleSet implements TupleSet2 {

	protected BucketMap<String, LogicTuple> tuples;
	protected LinkedList<LogicTupleEntry> tAdded;
	protected LinkedList<LogicTupleEntry> tRemoved;
	protected boolean transaction;

	protected abstract String getKey(LogicTuple t);

	public AbstractTupleSet() {
		super();
	}

	@Override
	public void add(LogicTuple t) {
		// TODO in questo caso l'eccezione viene gestita nella getKey
		// Cosa fare in caso di problemi?
		tuples.put(createEntry(t));
		if (transaction)
			tAdded.add(createEntry(t));
	}

	@Override
	public void remove(LogicTuple t) {
		// TODO in questo caso l'eccezione viene gestita nella getKey
		// Cosa fare in caso di problemi?
		tuples.remove(createEntry(t));
		if (transaction)
			tRemoved.add(createEntry(t));
	}

	@Override
	public boolean isEmpty() {
		return tuples.isEmpty();
	}

	@Override
	public int size() {
		return tuples.size();
	}

	@Override
	public void beginTransaction() {
		transaction = true;
		tAdded.clear();
		tRemoved.clear();
	}

	@Override
	public void endTransaction(boolean commit) {
		if (!commit) {
			Iterator<LogicTupleEntry> it = tAdded.listIterator();
			while (it.hasNext()) {
				tuples.remove(it.next());
			}
			it = tRemoved.listIterator();
			while (it.hasNext()) {
				tuples.put(it.next());
			}
		}
		transaction = false;
		tAdded.clear();
		tRemoved.clear();
	}

	@Override
	public void empty() {
		tuples.clear();
	}

	@Override
	public LogicTuple getMatchingTuple(LogicTuple templ) {
		if (templ == null)
			return null;
		Iterator<LogicTuple> l = tuples.iterator();
		while (l.hasNext()) {
			LogicTuple tu = l.next();
			if (templ.match(tu)) {
				l.remove();
				if (transaction)
					tRemoved.add(createEntry(tu));
				AbstractMap<Var, Var> v = new LinkedHashMap<Var, Var>();
				return new LogicTuple(tu.toTerm().copyGoal(v, 0));
			}
		}
		return null;
	}

	@Override
	public LogicTuple readMatchingTuple(LogicTuple templ) {
		if (templ == null)
			return null;
		Iterator<LogicTuple> l = tuples.iterator();
		while (l.hasNext()) {
			LogicTuple tu = l.next();
			if (templ.match(tu)) {
				AbstractMap<Var, Var> v = new LinkedHashMap<Var, Var>();
				return new LogicTuple(tu.toTerm().copyGoal(v, 0));
			}
		}
		return null;
	}

	@Override
	public Iterator<LogicTuple> getIterator() {
		return tuples.iterator();
	}

	@Override
	public LogicTuple[] toArray() {
		return tuples.toArray(new LogicTuple[tuples.size()]);
	}

	@Override
	public String toString() {
		String str = "";
		for (LogicTuple t : tuples)
			str += t.toString() + ".\n";
		return str;
	}

	@Override
	public boolean operationsPending() {
		if (tAdded.isEmpty() && tRemoved.isEmpty())
			return false;
		else
			return true;
	}

	private LogicTupleEntry createEntry(LogicTuple t) {
		return new LogicTupleEntry(getKey(t), t);
	}

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

		final String key;
		final LogicTuple value;

		LogicTupleEntry(String k, LogicTuple v) {
			this.key = k;
			this.value = v;
		}

		@Override
		public String getKey() {
			return key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LogicTuple getValue() {
			return value;
		}

		/**
		 * <p>
		 * NOTE: This method is not implemented, the value of this class can not be
		 * changed after initialization.
		 * </p>
		 * {@inheritDoc}
		 * 
		 * @throws NotImplementedException
		 *             on each invocation of method.
		 */
		@Override
		public LogicTuple setValue(LogicTuple value) {
			throw new NotImplementedException();
		}

	}

}
