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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import alice.respect.api.ILinkContext;
import alice.respect.api.RespectSpecification;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tucson.api.SpawnActivity;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.introspection.WSetEvent;
import alice.tucson.parsing.MyOpManager;
import alice.tucson.service.Spawn2PLibrary;
import alice.tucson.service.Spawn2PSolver;
import alice.tuplecentre.core.BehaviourSpecification;
import alice.tuplecentre.api.AgentId;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.core.*;
import alice.tuprolog.*;
import alice.tuprolog.Var;
import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;

/**
 * This class defines a ReSpecT Context
 * as a specialisation of a tuple centre VM context 
 * (defining VM specific structures)
 * 
 * @see alice.tuplecentre.core.TupleCentreVM
 *
 * @author aricci
 * @version 1.0
 */
public class RespectVMContext extends alice.tuplecentre.core.TupleCentreVMContext {

	/**are we setting a specification from outside?*/
	private boolean isExternalSetSpec;
	/**Used to keep trace of theory other than reactions*/
	private Theory noReactionTh;
	/**List of timers scheduled for execution*/
	private List<Timer> timers;
    /** multiset of tuples T */
    private TupleSet tSet;
    /** multiset of specification tuple Sigma */
    private TupleSet tSpecSet;
    /** multiset of pending query set  */
    private PendingQuerySet wSet;
    /** multiset of triggered reactions Z */
    private TRSet zSet;
    /** multiset of triggered timed reactions */
    private TRSet timeSet;
    /** list of temporary output event caused by linkability operation:
     * they are added to the output queue (outputEventList only when the
     * related reaction is successfully executed
     */
    private ArrayList temporaryOutputEventList;
    private RespectSpecification        reactionSpec;
    private boolean transaction;
    private RespectVM vm;
    private Prolog core;
    private Prolog trigCore;
    private Event currentReactionEvent;
    private Struct currentReactionTerm;
    private Object semaphore;
    private RespectTC respectTC; 
    private Prolog matcher = new Prolog();
    
