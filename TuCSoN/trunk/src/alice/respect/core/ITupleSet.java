package alice.respect.core;

import java.util.Iterator;

import alice.logictuple.LogicTuple;

/**
 * This interface representing a Tuple Set.
 * 
 * 
 * @author ????
 * @version 1.0
 * 
 * @author scicora
 * @version 1.1
 */
public interface ITupleSet {

    /**
     * Add a logic tuple to tuple set.
     * 
     * @param t
     *            a {@code LogicTuple} to add.
     */
    public void add(LogicTuple t);

    /**
     * Begins a transaction section
     * 
     * Every operation on multiset can be undone
     */
    public void beginTransaction();

    /**
     * Removes all element from Tuple Set.
     */
    public void empty();

    /**
     * Ends a transaction section specifying if operations must be committed or
     * undone
     * 
     * @param commit
     *            if {@code true} the operations are committed, else they are
     *            undone and the multiset is rolled back to the state before the
     *            <code>beginTransaction</code> invocation
     */
    public void endTransaction(boolean commit);

    public Iterator<LogicTuple> getIterator();

    public LogicTuple getMatchingTuple(LogicTuple templ);

    /**
     * Returns {@code true} if the tuple set contains no elements.
     * 
     * @return {@code true} if the tuple set contains no elements.
     */
    public boolean isEmpty();

    /**
     * Tells whether there are changes in the tuple multi-set during a
     * transaction
     * 
     * @return true if the ongoing transaction made any changes to the tuple
     *         multi-set
     */
    public boolean operationsPending();

    public LogicTuple readMatchingTuple(LogicTuple templ);

    /**
     * Remove a logic tuple from tuple set.
     * 
     * @param t
     *            a {@code LogicTuple} to remove.
     */
    public void remove(LogicTuple t);

    /**
     * Returns the number of elements in this tuple set.
     * 
     * @return the number of elements in this tuple set
     */
    public int size();

    public LogicTuple[] toArray();

    /**
     * Provides a representation of the tuple multi-set in the form of a String
     * containing a prolog theory.
     * 
     * @return a textual representation in the form of a prolog theory.
     */
    public String toString();

}
