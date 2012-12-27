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

import alice.logictuple.BioTuple;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tuprolog.Var;

/**
 * Class representing a Tuple Set: it is composed by a multiset of LogicTuple and a multiset of BioTuple. 
 */
public class TupleSet  {

	//LogicTuple multiset
    private LinkedList<LogicTuple> tuples;
    private LinkedList<LogicTuple> tAdded;
    private LinkedList<LogicTuple> tRemoved;
    
    //BioTuple multiset
    private LinkedList<BioTuple> bioTuples;
    private LinkedList<BioTuple> bioTAdded;
    private LinkedList<BioTuple> bioTRemoved;
    
    private boolean transaction;

    public TupleSet(){
    	//LogicTuple
		tuples=new LinkedList<LogicTuple>();
		tAdded=new LinkedList<LogicTuple>();
		tRemoved=new LinkedList<LogicTuple>();
		
		//BioTuple
		bioTuples = new LinkedList<BioTuple>();
		bioTAdded = new LinkedList<BioTuple>();
		bioTRemoved = new LinkedList<BioTuple>();
		
		transaction=false;
    }

    public void add(LogicTuple t){
    	//bio changes
    	
    	if(t instanceof BioTuple){
    		
        	if(bioTuples.size()==0)
        		bioTuples.add((BioTuple)t);
        	
        	else{
	    		ListIterator<BioTuple> l=bioTuples.listIterator();
	            while (l.hasNext()){
	                BioTuple tu=l.next();
	                if (t.match(tu)){
	                    l.remove();
	                    long oldValue = tu.getMultiplicity();
	                    tu.setMultiplicity(oldValue + ((BioTuple) t).getMultiplicity());
	                    l.add(tu);
	                    return;
	                }
	            }
	    		bioTuples.add((BioTuple)t);
	    		if (transaction)
	                bioTAdded.add((BioTuple)t);
        	}
    	}else{
	        tuples.add(t);
	        if (transaction)
	            tAdded.add(t);
    	}
    }

    public void remove(LogicTuple t){
    	//bio changes
    	if(t instanceof BioTuple){
    		bioTuples.remove((BioTuple)t);
	        if (transaction)
	            bioTRemoved.add((BioTuple)t);
    	}else{
	        tuples.remove(t);
	        if (transaction)
	            tRemoved.add(t);
    	}
    }

    //bio changes
    public boolean isEmpty(){
        return (tuples.isEmpty() && bioTuples.isEmpty());
    }
    
    //bio added
    public boolean isEmptyLogicSet(){
    	return tuples.isEmpty();
    }
    public boolean isEmptyBioSet(){
    	return bioTuples.isEmpty();
    }

    //bio changes
    public int size(){
        return tuples.size() + bioTuples.size();
    }
    
