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
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidMultiplicityException;
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

    //out
    public void add(LogicTuple t){
    	
    	if(t instanceof BioTuple){
    	
	    	BioTuple bioT = (BioTuple) t;
	    	
	    	if(bioTuples.size()==0)
	    		bioTuples.add(bioT);
	    	
	    	else{
	    		ListIterator<BioTuple> l=bioTuples.listIterator();
	            while (l.hasNext()){
	                BioTuple tu=l.next();
	                if (bioT.match(tu)){
	                    l.remove();
	                    long oldValue = tu.getMultiplicity();
	                    try {
							tu.setMultiplicity(oldValue + (bioT).getMultiplicity());
						} catch (InvalidMultiplicityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    l.add(tu);
	                    return;
	                }
	            }
	    		bioTuples.add(bioT);
	    		if (transaction)
	                bioTAdded.add(bioT);
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
        
        bioTAdded.clear();
        bioTRemoved.clear();
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
        
        if (!commit){
            Iterator<BioTuple> it = bioTAdded.listIterator();
            while (it.hasNext())
                bioTuples.remove(it.next());
            it=bioTRemoved.listIterator();
			while (it.hasNext())
				bioTuples.add(it.next());
        }
        
        transaction=false;
        
        tAdded.clear();
        tRemoved.clear();
        
        bioTAdded.clear();
        bioTRemoved.clear();
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

    
    //in
    public LogicTuple getMatchingTuple(LogicTuple t){

    	if (t==null)
            return null;
    	
    	if(t instanceof BioTuple){
    	
	    	BioTuple templ = (BioTuple) t;
	    	
	    	
	    	ListIterator<BioTuple> l=bioTuples.listIterator();
	    	
	    	if(templ.isMultGround()){//if multiplicity is specified
	    		long multTempl = templ.getMultiplicity();
			    while (l.hasNext()){
			        BioTuple tu=l.next();
			        long multTu = tu.getMultiplicity();
			        if (templ.match(tu) && multTu >= multTempl){
			        	if(multTu==multTempl){
			        		l.remove();
			        		if(transaction)
			        			bioTRemoved.add(tu);
			        	}
			        	else{
			        		l.remove();
			        		if(transaction)
			        			bioTRemoved.add(tu);
			        		try {
								tu.setMultiplicity(multTu-multTempl);
							} catch (InvalidMultiplicityException e) {
								e.printStackTrace();
							}
			        		l.add(tu);
			        		if(transaction)
			        			bioTAdded.add(tu);
			        	}
			            AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
			            try {
							return new BioTuple(tu.toTerm(multTempl).copyGoal(v, 0));
						} catch (InvalidMultiplicityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    }   
	    	}else{//if multiplicity is not specified
			    while (l.hasNext()){
			        BioTuple tu=l.next();
			        if (templ.match(tu)){
			            l.remove();
			            if(transaction)
		        			bioTRemoved.add(tu);
			            AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
			            try {
							return new BioTuple(tu.toTerm().copyGoal(v, 0));
						} catch (InvalidMultiplicityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    } 
	    	}
    	}else{
    		ListIterator<LogicTuple> l=tuples.listIterator();
	        while (l.hasNext()){
	            LogicTuple tu=l.next();
	            if (t.match(tu)){
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

    
    //rd
    public LogicTuple readMatchingTuple(LogicTuple t){
    	
    	if (t==null)
            return null;
    	
    	if(t instanceof BioTuple){
    	
	    	BioTuple templ = (BioTuple) t;
	    	
	    	ListIterator<BioTuple> l=bioTuples.listIterator();
	
	    	if(templ.isMultGround()){//if multiplicity is specified
	    		long multTempl = templ.getMultiplicity();
			    while (l.hasNext()){
			        BioTuple tu=l.next();
			        long multTu = tu.getMultiplicity();
			        if (templ.match(tu) && multTu >= multTempl){
			        	
			            AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
			            try {
							return new BioTuple(tu.toTerm(multTempl).copyGoal(v, 0));
						} catch (InvalidMultiplicityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    }   
	    	}else{//if multiplicity is not specified
			    while (l.hasNext()){
			        BioTuple tu=l.next();
			        if (templ.match(tu)){
			            AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
			            try {
							return new BioTuple(tu.toTerm().copyGoal(v, 0));
						} catch (InvalidMultiplicityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
			    } 
	    	}
    	}else{
    		ListIterator<LogicTuple> l=tuples.listIterator();
            while (l.hasNext()){
                LogicTuple tu=l.next();
                if (t.match(tu)){
                	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
                    return new LogicTuple(tu.toTerm().copyGoal(v, 0));
                }
            }
    	}
	    return null;
    }

    
    //uniform in without multiplicity
    public BioTuple getUniformMatchingTuple(BioTuple templ){
        if (templ==null)
        	return null;
        
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
        else if(tmp.size() == 1){	//if only one tuple matches the template
        	BioTuple t = tmp.getFirst();
        	while(l.hasPrevious()){		//find that tuple into the ListIterator (backward navigation)	
        		BioTuple tr = l.previous();
				if(t.toString().equals(tr.toString())){
					l.remove();
					if(transaction)
	        			bioTRemoved.add(tr);
					AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	                try {
						return new BioTuple(tr.toTerm().copyGoal(v, 0));
					} catch (InvalidMultiplicityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
    						l.remove();
    						if(transaction)
			        			bioTRemoved.add(tr);
    						AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    		                try {
								return new BioTuple(tr.toTerm().copyGoal(v, 0));
							} catch (InvalidMultiplicityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					}
                	}
            	}
            }
        }
    	
        return null;
        
    }
    

    //uniform in with multiplicity
    public BioTuple getUniformMatchingTupleGround(BioTuple templ){
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
						if(transaction)
		        			bioTRemoved.add(tr);
					}else if(multTr > multTempl){	//if it's required a part of tuple multiplicity
						l.remove();
						if(transaction)
		        			bioTRemoved.add(tr);
						try {
							tr.setMultiplicity(multTr-multTempl);
						} catch (InvalidMultiplicityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						l.add(tr);
						if(transaction)
		        			bioTAdded.add(tr);
					}
					AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	                try {
						return new BioTuple(tr.toTerm(multTempl).copyGoal(v, 0));
					} catch (InvalidMultiplicityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
    							if(transaction)
    			        			bioTRemoved.add(tr);
    						}else if(multTr > multTempl){	//if it's required a part of tuple multiplicity
    							l.remove();
    							if(transaction)
    			        			bioTRemoved.add(tr);
    							try {
									tr.setMultiplicity(multTr-multTempl);
								} catch (InvalidMultiplicityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
    							l.add(tr);
    							if(transaction)
    			        			bioTAdded.add(tr);
    						}
    						AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    		                try {
								return new BioTuple(tr.toTerm(multTempl).copyGoal(v, 0));
							} catch (InvalidMultiplicityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    					}
                	}
            	}
            }
        }
        return null;
    }

    
    public BioTuple readUniformMatchingTuple(BioTuple templ){
    	 if (templ==null)
             return null;
    		
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
         else if(tmp.size() == 1){	//if only one tuple matches the template
         	BioTuple t = tmp.getFirst();
         	while(l.hasPrevious()){		//find that tuple into the ListIterator (backward navigation)	
         		BioTuple tr = l.previous();
 				if(t.toString().equals(tr.toString())){
 					long multTr = tr.getMultiplicity();
 					AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
 	                try {
 						return new BioTuple(tr.toTerm().copyGoal(v, 0));
 					} catch (InvalidMultiplicityException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
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
            				 AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
            				 try {
            					 return new BioTuple(tr.toTerm().copyGoal(v, 0));
            				 } catch (InvalidMultiplicityException e) {
            					 e.printStackTrace();
            				 }
            			 }
            		 }
            	 }
             }
         }
         return null;
    }
    
    
    //bio rd
    public BioTuple readUniformMatchingTupleGround(BioTuple templ){
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
            try {
				return new BioTuple(t.toTerm(multTempl).copyGoal(v, 0));
			} catch (InvalidMultiplicityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
                    try {
						return new BioTuple(tuple.toTerm(multTempl).copyGoal(v, 0));
					} catch (InvalidMultiplicityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        }
        return null;
    }
    
   

    public Iterator<LogicTuple> getIterator(){
        return tuples.listIterator();
    }
    
	public Iterator<BioTuple> getBioIterator(){
        return bioTuples.listIterator();
    }

	public LogicTuple[] toArray() {
    	return tuples.toArray(new LogicTuple[tuples.size()]);
    }
	
    public BioTuple[] toArrayBio() {
    	return bioTuples.toArray(new BioTuple[bioTuples.size()]);
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
     * Provides a representation of the bio tuple multi-set in
     * the form of a String containing a prolog theory.
     * @return a textual representation in the form of a prolog theory.    
     */
    public String toStringBio() {
    	String str = "";
    	for(BioTuple t: bioTuples)
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
    
	/**
	 * Tells whether there are changes in the bio tuple multi-set during a transaction  
	 * 
	 * @return true if the ongoing transaction made any changes to the tuple multi-set
	 */
    public boolean operationsPendingBio() {
		if(bioTAdded.isEmpty() && bioTRemoved.isEmpty())
			return false;
		else
			return true;
	}

}

