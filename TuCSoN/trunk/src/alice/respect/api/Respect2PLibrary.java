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
package alice.respect.api;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.core.InternalEvent;
import alice.respect.core.InternalOperation;
import alice.respect.core.RespectOperation;
import alice.respect.core.RespectVMContext;

import alice.tucson.parsing.MyOpManager;
import  alice.tuprolog.*;
import alice.tuprolog.Var;

import  alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.*;

/**
 * TuProlog library defining the behaviour
 * of ReSpecT primitives, used inside ReSpecT VM.
 * 
 * @author aricci
 */
public class Respect2PLibrary extends alice.tuprolog.Library {

    private static final long serialVersionUID = 7865604500315298959L;
	RespectVMContext vm;
    
	/**
	 * ReSpecT theory to interface with Prolog.
	 */
    public String getTheory(){
        
    	return 
        
//    	":- op(600, xfx, '?'). \n"+
//      ":- op(550, xfx, '@'). \n"+
		":- op(551, xfx, '?'). \n"
		+ ":- op(550, xfx, '@'). \n"
		+ ":- op(549, xfx, ':'). \n"
		+ ":- op(548, xfx, '.'). \n" +

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
        
        "TC ? spawn(T) :- spawn(T,TC). \n"+
        
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
		
		"spawn(T):-spawn(T,this@localhost). \n"+
		
		"out_s(E,G,R):-out_s(E,G,R,this@localhost). \n" +
		"in_s(E,G,R):-in_s(E,G,R,this@localhost). \n"+
		"rd_s(E,G,R):-rd_s(E,G,R,this@localhost). \n"+
		"inp_s(E,G,R):-inp_s(E,G,R,this@localhost). \n"+
		"rdp_s(E,G,R):-rdp_s(E,G,R,this@localhost). \n"+
		"no_s(E,G,R):-no_s(E,G,R,this@localhost). \n"+
		"nop_s(E,G,R):-nop_s(E,G,R,this@localhost). \n"+
		"set_s(E,G,R):-set_s(E,G,R,this@localhost). \n"+
		"get_s(T):-get_s(T,this@localhost). \n"+
        
		"TC ? uin(T) :- uin(T,TC). \n"+
		"TC ? uinp(T) :- uinp(T,TC). \n"+
		"TC ? urd(T) :- urd(T,TC). \n"+
		"TC ? urdp(T) :- urdp(T,TC). \n"+
		"TC ? uno(T) :- uno(T,TC). \n"+
		"TC ? unop(T) :- unop(T,TC). \n"+
		"TC ? out_all(L) :- out_all(L,TC). \n"+
		"TC ? in_all(T,L) :- in_all(T,L,TC). \n"+
		"TC ? rd_all(T,L) :- rd_all(T,L,TC). \n"+
		"TC ? no_all(T,L) :- no_all(T,L,TC). \n"+
		
		"urd(T):-urd(T,this@localhost). \n"+
		"urdp(T):-urdp(T,this@localhost). \n"+
		"uin(T):-uin(T,this@localhost). \n"+
		"uinp(T):-uinp(T,this@localhost). \n"+
		"uno(T):-uno(T,this@localhost). \n"+
		"unop(T):-unop(T,this@localhost). \n"+
		"out_all(L):-out_all(L,this@localhost). \n"+
		"in_all(T,L):-in_all(T,L,this@localhost). \n"+
		"rd_all(T,L):-rd_all(T,L,this@localhost). \n"+
		"no_all(T,L):-no_all(T,L,this@localhost). \n"+
        
//        "Env ? getEnv(Key,Value) :- getEnv(Env,Key,Value). \n" +
//        "Env ? setEnv(Key,Value) :- setEnv(Env,Key,Value). \n" +
//        "prolog(Term):- Term. \n" +
        
        "completion :- response. \n"+
        "compl :- response. \n"+
        "resp :- response. \n"+
        "post :- response. \n"+
        "invocation :- request. \n"+
        "inv :- request. \n"+
        "req :- request. \n"+
        "pre :- request. \n"+
        
        "operation :- from_agent, to_tc. \n"+
        "internal :- from_tc, to_tc, endo, intra. \n"+
        "link_in :- from_tc, to_tc, exo, intra. \n"+
        "link_out :- from_tc, to_tc, endo, inter. \n"+
        
		"between(T1,T2) :- after(T1), before(T2). \n";
    	
    }
    
