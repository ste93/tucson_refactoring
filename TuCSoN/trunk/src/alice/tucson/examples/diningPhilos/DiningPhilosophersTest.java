package alice.tucson.examples.diningPhilos;

import java.io.IOException;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Classic Dining Philosophers coordination problem tackled by adopting a clear
 * separation of concerns between coordinables (philosophers) and coordination
 * medium (table) thanks to TuCSoN ReSpecT tuple centres programmability.
 * 
 * @author s.mariani@unibo.it
 */
public class DiningPhilosophersTest extends TucsonAgent{
	
	/*
	 * Max number of simultaneously eating philosophers should be N_PHILOSOPHERS-2.
	 */
	private static final int N_PHILOSOPHERS = 5;
	private String ip;
	private String port;

	public DiningPhilosophersTest(String aid) throws TucsonInvalidAgentIdException {
		super(aid);
		/*
		 * To experiment with a distributed setting, launch the TuCSoN Node
		 * hosting the 'table' tuple centre on a remote node.
		 */
		ip = "localhost";
		port = "20504";
	}

	@Override
	protected void main() {
		SynchACC acc = getContext();
		try{
			TucsonTupleCentreId table = new TucsonTupleCentreId("table", ip, port);
			say("Injecting 'table' ReSpecT specification in tc < " + table.toString() + " >...");
			/*
			 * Program the tuple centre by setting a ReSpecT specification (a set
			 * of ReSpecT specification tuples) in its specification space.
			 */
			acc.set_s(table, Utils.fileToString("ds/lab/tucson/respect/diningPhilosophers/table.rsp"), null);
			for(int i=0; i<N_PHILOSOPHERS; i++){
				/*
				 * Init chopsticks required to eat.
				 */
				acc.out(table, LogicTuple.parse("chop("+i+")"), null);
			}
			for(int i=0; i<N_PHILOSOPHERS; i++){
				/*
				 * Start philosophers by telling them which chopsticks pair they
				 * need.
				 */
				new DiningPhilosopher("'philo-"+i+"'", table, i, (i+1)%N_PHILOSOPHERS).go();
			}
			acc.exit();
		} catch (TucsonInvalidTupleCentreIdException e) {
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		} catch (TucsonInvalidAgentIdException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void operationCompleted(ITucsonOperation arg0) { }
	
	/**
	 * 
	 * @param args no args expected
	 */
	public static void main(String[] args){
		try {
			new DiningPhilosophersTest("boot").go();
		} catch (TucsonInvalidAgentIdException e) {
			e.printStackTrace();
		}
	}

}