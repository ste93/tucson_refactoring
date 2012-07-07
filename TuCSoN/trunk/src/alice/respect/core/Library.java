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

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import alice.respect.api.EnvId;
import alice.respect.api.InvalidTupleCentreIdException;
import alice.respect.api.TupleCentreId;

import alice.tucson.parsing.MyOpManager;
import  alice.tuprolog.*;
import alice.tuprolog.Var;

import  alice.logictuple.*;

import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.*;

/**
 * TuProlog library defining the behaviour
 * of ReSpecT primitives, used inside ReSpecT VM.
 * 
 * @author aricci
 */
@SuppressWarnings("serial")
public class Library extends alice.tuprolog.Library {

    RespectVMContext vm;
    
    public String getTheory(){
        
    	return 
        
    	":- op(600, xfx, '?'). \n"+
        ":- op(550, xfx, '@'). \n"+

        "TC ? Op :- not(TC = Name @ Host), TC@localhost ? Op, !. \n"+
        
        "TC ? out(T) :- out(T,TC). \n"+
        "TC ? in(T) :- in(T,TC). \n"+
        "TC ? rd(T) :- rd(T,TC). \n"+
        "TC ? inp(T) :- inp(T,TC). \n"+
        "TC ? rdp(T) :- rdp(T,TC). \n"+
        "TC ? no(T) :- no(T,TC). \n"+
        "TC ? nop(T) :- nop(T,TC). \n"+
        "TC ? set(T) :- set(T,TC). \n"+
        "TC ? get(T) :- get(T,TC). \n"+
        
        "TC ? out_s(E,G,R) :- out_s(E,G,R,TC). \n"+
        "TC ? in_s(E,G,R) :- in_s(E,G,R,TC). \n"+
        "TC ? rd_s(E,G,R) :- rd_s(E,G,R,TC). \n"+
        "TC ? inp_s(E,G,R) :- inp_s(E,G,R,TC). \n"+
        "TC ? rdp_s(E,G,R) :- rdp_s(E,G,R,TC). \n"+
        "TC ? no_s(E,G,R) :- no_s(E,G,R,TC). \n"+
        "TC ? nop_s(E,G,R) :- nop_s(E,G,R,TC). \n"+
        "TC ? set_s(E,G,R) :- set_s(E,G,R,TC). \n"+
        "TC ? get_s(T) :- get_s(T,TC). \n"+
        
		"out(T):-out(T,this@localhost). \n" +
		"in(T):-in(T,this@localhost). \n"+
		"rd(T):-rd(T,this@localhost). \n"+
		"inp(T):-inp(T,this@localhost). \n"+
		"rdp(T):-rdp(T,this@localhost). \n"+
		"no(T):-no(T,this@localhost). \n"+
		"nop(T):-nop(T,this@localhost). \n"+
		"set(T):-set(T,this@localhost). \n"+
		"get(T):-get(T,this@localhost). \n"+
		
		"out_s(E,G,R):-out_s(E,G,R,this@localhost). \n" +
		"in_s(E,G,R):-in_s(E,G,R,this@localhost). \n"+
		"rd_s(E,G,R):-rd_s(E,G,R,this@localhost). \n"+
		"inp_s(E,G,R):-inp_s(E,G,R,this@localhost). \n"+
		"rdp_s(E,G,R):-rdp_s(E,G,R,this@localhost). \n"+
		"no_s(E,G,R):-no_s(E,G,R,this@localhost). \n"+
		"nop_s(E,G,R):-nop_s(E,G,R,this@localhost). \n"+
		"set_s(E,G,R):-set_s(E,G,R,this@localhost). \n"+
		"get_s(T):-get_s(T,this@localhost). \n"+
        
		// my personal updates
		"TC ? uin(T) :- uin(T,TC). \n"+
		"TC ? uinp(T) :- uinp(T,TC). \n"+
		"TC ? urd(T) :- urd(T,TC). \n"+
		"TC ? urdp(T) :- urdp(T,TC). \n"+
		"TC ? in_all(T,L) :- in_all(T,L,TC). \n"+
		"TC ? rd_all(T,L) :- rd_all(T,L,TC). \n"+
		
		"urd(T):-urd(T,this@localhost). \n"+
		"urdp(T):-urdp(T,this@localhost). \n"+
		"uin(T):-uin(T,this@localhost). \n"+
		"uinp(T):-uinp(T,this@localhost). \n"+
		"in_all(T,L):-in_all(T,L,this@localhost). \n"+
		"rd_all(T,L):-rd_all(T,L,this@localhost). \n"+
		//***********************
        
//        "TC ? out_tc(T) :- out_tc(TC,T). \n"+
        
        "Env ? getEnv(Key,Value) :- getEnv(Env,Key,Value). \n" +
        "Env ? setEnv(Key,Value) :- setEnv(Env,Key,Value). \n" +
        "prolog(Term):- Term. \n" +
        
        //GUARD ALIASES
        "completion :- response. \n"+
        "compl :- response. \n"+
        "resp :- response. \n"+
        "post :- response. \n"+
        "invocation :- request. \n"+
        "inv :- request. \n"+
        "req :- request. \n"+
        "pre :- request. \n"+
        "between(T1,T2) :- before(T1), after(T2). \n"+
        "operation :- from_agent, to_tc. \n"+
        "link_in :- from_tc,to_tc,exo,intra. \n"+
        "link_out :- from_tc,to_tc,endo,inter. \n"+
        "internal :- from_tc,to_tc,endo,intra. \n";
    	
    }
    