    //bio added
    public int sizeLogicSet(){
    	return tuples.size();
    }
    public int sizeBioSet(){
    	return bioTuples.size();
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
            Iterator<LogicTuple> it = tAdded.listIterator();
            while (it.hasNext())
                tuples.remove(it.next());
            it=tRemoved.listIterator();
			while (it.hasNext())
                tuples.add(it.next());
        }
        transaction=false;
        tAdded.clear();
        tRemoved.clear();
    }

    //bio changes
    public void empty(){
        tuples.clear();
        bioTuples.clear();
    }
    
    //bio added
    public void emptyLogicSet(){
        tuples.clear();
    }
    public void emptyBioSet(){
        bioTuples.clear();
    }

    //in & bio inv
    public LogicTuple getMatchingTuple(LogicTuple templ){
        if (templ==null)
        	return null;
        //bio changes
        if(templ instanceof BioTuple){
        	ListIterator<BioTuple> l=bioTuples.listIterator();
            while (l.hasNext()){
                BioTuple tu=l.next();
                if (templ.match(tu)){
                    l.remove();
                    if (transaction)
                        bioTRemoved.add(tu);
                    AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
                    return new BioTuple(tu.toTerm().copyGoal(v, 0),tu.getMultiplicity());
                }
            }
        }else{
	        ListIterator<LogicTuple> l=tuples.listIterator();
	        while (l.hasNext()){
	            LogicTuple tu=l.next();
	            if (templ.match(tu)){
	                l.remove();
	                if (transaction)
	                    tRemoved.add(tu);
	                AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	                return new LogicTuple(tu.toTerm().copyGoal(v, 0));
	            }
	        }
        }
        return null;
    }

    //rd & bio rdv
    public LogicTuple readMatchingTuple(LogicTuple templ){
        if (templ==null)
            return null;
        //bio changes
        if(templ instanceof BioTuple){
        	ListIterator<BioTuple> l=bioTuples.listIterator();
            while (l.hasNext()){
                BioTuple tu=l.next();
                if (templ.match(tu)){
                	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
                    return new BioTuple(tu.toTerm().copyGoal(v, 0),tu.getMultiplicity());
                }
            }
        }else{
        	ListIterator<LogicTuple> l=tuples.listIterator();
            while (l.hasNext()){
                LogicTuple tu=l.next();
                if (templ.match(tu)){
                	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
                    return new LogicTuple(tu.toTerm().copyGoal(v, 0));
                }
            }
        }
        return null;
    }

    //bio added
    //bio in
    public BioTuple getMatchingTupleGround(BioTuple templ){
        if (templ==null)
            return null;
        long multTempl = templ.getMultiplicity();
    	ListIterator<BioTuple> l=bioTuples.listIterator();
    	LinkedList<BioTuple> tmp = new LinkedList<BioTuple>();
        long multTot = 0;
        
        //find matching tuples
    	while (l.hasNext()){
            BioTuple tu=l.next();
            long multTu = tu.getMultiplicity();
            if(multTempl<=multTu){
	            if (templ.match(tu)){
	            	multTot += tu.getMultiplicity();
	            	tmp.add(tu);
	            }
            }
        }
    	
    	//check list size of matching tuples
        if(tmp.size() == 0) return null;
        else if(tmp.size() == 1){	//if only one tuple matches the template
        	BioTuple t = tmp.getFirst();
        	while(l.hasPrevious()){		//find that tuple into the ListIterator (backward navigation)	
        		BioTuple tr = l.previous();
        		
				if(t.toString().equals(tr.toString())){
					long multTr = tr.getMultiplicity();
					if(multTr == multTempl){	//if it's required the entire tuple multiplicity
						l.remove();
					}else if(multTr > multTempl){	//if it's required a part of tuple multiplicity
						l.remove();
						tr.setMultiplicity(multTr-multTempl);
						l.add(tr);
					}
					AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	                return new BioTuple(tr.toTerm().copyGoal(v, 0),multTempl);
				}
        	}
        }else if(tmp.size()>1){	//if two or more tuples match the template
        	long r = (long) (Math.random() * multTot);
        	//LinkedList<BioTuple> tmp2 = sortBio(tmp);	//matching list sorting - unnecessary!!
        	long counter = 0;
        	int i = 0;
        	BioTuple tuple;
            for(BioTuple t : tmp){
            	tuple = tmp.get(i);
            	i++;
            	counter += tuple.getMultiplicity(); 
            	if(counter >= r){
            		while(l.hasPrevious()){		//find that tuple into the ListIterator (backward navigation)	
                		BioTuple tr = l.previous();
                		
    					if(tuple.toString().equals(tr.toString())){
    						long multTr = tr.getMultiplicity();
    						if(multTr == multTempl){	//if it's required the entire tuple multiplicity
    							l.remove();
    						}else if(multTr > multTempl){	//if it's required a part of tuple multiplicity
    							l.remove();
    							tr.setMultiplicity(multTr-multTempl);
    							l.add(tr);
    						}
    						AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    		                return new BioTuple(tr.toTerm().copyGoal(v, 0),multTempl);
    					}
                	}
            	}
            }
        }
        return null;
    }

    //bio rd
    public BioTuple readMatchingTupleGround(BioTuple templ){
        if (templ==null)
            return null;
        long multTempl = templ.getMultiplicity();
    	ListIterator<BioTuple> l=bioTuples.listIterator();
    	LinkedList<BioTuple> tmp = new LinkedList<BioTuple>();
        long multTot = 0;
        
        //find matching tuples
    	while (l.hasNext()){
            BioTuple tu=l.next();
            if (templ.match(tu)){
            	multTot += tu.getMultiplicity();
            	tmp.add(tu);
            }
        }
    	
    	//check list size of matching tuples
        if(tmp.size() == 0) return null;
        else if(tmp.size() == 1){
        	BioTuple t = tmp.getFirst();
        	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
            return new BioTuple(t.toTerm().copyGoal(v, 0),multTempl);
        }else if(tmp.size()>1){	//if two or more tuples match the template
        	long r = (long)(Math.random() * multTot);
        	//LinkedList<BioTuple> tmp2 = sortBio(tmp);	//matching list sorting - unnecessary!!
        	long counter = 0;
        	int i = 0;
        	BioTuple tuple;
            for(BioTuple t : tmp){
            	tuple = tmp.get(i);
            	i++;
            	counter += tuple.getMultiplicity(); 
            	if(counter >= r){
            		AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
                    return new BioTuple(tuple.toTerm().copyGoal(v, 0),multTempl);
            	}
            }
        }
        return null;
    }
    
    //BIO added
    /*unnecessary!!
    private LinkedList<BioTuple> sortBio(LinkedList<BioTuple> tmp) {
		if(tmp.size()<=1){ 
			return tmp;
		}else{
			BioTuple pivot = tmp.getFirst();
			LinkedList<BioTuple> less = new LinkedList<BioTuple>();
			LinkedList<BioTuple> greater = new LinkedList<BioTuple>();
			for(BioTuple t : tmp){
				if(t.getMultiplicity() <= pivot.getMultiplicity()){
					less.add(t);
				}else{
					greater.add(t);
				}
			}
			LinkedList<BioTuple> a1 = sortBio(less);
			LinkedList<BioTuple> a2 = sortBio(greater);
			a1.add(pivot);
			a1.addAll(a2);
			return a1;
		}
	}
	*/

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
    	for(LogicTuple t: tuples)
    		str += t.toString() + ".\n";
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
		else
			return true;
	}

}