    public void init(RespectVMContext m){
        vm=m;
    }
    
    /********************************************************************
     * ReSpecT primitives provided as Java methods.
     ********************************************************************/
    
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
    		log("Local out triggered...");
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
    		log("Remote out triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeOut(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    	
    }
    
    public boolean out_all_2(Term arg0,Term arg1){

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
    		log("Local out_all triggered...");
	        Term newArg=arg0.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        vm.addListTuple(tuArg);
	        InputEvent ce=vm.getCurrentEvent();
			InternalEvent ev=new InternalEvent(ce,InternalOperation.makeOutAllR(new LogicTuple(arg0.copyGoal(v,0))));
			ev.setSource(ce.getReactingTC());
	        ev.setTarget(ce.getReactingTC());
			vm.fetchTriggeredReactions(ev);
	        return true;
    	}else{
    		log("Remote out_all triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeOutAll(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
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
    	log("tid = " + tid);
    	tcName = tid.getName();
    	
    	LogicTuple tuArg=new LogicTuple(arg0);
    	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
    	
    	if(tcName.equals("this")){
    		log("Local in triggered...");
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
	    	log("Remote in triggered...");
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
	  		log("Local uin triggered...");
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
	  		log("Remote uin triggered...");
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
	  		log("Local in_all triggered...");
	        List<alice.tuplecentre.api.Tuple> tuples = vm.inAllTuples(tuArg);
	        if (tuples!=null){
	            Term term = list2tuple(tuples);
	            unify(arg1,term.copyGoal(v,0));
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	log("Remote in_all triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
//	    	String tuple = arg0.getTerm().toString()+","+arg1.copyGoal(v,0);
	    	String tuple = arg0.getTerm().toString()+","+arg1;
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
    		log("Local inp triggered...");
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
	    	log("Remote inp triggered...");
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
	  		log("Local uinp triggered...");
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
	    	log("Remote uinp triggered...");
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
    		log("Local rd triggered...");
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
	    	log("Remote rd triggered...");
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
	  		log("Local urd triggered...");
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
	    	log("Remote urd triggered...");
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
	  		log("Local rd_all triggered...");
	        List<alice.tuplecentre.api.Tuple> tuples = vm.readAllTuples(tuArg);
	        if (tuples!=null){
	            Term term = list2tuple(tuples);
	            unify(arg1,term.copyGoal(v,0));
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	log("Remote rd_all triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
//	    	String tuple = arg0.getTerm().toString()+","+arg1.copyGoal(v,0);
	    	String tuple = arg0.getTerm().toString()+","+arg1;
	    	LogicTuple resultArg = null;
			try {
//				log("tuple = " + tuple);
				resultArg = LogicTuple.parse(tuple);
//				log("resultArg = " + resultArg);
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
    		log("Local rdp triggered...");
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
	    	log("Remote rdp triggered...");
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
	  		log("Local urdp triggered...");
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
	    	log("Remote urdp triggered...");
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
    		log("Local no triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingTuple(tuArg);
	        if (tuple==null){
	        	log("no success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNoR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	log("no failure");
	            return false;
	        }
    	}else{
    		log("Remote no triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeNo(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    	
    }
    
    public boolean no_all_3(Term arg0, Term arg1, Term arg2){
        
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
	  		log("Local no_all triggered...");
	        List<alice.tuplecentre.api.Tuple> tuples = vm.readAllTuples(tuArg);
	        if (tuples==null){
	            Term term = list2tuple(tuples);
	            unify(arg1,term.copyGoal(v,0));
	            return true;
	        } else {
	            return false;
	        }
	    }else{
	    	log("Remote no_all triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
//	    	String tuple = arg0.getTerm().toString()+","+arg1.copyGoal(v,0);
	    	String tuple = arg0.getTerm().toString()+","+arg1;
	    	LogicTuple resultArg = null;
			try {
				resultArg = LogicTuple.parse(tuple);
			} catch (InvalidLogicTupleException e) {
				e.printStackTrace();
			}
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeNoAll(getProlog(),resultArg,null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
	  	
    }
    
    public boolean uno_2(Term arg0, Term arg1){

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
    		log("Local uno triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readUniformTuple(tuArg);
	        if (tuple==null){
	        	log("uno success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeUnoR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	log("uno failure");
	            return false;
	        }
    	}else{
    		log("Remote uno triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeUno(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
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
    		log("Local nop triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingTuple(tuArg);
	        if (tuple==null){
	        	log("nop success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNoR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	log("nop failure");
	            return false;
	        }
    	}else{
    		log("Remote nop triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeNop(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    	
    }
    
    public boolean unop_2(Term arg0, Term arg1){

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
    		log("Local unop triggered...");
	        alice.tuplecentre.api.Tuple tuple=vm.readUniformTuple(tuArg);
	        if (tuple==null){
	        	log("unop success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeUnoR(new LogicTuple(arg0.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	log("unop failure");
	            return false;
	        }
    	}else{
    		log("Remote unop triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeUnop(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
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
    		log("Local get triggered...");
    		List<Tuple> list = vm.getAllTuples();
    		TupleArgument[] array = new TupleArgument[list.size()];
    		int i = 0;
    		while(!list.isEmpty())
    			array[i++] = new TupleArgument(((LogicTuple)list.remove(0)).toTerm());
	        alice.tuplecentre.api.Tuple tuple = new LogicTuple("get", array);
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
	    	log("Remote get triggered...");
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
    		log("Local out_s triggered...");
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
    		log("Remote out_s triggered...");
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
    		log("Local in_s triggered...");
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
	    	log("Remote in_s triggered...");
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
    		log("Local rd_s triggered...");
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
	    	log("Remote rd_s triggered...");
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
    		log("Local inp_s triggered...");
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
	    	log("Remote inp_s triggered...");
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
    		log("Local rdp_s triggered...");
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
	    	log("Remote rdp_s triggered...");
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
    		log("Local no_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingSpecTuple(tuArg);
	        if (tuple==null){
	        	log("no_s success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNo_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	log("no_s failure");
	            return false;
	        }
    	}else{
    		log("Remote no_s triggered...");
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
    		log("Local nop_s triggered...");
    		Term newArg=goal.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        alice.tuplecentre.api.Tuple tuple=vm.readMatchingSpecTuple(tuArg);
	        if (tuple==null){
	        	log("nop_s success");
	        	InputEvent ce=vm.getCurrentEvent();
				InternalEvent ev=new InternalEvent(ce,InternalOperation.makeNo_sR(new LogicTuple(goal.copyGoal(v,0)))); 
				ev.setSource(ce.getReactingTC());
	            ev.setTarget(ce.getReactingTC());
				vm.fetchTriggeredReactions(ev);
	            return true;
	        } else {
	        	log("nop_s failure");
	            return false;
	        }
    	}else{
    		log("Remote nop_s triggered...");
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
    		log("Local get_s triggered...");
    		Iterator<LogicTuple> it = vm.getSpecTupleSetIterator();
    		List<Tuple> list = new LinkedList<Tuple>();
    		while(it.hasNext()){
    			list.add(it.next());
    		}
    		TupleArgument[] array = new TupleArgument[list.size()];
    		int i = 0;
    		while(!list.isEmpty())
    			array[i++] = new TupleArgument(((LogicTuple)list.remove(0)).toTerm());
	        alice.tuplecentre.api.Tuple tuple = new LogicTuple("get_s", array);
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
	    	log("Remote get_s triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
	    	InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeGet_s(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
            return true;
	    }
    	
    }

//    MANCA SET_S
    
    public boolean spawn_2(Term arg0, Term arg1){
    	
    	String tcName = null;
    	TupleCentreId tid = null;
    	try{
    		tid=new TupleCentreId(arg1);
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
//    	log("tid = " + tid);
//    	log("arg0 = " + arg0);
    	tcName = tid.getName();
    	
        AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();

    	if(tcName.equals("this")){
    		log("Local spawn triggered...");
	        Term newArg=arg0.copyGoal(v,0);
	        LogicTuple tuArg=new LogicTuple(newArg);
	        InputEvent ce=vm.getCurrentEvent();
//	        AS A FIRST IMPL, CONSIDER THE OWNER AS THE REACTING TC (WHICH IS ALSO TARGET)
//	        log("ce.getReactingTC() = " + ce.getReactingTC());
	        vm.spawnActivity(tuArg, ce.getReactingTC(), ce.getReactingTC());
//	        vm.spawnActivity(tuArg, ce.getSource(), tid);
			InternalEvent ev=new InternalEvent(ce,InternalOperation.makeSpawnR(new LogicTuple(arg0.copyGoal(v,0))));
			ev.setSource(ce.getReactingTC());
	        ev.setTarget(ce.getReactingTC());
			vm.fetchTriggeredReactions(ev);
	        return true;
    	}else{
    		log("Remote spawn triggered...");
	    	InputEvent ce=vm.getCurrentEvent();
			InputEvent out_ev = new InputEvent(ce.getReactingTC(),RespectOperation.makeSpawn(getProlog(), new LogicTuple(arg0.copyGoal(v,0)),null),tid,vm.getCurrentTime());
			out_ev.setIsLinking(true);
			out_ev.setTarget(tid);
			vm.addTemporaryOutputEvent(out_ev);
			return true;
	    }
    	
    }
    
    /********************************************************************
     * ReSpecT guard predicates.
     ********************************************************************/
    
    /**
     * @return true if the ReSpecT VM is in the 'completion phase'.
     */
    public boolean response_0(){
    	Event ev = vm.getCurrentReactionEvent();
        TupleCentreOperation op = ev.getSimpleTCEvent();
//        return op.isResultDefined();
        return op.isResultDefined() && "SpeakingState".equals(vm.getCurrentState());
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
    
    /**
     * @return true if the ReSpecT VM is in the 'invocation phase'.
     */
    public boolean request_0(){
    	Event ev = vm.getCurrentReactionEvent();
        TupleCentreOperation op = ev.getSimpleTCEvent();
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
    
    /**
     * @return true if the ReSpecT operation completed successfully.
     */
    public boolean success_0(){
        Event ev = vm.getCurrentReactionEvent();
        TupleCentreOperation op = ev.getSimpleTCEvent();
        return op.isResultSuccess();
    }
    
    /**
     * @return true if the ReSpecT operation failed.
     */
    public boolean failure_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	TupleCentreOperation op = ev.getSimpleTCEvent();
        return op.isResultFailure();
    }
    
    /**
     * @return true if the final target of the ReSpecT operation is the currently
     * reacting tuplecentre.
     */
    public boolean intra_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId target = ev.getTarget();
    	IId current_tc = this.vm.getId();
//    	IId current_tc = ev.getReactingTC();
    	log("  intra) target = "+target.toString()+", current_tc = "+current_tc.toString());
    	if(current_tc.toString().equals(target.toString()))
    		return true;
    	else
    		return false;
    }
    
    public boolean inter_0(){
    	return !intra_0();
    }
    
    /**
     * @return true if the initial requestor of the ReSpecT operation IS NOT the
     * currently reacting tuplecentre.
     */
    public boolean exo_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId source = ev.getSource();
    	IId current_tc = this.vm.getId();
//    	IId current_tc = ev.getReactingTC();
    	log("  exo) source = "+source.toString()+", current_tc = "+current_tc.toString());
    	if(!current_tc.toString().equals(source.toString()))
    		return true;
    	else
    		return false;
    }
    
    public boolean endo_0(){
    	return !exo_0();
    }
    
    /**
     * @return true if the initial requestor of the ReSpecT operation is an agent
     * (either Java or tuProlog or whatever).
     */
    public boolean from_agent_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId source = ev.getSource();
//    	log("\tfrom_agent) source = "+source.toString());
    	if(source.isAgent())
    		return true;
    	else
    		return false;
    }
    
    /**
     * @return true if the final target of the ReSpecT operation is an agent
     * (either Java or tuProlog or whatever).
     */
    /*
     * When is this guard predicate supposed to succeed? Are there any ReSpecT
     * operation whose final target is an agent? Does this make sense at all?
     */
    public boolean to_agent_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId target = ev.getTarget();
//    	log("\tto_agent) target = "+target.toString());
    	if(target.isAgent())
    		return true;    		
    	else
    		return false;
    }
    
    /**
     * @return true if the initial requestor of the ReSpecT operation is a
     * ReSpecT tuplecentre.
     */
    public boolean from_tc_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId source = ev.getSource();
    	log("  from_tc) source = "+source.toString());
    	if(source.isTC()){
    		log("    source.isTC() is true");
    		return true;    		
    	}else{
    		log("    source.isTC() is false");
    		return false;
    	}
    }
    
    /**
     * @return true if the final target of the ReSpecT operation is a ReSpecT
     * tuplecentre.
     */
    /*
     * When is this guard predicate supposed to fail? Are there any ReSpecT
     * operation whose final target is NOT a tc? Does this make sense at all?
     */
    public boolean to_tc_0(){
    	Event ev = vm.getCurrentReactionEvent();
    	IId target = ev.getTarget();
    	log("  to_tc) target = "+target.toString());
    	if(target.isTC())
    		return true;
    	else
    		return false;
    }
    
    /**
     * @param time the time to compare.
     * 
     * @return true if the ReSpecT reaction has been triggered before the given
     * time.
     */
    public boolean before_1(Term time){
    	Event ev = vm.getCurrentReactionEvent();
    	long evtTime = ev.getTime();
    	long compareTime;
    	try{
    		compareTime = ((alice.tuprolog.Number)time).longValue();
    	}catch(Exception e){
    		return false;
    	}
//    	log("\t before) event = " + evtTime + ", compare = " + compareTime);
    	if(evtTime <= compareTime)
    		return true;
    	else
        	return false;
    }
    
    public boolean after_1(Term time){
    	return !before_1(time);
    }
    
    /********************************************************************
     * ReSpecT composite guard predicates.
     ********************************************************************/
    
    public boolean operation_0(){
//    	log("operation ->");
    	return from_agent_0() && to_tc_0();
    }
    
    public boolean internal_0(){
//    	log("internal ->");
    	return from_tc_0() && to_tc_0() && endo_0() && intra_0();
    }
    
    public boolean link_in_0(){
    	log("link_in ->");
    	return from_tc_0() && to_tc_0() && exo_0() && intra_0();
    }
    
    public boolean link_out_0(){
    	log("link_out ->");
    	return from_tc_0() && to_tc_0() && endo_0() && inter_0();
    }
    
    public boolean between_2(Term time1, Term time2){
//    	log("between ->");
    	return after_1(time1) && before_1(time2);
    }
    
    /********************************************************************
     * ReSpecT reaction observation predicates.
     ********************************************************************/
    
    /**
     * @param predicate the expected ReSpecT predicate currently under solving
     * process.
     * 
     * @return true if the actual ReSpecT predicate currently under solving process
     * is the expected ReSpecT predicate currently under solving
     * process.
     */
    public boolean current_predicate_1(Term predicate){
    	return unify(predicate, new Struct("current_predicate("+predicate+")"));
    }
    
    public boolean event_predicate_1(Term predicate){
//    	log("\tevent_predicate) " + vm.getCurrentReactionTerm().getTerm());
    	return unify(predicate, vm.getCurrentReactionTerm().getTerm());
    }
    
    public boolean start_predicate_1(Term predicate){
    	Event e = vm.getCurrentReactionEvent();
    	if(e.isInternal()){
    		InternalEvent ie = (InternalEvent)e;
//    		log("\tstart_predicate) IE: " + ie.getInputEvent().getSimpleTCEvent().getPredicate());
    		return unify(predicate, ie.getInputEvent().getSimpleTCEvent().getPredicate().toTerm());
    	}
    	if(e.isOutput()){
    		OutputEvent oe = (OutputEvent)e;
//    		log("\tstart_predicate) OE: " + oe.getInputEvent().getSimpleTCEvent().getPredicate());
    		return unify(predicate, oe.getInputEvent().getSimpleTCEvent().getPredicate().toTerm());
    	}
//    	log("\tstart_predicate) E: " + vm.getCurrentReactionTerm().getTerm());
    	return unify(predicate, vm.getCurrentReactionTerm().getTerm());
    }
    
    /**
     * @param tuple the expected logic tuple which directly started the
     * current ReSpecT computation.
     * 
     * @return true if the actual logic tuple which directly started the
     * current ReSpecT computation is the expected logic tuple which directly
     * started the current ReSpecT computation.
     */
    public boolean current_tuple_1(Term tuple){
    	return unify(tuple, new Var());
    }
    
    /**
     * @param tuple
     * 
     * @return
     */
    public boolean event_tuple_1(Term tuple){
    	Term t = vm.getCurrentReactionTerm().getArg(0);
//    	log("\tevent_tuple) " + t);
    	return unify(tuple, t);
    }
    
    public boolean start_tuple_1(Term tuple){
    	Event e = vm.getCurrentReactionEvent();
    	if(e.isInternal()){
    		InternalEvent ie = (InternalEvent)e;
//    		log("\tstart_tuple) IE: " + ie.getInputEvent().getTuple());
    		return unify(tuple, Term.createTerm(""+ie.getInputEvent().getTuple()));
    	}
    	if(e.isOutput()){
    		OutputEvent oe = (OutputEvent)e;
//    		log("\tstart_tuple) OE: " + oe.getInputEvent().getTuple());
    		return unify(tuple, Term.createTerm(""+oe.getInputEvent().getTuple()));
    	}
//    	log("\tstart_tuple) E: " + e.getTuple());
    	return unify(tuple, Term.createTerm(""+e.getTuple()));
    }
    
    /**
     * @param source the expected tuplecentre which is responsible for the 
     * generation of the currently processing ReSpecT reaction.
     * 
     * @return true if the actual tuplecentre  is the expected tuplecentre which
     * is responsible for the generation of the currently processing ReSpecT
     * reaction.
     */
    public boolean current_source_1(Term source){
    	Term t = ((TupleCentreId)vm.getId()).toTerm();
    	return unify(source, t);
    }
    
    /**
     * @param source
     * 
     * @return
     */
    public boolean event_source_1(Term source){
//    	log("\tevent_source) " + vm.getCurrentReactionEvent().getSource());
    	return unify(source,
    			new Struct(vm.getCurrentReactionEvent().getSource().toString()));
    }
    
    public boolean start_source_1(Term source){
    	Event e = vm.getCurrentReactionEvent();
    	if(e.isInternal()){
    		InternalEvent ie = (InternalEvent)e;
//    		log("\tstart_source) IE: " + ie.getInputEvent().getSource());
    		return unify(source,
        			new Struct(ie.getInputEvent().getSource().toString()));
    	}
    	if(e.isOutput()){
    		OutputEvent oe = (OutputEvent)e;
//    		log("\tstart_source) OE: " + oe.getInputEvent().getSource());
    		return unify(source,
        			new Struct(oe.getInputEvent().getSource().toString()));
    	}
//    	log("\tstart_source) E: " + e.getSource());
    	return unify(source,
    			new Struct(e.getSource().toString()));
    }
    
    /**
     * @param target
     * 
     * @return
     */
    public boolean current_target_1(Term target){
//    	log("\tcurrent_target) " + vm.getId().toString());
    	Term t = ((TupleCentreId)vm.getId()).toTerm();
    	return unify(target, t);
    }
    
    public boolean event_target_1(Term target){
//    	log("\tevent_target) " + vm.getCurrentReactionEvent().getTarget());
    	return unify(target, 
    			new Struct(vm.getCurrentReactionEvent().getTarget().toString()));
    }
    
    public boolean start_target_1(Term target){
    	Event e = vm.getCurrentReactionEvent();
    	if(e.isInternal()){
    		InternalEvent ie = (InternalEvent)e;
//    		log("\tstart_target) IE: " + ie.getInputEvent().getTarget());
    		return unify(target, 
        			new Struct(ie.getInputEvent().getTarget().toString()));
    	}
    	if(e.isOutput()){
    		OutputEvent oe = (OutputEvent)e;
//    		log("\tstart_target) OE: " + oe.getInputEvent().getTarget());
    		return unify(target, 
        			new Struct(oe.getInputEvent().getTarget().toString()));
    	}
//    	log("\tstart_target) E: " + e.getTarget());
    	return unify(target, 
    			new Struct(e.getTarget().toString()));
    }
    
    /**
     * @param time the expected time we're at.
     * 
     * @return true if the actual time is the expected time we're at.
     */
    public boolean current_time_1(Term time){
    	long vmTime = vm.getCurrentTime();
//    	log("\t### current_time = " + vmTime + " ###");
	    return unify(time, new alice.tuprolog.Long(vmTime));
	}
    
    /**
     * @param time the expected time at which the current ReSpecT computation
     * has been triggered.
     * 
     * @return true if the actual time at which the current ReSpecT computation
     * has been triggered is the expected time at which the current ReSpecT
     * computation has been triggered.
     */
    public boolean event_time_1(Term time){
	    long reTime = vm.getCurrentReactionEvent().getTime();
//	    log("\tevent_time) " + reTime);
	    return unify(time, new alice.tuprolog.Long(reTime));
	}
    
    public boolean start_time_1(Term time){
    	Event e = vm.getCurrentReactionEvent();
    	if(e.isInternal()){
    		InternalEvent ie = (InternalEvent)e;
//    		log("\tstart_time) IE: " + ie.getInputEvent().getTime());
    		return unify(time, new alice.tuprolog.Long(ie.getInputEvent().getTime()));
    	}
    	if(e.isOutput()){
    		OutputEvent oe = (OutputEvent)e;
//    		log("\tstart_time) OE: " + oe.getInputEvent().getTime());
    		return unify(time, new alice.tuprolog.Long(oe.getInputEvent().getTime()));
    	}
//    	log("\tstart_time) E: " + e.getTime());
    	return unify(time, new alice.tuprolog.Long(e.getTime()));
	}

    /********************************************************************
     * Situated ReSpecT extension: still to test.
     ********************************************************************/
    
    public boolean from_env_0(){
    	return vm.getCurrentReactionEvent().getSource().isEnv(); 
    }
    
    public boolean to_env_0(){
    	return vm.getCurrentReactionEvent().getTarget().isEnv(); 
    }
    
    public boolean env_2(Term key, Term value){
    	if (value instanceof alice.tuprolog.Var){
    		String res=vm.getCurrentReactionEvent().getEventProp(key.getTerm().toString());
    		if (res==null)
    			return false;
    		return unify(value, new Struct(res));
    	}
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
    
    /********************************************************************
     ********************************************************************/
    
    private Prolog getProlog(){
    	return vm.getPrologCore();
    }
    
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
    
    private void log(String s){
    	System.out.println("....[Respect2PLibrary]: " + s);
    }
    
}
