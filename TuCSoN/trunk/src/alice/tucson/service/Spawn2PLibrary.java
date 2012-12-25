package alice.tucson.service;

import java.util.Iterator;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonIdWrapper;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
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
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}

	public boolean in_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}

	public boolean rd_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}

	public boolean inp_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}

	public boolean rdp_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdpCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdpCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}
	
	public boolean no_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.noCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.noCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}
	
	public boolean nop_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.nopCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.nopCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}
	
	public boolean out_all_1(Term arg0){
		List<LogicTuple> res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.out_allCode(), aid, target, arg);
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
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.out_allCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, list2tuple(res));
	}
	
	public boolean in_all_2(Term arg0, Term arg1){
		List<LogicTuple> res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.in_allCode(), aid, target, arg);
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
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.in_allCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg1, list2tuple(res));
	}
	
	public boolean rd_all_2(Term arg0, Term arg1){
		List<LogicTuple> res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.rd_allCode(), aid, target, arg);
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
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.rd_allCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg1, list2tuple(res));
	}
	
	public boolean no_all_2(Term arg0, Term arg1){
		List<LogicTuple> res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.no_allCode(), aid, target, arg);
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
				res = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.no_allCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg1, list2tuple(res));
	}
	
	public boolean uin_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}

	public boolean urd_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}

	public boolean uinp_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinpCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinpCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}

	public boolean urdp_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdpCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdpCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}
	
	public boolean uno_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unoCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unoCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}
	
	public boolean unop_1(Term arg0){
		LogicTuple res = null;
		LogicTuple arg = new LogicTuple(arg0);
		if(aid != null)
			try {
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unopCode(), aid, target, arg);
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
				res = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unopCode(), tcid, target, arg);
			} catch (TucsonInvalidLogicTupleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			} catch (TucsonOperationNotPossibleException e) {
				System.err.println("[Spawn2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		return unify(arg0, res.toTerm());
	}
	
	/**
	 * Utility to convert a list of tuple into a tuple list of tuples
	 * 
	 * @param list the list of tuples to convert
	 * 
	 * @return the tuple list of tuples result of the conversion
	 */
	private Term list2tuple(List<LogicTuple> list){
		Term [] termArray = new Term[list.size()];
		Iterator<LogicTuple> it = list.iterator();
		int i=0;
		while(it.hasNext()){
			termArray[i] = ((LogicTuple)it.next()).toTerm();
			i++;
		}
		return new Struct(termArray);
	}

}
