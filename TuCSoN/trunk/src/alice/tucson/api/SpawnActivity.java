package alice.tucson.api;

import java.io.Serializable;
import java.util.List;

import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

import alice.tucson.service.TucsonOperation;
import alice.tucson.service.TupleCentreContainer;

/**
 * The "parallel computation" to be started with a <code>spawn</code> primitive.
 * The spawned activity should be a PURELY COMPUTATIONAL (algorithmic) process, with
 * the purpose to delegate computations to the coordination medium. For this reason,
 * a set of "constrained" Linda primitives are provided: they CANNOT access a remote
 * space. Furthermore, the programmer is strongly encouraged not to put communications,
 * locks or other potentially "extra-algorithmic" features in its SpawnActivity.
 * 
 * @author ste (mailto: s.mariani@unibo.it)
 */
public abstract class SpawnActivity implements Serializable, Runnable{
	
	private static final long serialVersionUID = -6354837455366449916L;
	private TucsonAgentId aid;
	private TucsonTupleCentreId tcid;
	private TucsonTupleCentreId target;
	
	/**
	 * Linda operations used in the spawned activity are performed ON BEHALF of the
	 * agent who issued the <code>spawn</code> (its "owner").
	 * 
	 * @param aid the identifier of the agent "owner" of the spawned activity.
	 */
	public final void setSpawnerId(TucsonAgentId aid){
		this.aid = aid;
		tcid = null;
	}
	
	/**
	 * Linda operations used in the spawned activity are performed ON BEHALF of the
	 * tuplecentre who issued the <code>spawn</code> (its "owner").
	 * 
	 * @param tcid the identifier of the tuplecentre "owner" of the spawned activity.
	 */
	public final void setSpawnerId(TucsonTupleCentreId tcid){
		aid = null;
		this.tcid = tcid;
	}
	
	/**
	 * The tuplecentre target, which will "host" the spawned computation. It is
	 * automagically set by the ReSpecT engine.
	 * 
	 * @param tcid the identifier of the tuplecentre target of the spawned activity.
	 */
	public final void setTargetTC(TucsonTupleCentreId tcid){
		target = tcid;
	}
	
	/**
	 * Gets the tuplecentre identifier hosting the spawned activity. 
	 * 
	 * @return the identifier of the tuplecentre hosting the spawned activity.
	 */
	public final TucsonTupleCentreId getTargetTC(){
		return target;
	}
	
	/**
	 * Both agents and the coordination medium itself can <code>spawn</code> a computation,
	 * hence we need to handle both.
	 * 
	 * @return the "spawner" id (actually, a generic wrapper hosting either a TucsonAgentId
	 * or a TucsonTupleCentreId, accessible with method <code>getId()</code>)
	 * 
	 * @see alice.tucson.api.TucsonIdWrapper TucsonIdWrapper
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
	 * Called by the ReSpecT engine.
	 */
	public final void run(){
		if(checkInstantiation())
			doActivity();
	}
	
	/**
	 * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
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
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
	 */
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
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
	 */
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
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
	 */
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
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.noCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
	 */
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
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
	 */
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
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.rdpCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
	 */
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
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.nopCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.BulkSynchACC BulkSynchACC
	 */
	protected final List<LogicTuple> out_all(LogicTuple tuple){
		if(aid != null)
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.out_allCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.out_allCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.BulkSynchACC BulkSynchACC
	 */
	protected final List<LogicTuple> in_all(LogicTuple tuple){
		if(aid != null)
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.in_allCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.in_allCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.BulkSynchACC BulkSynchACC
	 */
	protected final List<LogicTuple> rd_all(LogicTuple tuple){
		if(aid != null)
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.rd_allCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.rd_allCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.BulkSynchACC BulkSynchACC
	 */
	protected final List<LogicTuple> no_all(LogicTuple tuple){
		if(aid != null)
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.no_allCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(TucsonOperation.no_allCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.UniformSynchACC UniformSynchACC
	 */
	protected final LogicTuple uin(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.UniformSynchACC UniformSynchACC
	 */
	protected final LogicTuple urd(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.UniformSynchACC UniformSynchACC
	 */
	protected final LogicTuple uno(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unoCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unoCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.UniformSynchACC UniformSynchACC
	 */
	protected final LogicTuple uinp(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinpCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.uinpCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.UniformSynchACC UniformSynchACC
	 */
	protected final LogicTuple urdp(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdpCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.urdpCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * @see alice.tucson.api.UniformSynchACC UniformSynchACC
	 */
	protected final LogicTuple unop(LogicTuple tuple){
		if(aid != null)
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unopCode(), aid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				return (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.unopCode(), this.tcid, target, tuple);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	/**
	 * Checks if the activity to spawn has been correctly instantiated.
	 * 
	 * @return true if instantiation is complete, false otherwise.
	 */
	public final boolean checkInstantiation(){
		if(aid != null || tcid != null)
			if(target != null)
				return true;
		return false;
	}
	
	/**
	 * Standard output log utility.
	 * 
	 * @param msg the message to log on standard output.
	 */
	protected void log(String msg){
		if(aid != null)
			System.out.println("["+aid.getAgentName()+"-spawned]: " + msg);
		else
			System.out.println("["+tcid.toString()+"-spawned]: " + msg);
	}
	
}
