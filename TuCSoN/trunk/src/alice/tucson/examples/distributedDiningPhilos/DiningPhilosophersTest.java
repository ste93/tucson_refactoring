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
public class DiningPhilosophersTest extends TucsonAgent {

    private static final int N_PHILOSOPHERS = 5;

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

    private final int port;

    public DiningPhilosophersTest(final String aid)
            throws TucsonInvalidAgentIdException {
        super(aid);
        this.ip = "localhost";
        this.port = 20504;
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
            final TucsonTupleCentreId[] seats =
                    new TucsonTupleCentreId[DiningPhilosophersTest.N_PHILOSOPHERS];
            for (int i = 0; i < DiningPhilosophersTest.N_PHILOSOPHERS; i++) {
                // seats[i] = new
                // TucsonTupleCentreId("seat("+i+","+((i+1)%N_PHILOSOPHERS)+")",
                // ip, (port++)+"");
                seats[i] =
                        new TucsonTupleCentreId(
                                "seat("
                                        + i
                                        + ","
                                        + ((i + 1) % DiningPhilosophersTest.N_PHILOSOPHERS)
                                        + ")", this.ip,
                                String.valueOf(this.port));
                this.say("Injecting 'seat' ReSpecT specification in tc < "
                        + seats[i].toString() + " >...");
                acc.set_s(
                        seats[i],
                        Utils.fileToString("alice/tucson/examples/distributedDiningPhilos/seat.rsp"),
                        null);
                acc.out(seats[i], LogicTuple.parse("philosopher(thinking)"),
                        null);
            }
            final TucsonTupleCentreId table =
                    new TucsonTupleCentreId("table", this.ip,
                            String.valueOf(this.port));
            this.say("Injecting 'table' ReSpecT specification in tc < "
                    + table.toString() + " >...");
            acc.set_s(
                    table,
                    Utils.fileToString("alice/tucson/examples/distributedDiningPhilos/table.rsp"),
                    null);
            for (int i = 0; i < DiningPhilosophersTest.N_PHILOSOPHERS; i++) {
                acc.out(table, LogicTuple.parse("chop(" + i + ")"), null);
            }
            for (int i = 0; i < DiningPhilosophersTest.N_PHILOSOPHERS; i++) {
                new DiningPhilosopher("'philo-" + i + "'", seats[i]).go();
            }
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
