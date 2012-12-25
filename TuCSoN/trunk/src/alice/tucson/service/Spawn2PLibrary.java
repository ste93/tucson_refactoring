package alice.tucson.service;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonIdWrapper;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuprolog.Library;
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
				+ "nop(T). \n";
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
		System.out.println("[Spawn2PLibrary]: returning");
		return (res != null);
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
		return (!res.toString().equals(arg.toString()));
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
		return (!res.toString().equals(arg.toString()));
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
		return (!res.toString().equals(arg.toString()));
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
		return (!res.toString().equals(arg.toString()));
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
		return (res.toString().equals(arg.toString()));
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
		return (res.toString().equals(arg.toString()));
	}

}
