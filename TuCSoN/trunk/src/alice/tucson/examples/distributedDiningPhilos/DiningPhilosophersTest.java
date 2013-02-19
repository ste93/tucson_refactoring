package alice.tucson.examples.distributedDiningPhilos;

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
 * 
 * 
 * @author s.mariani@unibo.it
 */
public class DiningPhilosophersTest extends TucsonAgent{
	
	private static final int N_PHILOSOPHERS = 5;
	private String ip;
	private int port;

	public DiningPhilosophersTest(String aid) throws TucsonInvalidAgentIdException {
		super(aid);
		ip = "localhost";
		port = 20504;
	}

	@Override
	protected void main() {
		SynchACC acc = getContext();
		try{
			TucsonTupleCentreId[] seats = new TucsonTupleCentreId[N_PHILOSOPHERS];
			for(int i=0; i<N_PHILOSOPHERS; i++){
//				seats[i] = new TucsonTupleCentreId("seat("+i+","+((i+1)%N_PHILOSOPHERS)+")", ip, (port++)+"");
				seats[i] = new TucsonTupleCentreId("seat("+i+","+((i+1)%N_PHILOSOPHERS)+")", ip, port+"");
				say("Injecting 'seat' ReSpecT specification in tc < " + seats[i].toString() + " >...");
				acc.set_s(seats[i], Utils.fileToString("alice/tucson/examples/distributedDiningPhilos/seat.rsp"), null);
				acc.out(seats[i], LogicTuple.parse("philosopher(thinking)"), null);
			}
			TucsonTupleCentreId table = new TucsonTupleCentreId("table", ip, port+"");
			say("Injecting 'table' ReSpecT specification in tc < " + table.toString() + " >...");
			acc.set_s(table, Utils.fileToString("alice/tucson/examples/distributedDiningPhilos/table.rsp"), null);
			for(int i=0; i<N_PHILOSOPHERS; i++){
				acc.out(table, LogicTuple.parse("chop("+i+")"), null);
			}
			for(int i=0; i<N_PHILOSOPHERS; i++){
				new DiningPhilosopher("'philo-"+i+"'", seats[i]).go();
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