    public RespectVMContext(RespectVM vm, TupleCentreId tid, int queueSize, RespectTC respectTC){
        
    	super(tid, queueSize,respectTC);
        timers = new Vector<Timer>();
        semaphore = new Object();
        tSet=new TupleSet();
        tSpecSet = new TupleSet();
        wSet=new PendingQuerySet();
        zSet=new TRSet();
        this.timeSet=new TRSet();
        this.vm = vm;
        temporaryOutputEventList = new ArrayList();
        this.respectTC = respectTC;
		
        try {
	    	core=new Prolog(new String[]{});
	    	alice.tuprolog.event.OutputListener l=new alice.tuprolog.event.OutputListener() {
	        	public void onOutput(alice.tuprolog.event.OutputEvent ev) {
	            	System.out.print(ev.getMsg());
	    	}};
	    	core.addOutputListener(l);
			core.loadLibrary("alice.tuprolog.lib.BasicLibrary");
			core.loadLibrary("alice.tuprolog.lib.ISOLibrary");
			core.loadLibrary("alice.tuprolog.lib.JavaLibrary");
			((alice.respect.api.Respect2PLibrary)core.loadLibrary("alice.respect.api.Respect2PLibrary")).init(this);
        }catch(Exception ex){
            ex.printStackTrace();
        }
		try {
			trigCore=new Prolog(new String[]{});
			trigCore.loadLibrary("alice.tuprolog.lib.BasicLibrary");
			trigCore.loadLibrary("alice.tuprolog.lib.JavaLibrary");
			trigCore.loadLibrary("alice.tuprolog.lib.ISOLibrary");
            trigCore.loadLibrary("alice.respect.api.Respect2PLibrary");
            ((alice.respect.api.Respect2PLibrary)trigCore.getLibrary("alice.respect.api.Respect2PLibrary")).init(this);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        reactionSpec=new RespectSpecification("");
        this.reset();
        this.isExternalSetSpec = false;

    }

    public boolean triggeredReaction(){
        return !zSet.isEmpty();
    }
    
    public void fetchTriggeredReactions(Event ev){
       
    	synchronized(semaphore){
    		try {
	            currentReactionTerm=null;
	            
	            if (ev.isInput()){
	            	
	            	log("INVOCATION phase");
	                InputEvent ie = (InputEvent)ev;
					RespectOperation op=(RespectOperation)ev.getOperation();
//					log("op.getLogicTupleArgument() = " + op.getLogicTupleArgument());
					
					if (op.isSpawn()){
						currentReactionTerm=new Struct("spawn",op.getLogicTupleArgument().toTerm());
					}else if (op.isOut()){
	                    currentReactionTerm=new Struct("out",op.getLogicTupleArgument().toTerm());
	                }else if (op.isIn()){
		                currentReactionTerm=new Struct("in",op.getLogicTupleArgument().toTerm());
					}else if (op.isRd()){
						currentReactionTerm=new Struct("rd",op.getLogicTupleArgument().toTerm());
	                }else if (op.isInp()){
	                    currentReactionTerm=new Struct("inp",op.getLogicTupleArgument().toTerm());
	                }else if (op.isRdp()){
	                    currentReactionTerm=new Struct("rdp",op.getLogicTupleArgument().toTerm());
	                }else if (op.isNo()){
	                    currentReactionTerm=new Struct("no",op.getLogicTupleArgument().toTerm());
	                }else if (op.isNop()){
	                    currentReactionTerm=new Struct("nop",op.getLogicTupleArgument().toTerm());
	                }else if (op.isOut_s()){
	                	currentReactionTerm = new Struct("out_s", op.getLogicTupleArgument().toTerm());
	                }else if (op.isRd_s()){
	                	currentReactionTerm = new Struct("rd_s", op.getLogicTupleArgument().toTerm());
	                }else if (op.isIn_s()){
	                	currentReactionTerm = new Struct("in_s", op.getLogicTupleArgument().toTerm());
	                }else if (op.isRdp_s()){
	                	currentReactionTerm = new Struct("rdp_s", op.getLogicTupleArgument().toTerm());
	                }else if (op.isInp_s()){
	                	currentReactionTerm = new Struct("inp_s", op.getLogicTupleArgument().toTerm());
		            }else if (op.isNo_s()){
	                    currentReactionTerm=new Struct("no_s",op.getLogicTupleArgument().toTerm());
	                }else if (op.isNop_s()){
	                    currentReactionTerm=new Struct("nop_s",op.getLogicTupleArgument().toTerm());
	                }else if (op.isGetEnv()){
	                	currentReactionTerm = new Struct("getEnv", op.getLogicTupleArgument().getArg(0).toTerm(),op.getLogicTupleArgument().getArg(1).toTerm());
	                }else if (op.isSetEnv()){
	                	currentReactionTerm = new Struct("setEnv", op.getLogicTupleArgument().getArg(0).toTerm(),op.getLogicTupleArgument().getArg(1).toTerm());
	                }else if (op.isTime()){
	                	currentReactionTerm = new Struct("time", op.getLogicTupleArgument().toTerm());
	                }else if (op.isUrd()){
	                	currentReactionTerm = new Struct("urd", op.getLogicTupleArgument().toTerm());
	                }else if (op.isUno())
	                	currentReactionTerm = new Struct("uno", op.getLogicTupleArgument().toTerm());
	                else if (op.isUin()){
	                	currentReactionTerm = new Struct("uin", op.getLogicTupleArgument().toTerm());
	                }else if (op.isUrdp()){
	                	currentReactionTerm = new Struct("urdp", op.getLogicTupleArgument().toTerm());
	                }else if (op.isUnop())
	                	currentReactionTerm = new Struct("unop", op.getLogicTupleArgument().toTerm());
	                else if (op.isUinp()){
	                	currentReactionTerm = new Struct("uinp", op.getLogicTupleArgument().toTerm());
	                }else if (op.isOutAll()){
	                	currentReactionTerm = new Struct("out_all", op.getLogicTupleArgument().toTerm());
	                }else if (op.isInAll()){
	                	currentReactionTerm = new Struct("in_all", op.getLogicTupleArgument().toTerm());
	                }else if (op.isRdAll()){
	                	currentReactionTerm = new Struct("rd_all", op.getLogicTupleArgument().toTerm());
	                }else if (op.isNoAll())
	                	currentReactionTerm = new Struct("no_all", op.getLogicTupleArgument().toTerm());

	            }else if (ev.isOutput()){
	            	
	                alice.tuplecentre.core.OutputEvent oe = (alice.tuplecentre.core.OutputEvent)ev;
					RespectOperation op=(RespectOperation)ev.getOperation();
				
					if(((OutputEvent)ev).isLinking()){
						log("linking event processing");
						
						if(op.isSpawn()){
							currentReactionTerm=new Struct("spawn",op.getLogicTupleArgument().toTerm());
						}else if (op.isOut()){
		                    currentReactionTerm=new Struct("out",op.getLogicTupleArgument().toTerm());
		                }else if (op.isIn()){
			                currentReactionTerm=new Struct("in",op.getLogicTupleArgument().toTerm());
						}else if (op.isRd()){
							currentReactionTerm=new Struct("rd",op.getLogicTupleArgument().toTerm());
		                }else if (op.isInp()){
		                    currentReactionTerm=new Struct("inp",op.getLogicTupleArgument().toTerm());
		                }else if (op.isRdp()){
		                    currentReactionTerm=new Struct("rdp",op.getLogicTupleArgument().toTerm());
		                }else if (op.isNo()){
		                    currentReactionTerm=new Struct("no",op.getLogicTupleArgument().toTerm());
		                }else if (op.isNop()){
		                    currentReactionTerm=new Struct("nop",op.getLogicTupleArgument().toTerm());
		                }else if (op.isOut_s()){
		                	currentReactionTerm = new Struct("out_s", op.getLogicTupleArgument().toTerm());
		                }else if (op.isIn_s()){
			                currentReactionTerm=new Struct("in_s",op.getLogicTupleArgument().toTerm());
						}else if (op.isRd_s()){
							currentReactionTerm=new Struct("rd_s",op.getLogicTupleArgument().toTerm());
		                }else if (op.isInp_s()){
		                    currentReactionTerm=new Struct("inp_s",op.getLogicTupleArgument().toTerm());
		                }else if (op.isRdp_s()){
		                    currentReactionTerm=new Struct("rdp_s",op.getLogicTupleArgument().toTerm());
		                }else if (op.isNo_s()){
		                    currentReactionTerm=new Struct("no_s",op.getLogicTupleArgument().toTerm());
		                }else if (op.isNop_s()){
		                    currentReactionTerm=new Struct("nop_s",op.getLogicTupleArgument().toTerm());
		                }else if (op.isUrd()){
		                	currentReactionTerm = new Struct("urd", op.getLogicTupleArgument().toTerm());
		                }else if (op.isUno())
		                	currentReactionTerm = new Struct("uno", op.getLogicTupleArgument().toTerm());
		                else if (op.isUin()){
		                	currentReactionTerm = new Struct("uin", op.getLogicTupleArgument().toTerm());
		                }else if (op.isUrdp()){
		                	currentReactionTerm = new Struct("urdp", op.getLogicTupleArgument().toTerm());
		                }else if (op.isUnop())
		                	currentReactionTerm = new Struct("unop", op.getLogicTupleArgument().toTerm());
		                else if (op.isUinp()){
		                	currentReactionTerm = new Struct("uinp", op.getLogicTupleArgument().toTerm());
		                }else if (op.isOutAll()){
		                	currentReactionTerm = new Struct("out_all", op.getLogicTupleArgument().toTerm());
		                }else if (op.isInAll()){
		                	currentReactionTerm = new Struct("in_all", op.getLogicTupleArgument().toTerm());
		                }else if (op.isRdAll()){
		                	currentReactionTerm = new Struct("rd_all", op.getLogicTupleArgument().toTerm());
		                }else if (op.isNoAll())
		                	currentReactionTerm = new Struct("no_all", op.getLogicTupleArgument().toTerm());

					}else{
						
						log("COMPLETION phase");
						
						if (op.isSpawn()){
							currentReactionTerm=new Struct("spawn",op.getLogicTupleResult().toTerm());
						}else if (op.isOut()){
		                    currentReactionTerm=new Struct("out",op.getLogicTupleResult().toTerm());
		                }else if (op.isIn()){
		                    currentReactionTerm=new Struct("in",op.getLogicTupleResult().toTerm());
		                }else if (op.isRd()){
							currentReactionTerm=new Struct("rd",op.getLogicTupleResult().toTerm());
						}else if (op.isInp()){
							LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("inp",result.toTerm());
							} else {
								currentReactionTerm=new Struct("inp",op.getLogicTupleArgument().toTerm());
							}
		                }else if (op.isRdp()){
							LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("rdp",result.toTerm());
							} else {
								currentReactionTerm=new Struct("rdp",op.getLogicTupleArgument().toTerm());
							}
		                }else  if (op.isNo()){
							currentReactionTerm=new Struct("no",op.getLogicTupleArgument().toTerm());
		                }else if (op.isNop()){
		                	LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("nop",result.toTerm());
							} else {
								currentReactionTerm=new Struct("nop",op.getLogicTupleArgument().toTerm());
							}
		                }else if (op.isOut_s()){
		                	currentReactionTerm=new Struct("out_s",op.getLogicTupleResult().toTerm());
		                }else if (op.isIn_s()){
		                    currentReactionTerm=new Struct("in_s",op.getLogicTupleResult().toTerm());
		                }else if (op.isRd_s()){
							currentReactionTerm=new Struct("rd_s",op.getLogicTupleResult().toTerm());
						}else if (op.isInp_s()){
							LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("inp_s",result.toTerm());
							} else {
								currentReactionTerm=new Struct("inp_s",op.getLogicTupleArgument().toTerm());
							}
		                }else if (op.isRdp_s()){
							LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("rdp_s",result.toTerm());
							} else {
								currentReactionTerm=new Struct("rdp_s",op.getLogicTupleArgument().toTerm());
							}
		                }else if (op.isNo_s()){
							currentReactionTerm=new Struct("no_s",op.getLogicTupleArgument().toTerm());
		                }else if (op.isNop_s()){
		                	LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("nop_s",result.toTerm());
							} else {
								currentReactionTerm=new Struct("nop_s",op.getLogicTupleArgument().toTerm());
							}
		                }else if (op.isUrd()){
		                	currentReactionTerm = new Struct("urd", op.getLogicTupleArgument().toTerm());
		                }else if (op.isUno())
		                	currentReactionTerm = new Struct("uno", op.getLogicTupleArgument().toTerm());
		                else if (op.isUin()){
		                	currentReactionTerm = new Struct("uin", op.getLogicTupleArgument().toTerm());
		                }else if (op.isUrdp()){
		                	LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("urdp",result.toTerm());
							} else {
								currentReactionTerm=new Struct("urdp",op.getLogicTupleArgument().toTerm());
							}		                
						}else if (op.isUnop()){
		                	LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("unop",result.toTerm());
							} else {
								currentReactionTerm=new Struct("unop",op.getLogicTupleArgument().toTerm());
							}
						}else if (op.isUinp()){
							LogicTuple result = op.getLogicTupleResult();
							if (result!=null){
								currentReactionTerm=new Struct("uinp",result.toTerm());
							} else {
								currentReactionTerm=new Struct("uinp",op.getLogicTupleArgument().toTerm());
							}		                
						}else if (op.isOutAll()){
		                	currentReactionTerm = new Struct("out_all", op.getLogicTupleArgument().toTerm());
		                }else if (op.isInAll()){
		                	currentReactionTerm = new Struct("in_all", op.getLogicTupleArgument().toTerm());
		                }else if (op.isRdAll()){
		                	currentReactionTerm = new Struct("rd_all", op.getLogicTupleArgument().toTerm());
		                }else if (op.isNoAll()){
		                	currentReactionTerm = new Struct("no_all", op.getLogicTupleArgument().toTerm());
		                }
					}
					
	            }else if (ev.isInternal()){
	            	
	            	log("internal event processing");
	                InternalEvent ev1=(InternalEvent)ev;
	                InternalOperation rop=ev1.getInternalOperation();
	                
	                if (rop.isSpawnR()){
	                	currentReactionTerm=new Struct("spawn",rop.getArgument().toTerm());
	                }else if (rop.isOutR()){
	                    currentReactionTerm=new Struct("out",rop.getArgument().toTerm());
	                }else if (rop.isInR()){
	                    currentReactionTerm=new Struct("in",rop.getArgument().toTerm());
	                }else if (rop.isRdR()){
	                    currentReactionTerm=new Struct("rd",rop.getArgument().toTerm());
	                }else if (rop.isNoR()){
	                    currentReactionTerm=new Struct("no",rop.getArgument().toTerm());
	                }else if (rop.isOut_sR()){
	                	currentReactionTerm = new Struct("out_s", rop.getArgument().toTerm());
	                }else if (rop.isIn_sR()){
	                	currentReactionTerm = new Struct("in_s", rop.getArgument().toTerm());
	                }else if (rop.isRd_sR()){
	                	currentReactionTerm = new Struct("rd_s", rop.getArgument().toTerm());
	                }else if (rop.isNo_sR()){
	                    currentReactionTerm=new Struct("no_s",rop.getArgument().toTerm());
	                }else if (rop.isGetEnv()){
	                	currentReactionTerm = new Struct("getEnv", rop.getArgument().getArg(0).toTerm(),rop.getArgument().getArg(1).toTerm());
	                }else if (rop.isSetEnv()){
	                	currentReactionTerm = new Struct("setEnv", rop.getArgument().getArg(0).toTerm(),rop.getArgument().getArg(1).toTerm());
	                }else if (rop.isUrdR()){
	                	currentReactionTerm = new Struct("urd", rop.getArgument().toTerm());
	                }else if (rop.isUnoR())
	                	currentReactionTerm = new Struct("uno", rop.getArgument().toTerm());
	                else if (rop.isUinR()){
	                	currentReactionTerm = new Struct("uin", rop.getArgument().toTerm());
	                }else if (rop.isOutAllR()){
	                	currentReactionTerm = new Struct("out_all", rop.getArgument().toTerm());
	                }else if (rop.isInAllR()){
	                	currentReactionTerm = new Struct("in_all", rop.getArgument().toTerm());
	                }else if (rop.isRdAllR()){
	                	currentReactionTerm = new Struct("rd_all", rop.getArgument().toTerm());
	                }else if (rop.isNoAllR())
	                	currentReactionTerm = new Struct("no_all", rop.getArgument().toTerm());

	            }
	            
	            if (currentReactionTerm!=null){
	            	AbstractMap<Var,Var> v = new LinkedHashMap<Var,Var>();
	            	currentReactionTerm = (Struct)currentReactionTerm.copyGoal(v, 0);
	            	Struct tev=new Struct("reaction",currentReactionTerm,new alice.tuprolog.Var("G"),new alice.tuprolog.Var("R"));
	                SolveInfo info = trigCore.solve(tev);
	                alice.tuprolog.Term guard=null;
	                while (info.isSuccess()){
	                	guard = info.getVarValue("G");
	                	this.currentReactionEvent = ev;
	                	if( this.evalGuard(guard)){
	                		Struct trigReaction = new Struct("reaction",currentReactionTerm,info.getVarValue("R"));
	                		TriggeredReaction tr=new TriggeredReaction(ev,new LogicReaction(trigReaction));
	                		zSet.add(tr);
	                		log("triggered reaction = "+tr.getReaction());
	                	}
	                    if (trigCore.hasOpenAlternatives()){
	                        info=trigCore.solveNext();
	                    } else {
	                        break;
	                    }
	                    trigCore.solveEnd();
	                }
	            }
	            
	        }catch (Exception ex){
//	            notifyException("INTERNAL ERROR: fetchTriggeredReactions "+ev);
				ex.printStackTrace();
	            trigCore.solveEnd();
	        }
        
    	}
    	
    }

    public void evalReaction(TriggeredReaction z){
        	
    	transaction = true;
        tSet.beginTransaction();
        tSpecSet.beginTransaction();
        wSet.beginTransaction();
        zSet.beginTransaction();
        timeSet.beginTransaction();

		temporaryOutputEventList.clear();

        Term goalList=((LogicReaction)z.getReaction()).getStructReaction().getTerm(1);
        currentReactionEvent=z.getEvent();

        SolveInfo info=core.solve(goalList);
        core.solveEnd();
        log("Reaction evaluation success = "+info.isSuccess());
        if (info.isSuccess()){
            if (vm.hasInspectors())
            	vm.notifyInspectableEvent(new ObservableEventReactionOK(this,z));            
			int n = temporaryOutputEventList.size();
			for (int i=0; i<n; i++){
				InputEvent curr = (alice.tuplecentre.core.InputEvent)temporaryOutputEventList.get(i);
				this.addPendingQueryEvent(curr);
			}
        }else {
            if (vm.hasInspectors())
            	vm.notifyInspectableEvent(new ObservableEventReactionFail(this,z));            
        }
        boolean success=info.isSuccess();
        boolean specModified = tSpecSet.operationsPending();
        
        temporaryOutputEventList.clear();
        
        zSet.endTransaction(success);
        wSet.endTransaction(success);
        tSet.endTransaction(success);
        tSpecSet.endTransaction(success);
        timeSet.endTransaction(success);
        transaction = false;
        if(specModified){
        	setReactionSpecHelper(new RespectSpecification(
        			tSpecSet.toString()));
        }
        
    }
    
    public void addTemporaryOutputEvent(InputEvent out){
    	synchronized(this.temporaryOutputEventList){
    		this.temporaryOutputEventList.add(out);
    	}
    }
    
    private boolean evalGuard(Term g){
    	log("guard = " + g);
        SolveInfo info=core.solve(g);
        core.solveEnd();
        log("evaluation = " + info.isSuccess());
        return info.isSuccess();
    }
    
    public void removeReactionSpec(){
    	this.core.clearTheory();
    	this.trigCore.clearTheory();
    	this.tSpecSet.empty();
    }
   
    protected boolean setReactionSpecHelper(BehaviourSpecification spec){
    	
    	if (transaction)
    		return false;
    	
        try {
        	
        	this.timers.clear();
            Struct co=new Struct(spec.toString());
            if (co.isAtom()){
                alice.tuprolog.Theory thspec=new alice.tuprolog.Theory(co.getName());
                core.setTheory(thspec);
//                for(Operator op : core.getCurrentOperatorList()){
//                	log("op = " + op.name + ", " + op.prio);
//                }
                trigCore.setTheory(thspec);
            }else if (co.isList()){
                alice.tuprolog.Theory thspec=new alice.tuprolog.Theory(co);
                core.setTheory(thspec);
                trigCore.setTheory(thspec);
            }else {
                notifyException("Invalid reaction spec:\n"+co);
                return false;
            }

            if((noReactionTh != null) && !this.isExternalSetSpec){
            	core.addTheory(noReactionTh);
            	trigCore.addTheory(noReactionTh);
            }
            reactionSpec=(RespectSpecification)spec;
            Iterator<Term> it;
            it = this.foundTimeReactions();

            while(it.hasNext()){
            	Term current = it.next();
            	Timer currTimer = new Timer();
            	long timeValue = ((alice.tuprolog.Number)current).longValue();
            	long currLocalTime = this.getCurrentTime();
            	long delay;
            	if(timeValue > currLocalTime)
            		delay = timeValue - currLocalTime;
            	else
            		delay = 0;
            	currTimer.schedule(new RespectTimerTask(this,
            			RespectOperation.makeTime(getPrologCore(),
            					new LogicTuple("time",new TupleArgument(current)), null)),delay);
            }
            return true;

        }catch(alice.tuprolog.InvalidTheoryException ex){
        	ex.printStackTrace();
            notifyException("Invalid reaction spec. "+ex.line+" "+ex.pos);
            notifyException(spec.toString());
            return false;
        }catch(Exception ex){
            notifyException("Invalid reaction spec.");
            return false;
        }     
        
    }
    
    protected boolean addReactionSpecHelper(BehaviourSpecification spec){
    
    	if (transaction)
    		return false;

        try {
        	
        	this.timers.clear();
            Struct co=new Struct(spec.toString());
            if (co.isAtom()){
                alice.tuprolog.Theory thspec=new alice.tuprolog.Theory(co.getName());
                core.addTheory(thspec);
                trigCore.addTheory(thspec);
            }else if (co.isList()){
                alice.tuprolog.Theory thspec=new alice.tuprolog.Theory(co);
                core.addTheory(thspec);
                trigCore.addTheory(thspec);
            }else {
                notifyException("Invalid reaction spec:\n"+co);
                return false;
            }

            reactionSpec=(RespectSpecification)spec;
            Iterator<Term> it;
            it = this.foundTimeReactions();
            while(it.hasNext()){
            	Term current = it.next();
            	Timer currTimer = new Timer();
            	long timeValue = ((alice.tuprolog.Number)current).longValue();
            	long currLocalTime = this.getCurrentTime();
            	long delay;
            	if(timeValue > currLocalTime)
            		delay = timeValue - currLocalTime;
            	else
            		delay = 0;
            	currTimer.schedule(new RespectTimerTask(this,
            			RespectOperation.makeTime(getPrologCore(), 
            					new LogicTuple("time",new TupleArgument(current)), null)),delay);
            }
            return true;

        }catch (alice.tuprolog.InvalidTheoryException ex){
            notifyException("Invalid reaction spec. "+ex.line+" "+ex.pos);
            notifyException(spec.toString());
            return false;
        }catch (Exception ex){
            notifyException("Invalid reaction spec.");
            return false;
        }
        
    }
    
    public boolean setReactionSpec(BehaviourSpecification spec){
    	
    	this.isExternalSetSpec = true;
    	noReactionTh = null;
    	Prolog engine = new Prolog();

    	try{
    		engine.solve("retractall(reaction(X,Y,Z)).");
    		engine.solveEnd();
    		Parser parser = new Parser(new MyOpManager(), spec.toString());
    		Term term = parser.nextTerm(true);
//    		log("term = " + term);
    		while(term!=null){
    			engine.solve("assert("+term+").");
    			term = parser.nextTerm(true);
//    			log("term = " + term);
    		}
    		engine.solveEnd();
    		noReactionTh = engine.getTheory();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	boolean result = setReactionSpecHelper(spec);
    	
    	if (result){
    		tSpecSet.empty();
    		try {
    			alice.tuprolog.SolveInfo info = core.solve("reaction(X,Y,Z).");
    				while (true){
    					alice.tuprolog.Term solution = info.getSolution();
    					tSpecSet.add(new LogicTuple(solution));
    					info = core.solveNext();
    				}
    		} catch (alice.tuprolog.NoMoreSolutionException ex) {
    		} catch (alice.tuprolog.NoSolutionException ex){
    		} catch(MalformedGoalException ex) {
    			ex.printStackTrace();
    		}
    	}
    	
    	this.isExternalSetSpec = false;
//    	log("result = " + result);
		return result;
		
    }
 
    public BehaviourSpecification getReactionSpec(){
        return reactionSpec;
    }

    /**
     *  Static services that checks if a source text
     *  contains a valid ReSpecT specification
     *
     *  @return a logic tuple that provides information about
     *          the check: valid is the specification is OK, or
     *          invalid(L) if there are errors (at line L).
     *
     */
    static public LogicTuple checkReactionSpec(String spec){
        Prolog core=new Prolog();
        try {
            Struct co=new Struct(spec);
            if (co.isAtom()){
                alice.tuprolog.Theory thspec=new alice.tuprolog.Theory(co.getName());
                core.setTheory(thspec);
            } else if (co.isList()){
                alice.tuprolog.Theory thspec=new alice.tuprolog.Theory(co);
                core.setTheory(thspec);
            } else {
                core=null;
                return new LogicTuple("invalid",new alice.logictuple.Var());
            }
            core=null;
            return new LogicTuple("valid");
        } catch (alice.tuprolog.InvalidTheoryException ex){
            core=null;
            return new LogicTuple("invalid",new Value(ex.line));
        }
    }

    public Struct getCurrentReactionTerm(){
        return currentReactionTerm;
    }
    
    public Event getCurrentReactionEvent(){
        return currentReactionEvent;
    }

    /**
     * resets the virtual machine to boot state
     */
    public  void reset(){
        tSet.empty();
        wSet.empty();
        zSet.empty();
        timeSet.empty();
        setBootTime();
    }
    
    @Override
	public boolean spawnActivity(Tuple tuple, IId owner, IId targetTC) {
    	try {
    		ClassLoader cl = ClassLoader.getSystemClassLoader();
            URL[] urls = ((URLClassLoader)cl).getURLs();
            log("Known paths:");
            for(URL url: urls)
            	System.out.println("	" + url.getFile());
//            When starting the TuCSoN Node it is necessary to properly add the classpath where to
//            find the Java class (or the Prolog theory) to be executed with the
//            spawn()!!
            LogicTuple t = (LogicTuple)tuple;
            if(!(t.getName().equals("exec") || t.getName().equals("solve"))){
            	log("spawn argument must be a tuple with functor name 'exec' or 'solve'");
            	return false;
            }
            log("---> " + t.getArity());
            if(t.getArity() == 2){
            	log("Prolog theory expected");
            	if(!t.getName().equals("solve")){
                	log("Prolog spawn argument must be a tuple with functor name 'solve'");
                	return false;
                }
            	String theoryPath = alice.util.Tools.removeApices(t.getArg(0).toString());
            	Term goal = t.getArg(1).toTerm();
            	if(theoryPath.endsWith(".pl")){
	            	Prolog solver = new Prolog(new String[]{});
	            	Spawn2PLibrary s2pLib = new Spawn2PLibrary();
	            	if(owner.isAgent()){
						TucsonAgentId aid = new TucsonAgentId(((AgentId)owner).toString());
						log("spawnActivity.aid = " + aid);
						s2pLib.setSpawnerId(aid);
					}else{
						TucsonTupleCentreId tcid = new TucsonTupleCentreId(
								((TupleCentreId)owner).getName(),
								((TupleCentreId)owner).getNode(),
								""+((TupleCentreId)owner).getPort());
						log("spawnActivity.tcid = " + tcid);
						s2pLib.setSpawnerId(tcid);
					}
					TucsonTupleCentreId target = new TucsonTupleCentreId(
							((TupleCentreId)targetTC).getName(),
							((TupleCentreId)targetTC).getNode(),
							""+((TupleCentreId)targetTC).getPort());
					log("spawnActivity.target = " + target);
					s2pLib.setTargetTC(target);
	            	solver.loadLibrary(s2pLib);
	            	solver.loadLibrary("alice.tuprolog.lib.BasicLibrary");
	    			solver.loadLibrary("alice.tuprolog.lib.JavaLibrary");
	    			solver.loadLibrary("alice.tuprolog.lib.ISOLibrary");
//	                solver.loadLibrary("alice.respect.api.Respect2PLibrary");
//	                ((alice.respect.api.Respect2PLibrary)solver.getLibrary("alice.respect.api.Respect2PLibrary")).init(this);
//	            	theoryPath should be a pathname but it is not now!!
	            	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(theoryPath);
					Theory toSpawn = new Theory(new BufferedInputStream(is));
	            	solver.setTheory(toSpawn);
	            	String[] libs = solver.getCurrentLibraries();
	            	log("Known libs:");
	            	for(String lib: libs)
	            		System.out.println("	" + lib);
	            	new Spawn2PSolver(solver, goal).start();
	            	return true;
	            }else{
	            	log("Prolog theory file must end with .pl extension");
	            	return false;
	            }
            }else if(t.getArity() == 1){
            	log("Java class expected");
            	if(!t.getName().equals("exec")){
                	log("Java spawn argument must be a tuple with functor name 'exec'");
                	return false;
                }
            	String className = alice.util.Tools.removeApices(t.getArg(0).toString());
//            	log("---> "+className);
            	if(className.endsWith(".class")){
					Class toSpawn = ClassLoader.getSystemClassLoader().loadClass(className.substring(0, className.length()-6));
					if(SpawnActivity.class.isAssignableFrom(toSpawn)){
						SpawnActivity instance = (SpawnActivity) toSpawn.newInstance();
						if(owner.isAgent()){
							TucsonAgentId aid = new TucsonAgentId(((AgentId)owner).toString());
							log("spawnActivity.aid = " + aid);
							instance.setSpawnerId(aid);
						}else{
							TucsonTupleCentreId tcid = new TucsonTupleCentreId(
									((TupleCentreId)owner).getName(),
									((TupleCentreId)owner).getNode(),
									""+((TupleCentreId)owner).getPort());
							log("spawnActivity.tcid = " + tcid);
							instance.setSpawnerId(tcid);
						}
						TucsonTupleCentreId target = new TucsonTupleCentreId(
								((TupleCentreId)targetTC).getName(),
								((TupleCentreId)targetTC).getNode(),
								""+((TupleCentreId)targetTC).getPort());
						log("spawnActivity.target = " + target);
						instance.setTargetTC(target);
						if(instance.checkInstantiation()){
							new Thread(instance).start();
							return true;
						}
					}else{
						log("Java class to spawn must be assignable from SpawnActivity.class");
		            	return false;
					}
            	}else{
            		log("Java class file must end with .class extension");
                	return false;
            	}
            }else{
            	log("Prolog predicate arity must be 1 (Java class name) or 2 (Prolog theory filepath, goal to solve)");
            	return false;
            }
		} catch (ClassNotFoundException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		} catch (TucsonInvalidTupleCentreIdException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		} catch (TucsonInvalidAgentIdException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		} catch (InvalidLibraryException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		} catch (InvalidTheoryException e) {
			System.err.println("[RespectVMContext]: " + e);
			System.err.println("[RespectVMContext]: " + e.clause);
			System.err.println("[RespectVMContext]: " + e.line);
			System.err.println("[RespectVMContext]: " + e.pos);
			e.printStackTrace();
			return false;
		} catch (InvalidTupleOperationException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
//		} catch (NoMoreSolutionException e) {
//			return true;
		} catch (TucsonGenericException e) {
			System.err.println("[RespectVMContext]: " + e);
			e.printStackTrace();
			return false;
		}
		return false;
	}

    public void addTuple(Tuple t){
        tSet.add((alice.logictuple.LogicTuple)t);
    }
    
    public List<Tuple> addListTuple(Tuple t){
    	List<Tuple> list = new LinkedList<Tuple>();
    	LogicTuple tuple = (LogicTuple)t;
    	while(!(tuple.toString().equals("[]"))){
			try {
				tSet.add(new LogicTuple(tuple.getArg(0)));
				list.add(new LogicTuple(tuple.getArg(0)));
				tuple = new LogicTuple(tuple.getArg(1));
			} catch (InvalidTupleOperationException e) {
				e.printStackTrace();
				System.exit(-1);
			}
    	}
    	return list;
    }

    public  Tuple removeMatchingTuple(TupleTemplate t){
        Tuple tuple=tSet.getMatchingTuple((alice.logictuple.LogicTuple)t);
        return tuple;
    }

    public Tuple readMatchingTuple(TupleTemplate t){
        return tSet.readMatchingTuple((alice.logictuple.LogicTuple)t);
    }
    
    public void addSpecTuple(Tuple t){
    	try {
			if(((LogicTuple)t).getName().equals(","))
				t = new LogicTuple("reaction", ((LogicTuple)t).getArg(0), ((LogicTuple)t).getArg(1).getArg(0), ((LogicTuple)t).getArg(1).getArg(1));
		} catch (InvalidTupleOperationException e) {
			e.printStackTrace();
		}
    	tSpecSet.add((LogicTuple)t);
    		this.setReactionSpecHelper(
   				new alice.respect.api.RespectSpecification(
    					tSpecSet.toString())
				);
    }
    
    public Tuple readMatchingSpecTuple(TupleTemplate t){
    	return tSpecSet.readMatchingTuple((alice.logictuple.LogicTuple)t);
    }

    public Tuple removeMatchingSpecTuple(TupleTemplate t){
    	Tuple tuple = tSpecSet.getMatchingTuple((alice.logictuple.LogicTuple)t);
    	if(tuple != null)
    		this.setReactionSpecHelper(
    				new alice.respect.api.RespectSpecification(
    						tSpecSet.toString())
					);
    	return tuple;
    }
    
    public Iterator getTupleSetIterator(){
        return tSet.getIterator();
    }
    
    public Iterator getSpecTupleSetIterator(){
    	return tSpecSet.getIterator();
    }

    public void emptyTupleSet(){
        tSet.empty();
    }

    public void addPendingQueryEvent(InputEvent w){
        wSet.add(w);
    }

    public Iterator getPendingQuerySetIterator(){
        return wSet.getIterator();
    }

	/**
	 * Removes all events of specified agent
	 *
	 * @param id the identifier of the agent whose events must be removed
	 */
	public void removePendingQueryEventsOf(alice.tuplecentre.api.AgentId id){
		wSet.removeEventsOf(id);
	}
    
	/**
	 * Removes the event related to a specific executed operation  
	 *
	 * @param id identifier of the operation
	 */
	public boolean removePendingQueryEvent(long operationId){
		return wSet.removeEventOfOperation(operationId);
	}
    
    public TriggeredReaction removeTriggeredReaction(){
        if (zSet.isEmpty()){
            return null;
        } else {
            return zSet.get();
        }
    }

    public Iterator getTriggeredReactionSetIterator(){
        return zSet.getIterator();
    }
    
    public void notifyNewInputEvent() {
        this.vm.notifyNewInputEvent();
    }
    
    public void notifyInputEnvEvent(InputEvent in){
    	addEnvInputEvent(in);
    	vm.notifyNewInputEvent();
    }
    
    public void notifyInputEvent(InputEvent in){ 	
    	this.addInputEvent(in); 	
    	this.notifyNewInputEvent();
    }
    
	public LogicTuple[] getTSet(LogicTuple filter){
		LogicTuple[] ltSet = tSet.toArray();
		ArrayList<LogicTuple> supportList = new ArrayList<LogicTuple>();
		if (filter == null)
			return ltSet;
		for(LogicTuple tuple : ltSet)
			if (filter.match(tuple))
				supportList.add(tuple);		
		return supportList.toArray(new LogicTuple[0]);
	}
	
	public WSetEvent[] getWSet(LogicTuple filter){
		Event[] ev = wSet.toArray();
		ArrayList<WSetEvent> events = new ArrayList<WSetEvent>();
		if (filter == null){
			for(Event e: ev){
				events.add(new WSetEvent(((RespectOperation)e.getOperation()).toTuple(), e.getSource(), e.getTarget()));
			}
			return events.toArray(new WSetEvent[0]);
		}
		LogicTuple[] tuples = new LogicTuple[this.wSet.size()];
//		ArrayList<Event> supportList = new ArrayList<Event>();				
		for(int i=0;i<tuples.length;i++){			
			tuples[i] = ((RespectOperation)ev[i].getOperation()).toTuple();
		}
		int i = 0;
		for(LogicTuple tuple : tuples){
			if (filter.match(tuple))
				events.add(new WSetEvent(((RespectOperation)ev[i].getOperation()).toTuple(), ev[i].getSource(), ev[i].getTarget()));
			i++;
		}
		return events.toArray(new WSetEvent[0]);		
	}
	
	public void setWSet(List<LogicTuple> wSet){
		
		this.wSet.empty();
		
		for(LogicTuple t : wSet){
			
			String operation = t.toString();
			String opKind = operation.substring(0,2);
			
			if(opKind.equals("rd")){
				
				String tupla = operation.substring(3,operation.length()-1);
				LogicTuple logicTuple = null;
				try{
					logicTuple = LogicTuple.parse(tupla);
					RespectOperation op = RespectOperation.makeRd(getPrologCore(), logicTuple, null);
					vm.doOperation(null,op);
				}catch (InvalidLogicTupleException e1){
					e1.printStackTrace();
				} catch (OperationNotPossibleException e) {
					e.printStackTrace();
				}
				
			}else if(opKind.equals("in")){
				
				String tupla = operation.substring(3,operation.length()-1);
				LogicTuple logicTuple = null;
				try{
					logicTuple = LogicTuple.parse(tupla);
					RespectOperation op = RespectOperation.makeIn(getPrologCore(), logicTuple,null);
					vm.doOperation(null,op);
				}catch (InvalidLogicTupleException e1){
					e1.printStackTrace();
				} catch (OperationNotPossibleException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}

	public LogicTuple[] getTRSet(LogicTuple t){
		TriggeredReaction[] trig = this.zSet.toArray();
		LogicTuple[] tuples = new LogicTuple[trig.length];
		for(int i=0;i<tuples.length;i++){
			Term term = ((LogicReaction)trig[i].getReaction()).getStructReaction().getTerm();
			tuples[i] = new LogicTuple(term);
		}
		return tuples;
	}

	public Iterator<Term> foundTimeReactions() {
		
		Vector<Term> foundReactions = new Vector<Term>();
		try {
        	Struct timed = new Struct("time",new alice.tuprolog.Var("Time"));
        	Struct tev=new Struct("reaction",timed,new alice.tuprolog.Var("G"),new alice.tuprolog.Var("R"));
        	SolveInfo info = trigCore.solve(tev);
            alice.tuprolog.Term guard=null;
            while (info.isSuccess()){
            	foundReactions.add(info.getVarValue("Time"));
                if (trigCore.hasOpenAlternatives())
                    info=trigCore.solveNext();
                else
                    break;
                trigCore.solveEnd();
            }
        }catch (Exception ex){
            notifyException("INTERNAL ERROR: fetchTimedReactions ");
			ex.printStackTrace();
            trigCore.solveEnd();
        }
        return foundReactions.iterator();
			
	}
	
	public void fetchTimedReactions(Event ev) {
		
		try {
            
     		boolean timedReaction = false;
        	Term timed = (((RespectOperation)ev.getOperation()).getLogicTupleArgument().toTerm());
        	Struct tev=new Struct("reaction",timed,new alice.tuprolog.Var("G"),new alice.tuprolog.Var("R"));
        	SolveInfo info = trigCore.solve(tev);
            alice.tuprolog.Term guard=null;
            while (info.isSuccess()){
            	guard = info.getVarValue("G");
            	this.currentReactionEvent = ev;
            	if( this.evalGuard(guard)){
            		Term reactions = info.getVarValue("R");
            		Struct trigReaction = new Struct("reaction",timed,reactions);
            		TriggeredReaction tr=new TriggeredReaction(ev,new LogicReaction(trigReaction));
            		this.timeSet.add(tr);
            		timedReaction = true;
            		SolveInfo removeInfo = this.core.solve("retract(reaction( "+timed+", (G),("+reactions+"))) .");
            		this.core.solveEnd();
            	}
                if (trigCore.hasOpenAlternatives())
                    info=trigCore.solveNext();
                else
                    break;
                trigCore.solveEnd();
                SolveInfo removeInfo = this.trigCore.solve("retract(reaction( "+timed+", (G),("+info.getVarValue("R")+"))) .");
        		trigCore.solveEnd();
            }
    
//		}catch (Exception ex){
//            notifyException("INTERNAL ERROR: fetchTimedReactions "+ev);
//            trigCore.solveEnd();
//        }
            
		} catch(NoMoreSolutionException e){
			trigCore.solveEnd();
		} catch(NoSolutionException e){
			trigCore.solveEnd();
		} catch (MalformedGoalException e) {
			notifyException("INTERNAL ERROR: fetchTimedReactions "+ev);
			trigCore.solveEnd();
		}
		
	}

	public boolean time_triggeredReaction() {
		return !timeSet.isEmpty();
	}

	public TriggeredReaction removeTimeTriggeredReaction() {
		if (timeSet.isEmpty())
            return null;
        else
            return timeSet.get();
	}
	
	public void updateSpecAfterTimedReaction(TriggeredReaction tr) {
		LogicReaction lr = (LogicReaction)tr.getReaction();
		Struct rStruct = lr.getStructReaction();
		Struct rg = new Struct(rStruct.getName(),
			rStruct.getArg(0),
			new Var(),
			rStruct.getArg(1)
		);
		this.removeMatchingSpecTuple(new LogicTuple(rg));
	}

	public void linkOperation(OutputEvent oe) {
		TupleCentreId target = (TupleCentreId)oe.getTarget();
		try{
			TupleCentreOperation op = oe.getOperation();
			op.addListener(new CompletionListener(oe,target));
			ILinkContext link = RespectTCContainer.getRespectTCContainer().getLinkContext(target);
			link.doOperation((TupleCentreId)oe.getSource(), op);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	class CompletionListener implements OperationCompletionListener{

    	private OutputEvent inter;
    	private TupleCentreId currentTC;
    	
    	public CompletionListener(OutputEvent inter,TupleCentreId source){
    		this.inter = inter;
    		this.currentTC = source;
    	}
    	
		public void operationCompleted(TupleCentreOperation arg0) {
			arg0.removeListener();
			InputEvent res = new InputEvent(inter.getTarget(),arg0,currentTC,getCurrentTime());
			notifyInputEvent(res);
		}
    	
    }

	public List<Tuple> getAllTuples() {
		List<Tuple> tl = new LinkedList<Tuple>();
		Iterator<LogicTuple> it = this.tSet.getIterator();
		while(it.hasNext())
			tl.add((Tuple)it.next());
		return tl;
	}
	
	public void setAllTuples(List<Tuple> tupleList) {		
		this.tSet.empty();
		for(Tuple t : tupleList)
			this.addTuple(t);
	}
	
	public void setAllSpecTuples(List<Tuple> tupleList) {		
		this.tSpecSet.empty();
		for(Tuple t : tupleList)
			this.addSpecTuple(t);
	}
	
	public RespectVM getRespectVM(){
		return vm;
	}
	
	public Prolog getPrologCore(){
		return matcher;
	}
	
	public List<Tuple> inAllTuples(TupleTemplate t) {
		List<Tuple> tl = new LinkedList<Tuple>();
		TupleTemplate t2=t;
        Tuple tuple=removeMatchingTuple(t2);
//        if(tuple ==null) return null;
		while(tuple!=null){
			t2=t;
			tl.add((Tuple)tuple);
			tuple=removeMatchingTuple(t2);
		}
		return tl;
	}
	
	public List<Tuple> readAllTuples(TupleTemplate t) {
		List<Tuple> tl = new LinkedList<Tuple>();
		TupleTemplate t2=t;
        Tuple tuple=removeMatchingTuple(t2);
//        if(tuple==null) return null;
        while(tuple!=null){
			t2=t;
			tl.add((Tuple)tuple);
			tuple=removeMatchingTuple(t2);
		}
		List<Tuple> tl2 = tl;
		Iterator<Tuple> it = tl2.iterator();
		while(it.hasNext()){
			addTuple(it.next());
		}
		return tl;
	}
	
	public Tuple readUniformTuple(TupleTemplate t){
		List<Tuple> tl = new LinkedList<Tuple>();
		tl = readAllTuples(t);
		if(tl==null)
			return null;
		else{
			int extracted = new Random().nextInt(tl.size());
			return tl.get(extracted);
		}
	}
	
	public Tuple removeUniformTuple(TupleTemplate t){
		List<Tuple> tl = new LinkedList<Tuple>();
		tl = readAllTuples(t);
		if(tl == null)
			return null;
		else{
			int extracted = new Random().nextInt(tl.size());
			Tuple toRemove = tl.get(extracted);
			tSet.getMatchingTuple((LogicTuple)toRemove);
			return toRemove;
		}
	}
	
	private void log(String s){
		System.out.println("..[RespectVMContext]: " + s);
	}
	
}


