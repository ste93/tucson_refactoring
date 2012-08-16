package alice.tucson.api;

import alice.logictuple.LogicTuple;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.service.TucsonOperation;
import alice.tucson.service.TupleCentreContainer;
import alice.tuplecentre.core.OperationCompletionListener;
import alice.tuplecentre.core.TupleCentreOperation;

public abstract class SpawnActivity implements OperationCompletionListener{
	
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
	
	public final TucsonIdWrapper<?> getSpawnerId(){
		if(aid == null)
			return new TucsonIdWrapper<TucsonTupleCentreId>(tcid);
		else
			return new TucsonIdWrapper<TucsonAgentId>(aid);
	}
	
	protected final boolean out(TucsonTupleCentreId tcid, LogicTuple tuple){
		if(this.aid != null)
			try {
				TupleCentreContainer.doNonBlockingOperation(TucsonOperation.outCode(), aid, tcid, tuple, this);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		else
			try {
				TupleCentreContainer.doNonBlockingOperation(TucsonOperation.outCode(), this.tcid, tcid, tuple, this);
			} catch (TucsonInvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			}
		return false;
	}
	
	abstract public void doActivity();
	
	@Override
	public void operationCompleted(TupleCentreOperation op) {
		// TODO Auto-generated method stub
		
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