    public void init(RespectVMContext m){
        vm=m;
    }
    
    public boolean out_2(Term arg0,Term arg1){

    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();

    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local out triggered...");
	        Term newArg=arg0.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        vm.addTuple(tuArg);
	        InputEvent ce=vm.getCurrentEvent();
			InternalEvent ev=new InternalEvent(ce,InternalOperation.makeOutR(new LogicTuple(arg0.copyGoal(v,0))));
			ev.setSource(ce.getReactingTC());
	        ev.setTarget(ce.getReactingTC());
			vm.fetchTriggeredReactions(ev);
	        return true;
    	}else{
    		System.out.println("[Library]: Remote out triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeOut(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }

    public boolean in_2(Term arg0, Term arg1){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local in triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.removeMatchingTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeInR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote in triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeIn(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }
    
    public boolean uin_2(Term arg0, Term arg1){
        
	  	String tcName = null;
	  	TupleCentreId tid = null;
	  	try{
	  		tid=new TupleCentreId(arg1);
	  	}catch(Exception e){
	  		e.printStackTrace();
	  		return false;
	  	}
	  	tcName = tid.getName();
	  	
	  	LogicTuple tuArg=new LogicTuple(arg0);
	  	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	  	
	  	if(tcName.equals("this")){
	  		System.out.println("[Library]: Local uin triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.removeUniformTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeUinR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	  	}else{
	  		System.out.println("[Library]: Remote uin triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeUin(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
		}
    }
    
    public boolean in_all_3(Term arg0, Term arg1, Term arg2){
        
	  	String tcName = null;
	  	TupleCentreId tid = null;
	  	try{
	  		tid=new TupleCentreId(arg2);
	  	}catch(Exception e){
	  		e.printStackTrace();
	  		return false;
	  	}
	  	tcName = tid.getName();
	  	
	  	LogicTuple tuArg=new LogicTuple(arg0);
	  	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	  	
	  	if(tcName.equals("this")){
	  		System.out.println("[Library]: Local in_all triggered...");
	        List<alice.tuplecentre.api.Tuple> tuples = vm.inAllTuples(tuArg);
	        System.out.println("[Library]: tuples = " + tuples);
	        if (tuples!=null){
	            Term term = list2tuple(tuples);
	            System.out.println("[Library]: term = " + term);
	            unify(arg1,term.copyGoal(v,0));
	            System.out.println("[Library]: arg1 = " + arg1);
//	            InputEvent ce=vm.getCurrentEvent();
//	            String tupleStr = arg0.toString()+","+arg1.copyGoal(v,0);
//	            LogicTuple resultArg = null;
//				try {
//					resultArg = LogicTuple.parse(tupleStr);
//				} catch (InvalidLogicTupleException e) {
//					e.printStackTrace();
//				}
//				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeRdAllR(resultArg)); 
//				ev.setSource(ce.getReactingTC());
//	            ev.setTarget(ce.getReactingTC());
//				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote in_all triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
	    	String tuple = arg0.toString()+","+arg1.copyGoal(v, 0);
	    	LogicTuple resultArg = null;
			try {
				resultArg = LogicTuple.parse(tuple);
			} catch (InvalidLogicTupleException e) {
				e.printStackTrace();
			}
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeInAll(getProlog(),resultArg,null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }

    public boolean inp_2(Term arg0, Term arg1){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();

    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local inp triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.removeMatchingTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeInR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote inp triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeInp(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }
    
    public boolean uinp_2(Term arg0, Term arg1){
        
	  	String tcName = null;
	  	TupleCentreId tid = null;
	  	try{
	  		tid=new TupleCentreId(arg1);
	  	}catch(Exception e){
	  		e.printStackTrace();
	  		return false;
	  	}
	  	tcName = tid.getName();
	  	
	  	LogicTuple tuArg=new LogicTuple(arg0);
	  	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	  	
	  	if(tcName.equals("this")){
	  		System.out.println("[Library]: Local uinp triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.removeUniformTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeUinR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	            	
	        }
	    }else{
	    	System.out.println("[Library]: Remote uinp triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeUinp(getProlog(),new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }
    
    public boolean rd_2(Term arg0, Term arg1){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();

    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local rd triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeRdR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote rd triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeRd(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean urd_2(Term arg0, Term arg1){
        
	  	String tcName = null;
	  	TupleCentreId tid = null;
	  	try{
	  		tid=new TupleCentreId(arg1);
	  	}catch(Exception e){
	  		e.printStackTrace();
	  		return false;
	  	}
	  	tcName = tid.getName();
	  	
	  	LogicTuple tuArg=new LogicTuple(arg0);
	  	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	  	
	  	if(tcName.equals("this")){
	  		System.out.println("[Library]: Local urd triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readUniformTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeUrdR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote urd triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeUrd(getProlog(),new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }
    
    public boolean rd_all_3(Term arg0, Term arg1, Term arg2){
        
	  	String tcName = null;
	  	TupleCentreId tid = null;
	  	try{
	  		tid=new TupleCentreId(arg2);
	  	}catch(Exception e){
	  		e.printStackTrace();
	  		return false;
	  	}
	  	tcName = tid.getName();
	  	
	  	LogicTuple tuArg=new LogicTuple(arg0);
	  	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	  	
	  	if(tcName.equals("this")){
	  		System.out.println("[Library]: Local rd_all triggered...");
	        List<alice.tuplecentre.api.Tuple> tuples = vm.readAllTuples(tuArg);
	        System.out.println("[Library]: tuples = " + tuples);
	        if (tuples!=null){
	            Term term = list2tuple(tuples);
	            System.out.println("[Library]: term = " + term);
	            unify(arg1,term.copyGoal(v,0));
	            System.out.println("[Library]: arg1 = " + arg1);
//	            InputEvent ce=vm.getCurrentEvent();
//	            String tupleStr = arg0.toString()+","+arg1.copyGoal(v,0);
//	            LogicTuple resultArg = null;
//				try {
//					resultArg = LogicTuple.parse(tupleStr);
//				} catch (InvalidLogicTupleException e) {
//					e.printStackTrace();
//				}
//				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeRdAllR(resultArg)); 
//				ev.setSource(ce.getReactingTC());
//	            ev.setTarget(ce.getReactingTC());
//				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote rd_all triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
	    	String tuple = arg0.toString()+","+arg1.copyGoal(v,0);
	    	LogicTuple resultArg = null;
			try {
				resultArg = LogicTuple.parse(tuple);
			} catch (InvalidLogicTupleException e) {
				e.printStackTrace();
			}
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeRdAll(getProlog(),resultArg,null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }
        	
    public boolean rdp_2(Term arg0, Term arg1){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local rdp triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeRdR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote rdp triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeRdp(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean urdp_2(Term arg0, Term arg1){
        
	  	String tcName = null;
	  	TupleCentreId tid = null;
	  	try{
	  		tid=new TupleCentreId(arg1);
	  	}catch(Exception e){
	  		e.printStackTrace();
	  		return false;
	  	}
	  	tcName = tid.getName();
	  	
	  	LogicTuple tuArg=new LogicTuple(arg0);
	  	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	  	
	  	if(tcName.equals("this")){
	  		System.out.println("[Library]: Local urdp triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readUniformTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(arg0,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeUrdR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote urdp triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeUrdp(getProlog(),new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }
    
    public boolean no_2(Term arg0, Term arg1){

    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();

    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local no triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingTuple(tuArg);
	        if (tuple==null){
	        	System.out.println("[Library]: no success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNoR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	System.out.println("[Library]: no failure");
//	            InputEvent ce=vm.getCurrentEvent();
//	            InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNoR(new LogicTuple(arg0.copyGoal(v,0)))); 
//				ev.setSource(ce.getReactingTC());
//	            ev.setTarget(ce.getReactingTC());
//				vm.fetchTriggeredReactions(ev);
	            return false;
	        }
    	}else{
    		System.out.println("[Library]: Remote no triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeNo(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean nop_2(Term arg0, Term arg1){

    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();

    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local nop triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingTuple(tuArg);
	        if (tuple==null){
	        	System.out.println("[Library]: nop success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNoR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	System.out.println("[Library]: nop failure");
//	            InputEvent ce=vm.getCurrentEvent();
//	            InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNoR(new LogicTuple(arg0.copyGoal(v,0)))); 
//				ev.setSource(ce.getReactingTC());
//	            ev.setTarget(ce.getReactingTC());
//				vm.fetchTriggeredReactions(ev);
	            return false;
	        }
    	}else{
    		System.out.println("[Library]: Remote nop triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeNop(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean get_2(Term arg0, Term arg1){
    	
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();

    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local get triggered...");
    		List<Tuple> list = vm.getAllTuples();
    		TupleArgument[] array = new TupleArgument[list.size()];
    		int i = 0;
    		while(!list.isEmpty())
    			array[i++] = new TupleArgument(((LogicTuple)list.remove(0)).toTerm());
    		System.out.println("[Library]: array = " + array);
	        alice.tuplecentre.api.Tuple tuple = new LogicTuple("get", array);
	        System.out.println("[Library]: tuple = " + tuple);
	        try {
				if (((LogicTuple)tuple).getArg(0) != null){
				    Term term=((LogicTuple)tuple).toTerm();
				    unify(arg0,term.copyGoal(v,0));
				    InputEvent ce=vm.getCurrentEvent();
					InternalEvent ev=new InternalEvent(ce,InternalOperation.makeGetR(new LogicTuple(arg0.copyGoal(v,0)))); 
					ev.setSource(ce.getReactingTC());
				    ev.setTarget(ce.getReactingTC());
					vm.fetchTriggeredReactions(ev);
				    return true;
				} else {
				    return false;
				}
			} catch (InvalidTupleOperationException e) {
				e.printStackTrace();
				return false;
			}
	    }else{
	    	System.out.println("[Library]: Remote get triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
	    	InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeGet(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    	
    }
    
//    MANCA SET

    public boolean out_s_4(Term e, Term g, Term r, Term tc){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(tc);
    	}catch(InvalidTupleCentreIdException ex){
    		ex.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        Term goal;
        try{
        	goal = Term.createTerm("reaction("+e.getTerm()+","+g.getTerm()+","+r.getTerm()+")", new MyOpManager());
        }catch (InvalidTermException ex) {
        	ex.printStackTrace();
    		return false;
		}

    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local out_s triggered...");
	        Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        vm.addSpecTuple(tuArg);
	        InputEvent ce=vm.getCurrentEvent();
			InternalEvent ev=new InternalEvent(ce,InternalOperation.makeOut_sR(new LogicTuple(goal.copyGoal(v,0)))); 
			ev.setSource(ce.getReactingTC());
	        ev.setTarget(ce.getReactingTC());
			vm.fetchTriggeredReactions(ev);
	        return true;
    	}else{
    		System.out.println("[Library]: Remote out_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeOut_s(getProlog(), new LogicTuple(goal.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    }
    
    public boolean in_s_4(Term e, Term g, Term r, Term tc){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(tc);
    	}catch(InvalidTupleCentreIdException ex){
    		ex.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        Term goal;
        try{
        	goal = Term.createTerm("reaction("+e.getTerm()+","+g.getTerm()+","+r.getTerm()+")", new MyOpManager());
        }catch (InvalidTermException ex) {
        	ex.printStackTrace();
    		return false;
		}
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local in_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.removeMatchingSpecTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(goal,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeIn_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote in_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeIn_s(getProlog(), new LogicTuple(goal.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean rd_s_4(Term e, Term g, Term r, Term tc){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(tc);
    	}catch(InvalidTupleCentreIdException ex){
    		ex.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        Term goal;
        try{
        	goal = Term.createTerm("reaction("+e.getTerm()+","+g.getTerm()+","+r.getTerm()+")", new MyOpManager());
        }catch (InvalidTermException ex) {
        	ex.printStackTrace();
    		return false;
		}
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local rd_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingSpecTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(goal,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeRd_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote rd_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeRd_s(getProlog(), new LogicTuple(goal.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean inp_s_4(Term e, Term g, Term r, Term tc){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(tc);
    	}catch(InvalidTupleCentreIdException ex){
    		ex.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        Term goal;
        try{
        	goal = Term.createTerm("reaction("+e.getTerm()+","+g.getTerm()+","+r.getTerm()+")", new MyOpManager());
        }catch (InvalidTermException ex) {
        	ex.printStackTrace();
    		return false;
		}
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local inp_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.removeMatchingSpecTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(goal,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeIn_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote inp_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeInp_s(getProlog(), new LogicTuple(goal.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean rdp_s_4(Term e, Term g, Term r, Term tc){
        
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(tc);
    	}catch(InvalidTupleCentreIdException ex){
    		ex.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        Term goal;
        try{
        	goal = Term.createTerm("reaction("+e.getTerm()+","+g.getTerm()+","+r.getTerm()+")", new MyOpManager());
        }catch (InvalidTermException ex) {
        	ex.printStackTrace();
    		return false;
		}
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local rdp_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingSpecTuple(tuArg);
	        if (tuple!=null){
	            Term term=((LogicTuple)tuple).toTerm();
	            unify(goal,term.copyGoal(v,0));
	            InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeRd_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	System.out.println("[Library]: Remote rdp_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeRdp_s(getProlog(), new LogicTuple(goal.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean no_s_4(Term e, Term g, Term r, Term tc){

    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(tc);
    	}catch(InvalidTupleCentreIdException ex){
    		ex.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        Term goal;
        try{
        	goal = Term.createTerm("reaction("+e.getTerm()+","+g.getTerm()+","+r.getTerm()+")", new MyOpManager());
        }catch (InvalidTermException ex) {
        	ex.printStackTrace();
    		return false;
		}

    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local no_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingSpecTuple(tuArg);
	        if (tuple==null){
	        	System.out.println("[Library]: no_s success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNo_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	System.out.println("[Library]: no_s failure");
//	        	Term term=((LogicTuple)tuple).toTerm();
//	            unify(goal,term.copyGoal(v,0));
	            return false;
	        }
    	}else{
    		System.out.println("[Library]: Remote no_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeNo_s(getProlog(), new LogicTuple(goal.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean nop_s_4(Term e, Term g, Term r, Term tc){

    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(tc);
    	}catch(InvalidTupleCentreIdException ex){
    		ex.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        Term goal;
        try{
        	goal = Term.createTerm("reaction("+e.getTerm()+","+g.getTerm()+","+r.getTerm()+")", new MyOpManager());
        }catch (InvalidTermException ex) {
        	ex.printStackTrace();
    		return false;
		}

    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local nop_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingSpecTuple(tuArg);
	        if (tuple==null){
	        	System.out.println("[Library]: nop_s success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNo_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	System.out.println("[Library]: nop_s failure");
//	        	Term term=((LogicTuple)tuple).toTerm();
//	            unify(goal,term.copyGoal(v,0));
	            return false;
	        }
    	}else{
    		System.out.println("[Library]: Remote nop_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeNop_s(getProlog(), new LogicTuple(goal.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    }
    
    public boolean get_s_2(Term arg0, Term arg1){
    	
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	tcName = tid.getName();

    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	
    	if(tcName.equals("this")){
    		System.out.println("[Library]: Local get_s triggered...");
    		Iterator<LogicTuple> it = vm.getSpecTupleSetIterator();
    		List<Tuple> list = new LinkedList<Tuple>();
    		while(it.hasNext()){
    			list.add(it.next());
    		}
    		TupleArgument[] array = new TupleArgument[list.size()];
    		int i = 0;
    		while(!list.isEmpty())
    			array[i++] = new TupleArgument(((LogicTuple)list.remove(0)).toTerm());
    		System.out.println("[Library]: array = " + array);
	        alice.tuplecentre.api.Tuple tuple = new LogicTuple("get_s", array);
	        System.out.println("[Library]: tuple = " + tuple);
	        try {
				if (((LogicTuple)tuple).getArg(0) != null){
				    Term term=((LogicTuple)tuple).toTerm();
				    unify(arg0,term.copyGoal(v,0));
				    InputEvent ce=vm.getCurrentEvent();
					InternalEvent ev=new InternalEvent(ce,InternalOperation.makeGet_sR(new LogicTuple(arg0.copyGoal(v,0)))); 
					ev.setSource(ce.getReactingTC());
				    ev.setTarget(ce.getReactingTC());
					vm.fetchTriggeredReactions(ev);
				    return true;
				} else {
				    return false;
				}
			} catch (InvalidTupleOperationException e) {
				e.printStackTrace();
				return false;
			}
	    }else{
	    	System.out.println("[Library]: Remote get_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
	    	InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeGet_s(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    	
    }

//    MANCA SET_S
    
    public boolean current_agent_1(Term arg0){
        Term term=new Struct(vm.getCurrentReactionEvent().getId().toString());
        return unify(arg0,term);
    }

    public boolean current_tc_1(Term arg0){
        Term term=new Struct(vm.getId().toString());
        return unify(arg0,term);
    }

    public boolean current_operation_1(Term arg0){
        return unify(arg0,vm.getCurrentReactionTerm());
    }
    
    public boolean current_tuple_1(Term arg0){
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
        return unify(arg0,vm.getCurrentReactionTerm().getArg(0).copyGoal(v,0));
    }

    public boolean current_source_1(Term arg0){
        return unify(arg0,new Struct(""+vm.getCurrentReactionEvent().getReactingTC()));
    }
    
    public boolean current_target_1(Term arg0){
        return unify(arg0,new Struct(""+vm.getCurrentReactionEvent().getReactingTC()));
    }
    
    public boolean current_time_1(Term arg0){
        long time=vm.getCurrentTime();
        return unify(arg0,new alice.tuprolog.Long(time));
    }

    public boolean event_operation_1(Term arg0){
        return unify(arg0,new Struct(""+vm.getCurrentReactionEvent().getOperation()));
    }
    
    public boolean event_tuple_1(Term arg0){
        return unify(arg0,new Struct(""+((RespectOperation)vm.getCurrentReactionEvent().getOperation()).getLogicTupleArgument()));
    }
    
    public boolean event_time_1(Term arg0){
        return unify(arg0,new alice.tuprolog.Long(vm.getCurrentReactionEvent().getTime()));
    }
    
    public boolean event_source_1(Term arg0){
        return unify(arg0,new Struct(""+vm.getCurrentReactionEvent().getSource()));
    }
    
    public boolean event_target_1(Term arg0){
        return unify(arg0,new Struct(""+vm.getCurrentReactionEvent().getTarget()));
    }
    
    public boolean from_env_0(){
    	return vm.getCurrentReactionEvent().getSource().isEnv(); 
    }
    
    public boolean to_env_0(){
    	return vm.getCurrentReactionEvent().getTarget().isEnv(); 
    }
    
    public boolean env_2(Term key, Term value){
    	if (value instanceof alice.tuprolog.Var){
    		String res=vm.getCurrentReactionEvent().getEventProp(key.getTerm().toString());
    		//The specified value doesn't exists.
    		if (res==null)return false;
    		return unify(value, new Struct(res));
    	}
    	return false;
    }
    
    public boolean request_0(){
    	Event ev = vm.getCurrentReactionEvent();
        TupleCentreOperation op = ev.getOperation();
        System.out.println("[Library-request]: isResultDef = " + op.isResultDefined());
        return !op.isResultDefined();
    }
    
    public boolean invocation_0(){
        return request_0();
    }
    
    public boolean inv_0(){
        return request_0();
    }
    
    public boolean req_0(){
        return request_0();
    }
    
    public boolean pre_0(){
        return request_0();
    }
    
    public boolean response_0(){
    	Event ev = vm.getCurrentReactionEvent();
        TupleCentreOperation op = ev.getOperation();
        System.out.println("[Library-response]: isResultDef = " + op.isResultDefined());
        return op.isResultDefined();
//        if(op.isResultDefined()){
//        	System.out.println("[Library-response]: isOutputEv = " + (ev instanceof OutputEvent));
//        	if(ev instanceof OutputEvent){
//        		System.out.println("[Library-response]: isLinking = " + ((OutputEvent)ev).isLinking());
//        		if(((OutputEvent)ev).isLinking())
//        			return false;
//        		else{
//        			System.out.println("[Library-response]: returning TRUE");
//        			return true;
//        		}
//        	}else
//        		return true;
//        }
//        return false;
    }
    
    public boolean completion_0(){
        return response_0();
    }
    
    public boolean compl_0(){
        return response_0();
    }
    
    public boolean resp_0(){
        return response_0();
    }
    
    public boolean post_0(){
        return response_0();
    }
    
    public boolean success_0(){
        Event ev = vm.getCurrentReactionEvent();
        RespectOperation op = (RespectOperation)ev.getOperation();
        System.out.println("[Library-success]: isResultSuccess = " + op.isResultSuccess());
        return op.isResultSuccess();
//        if(op.isResultDefined()){
//        	Term res = op.getLogicTupleResult().toTerm();
//        	if(res != null)
//    			return true;
//        	else
//    			return false;
//        }else
//        	return false;
    }
    
    public boolean failure_0(){
    	Event ev = vm.getCurrentReactionEvent();
        RespectOperation op = (RespectOperation)ev.getOperation();
        System.out.println("[Library-failure]: isResultFailure = " + op.isResultFailure());
        return op.isResultFailure();
//        System.out.println("[Library-failure]: isResultDef = " + op.isResultDefined());
//        if(op.isResultDefined()){
//        	if(op.isNo() || op.isInp() || op.isRdp() || op.isNo_s() || op.isInp_s() || op.isRdp_s()){
//        		System.out.println("[Library-failure]: isPredicativeOp = true");
//        		if(op.getTupleResult()==null){
//        			System.out.println("[Library-failure]: returning TRUE");
//        			return true;
//        		}else
//        			return false;
//        	}else{
//	        	Term res = op.getLogicTupleResult().toTerm();
//	        	if(res != null)
//	    			return false;
//	        	else
//	    			return true;
//        	}
//        }else
//        	return true;
    }

    public boolean before_1(Term time){
    	Event ev = vm.getCurrentReactionEvent();
    	long evtTime = ev.getTime();
    	long compareTime;
    	try{
    		compareTime = ((alice.tuprolog.Number)time).longValue();
    	}catch(Exception e){
    		return false;
    	}
    	if(evtTime<=compareTime)
    		return true;
        return false;
    }
    
    public boolean after_1(Term time){
    	return !before_1(time);
    }
    
    public boolean intra_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId id = ev.getTarget();
    	IId c = this.vm.getId();
    	if(c.toString().equals(id.toString()))
    		return true;
    	else
    		return false;
    }
    
    public boolean inter_0(){
    	return !intra_0();
    }
    
    public boolean endo_0(){
    	return !exo_0();
    }
    
    public boolean exo_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId id = ev.getId();
    	IId c = this.vm.getId();
    	if(!c.toString().equals(id.toString()))
    		return true;
    	else
    		return false;
    }
    
    public boolean from_agent_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId id = ev.getId();
    	if(id.isAgent())
    		return true;
    	else
    		return false;
    }
    
    public boolean to_agent_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId id = ev.getTarget();
    	if(id.isAgent())
    		return true;    		
    	else
    		return false;
    }
    
    public boolean from_tc_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId id = ev.getId();
    	if(id.isTC())
    		return true;    		
    	else
    		return false;
    }
    
    public boolean to_tc_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId id = ev.getTarget();
    	if(id.isTC())
    		return true;
    	else
    		return false;
    }
    
    public boolean getEnv_3(Term env, Term key, Term val){
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	AbstractMap<Var,Var> v1 = new LinkedHashMap<Var,Var>();
    	LogicTuple lt=new LogicTuple("getEnv",new TupleArgument(key.copyGoal(v,0)),new TupleArgument(val.copyGoal(v1,0)));
    	InputEvent ev=vm.getCurrentEvent();
    	InternalEvent ie=new InternalEvent(ev,InternalOperation.makeGetEnv(lt));
    	ie.setTarget(new EnvId((Struct)env));
    	ie.setSource(vm.getId());
    	
//    	if (vm.getTransducerManager().notifyOutputEnv(ie)){
//    		vm.fetchTriggeredReactions(ie);
//    		return true;
//    	}
    	return false;
    }
    
    public boolean setEnv_3(Term env, Term key, Term val){
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	AbstractMap<Var,Var> v1 = new LinkedHashMap<Var,Var>();
    	LogicTuple lt=new LogicTuple("setEnv",new TupleArgument(key.copyGoal(v,0)),new TupleArgument(val.copyGoal(v1,0)));
    	InputEvent ev=vm.getCurrentEvent();
    	InternalEvent ie=new InternalEvent(ev,InternalOperation.makeSetEnv(lt));
    	ie.setTarget(new EnvId((Struct)env));
    	ie.setSource(vm.getId());
    	
//    	if (vm.getTransducerManager().notifyOutputEnv(ie)){
//    		vm.fetchTriggeredReactions(ie);
//    		return true;
//    	}
    	return false;
    }
    
    private Prolog getProlog(){
    	return vm.getPrologCore();
    }

//    public boolean out_tc_2(Struct stid, Term tuple){
//        LogicTuple tid=new LogicTuple(stid);
//        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
//        // get a renamed copy
//        Term newTuple=tuple.copyGoal(v,0);
//
//		Struct outTc = new Struct("out_tc",tid.toTerm(),tuple);
//        InternalOperation outTCop = InternalOperation.makeOutTC(new LogicTuple(outTc));
//        													
//        alice.tuplecentre.core.OutputEvent ev=new RespectOutputEvent(vm.getCurrentEvent(), outTCop);
//        
//      //  vm.addOutputEvent(ev);
//        
//        AgentId aid;
//        try {
//                aid = new AgentId("outTcAgent");
//                TucsonTupleCentreId tcid = new TucsonTupleCentreId(stid.toString()); //OCCHIOOOOOOOOOOO!
//                DefaultACC cnt = TucsonMetaACC.getContext(aid);
//
//                LogicTuple tuple2 = new LogicTuple(newTuple);
//
//                cnt.out(tcid, tuple2, (java.lang.Long) null);
//        
//                cnt.exit();
//
//                return true;
//
//        } catch (InvalidAgentIdException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//        } catch (TucsonInvalidTupleCentreIdException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//        } catch (TucsonOperationNotPossibleException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//        } catch (UnreachableNodeException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//        } catch (OperationTimeOutException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//        }
//        return false;
//    }
    
    /** public boolean success_0(){
    Event ev = vm.getCurrentReactionEvent();
    if (!ev.isOutput()){
        return true;
    } else {
    	RespectOperation op = (RespectOperation)ev.getOperation();
		if (op.isInp() && op.getLogicTupleResult()==null){
			return false;
		} else if (op.isRdp() && op.getLogicTupleResult()==null){
			return false;
		} else {
			return true;
		}
    }
}

public boolean failure_0(){
    Event ev = vm.getCurrentReactionEvent();
	if (!ev.isOutput()){
		return false;
	} else {
		RespectOperation op = (RespectOperation)ev.getOperation();
		if (op.isInp() && op.getLogicTupleResult()==null){
			return true;
		} else if (op.isRdp() && op.getLogicTupleResult()==null){
			return true;
		} else {
			return false;
		}
	}
}


public boolean spawn_3(Term agentId, Term agentType, Term arg){
    if (arg.isList()){
        Struct args = (Struct)arg;
        Struct sp_arg = new Struct("spawn",agentId.getRenamedCopy(),
										 agentType.getRenamedCopy(),
										(Struct)args.getRenamedCopy());
		
        InternalOperation spop = InternalOperation.makeSpawn(new LogicTuple(sp_arg));
        alice.tuplecentre.core.OutputEvent ev= new RespectOutputEvent(vm.getCurrentEvent(),spop);
        vm.addOutputEvent(ev);
        return true;
    } else {
        return false;
    }

}*/
    
    private Term list2tuple(List<Tuple> list){
		Term [] termArray = new Term[list.size()];
		Iterator<Tuple> it = list.iterator();
		int i=0;
		while(it.hasNext()){
			termArray[i] = ((LogicTuple)it.next()).toTerm();
			i++;
		}
		return new Struct(termArray);
	}
    
}
