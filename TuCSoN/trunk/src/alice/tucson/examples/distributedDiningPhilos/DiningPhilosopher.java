package alice.tucson.examples.distributedDiningPhilos;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class DiningPhilosopher extends TucsonAgent {
	
	private TucsonTupleCentreId mySeat;

	public DiningPhilosopher(String aid, TucsonTupleCentreId seat) throws TucsonInvalidAgentIdException {
		super(aid);
		mySeat = seat;
	}

	@Override
	protected void main() {
		SynchACC acc = getContext();
		ITucsonOperation op;
		// Ugly but effective, pardon me...
		while(true){
			try {
				op = acc.rd(mySeat, LogicTuple.parse("philosopher(thinking)"), null);
				if(op.isResultSuccess()){
					say("Now thinking...");
					think();
				}else{
					say("I'm exploding!");
				}
				say("I'm hungry, let's try to eat something...");
				acc.out(mySeat, LogicTuple.parse("wanna_eat"), null);
				op = acc.rd(mySeat, LogicTuple.parse("philosopher(eating)"), null);
				if(op.isResultSuccess()){
					eating();
					say("I'm done, wonderful meal :)");
					acc.out(mySeat, LogicTuple.parse("wanna_think"), null);
				}else{
					say("I'm starving!");
				}
			} catch (InvalidLogicTupleException e) {
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				e.printStackTrace();
			} catch (UnreachableNodeException e) {
				e.printStackTrace();
			} catch (OperationTimeOutException e) {
				e.printStackTrace();
			}
		}
	}

	private void think() {
		say("...mumble mumble...rat rat...mumble mumble...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void eating() {
		say("...gnam gnam...chomp chomp...munch munch...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void operationCompleted(ITucsonOperation arg0) { }

}
