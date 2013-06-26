package alice.tucson.examples.timedDiningPhilos;

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
public class DiningPhilosophersTest extends TucsonAgent {

    private static final int EATING_STEP = 1000;
    /*
     * Should be exactly divisible.
     */
    private static final int EATING_TIME = 5000;
    private static final int MAX_EATING_TIME = 7000;
    /*
     * Max number of simultaneously eating philosophers should be
     * N_PHILOSOPHERS-2.
     */
    private static final int N_PHILOSOPHERS = 3;

    /**
     * 
     * @param args
     *            no args expected
     */
    public static void main(final String[] args) {
        try {
            new DiningPhilosophersTest("boot").go();
        } catch (final TucsonInvalidAgentIdException e) {
            // TODO Properly handle Exception
        }
    }

    private final String ip;

    private final String port;

    public DiningPhilosophersTest(final String aid)
            throws TucsonInvalidAgentIdException {
        super(aid);
        /*
         * To experiment with a distributed setting, launch the TuCSoN Node
         * hosting the 'table' tuple centre on a remote node.
         */
        this.ip = "localhost";
        this.port = "20504";
    }

    @Override
    public void operationCompleted(final ITucsonOperation arg0) {
        /*
         * 
         */
    }

    @Override
    protected void main() {
        final SynchACC acc = this.getContext();
        try {
            final TucsonTupleCentreId table =
                    new TucsonTupleCentreId("table", this.ip, this.port);
            this.say("Injecting 'table' ReSpecT specification in tc < "
                    + table.toString() + " >...");
            /*
             * Program the tuple centre by setting a ReSpecT specification (a
             * set of ReSpecT specification tuples) in its specification space.
             */
            acc.set_s(
                    table,
                    Utils.fileToString("ds/lab/tucson/respect/timedDiningPhilosophers/table.rsp"),
                    null);
            /*
             * Init max eating time.
             */
            acc.out(table,
                    LogicTuple.parse("max_eating_time("
                            + DiningPhilosophersTest.MAX_EATING_TIME + ")"),
                    null);
            for (int i = 0; i < DiningPhilosophersTest.N_PHILOSOPHERS; i++) {
                /*
                 * Init chopsticks required to eat.
                 */
                acc.out(table, LogicTuple.parse("chop(" + i + ")"), null);
            }
            for (int i = 0; i < (DiningPhilosophersTest.N_PHILOSOPHERS - 1); i++) {
                /*
                 * Start philosophers by telling them which chopsticks pair they
                 * need.
                 */
                new DiningPhilosopher("'philo-" + i + "'", table, i, (i + 1)
                        % DiningPhilosophersTest.N_PHILOSOPHERS,
                        DiningPhilosophersTest.EATING_TIME,
                        DiningPhilosophersTest.EATING_STEP).go();
            }
            /*
             * Sloth philosopher.
             */
            new DiningPhilosopher("'philo-"
                    + (DiningPhilosophersTest.N_PHILOSOPHERS - 1) + "'", table,
                    DiningPhilosophersTest.N_PHILOSOPHERS - 1, 0, 10000,
                    DiningPhilosophersTest.EATING_STEP).go();
            acc.exit();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // TODO Properly handle Exception
        } catch (final TucsonOperationNotPossibleException e) {
            // TODO Properly handle Exception
        } catch (final UnreachableNodeException e) {
            // TODO Properly handle Exception
        } catch (final OperationTimeOutException e) {
            // TODO Properly handle Exception
        } catch (final IOException e) {
            // TODO Properly handle Exception
        } catch (final InvalidLogicTupleException e) {
            // TODO Properly handle Exception
        } catch (final TucsonInvalidAgentIdException e) {
            // TODO Properly handle Exception
        }
    }

}
