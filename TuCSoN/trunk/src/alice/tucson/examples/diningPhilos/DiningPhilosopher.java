package alice.tucson.examples.diningPhilos;

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

/**
 * A Dining Philosopher: thinks and eats in an endless loop.
 * 
 * @author s.mariani@unibo.it
 */
public class DiningPhilosopher extends TucsonAgent {
	
	private TucsonTupleCentreId myTable;
	int chop1, chop2;
	private SynchACC acc;

	public DiningPhilosopher(String aid, TucsonTupleCentreId table,
			int left, int right) throws TucsonInvalidAgentIdException {
		super(aid);
		myTable = table;
		chop1 = left;
		chop2 = right;
	}

	@Override
	protected void main() {
		acc = getContext();
		// Ugly but effective, pardon me...
		while(true){
			say("Now thinking...");
			think();
			say("I'm hungry, let's try to eat something...");
			/*
			 * Try to get needed chopsticks.
			 */
			if(acquireChops()){
				/*
				 * If successful eat.
				 */
				eat();
				say("I'm done, wonderful meal :)");
				/*
				 * Then release chops.
				 */
				releaseChops();
			}else{
				say("I'm starving!");
			}
		}
	}

	private boolean acquireChops() {
		ITucsonOperation op = null;
		try {
			/*
			 * NB: The 2 needed chopsticks are "perceived" as a single item by
			 * the philosophers, while the coordination medium correctly handle
			 * them separately.
			 */
			op = acc.in(myTable, LogicTuple.parse("chops("+chop1+","+chop2+")"), null);
		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			e.printStackTrace();
		}
		if(op != null)
			return op.isResultSuccess();
		else
			return false;
	}

	private void releaseChops() {
		try {
			acc.out(myTable, LogicTuple.parse("chops("+chop1+","+chop2+")"), null);
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

	private void think() {
		say("...mumble mumble...rat rat...mumble mumble...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void eat() {
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
