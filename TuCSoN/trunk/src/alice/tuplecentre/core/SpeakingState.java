/*
 * Tuple Centre media - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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
package alice.tuplecentre.core;

import java.util.*;

import alice.logictuple.InvalidLogicTupleException;
import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.respect.api.RespectSpecification;
import alice.respect.core.RespectVMContext;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.TCCycleResult.Outcome;

/**
 * This is the speaking state of the TCVM
 *
 * @author  aricci
 */
public class SpeakingState extends TupleCentreVMState {
    
    private TupleCentreVMState reactingState;
    private TupleCentreVMState idleState;
    private boolean noMoreSatisfiablePendingQuery;
    
    public SpeakingState(TupleCentreVMContext vm) {
        super(vm);
    }
    
    public TupleCentreVMState getNextState(){
        if (vm.triggeredReaction())
            return reactingState;
        else if (noMoreSatisfiablePendingQuery)
            return idleState;
        else
        	return this;  
    }
    
    public void resolveLinks(){
        reactingState = vm.getState("ReactingState");
        idleState = vm.getState("IdleState");
    }
    
    public void execute(){
    	
        Iterator it = vm.getPendingQuerySetIterator();
        InputEvent ev = null;
        OutputEvent out_ev = null;
		noMoreSatisfiablePendingQuery=true;
		boolean foundSatisfied = false;
		boolean operationNotPossible = false;
		
		Tuple tuple = null;
		List<Tuple> tupleList = null;
		TupleCentreOperation op = null;

        while (it.hasNext() && !foundSatisfied) {
            
        	try {
            	
	            ev = (InputEvent) (it.next());
	            op = ev.getOperation();
	            
	            if(op.isResultDefined() || ev.isLinking()){
	            	foundSatisfied = true;
	            }else{

	            	if (op.isOut()){
		            	tuple = op.getTupleArgument();
		        		vm.addTuple(tuple);
		        		op.setOpResult(Outcome.SUCCESS);
			            op.setTupleResult(tuple);
			            foundSatisfied = true;
		            } else if (op.isIn()){
		                tuple = vm.removeMatchingTuple(op.getTemplateArgument());
						if (tuple!=null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(tuple);
							foundSatisfied=true;
						}// we do nothing: in is suspensive hence we cannot conclude FAILURE yet!
		            } else if (op.isRd()){
						tuple = vm.readMatchingTuple(op.getTemplateArgument());
						if (tuple!=null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(tuple);
							foundSatisfied=true;
						}// we do nothing: rd is suspensive hence we cannot conclude FAILURE yet!
					} else if (op.isInp()){
	            		tuple = vm.removeMatchingTuple(op.getTemplateArgument());
	            		if(tuple!=null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(tuple);
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}
	            		foundSatisfied=true;
					} else if (op.isRdp()){
	            		tuple = vm.readMatchingTuple(op.getTemplateArgument());
	            		if(tuple!=null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(tuple);
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}
	            		foundSatisfied=true;
					} else if (op.isNo()){
						tuple = vm.readMatchingTuple(op.getTemplateArgument());
						if (tuple==null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(op.getTemplateArgument());
							foundSatisfied=true;
						}// we do nothing: no is suspensive hence we cannot conclude FAILURE yet!
					} else if (op.isNop()){
						tuple = vm.readMatchingTuple(op.getTemplateArgument());
	            		if(tuple==null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(tuple);
	            		}
	            		foundSatisfied=true;
					}
//		            my personal updates
		            else if (op.isInAll()){
		            	List<Tuple> tuples = new LinkedList<Tuple>();
		            	tuples = vm.inAllTuples(op.getTemplateArgument());
		            	op.setOpResult(Outcome.SUCCESS);
		            	op.setTupleListResult(tuples);
	            		foundSatisfied=true;
		            }
		            else if (op.isRdAll()){
		            	List<Tuple> tuples = new LinkedList<Tuple>();
		                tuples = vm.readAllTuples(op.getTemplateArgument());
		                op.setOpResult(Outcome.SUCCESS);
		                op.setTupleListResult(tuples);
	            		foundSatisfied=true;
		            }
		            else if (op.isNoAll()){
		            	List<Tuple> tuples = new LinkedList<Tuple>();
		                tuples = vm.readAllTuples(op.getTemplateArgument());
		                op.setOpResult(Outcome.SUCCESS);
		                op.setTupleListResult(tuples);
	            		foundSatisfied=true;
		            }
		            else if (op.isUrd()){
		                tuple = vm.readUniformTuple(op.getTemplateArgument());
		                if (tuple!=null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(tuple);
							foundSatisfied=true;
						}// we do nothing: urd is suspensive hence we cannot conclude FAILURE yet!
		            }
		            else if (op.isUno()){
		                tuple = vm.readUniformTuple(op.getTemplateArgument());
		                if (tuple==null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(op.getTemplateArgument());
							foundSatisfied=true;
						}// we do nothing: urd is suspensive hence we cannot conclude FAILURE yet!
		            }
		            else if (op.isUin()){
		                tuple = vm.removeUniformTuple(op.getTemplateArgument());
		                if (tuple!=null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(tuple);
							foundSatisfied=true;
						}// we do nothing: uin is suspensive hence we cannot conclude FAILURE yet!
		            }
		            else if (op.isUrdp()){
		                tuple = vm.readUniformTuple(op.getTemplateArgument());
		                if(tuple!=null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(tuple);
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}
	            		foundSatisfied=true;
		            }
		            else if (op.isUnop()){
		            	tuple = vm.readUniformTuple(op.getTemplateArgument());
		                if(tuple==null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(tuple);
	            		}
	            		foundSatisfied=true;
		            }
		            else if (op.isUinp()){
		                tuple = vm.removeUniformTuple(op.getTemplateArgument());
		                if(tuple!=null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(tuple);
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}
	            		foundSatisfied=true;
		            }	            
//		            *********************
					else if (op.isOut_s()){
						tuple = op.getTupleArgument();
						vm.addSpecTuple(tuple);
						op.setOpResult(Outcome.SUCCESS);
			            op.setTupleResult(tuple);
			            foundSatisfied = true;
					} else if (op.isIn_s()){
						tuple = vm.removeMatchingSpecTuple(op.getTemplateArgument());
						if (tuple!=null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(tuple);
							foundSatisfied=true;
						}// we do nothing: in_s is suspensive hence we cannot conclude FAILURE yet!
					} else if (op.isRd_s()){
						tuple = vm.readMatchingSpecTuple(op.getTemplateArgument());
						if (tuple!=null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(tuple);
							foundSatisfied=true;
						}// we do nothing: rd_s is suspensive hence we cannot conclude FAILURE yet!
					} else if (op.isInp_s()){
						tuple = vm.removeMatchingSpecTuple(op.getTemplateArgument());
						if(tuple!=null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(tuple);
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}
	            		foundSatisfied=true;
					} else if (op.isRdp_s()){
						tuple = vm.readMatchingSpecTuple(op.getTemplateArgument());
						if(tuple!=null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(tuple);
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}
	            		foundSatisfied=true;
					} else if (op.isNo_s()){
						tuple=vm.readMatchingSpecTuple(op.getTemplateArgument());
						if (tuple==null){
							op.setOpResult(Outcome.SUCCESS);
				            op.setTupleResult(op.getTemplateArgument());
							foundSatisfied=true;
						}// we do nothing: no_s is suspensive hence we cannot conclude FAILURE yet!
					} else if (op.isNop_s()){
	            		tuple=vm.readMatchingSpecTuple(op.getTemplateArgument());
	            		if(tuple==null){
	            			op.setOpResult(Outcome.SUCCESS);
	            			op.setTupleResult(op.getTemplateArgument());
	            		}else{
	            			op.setOpResult(Outcome.FAILURE);
	            			op.setTupleResult(tuple);
	            		}
	            		foundSatisfied=true;
					}
					
					else if (op.isGet()){
						tupleList = new LinkedList<Tuple>();
						tupleList = vm.getAllTuples();
						op.setOpResult(Outcome.SUCCESS);
						op.setTupleListResult(tupleList);
						foundSatisfied=true;
					}else if(op.isGet_s()){
						Iterator<Tuple> rit = vm.getSpecTupleSetIterator();
						LinkedList<Tuple> reactionList = new LinkedList<Tuple>();
						while(rit.hasNext())
							reactionList.add(rit.next());
						op.setOpResult(Outcome.SUCCESS);
						op.setTupleListResult(reactionList);
						foundSatisfied = true;
		            }else if (op.isSet()){
		            	tupleList = op.getTupleListArgument();
		            	vm.setAllTuples(tupleList);
		            	op.setOpResult(Outcome.SUCCESS);
		            	op.setTupleListResult(tupleList);
		            	foundSatisfied=true;
					}else if(op.isSet_s()){
						tupleList = op.getTupleListArgument();
						vm.setAllSpecTuples(tupleList);
						op.setOpResult(Outcome.SUCCESS);
		            	op.setTupleListResult(tupleList);
		            	foundSatisfied=true;
					}
		            
					else throw new InvalidOperationException();
		            		            
		            if (((RespectVMContext)vm).getRespectVM().getObservers().size()>0)
						((RespectVMContext)vm).getRespectVM().notifyObservableEvent(ev);
	            }
	            
            } catch(InvalidOperationException ex){
            	vm.spy("ERROR: "+ev.getOperation().getType()+"");
            	vm.notifyException(ex);
            	it.remove();
            }
        	
        }

		if (foundSatisfied){
			
			out_ev = new OutputEvent(ev);
			
			if(!ev.isLinking()){
				vm.notifyOutputEvent(out_ev);
//				System.out.println("[SpeakingState]: out_ev = " + out_ev.toString());
				if (((RespectVMContext)vm).getRespectVM().getObservers().size()>0)
					((RespectVMContext)vm).getRespectVM().notifyObservableEvent(out_ev);
			}
			
			if(ev.isLinking() && !op.isResultDefined()){
				out_ev.setTarget(ev.getTarget());
				vm.linkOperation(out_ev);
			}
			
			vm.fetchTriggeredReactions(out_ev);			
			noMoreSatisfiablePendingQuery=false;
			it.remove();
			
		}
        
    }
    
}
