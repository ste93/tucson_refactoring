package alice.tucson.service;

import java.util.Iterator;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuprolog.Library;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * 
 * 
 * @author ste
 */
public class Spawn2PLibrary extends Library{

	private static final long serialVersionUID = 3019036120338145017L;
	private TucsonAgentId aid;
	private TucsonTupleCentreId tcid;
	private TucsonTupleCentreId target;
	
	public Spawn2PLibrary() throws TucsonGenericException{}
	
	public final void setSpawnerId(TucsonAgentId aid){
		this.aid = aid;
		tcid = null;
	}
	
	public final void setSpawnerId(TucsonTupleCentreId tcid){
		aid = null;
		this.tcid = tcid;
	}
	
	public final void setTargetTC(TucsonTupleCentreId tcid){
		target = tcid;
	}
	
	public final TucsonTupleCentreId getTargetTC(){
		return target;
	}
	
	/**
	 * Both agents and the coordination medium itself can spawn() a computation, hence we
	 * need to handle both.
	 * 
	 * @return the "spawner" id (actually, a generic wrapper hosting either a TucsonAgentId
	 * or a TucsonTupleCentreId, accessible with method <code> getId() </code>)
	 */
	public final TucsonIdWrapper<?> getSpawnerId(){
		if(aid == null)
			return new TucsonIdWrapper<TucsonTupleCentreId>(tcid);
		else
			return new TucsonIdWrapper<TucsonAgentId>(aid);
	}
	
	/**
	 * 
	 */
	public String getTheory(){
		return "out(T). \n"
				+ "in(T). \n"
				+ "inp(T). \n"
				+ "rd(T). \n"
				+ "rdp(T). \n"
				+ "no(T). \n"
				+ "nop(T). \n"
				+ "out_all(L). \n"
				+ "in_all(T,L). \n"
				+ "rd_all(T,L). \n"
				+ "no_all(T,L). \n"
				+ "uin(T). \n"
				+ "uinp(T). \n"
				+ "urd(T). \n"
				+ "urdp(T). \n"
				+ "uno(T). \n"
				+ "unop(T). \n";
	}
	
	public boolean out_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.outCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.outCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		return true;
	}

	public boolean in_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.inCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.inCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}

	public boolean rd_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.rdCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.rdCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}

	public boolean inp_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.inpCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.inpCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}

	public boolean rdp_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.rdpCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.rdpCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}
	
	public boolean no_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.noCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.noCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return true;
		else{
			unify(arg0, (Term) op.getTupleResult());
			return false;
		}
	}
	
	public boolean nop_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.nopCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.nopCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return true;
		else{
			unify(arg0, (Term) op.getTupleResult());
			return false;
		}
	}
	
	public boolean out_all_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.out_allCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.out_allCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		return true;
	}
	
	public boolean in_all_2(Term arg0, Term arg1){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.in_allCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.in_allCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg1, list2tuple(op.getTupleListResult()));
		else
			return false;
	}
	
	public boolean rd_all_2(Term arg0, Term arg1){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.rd_allCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.rd_allCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg1, list2tuple(op.getTupleListResult()));
		else
			return false;
	}
	
	public boolean no_all_2(Term arg0, Term arg1){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.no_allCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.no_allCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg1, list2tuple(op.getTupleListResult()));
		else
			return false;
	}
	
	public boolean uin_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.uinCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.uinCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}

	public boolean urd_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.urdCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.urdCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}

	public boolean uinp_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.uinpCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.uinpCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}

	public boolean urdp_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.urdpCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.urdpCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return unify(arg0, (Term) op.getTupleResult());
		else
			return false;
	}
	
	public boolean uno_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.unoCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.unoCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return true;
		else{
			unify(arg0, (Term) op.getTupleResult());
			return false;
		}
	}
	
	public boolean unop_1(Term arg0){
		ITupleCentreOperation op = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.unopCode(), aid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		else
			try {
				op = TupleCentreContainer.doNonBlockingOperation(
						TucsonOperation.unopCode(), tcid, target, arg, null);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		op.waitForOperationCompletion();
		if(op.isResultSuccess())
			return true;
		else{
			unify(arg0, (Term) op.getTupleResult());
			return false;
		}
	}
	
	/**
	 * Utility to convert a list of tuple into a tuple list of tuples
	 * 
	 * @param list the list of tuples to convert
	 * 
	 * @return the tuple list of tuples result of the conversion
	 */
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
