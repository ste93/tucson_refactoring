package alice.tucson.examples.spawnedWorkers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Master thread of a master-worker architecture.
 * Given a list of TuCSoN Nodes (hopefully up & listening), it submits jobs
 * regarding factorial computation, then collects expected results.
 * 
 * @author s.mariani@unibo.it
 */
public class MasterAgent extends TucsonAgent {
	
	private final int ITERs;
	private final int MAX_FACT;
	private boolean die;
	private LinkedList<TucsonTupleCentreId> tids;
	private EnhancedSynchACC acc;
	private int reqID;
	private HashMap<Integer, Integer> pendings;

	/**
	 * @param aid agent name
	 * @param nodes list of nodes where to submit jobs
	 * @param iters max number of jobs per node
	 * @param maxFact max number for which to calculate factorial
	 * 
	 * @throws TucsonInvalidAgentIdException
	 */
	public MasterAgent(String aid, LinkedList<String> nodes, int iters, int maxFact)
			throws TucsonInvalidAgentIdException {
		super(aid);
		die = false;
		tids = new LinkedList<TucsonTupleCentreId>();
		try{
			for(String node: nodes)
				tids.add(new TucsonTupleCentreId(node));
		}catch(TucsonInvalidTupleCentreIdException e){
			say("Invalid tid given, killing myself...");
			die = true;
		}
		ITERs = iters;
		MAX_FACT = maxFact;
		reqID = 0;
		pendings = new HashMap<Integer, Integer>();
	}

	@Override
	protected void main() {
		say("I'm started.");
		acc = getContext();
		ITucsonOperation op;
		TucsonTupleCentreId next;
		LogicTuple job;
		LogicTuple templ;
		List<LogicTuple> res;
		int num;
		try{
			while(!die){
				say("Checking termination...");
				for(int i=0; i<tids.size(); i++){
					op = acc.inp(tids.get(i), LogicTuple.parse("die("+myName()+")"), (Long)null);
					/*
					 * Only upon success the searched tuple was found. NB: we
					 * do not <break> cycle to consume all termination tuples
					 * if multiple exist.
					 */
					if(op.isResultSuccess())
						die = true;
				}
				if(die)
					continue;
				/*
				 * Jobs submission phase.
				 */
				for(int i=0; i<tids.size(); i++){
					/*
					 * We iterate nodes in a round-robin fashion...
					 */
					next = tids.get(i);
					say("Putting jobs in: "+next.toString());
					for(int j=0; j<ITERs; j++){
						/*
						 * ...to put in each <ITERs> jobs.
						 */
						num = drawRandomInt();
						job = LogicTuple.parse("fact(" +
								"master("+myName()+")," +
								"num("+num+")," +
								"reqID("+reqID+")" +
							")");
						say("Putting job: "+job.toString());
						/*
						 * Only non-reachability of target tuplecentre may cause
						 * <out> to fail, which raises a Java Exception.
						 */
						acc.out(next, job, (Long)null);
						/*
						 * We keep track of pending computations.
						 */
						pendings.put(reqID, num);
						reqID++;
					}
				}
				/*
				 * Result collection phase.
				 */
				for(int i=0; i<tids.size(); i++){
					/*
					 * Again we iterate nodes in a round-robin fashion...
					 */
					next = tids.get(i);
					say("Collecting results from: "+next.toString());
					for(int j=0; j<ITERs; j++){
						acc.spawn(next, LogicTuple.parse(
								"exec('ds.lab.tucson.masterWorkers.spawn.SpawnedWorkingActivity.class')"), 
								null);
						/*
						 * Just to let you view something on the console.
						 */
						Thread.sleep(1000);
						/*
						 * ...this time to retrieve factorial results.
						 */
						templ = LogicTuple.parse("res(" +
								"master("+myName()+")," +
								"fact(F)," +
								"reqID(N)" +
							")");
						/*
						 * No longer a suspensive primitive. We need to keep track
						 * of collected results.
						 */
						op = acc.in_all(next, templ, (Long)null);
						/*
						 * Check needed due to suspensive semantics.
						 */
						if(op.isResultSuccess()){
							res = op.getLogicTupleListResult();
							if(!res.isEmpty()){
								say("Collected results:");
								for(LogicTuple lt: res){
									/*
									 * We remove corresponding pending job.
									 */
									num = pendings.remove(lt.getArg("reqID").getArg(0).intValue());
									say("\tFactorial of "+num+" is "+lt.getArg("fact").getArg(0));
									j++;
								}
							}
						}
						j--;
					}
				}
				if(tids.isEmpty()){
					say("No nodes given to contact, killing myself...");
					die = true;
				}
			}
			say("Someone killed me, bye!");
		}catch(InvalidLogicTupleException e){
			say("ERROR: Tuple is not an admissible Prolog term!");
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			say("ERROR: Never seen this happen before *_*");
		} catch (UnreachableNodeException e) {
			say("ERROR: Given TuCSoN Node is unreachable!");
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			say("ERROR: Endless timeout expired!");
		} catch (InvalidTupleOperationException e) {
			say("ERROR: No tuple arguments to retrieve!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			say("ERROR: Sleep interrupted!");
		}
	}

	private int drawRandomInt() {
		return (int) Math.round(Math.random()*MAX_FACT);
	}

	@Override
	public void operationCompleted(ITucsonOperation op) { }
	
	/**
	 * @param args no args expected.
	 */
	public static void main(String[] args) {
		LinkedList<String> nodes = new LinkedList<String>();
		nodes.add("default@localhost:20504");
//		nodes.add("default@localhost:20505");
		try {
//			new MasterAgent("walter", nodes, 10, 20).go();
			new MasterAgent("lloyd", nodes, 10, 10).go();
		} catch (TucsonInvalidAgentIdException e) {
			e.printStackTrace();
		}
	}

}
