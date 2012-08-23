package alice.tucson.api;

import java.io.Serializable;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
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
public abstract class SpawnActivity implements Serializable, Runnable{
	
	private static final long serialVersionUID = -6354837455366449916L;
	private TucsonAgentId aid;
	private TucsonTupleCentreId tcid;
	private TucsonTupleCentreId target;
	
//	public SpawnActivity(TucsonAgentId aid){
//		this.aid = aid;
//		tcid = null;
//	}
//	
//	public SpawnActivity(TucsonTupleCentreId tcid){
//		this.tcid = tcid;
//		aid = null;
//	}
	
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
	 * To be overridden by user
	 */
	abstract public void doActivity();
	
	public final void run(){
		if(checkInstantiation())
			doActivity();
	}
	
	/**
	 * We try to enforce a "core" set of Linda-like primitives to be used inside a spawn()
	 */
	
	protected final LogicTuple out(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple in(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple rd(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.rdCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple no(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.noCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.noCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple inp(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple rdp(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdpCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.rdpCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	protected final LogicTuple nop(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.nopCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doBlockingOperation(TucsonOperation.nopCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	public final boolean checkInstantiation(){
		if(aid != null || tcid != null)
			if(target != null)
				return true;
		return false;
	}
	
	/**
	 * TEST METHOD, DO NOT USE
	 */
	public static void main(String[] args){
		SpawnActivity sa = null;
		try {
			sa = new SpawnActivity() {
				@Override
				public void doActivity() {
					System.out.println("[me]: Doing my business...");
				}
			};
			sa.setSpawnerId(new TucsonAgentId("me"));
//			sa.setSpawnerId(new TucsonTupleCentreId("default", "localhost", "20504"));
			sa.setTargetTC(new TucsonTupleCentreId("default", "localhost", "20504"));
		}catch(TucsonInvalidAgentIdException e){
			e.printStackTrace();
		} catch (TucsonInvalidTupleCentreIdException e) {
			e.printStackTrace();
		}
		sa.doActivity();
		System.out.println("[Main]: spawner is: <" + sa.getSpawnerId().getId() + ", " + sa.getSpawnerId().getId().getClass() + ">");
		try {
			LogicTuple tuple = LogicTuple.parse("'pcd.lab.mine.MyActivity'");
			System.out.println("[Main]: tuple = " + tuple);
		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		}
	}
	
}
