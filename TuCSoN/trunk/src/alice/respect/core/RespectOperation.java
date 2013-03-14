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

import java.util.LinkedList;
import java.util.List;

import alice.respect.api.IRespectOperation;
import alice.respect.api.RespectSpecification;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.core.*;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;

/**
 * This class represents a ReSpecT operation.
 * 
 * @author aricci
 */
public class RespectOperation extends TupleCentreOperation implements IRespectOperation {
	
	public static final int OPTYPE_TIME = 100;
	public static final int OPTYPE_GET_ENV = 101;
	public static final int OPTYPE_SET_ENV = 102;
	public static final int OPTYPE_ENV = 103;
	
	protected RespectOperation(Prolog p, int type, Tuple t, OperationCompletionListener l){
		super(p, type,t,l);
	}
	
	protected RespectOperation(Prolog p, int type, TupleTemplate t, OperationCompletionListener l){
		super(p, type,t,l);
	}
	
	protected RespectOperation(Prolog p, int type, List<Tuple> tupleList, OperationCompletionListener l){
		super(p, type, tupleList, l);
	}

	/**
	 * Gets the result of the operation,
	 * 
	 * @return
	 */
	public LogicTuple getLogicTupleResult(){
		return (LogicTuple)getTupleResult();
	}
	
	/**
	 * Gets the results of a get operation
	 */
	public List<LogicTuple> getLogicTupleListResult(){
		List<Tuple> tl = this.getTupleListResult();
		List<LogicTuple> tll = new LinkedList<LogicTuple>();
		for(Tuple t :tl){
			tll.add((LogicTuple)t);
		}
		return tll;
	}
	
	public Term getTermTupleListResult(){
		List<LogicTuple> listIn = this.getLogicTupleListResult();
		if(listIn==null) return null;
		else{
			Term[] test = new Term[listIn.size()];
			int numTuples = listIn.size();
			for(int i=0;i<numTuples;i++){
				test[i] = listIn.get(i).toTerm();
			}
			Term result = new Struct(test);
			return result;
		}
	}
	
	/**
	 * Gets the argument of the operation
	 * 
	 * @return
	 */
	public LogicTuple getLogicTupleArgument(){
		if (isOut() || isOut_s() || isOutAll() || isSpawn()){
			return (LogicTuple)getTupleArgument();
		} else {
			return (LogicTuple)getTemplateArgument();
		} 
	}

