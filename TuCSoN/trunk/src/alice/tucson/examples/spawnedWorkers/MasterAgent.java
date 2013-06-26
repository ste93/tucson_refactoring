package alice.tucson.examples.spawnedWorkers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
 * Master thread of a master-worker architecture. Given a list of TuCSoN Nodes
 * (hopefully up & listening), it submits jobs regarding factorial computation,
 * then collects expected results.
 * 
 * @author s.mariani@unibo.it
 */
public class MasterAgent extends TucsonAgent {

    /**
     * @param args
     *            no args expected.
     */
    public static void main(final String[] args) {
        final LinkedList<String> nodes = new LinkedList<String>();
        nodes.add("default@localhost:20504");
        // nodes.add("default@localhost:20505");
        try {
            // new MasterAgent("walter", nodes, 10, 20).go();
            new MasterAgent("lloyd", nodes, 10, 10).go();
        } catch (final TucsonInvalidAgentIdException e) {
            // TODO Properly handle Exception
        }
    }

    private boolean die;
    private final int ITERs;
    private final int MAX_FACT;
    private final Map<Integer, Integer> pendings;
    private int reqID;

    private final List<TucsonTupleCentreId> tids;

    /**
     * @param aid
     *            agent name
     * @param nodes
     *            list of nodes where to submit jobs
     * @param iters
     *            max number of jobs per node
     * @param maxFact
     *            max number for which to calculate factorial
     * 
     * @throws TucsonInvalidAgentIdException
     */
    public MasterAgent(final String aid, final List<String> nodes,
            final int iters, final int maxFact)
            throws TucsonInvalidAgentIdException {
        super(aid);
        this.die = false;
        this.tids = new LinkedList<TucsonTupleCentreId>();
        try {
            for (final String node : nodes) {
                this.tids.add(new TucsonTupleCentreId(node));
            }
        } catch (final TucsonInvalidTupleCentreIdException e) {
            this.say("Invalid tid given, killing myself...");
            this.die = true;
        }
        this.ITERs = iters;
        this.MAX_FACT = maxFact;
        this.reqID = 0;
        this.pendings = new HashMap<Integer, Integer>();
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * 
         */
    }

    @Override
    protected void main() {
        this.say("I'm started.");
        final EnhancedSynchACC acc = this.getContext();
        ITucsonOperation op;
        TucsonTupleCentreId next;
        LogicTuple job;
        LogicTuple templ;
        List<LogicTuple> res;
        int num;
        try {
            while (!this.die) {
                this.say("Checking termination...");
                for (int i = 0; i < this.tids.size(); i++) {
                    op =
                            acc.inp(this.tids.get(i),
                                    LogicTuple.parse("die(" + this.myName()
                                            + ")"), (Long) null);
                    /*
                     * Only upon success the searched tuple was found. NB: we do
                     * not <break> cycle to consume all termination tuples if
                     * multiple exist.
                     */
                    if (op.isResultSuccess()) {
                        this.die = true;
                    }
                }
                if (this.die) {
                    continue;
                }
                /*
                 * Jobs submission phase.
                 */
                for (int i = 0; i < this.tids.size(); i++) {
                    /*
                     * We iterate nodes in a round-robin fashion...
                     */
                    next = this.tids.get(i);
                    this.say("Putting jobs in: " + next.toString());
                    for (int j = 0; j < this.ITERs; j++) {
                        /*
                         * ...to put in each <ITERs> jobs.
                         */
                        num = this.drawRandomInt();
                        job =
                                LogicTuple.parse("fact(" + "master("
                                        + this.myName() + ")," + "num(" + num
                                        + ")," + "reqID(" + this.reqID + ")"
                                        + ")");
                        this.say("Putting job: " + job.toString());
                        /*
                         * Only non-reachability of target tuplecentre may cause
                         * <out> to fail, which raises a Java Exception.
                         */
                        acc.out(next, job, (Long) null);
                        /*
                         * We keep track of pending computations.
                         */
                        this.pendings.put(this.reqID, num);
                        this.reqID++;
                    }
                }
                /*
                 * Result collection phase.
                 */
                for (int i = 0; i < this.tids.size(); i++) {
                    /*
                     * Again we iterate nodes in a round-robin fashion...
                     */
                    next = this.tids.get(i);
                    this.say("Collecting results from: " + next.toString());
                    for (int j = 0; j < this.ITERs; j++) {
                        acc.spawn(
                                next,
                                LogicTuple
                                        .parse("exec('ds.lab.tucson.masterWorkers.spawn.SpawnedWorkingActivity.class')"),
                                null);
                        /*
                         * Just to let you view something on the console.
                         */
                        Thread.sleep(1000);
                        /*
                         * ...this time to retrieve factorial results.
                         */
                        templ =
                                LogicTuple.parse("res(" + "master("
                                        + this.myName() + ")," + "fact(F),"
                                        + "reqID(N)" + ")");
                        /*
                         * No longer a suspensive primitive. We need to keep
                         * track of collected results.
                         */
                        op = acc.in_all(next, templ, (Long) null);
                        /*
                         * Check needed due to suspensive semantics.
                         */
                        if (op.isResultSuccess()) {
                            res = op.getLogicTupleListResult();
                            if (!res.isEmpty()) {
                                this.say("Collected results:");
                                for (final LogicTuple lt : res) {
                                    /*
                                     * We remove corresponding pending job.
                                     */
                                    num =
                                            this.pendings.remove(lt
                                                    .getArg("reqID").getArg(0)
                                                    .intValue());
                                    this.say("\tFactorial of " + num + " is "
                                            + lt.getArg("fact").getArg(0));
                                    j++;
                                }
                            }
                        }
                        j--;
                    }
                }
                if (this.tids.isEmpty()) {
                    this.say("No nodes given to contact, killing myself...");
                    this.die = true;
                }
            }
            this.say("Someone killed me, bye!");
        } catch (final InvalidLogicTupleException e) {
            this.say("ERROR: Tuple is not an admissible Prolog term!");
            // TODO Properly handle Exception
        } catch (final TucsonOperationNotPossibleException e) {
            this.say("ERROR: Never seen this happen before *_*");
        } catch (final UnreachableNodeException e) {
            this.say("ERROR: Given TuCSoN Node is unreachable!");
            // TODO Properly handle Exception
        } catch (final OperationTimeOutException e) {
            this.say("ERROR: Endless timeout expired!");
        } catch (final InvalidTupleOperationException e) {
            this.say("ERROR: No tuple arguments to retrieve!");
            // TODO Properly handle Exception
        } catch (final InterruptedException e) {
            this.say("ERROR: Sleep interrupted!");
        }
    }

    private int drawRandomInt() {
        return (int) Math.round(Math.random() * this.MAX_FACT);
    }

}
