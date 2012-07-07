/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.respect.core;

import java.util.*;

import alice.logictuple.LogicTuple;
import alice.tuprolog.Var;
/**
 * Class representing a Tuple Set.
 */
public class TupleSet  {

    private LinkedList<LogicTuple> tuples;
    // to manage transactions
    private LinkedList<LogicTuple> tAdded;
    private LinkedList<LogicTuple> tRemoved;
    private boolean transaction;


    public TupleSet(){
		tuples=new LinkedList<LogicTuple>();
		tAdded=new LinkedList<LogicTuple>();
		tRemoved=new LinkedList<LogicTuple>();
		transaction=false;
    }

    public void add(LogicTuple t){
        tuples.add(t);
        if (transaction){
            tAdded.add(t);
        }
    }

    public void remove(LogicTuple t){
        tuples.remove(t);
        if (transaction){
            tRemoved.add(t);
        }
    }

    public boolean isEmpty(){
        return tuples.isEmpty();
    }

    public int size(){
        return tuples.size();
    }

    /**
     * Begins a transaction section
     *
     * Every operation on multiset can be undone
     */
    public void beginTransaction(){
        transaction=true;
        tAdded.clear();
        tRemoved.clear();
    }

    /**
     * Ends a transaction section specifying
     * if operations must be committed or undone
     *
     * @param commit if <code>true</code> the operations are committed, else
     *               they are undone and the multiset is rolled back to the
     *               state before the <code>beginTransaction</code> invocation
     */
    public void endTransaction(boolean commit){
        if (!commit){
            //System.out.println("[ tspace ] roll back!");
            Iterator<LogicTuple> it = tAdded.listIterator();
            while (it.hasNext()) {
            	//System.out.println("[tspace] removing "+(LogicTuple)list[i]);
                tuples.remove(it.next());
            }
            it=tRemoved.listIterator();
			while (it.hasNext()) {
                //System.out.println("[tspace] adding "+(LogicTuple)list[i]);
                tuples.add(it.next());
            }
        }
        transaction=false;
        tAdded.clear();
        tRemoved.clear();
    }

    public void    empty(){
        tuples.clear();
    }

    public LogicTuple getMatchingTuple(LogicTuple templ){
        if (templ==null)
            return null;
        //System.out.println("TSET: getMatchingTuple "+templ);
        ListIterator<LogicTuple> l=tuples.listIterator();
        while (l.hasNext()){
            LogicTuple tu=l.next();
            //System.out.println("-- "+tu);
            if (templ.match(tu)){
                //System.out.println(">> FOUND"+tu);
                l.remove();
                if (transaction){
                    tRemoved.add(tu);
                }
            	/* copy needs to be returned as tu refers to a tuple in
            	 * tuple set and it also gets stored in operation 
            	 * result thus creating aliasing, note that getMatchingTuple
            	 * should remove the tuple but as it is used in transactional
            	 * contexts the operation can be potentially aborted.
            	 */
                AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
                return new LogicTuple(tu.toTerm().copyGoal(v, 0));
            }
        }
        return null;
    }

    public LogicTuple readMatchingTuple(LogicTuple templ){
        if (templ==null)
            return null;
        //System.out.println("TSET: readMatchingTuple "+templ);
        ListIterator<LogicTuple> l=tuples.listIterator();
        while (l.hasNext()){
            LogicTuple tu=l.next();
            //System.out.println("-- "+tu);
            if (templ.match(tu)){
                //System.out.println(">> FOUND"+tu);
            	/* copy needs to be returned as tu referes to a tuple in
            	 * tuple set and it also gets stored in operation 
            	 * result thus creating aliasing
            	 */             	
            	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
                return new LogicTuple(tu.toTerm().copyGoal(v, 0));
            }
        }
        return null;
    }

    public Iterator<LogicTuple> getIterator(){
        return tuples.listIterator();
    }

    public LogicTuple[] toArray() {
    	return tuples.toArray(new LogicTuple[tuples.size()]);
    }
    
    /**
     * Provides a representation of the tuple multi-set in
     * the form of a String containing a prolog theory.
     * @return a textual representation in the form of a prolog theory.    
     */
    public String toString() {
    	String str = "";
    	for(LogicTuple t: tuples){
    		str += t.toString() + ".\n";
    	}
    	return str;
    }

	/**
	 * Tells whether there are changes in the tuple multi-set during a transaction  
	 * 
	 * @return true if the ongoing transaction made any changes to the tuple multi-set
	 */
    public boolean operationsPending() {
		if(tAdded.isEmpty() && tRemoved.isEmpty())
			return false;
		else return true;
	}

}

