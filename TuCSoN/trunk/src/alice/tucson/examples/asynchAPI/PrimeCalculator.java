package alice.tucson.examples.asynchAPI;

import java.util.concurrent.Semaphore;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.asynchSupport.AsynchQueueManager;
import alice.tucson.asynchSupport.operations.ordinary.In;
import alice.tucson.asynchSupport.operations.ordinary.Inp;
import alice.tucson.asynchSupport.operations.ordinary.Out;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * This agent calc the number of prime number (prime-counting(N)) until stop
 *
 * @author Riccardo
 *
 */
public class PrimeCalculator extends AbstractTucsonAgent {

    class InpListener implements TucsonOperationCompletionListener {

        EnhancedAsynchACC acc;
        AsynchQueueManager aqm;
        TucsonTupleCentreId tid;

        public InpListener(final EnhancedAsynchACC acc,
                final TucsonTupleCentreId tid, final AsynchQueueManager aqm) {
            this.acc = acc;
            this.tid = tid;
            this.aqm = aqm;
        }

        @Override
        public void operationCompleted(final AbstractTupleCentreOperation op) {
            if (op.isResultSuccess()) {
                try {
                    LogicTuple res = null;
                    LogicTuple tupleRes;
                    res = (LogicTuple) op.getTupleResult();
                    // Calculate result tuple
                    final int number = Integer.parseInt(res.getArg(0)
                            .toString());
                    final long primeN = PrimeCalculator.this
                            .getPrimeNumbers(number);
                    tupleRes = LogicTuple.parse("prime(" + number + ","
                            + primeN + ")");
                    // ITucsonOperation opRes = acc.out(tid, tupleRes, null);
                    // Send result to tuple centre
                    final Out out = new Out(this.tid, tupleRes);
                    this.aqm.add(out, null);
                    if (!PrimeCalculator.this.stop) {
                        // Send another inp
                        final LogicTuple tuple = LogicTuple
                                .parse("calcprime(X)");
                        final Inp inp = new Inp(this.tid, tuple);
                        this.aqm.add(inp, new InpListener(this.acc, this.tid,
                                this.aqm));
                    }
                } catch (final InvalidLogicTupleException e) {
                    e.printStackTrace();
                } catch (final NumberFormatException e) {
                    e.printStackTrace();
                } catch (final InvalidOperationException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(300);
                    if (!PrimeCalculator.this.stop) {
                        // if no tuple match
                        // Send another inp
                        final LogicTuple tuple = LogicTuple
                                .parse("calcprime(X)");
                        final Inp inp = new Inp(this.tid, tuple);
                        this.aqm.add(inp, new InpListener(this.acc, this.tid,
                                this.aqm));
                    }
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                } catch (final InvalidLogicTupleException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void operationCompleted(final ITucsonOperation op) {
            /*
             * Not used atm
             */
        }
    }

    class StopListener implements TucsonOperationCompletionListener {

        TucsonAgentId agentId;

        public StopListener(final TucsonAgentId tucsonAgentId) {
            this.agentId = tucsonAgentId;
        }

        @Override
        public void operationCompleted(final AbstractTupleCentreOperation op) {
            if (op.isResultSuccess()) {
                PrimeCalculator.this.stop = true;
                System.out.println("[primeCalc]: Stop");
                // LogicTuple tupleRes = LogicTuple.parse("mystop(" + agentId
                // + ")");
                // ITucsonOperation opRes = acc.out(tid, tupleRes, null);
                // Out out = new Out(tid, tupleRes);
                // aqm.add(out, null);
            }
        }

        @Override
        public void operationCompleted(final ITucsonOperation op) {
            /*
             * Not used atm
             */
        }
    }

    protected boolean stop;

    protected Semaphore waitIn;

    public PrimeCalculator(final String id)
            throws TucsonInvalidAgentIdException {
        super(id);
        this.stop = false;
    }

    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        /*
         * Not used atm
         */
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * Not used atm
         */
    }

    @Override
    protected void main() {
        try {
            final EnhancedAsynchACC acc = this.getContext();
            final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
                    "localhost", "20504");
            final AsynchQueueManager aqm = new AsynchQueueManager("aqm"
                    + this.getTucsonAgentId());
            //
            // First Inp to get request
            //
            final LogicTuple tuple = LogicTuple.parse("calcprime(X)");
            final Inp inp = new Inp(tid, tuple);
            aqm.add(inp, new InpListener(acc, tid, aqm));
            //
            // wait sinchronously a stop tuple
            //
            final EnhancedSynchACC accSynch = this.getContext();
            // LogicTuple stopTuple = LogicTuple.parse("stop("
            // + this.getTucsonAgentId() + ")");
            final LogicTuple stopTuple = LogicTuple.parse("stop(primecalc)");
            final In inStop = new In(tid, stopTuple);
            inStop.executeSynch(accSynch, null);
            this.stop = true;
            aqm.shutdownNow();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final NumberFormatException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }
    }

    int getPrimeNumbers(final int n) {
        final boolean[] primi = new boolean[n];
        primi[0] = primi[1] = false;
        for (int i = 2; i < n; i++) {
            primi[i] = true;
            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    primi[i] = false;
                    break;
                }
            }
        }
        int p = 0;
        for (int j = 2; j < n; j++) {
            if (primi[j]) {
                p++;
            }
        }
        return p;
    }
}
