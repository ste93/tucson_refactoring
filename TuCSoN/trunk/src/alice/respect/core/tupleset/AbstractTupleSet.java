package alice.respect.core.tupleset;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.core.collection.DoubleKeyMVMap;
import alice.respect.core.collection.MVMap;
import alice.tuprolog.Var;

public abstract class AbstractTupleSet implements TupleSet2 {

	protected DoubleKeyMVMap<String, String, LogicTuple> tuples;
	protected LinkedList<LTEntry> tAdded;
	protected LinkedList<LTEntry> tRemoved;
	protected boolean transaction;



	public AbstractTupleSet() {
		super();
	}

	abstract protected String getTupleKey1(LogicTuple t) throws alice.logictuple.exceptions.InvalidLogicTupleException;

	abstract protected String getTupleKey2(LogicTuple t) throws alice.logictuple.exceptions.InvalidLogicTupleException;

	
	@Override
	public void add(LogicTuple t) {
		try {
			LTEntry e = createEntry(t);
			tuples.put(e.getKey1(), e.getKey2(), e.getValue());
			if (transaction)
				tAdded.add(e);
		} catch (InvalidLogicTupleException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void remove(LogicTuple t) {
		try {
			LTEntry e = createEntry(t);
			boolean res = tuples.remove(e.getKey1(), e.getKey2(), e.getValue());
			if (res) {
				if (transaction)
					tRemoved.add(createEntry(t));
			}
		} catch (InvalidLogicTupleException e1) {
			System.out.println(t.toString());
			e1.printStackTrace();
		}
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
			Iterator<LTEntry> it = tAdded.listIterator();
			while (it.hasNext()) {
				LTEntry e = it.next();
				tuples.remove(e.getKey1(), e.getKey2(), e.getValue());
			}
			it = tRemoved.listIterator();
			while (it.hasNext()) {
				LTEntry e = it.next();
				tuples.put(e.getKey1(), e.getKey2(), e.getValue());
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

		Iterator<LogicTuple> l;
		try {
			String key2 = getTupleKey2(templ);
			if (key2.equals("VAR"))
				l = tuples.get(getTupleKey1(templ)).iterator();
			else {
				MVMap<String, LogicTuple> map = tuples.get(getTupleKey1(templ));
				if (map.get("VAR").size() > 0)
					l = map.iterator();
				else
					l = map.get(key2).iterator();
			}

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
		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public LogicTuple readMatchingTuple(LogicTuple templ) {
		if (templ == null)
			return null;

		Iterator<LogicTuple> l;
		try {
			String key2 = getTupleKey2(templ);
			if (key2.equals("VAR"))
				l = tuples.get(getTupleKey1(templ)).iterator();
			else {
				MVMap<String, LogicTuple> map = tuples.get(getTupleKey1(templ));
				if (map.get("VAR").size() > 0)
					l = map.iterator();
				else
					l = map.get(key2).iterator();
			}

			while (l.hasNext()) {
				LogicTuple tu = l.next();
				if (templ.match(tu)) {
					AbstractMap<Var, Var> v = new LinkedHashMap<Var, Var>();
					return new LogicTuple(tu.toTerm().copyGoal(v, 0));
				}
			}
		} catch (InvalidLogicTupleException e1) {
			e1.printStackTrace();
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
		return tuples.toString();
	}

	@Override
	public boolean operationsPending() {
		if (tAdded.isEmpty() && tRemoved.isEmpty())
			return false;
		else
			return true;
	}

	private LTEntry createEntry(LogicTuple t) throws InvalidLogicTupleException {
		return new LTEntry(getTupleKey1(t), getTupleKey2(t), t);
	}

	protected class LTEntry {

		final String key1;
		final String key2;
		final LogicTuple value;

		LTEntry(String keyOuter, String keyInner, LogicTuple v) {
			this.key1 = keyOuter;
			this.key2 = keyInner;
			this.value = v;
		}

		public String getKey1() {
			return key1;
		}

		public String getKey2() {
			return key2;
		}

		public LogicTuple getValue() {
			return value;
		}

		@Override
		public String toString() {
			return key1 + "\t" + key2 + "\t" + value.toString();
		}

	}

}