	public static RespectOperation makeSpawn(Prolog p, LogicTuple t, OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_SPAWN,(Tuple)t, l);
	}
	
	public static RespectOperation makeOut(Prolog p, LogicTuple t, OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_OUT,(Tuple)t, l);
	}		
	
	public static RespectOperation makeIn(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_IN,(TupleTemplate)t,l);
	}

	public static RespectOperation makeRd(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_RD,(TupleTemplate)t,l);
	}

	public static RespectOperation makeInp(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_INP,(TupleTemplate)t,l);
	}

	public static RespectOperation makeRdp(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_RDP,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeNo(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_NO,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeNop(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_NOP,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeGet(Prolog p, LogicTuple t, OperationCompletionListener l){
		RespectOperation temp=new RespectOperation(p, RespectOperation.OPTYPE_GET, (Tuple)t, l);
		return temp;
	}
	
	public static RespectOperation makeSet(Prolog p, LogicTuple logicTuple, OperationCompletionListener l) throws InvalidLogicTupleException{
		if(logicTuple.toString().equals("[]"))
			return new RespectOperation(p, RespectOperation.OPTYPE_SET, new LinkedList<Tuple>(), l);
		List<Tuple> list = new LinkedList<Tuple>();
		LogicTuple cpy = null;
		try {
			cpy = LogicTuple.parse(logicTuple.toString());
		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		}
		TupleArgument arg;
		try {
			arg = cpy.getArg(0);
			while(arg != null){
				if(!arg.isList()){
					LogicTuple t1 = new LogicTuple(arg);
					list.add(t1);
					arg = cpy.getArg(1);
				}else{
					LogicTuple t2 = new LogicTuple(arg);
					cpy = t2;
					if(!cpy.toString().equals("[]"))
						arg = cpy.getArg(0);
					else
						arg = null;
				}
			}
		} catch (InvalidTupleOperationException e) {
			e.printStackTrace();
		}
		RespectOperation temp=new RespectOperation(p, RespectOperation.OPTYPE_SET, list, l);
		return temp;
	}
	
	public static RespectOperation makeOutAll(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_OUT_ALL,(Tuple)t,l);
	}
	
	public static RespectOperation makeInAll(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_IN_ALL,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeRdAll(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_RD_ALL,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeNoAll(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_NO_ALL,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeUrd(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_URD,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeUin(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_UIN,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeUno(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_UNO,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeUrdp(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_URDP,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeUinp(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_UINP,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeUnop(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_UNOP,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeOut_s(Prolog p, LogicTuple t, OperationCompletionListener l){
		return new RespectOperation(p, RespectOperation.OPTYPE_OUT_S, (Tuple)t, l);
	}
	
	public static RespectOperation makeIn_s(Prolog p, LogicTuple t, OperationCompletionListener l){
		return new RespectOperation(p, RespectOperation.OPTYPE_IN_S, (TupleTemplate)t, l);
	}
	
	public static RespectOperation makeRd_s(Prolog p, LogicTuple t, OperationCompletionListener l){
		return new RespectOperation(p, RespectOperation.OPTYPE_RD_S, (TupleTemplate)t, l);
	}
	
	public static RespectOperation makeInp_s(Prolog p, LogicTuple t, OperationCompletionListener l){
		return new RespectOperation(p, RespectOperation.OPTYPE_INP_S, (TupleTemplate)t, l);
	}
	
	public static RespectOperation makeRdp_s(Prolog p, LogicTuple t, OperationCompletionListener l){
		return new RespectOperation(p, RespectOperation.OPTYPE_RDP_S, (TupleTemplate)t, l);
	}
	
	public static RespectOperation makeNo_s(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_NO_S,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeNop_s(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, TupleCentreOperation.OPTYPE_NOP_S,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeGet_s(Prolog p, LogicTuple t, OperationCompletionListener l){
		RespectOperation temp=new RespectOperation(p, RespectOperation.OPTYPE_GET_S, (Tuple)t, l);
		return temp;
	}
	
	public static RespectOperation makeSet_s(Prolog p, LogicTuple logicTuple,
			OperationCompletionListener l) {
		if(logicTuple.toString().equals("[]"))
			return new RespectOperation(p, RespectOperation.OPTYPE_SET_S, new LinkedList<Tuple>(), l);
		List<Tuple> list = new LinkedList<Tuple>();
		LogicTuple cpy = null;
		try {
			cpy = LogicTuple.parse(logicTuple.toString());
		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		}
		TupleArgument arg;
		try {
			arg = cpy.getArg(0);
			while(arg != null){
				if(!arg.isList()){
					LogicTuple t1 = new LogicTuple(arg);
					list.add(t1);
					arg = cpy.getArg(1);
				}else{
					LogicTuple t2 = new LogicTuple(arg);
					cpy = t2;
					if(!cpy.toString().equals("[]"))
						arg = cpy.getArg(0);
					else
						arg = null;
				}
			}
		} catch (InvalidTupleOperationException e) {
			e.printStackTrace();
		}
		RespectOperation temp=new RespectOperation(p, RespectOperation.OPTYPE_SET_S, list, l);
		return temp;
	}
	
	public static RespectOperation makeSet_s(Prolog p, RespectSpecification spec, OperationCompletionListener l){
		RespectOperation temp = null;
		try {
			temp = new RespectOperation(p, RespectOperation.OPTYPE_SET_S, (Tuple)LogicTuple.parse(spec.toString()), l);
		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public static RespectOperation makeSet_s(Prolog p, OperationCompletionListener l){
		return new RespectOperation(p, OPTYPE_SET_S, new LogicTuple(), l);
	}
	
	public static RespectOperation makeTime(Prolog p, LogicTuple t,OperationCompletionListener l){
		return new RespectOperation(p, RespectOperation.OPTYPE_TIME,(TupleTemplate)t,l);
	}
	
	public static RespectOperation makeGetEnv(Prolog p, LogicTuple t, OperationCompletionListener l){
		RespectOperation temp=new RespectOperation(p, RespectOperation.OPTYPE_GET_ENV, (TupleTemplate)t, l);
		temp.setTupleResult(t);
		return temp;
	}
	
	public static RespectOperation makeSetEnv(Prolog p, LogicTuple t, OperationCompletionListener l){
		RespectOperation temp= new RespectOperation(p, RespectOperation.OPTYPE_SET_ENV, (TupleTemplate)t, l);
		temp.setTupleResult(t);
		return temp;
	}
	
	public LogicTuple toTuple(){
		LogicTuple t=null;
		Term[] tl=null;
		if (isOperationCompleted()){
			t = getLogicTupleResult();
		} else {
			t = getLogicTupleArgument();
		}
		String opName;
		if (isSpawn())
			opName = "spawn";
		else if (isOut()){
			opName = "out";
		}else if (isIn()){
			opName = "in";
		}else if (isRd()){
			opName = "rd";
		}else if (isInp()){
			opName = "inp";
		} else if (isRdp()){
			opName = "rdp";
		}else if (isNo()){
			opName = "no";
		}else if (isNop()){
			opName = "nop";
		}else if (isOutAll()){
			opName = "out_all";
		}else if (isInAll()){
			opName = "in_all";
		}else if (isRdAll()){
			opName = "rd_all";
		}else if (isNoAll())
			opName = "no_all";
		else if (isUrd()){
			opName = "urd";
		}else if (isUin()){
			opName = "uin";
		}else if (isUno())
			opName = "uno";
		else if (isUrdp()){
			opName = "urdp";
		}else if (isUinp()){
			opName = "uinp";
		}else if (isUnop())
			opName = "unop";
		else if (isGet()){
			opName = "get";
			LogicTuple[] tupleL=new LogicTuple[]{};
			tupleL=this.getLogicTupleListResult().toArray(tupleL);
			tl = new Term[tupleL.length];
			for(int i=0;i<tupleL.length;i++){
				tl[i]=(tupleL[i]).toTerm();
			}
		} else if (isSet()){
			opName = "set";
			LogicTuple[] tupleL=new LogicTuple[]{};
			tupleL=this.getTupleListArgument().toArray(tupleL);
			tl = new Term[tupleL.length];
			for(int i=0;i<tupleL.length;i++){
				tl[i]=(tupleL[i]).toTerm();
			}
		}else if (isOut_s()){
			opName = "out_s";
		} else if (isIn_s()){
			opName = "in_s";
		} else if (isRd_s()){
			opName = "rd_s";
		} else if (isInp_s()){
			opName = "inp_s";
		} else if (isRdp_s()){
			opName = "rdp_s";
		} else if (isNo_s()){
			opName = "no_s";
		}else if (isNop_s()){
			opName = "nop_s";
		}else if (isGet_s()){
			opName = "get_s";
			LogicTuple[] tupleL=new LogicTuple[]{};
			tupleL=this.getLogicTupleListResult().toArray(tupleL);
			tl = new Term[tupleL.length];
			for(int i=0;i<tupleL.length;i++){
				tl[i]=(tupleL[i]).toTerm();
			}
		}else if (isSet_s()){
			opName = "set_s";
			LogicTuple[] tupleL=new LogicTuple[]{};
			tupleL=this.getTupleListArgument().toArray(tupleL);
			tl = new Term[tupleL.length];
			for(int i=0;i<tupleL.length;i++){
				tl[i]=(tupleL[i]).toTerm();
			}
		}
		else if (isGetEnv()){
			return t;
		} else if (isEnv()){
			opName = "env";
		} else if (isSetEnv()){
			return t;
		} else if (isTime()){
			opName = "time";
		} else {
			opName = "unknownOp";
		}
		return new LogicTuple(opName, new TupleArgument(tl!=null ? new Struct(tl) : t.toTerm()));
	}

	public String toString(){
		return toTuple().toString();
	}
	public boolean isTime() {
		return getType()==OPTYPE_TIME;
	}
	public boolean isEnv() {
		return getType()==OPTYPE_ENV;
	}
	public boolean isGetEnv() {
		return getType()==OPTYPE_GET_ENV;
	}
	public boolean isSetEnv() {
		return getType()==OPTYPE_SET_ENV;
	}

}
	
