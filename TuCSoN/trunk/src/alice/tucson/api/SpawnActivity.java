package alice.tucson.api;

import java.io.Serializable;

import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

import alice.tucson.service.TucsonOperation;
import alice.tucson.service.TupleCentreContainer;

/**
 * The "purely algorithmic computation" to be carried out by the spawn() primitive.
 * For a good engineering discipline, there should be no interaction, no communication,
 * no waitings for external stimuli inside <code> doActivity() </code> method: just plain,
 * simple, "old-fashioned" computation.
 * Unique exceptions are a core set of Linda-like primitives allowed to acquire inputs and
 * share outputs.
 * 
 * @author s.mariani@unibo.it
 */
public abstract class SpawnActivity implements Serializable{
	
	private static final long serialVersionUID = -6354837455366449916L;
	private TucsonAgentId aid;
	private TucsonTupleCentreId tcid;
	
	public SpawnActivity(TucsonAgentId aid){
		this.aid = aid;
		tcid = null;
	}
	
	public SpawnActivity(TucsonTupleCentreId tcid){
		this.tcid = tcid;
		aid = null;
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
	 * To be overridden by user
	 */
	abstract public void doActivity();
	
	/**
	 * We try to enforce a "core" set of Linda-like primitives to be used inside a spawn()
	 */
	
	protected final LogicTuple out(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), aid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), this.tcid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple in(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), aid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), this.tcid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple rd(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdCode(), aid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.rdCode(), this.tcid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple no(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.noCode(), aid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.noCode(), this.tcid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple inp(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), aid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), this.tcid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple rdp(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdpCode(), aid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.rdpCode(), this.tcid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple nop(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.nopCode(), aid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.nopCode(), this.tcid, tcid, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * TEST METHOD, DO NOT USE
	 */
	public static void main(String[] args){
		SpawnActivity sa = null;
		try {
			sa = new SpawnActivity(new TucsonAgentId("me")) {
//			sa = new SpawnActivity(new TucsonTupleCentreId("default", "localhost", "20504")) {
				@Override
				public void doActivity() {
					System.out.println("[me]: Doing my business...");
				}
			};
		} catch (TucsonInvalidAgentIdException e) {
			e.printStackTrace();
		}
		sa.doActivity();
		System.out.println("[Main]: spawner is: <" + sa.getSpawnerId().getId() + ", " + sa.getSpawnerId().getId().getClass() + ">");
	}
	
}
