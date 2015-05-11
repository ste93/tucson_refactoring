package alice.tucson.examples.asynchAPI;

import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Inp;
import it.unibo.sd.jade.operations.ordinary.Out;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.Semaphore;
import alice.logictuple.LogicTuple;
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
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * This agent calc the number of prime number (prime-counting(N)) until stop
 * 
 * @author Riccardo
 *
 */
public class PrimeCalculator extends AbstractTucsonAgent {
    protected boolean stop;
    protected Semaphore waitIn;

    public PrimeCalculator(String id) throws TucsonInvalidAgentIdException {
        super(id);
        stop = false;
    }

    @Override
    public void operationCompleted(AbstractTupleCentreOperation op) {}

    @Override
    public void operationCompleted(ITucsonOperation op) {}

    int getPrimeNumbers(int n) {
        boolean[] primi = new boolean[n];
        primi[0] = primi[1] = false;
        for (int i = 2; i < n; i++) {
            primi[i] = true;
            for (int j = 2; j < i; j++)
                if (i % j == 0) {
                    primi[i] = false;
                    break;
                }
        }
        int p = 0;
        for (int j = 2; j < n; j++)
            if (primi[j])
                p++;
        return p;
    }

    @Override
    protected void main() {
        try {
            final EnhancedAsynchACC acc = this.getContext();
            final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
                    "localhost", "20504");
            AsynchQueueManager aqm = new AsynchQueueManager("aqm"
                    + this.getTucsonAgentId());
            //
            // First Inp to get request
            //
            LogicTuple tuple = LogicTuple.parse("calcprime(X)");
            Inp inp = new Inp(tid, tuple);
            aqm.add(inp, new InpListener(acc, tid, aqm));
            //
            // wait sinchronously a stop tuple
            //
            final EnhancedSynchACC accSynch = this.getContext();
            // LogicTuple stopTuple = LogicTuple.parse("stop("
            // + this.getTucsonAgentId() + ")");
            LogicTuple stopTuple = LogicTuple.parse("stop(primecalc)");
            In inStop = new In(tid, stopTuple);
            inStop.executeSynch(accSynch, null);
            stop = true;
            aqm.shutdownNow();
        } catch (TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (InvalidTupleException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        } catch (TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (UnreachableNodeException e) {
            e.printStackTrace();
        } catch (OperationTimeOutException e) {
            e.printStackTrace();
        }
    }

    class InpListener implements TucsonOperationCompletionListener {
        EnhancedAsynchACC acc;
        TucsonTupleCentreId tid;
        AsynchQueueManager aqm;

        public InpListener(EnhancedAsynchACC acc, TucsonTupleCentreId tid,
                AsynchQueueManager aqm) {
            this.acc = acc;
            this.tid = tid;
            this.aqm = aqm;
        }

        @Override
        public void operationCompleted(AbstractTupleCentreOperation op) {
            if (op.isResultSuccess()) {
                try {
                    LogicTuple res = null;
                    LogicTuple tupleRes;
                    res = (LogicTuple) op.getTupleResult();
                    //Calculate result tuple
                    int number = Integer.parseInt(res.getArg(0).toString());
                    long primeN = getPrimeNumbers(number);
                    tupleRes = LogicTuple.parse("prime(" + number + ","
                            + primeN + ")");
                    // ITucsonOperation opRes = acc.out(tid, tupleRes, null);
                    //Send result to tuple centre
                    Out out = new Out(tid, tupleRes);
                    aqm.add(out, null);
                    if (!stop) {
                        // Send another inp
                        LogicTuple tuple = LogicTuple.parse("calcprime(X)");
                        Inp inp = new Inp(tid, tuple);
                        aqm.add(inp, new InpListener(acc, tid, aqm));
                    }
                } catch (InvalidTupleException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (InvalidOperationException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(300);
                    if (!stop) {
                        //if no tuple match
                        //Send another inp
                        LogicTuple tuple = LogicTuple.parse("calcprime(X)");
                        Inp inp = new Inp(tid, tuple);
                        aqm.add(inp, new InpListener(acc, tid, aqm));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvalidTupleException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void operationCompleted(ITucsonOperation op) {
        }
    }

    class StopListener implements TucsonOperationCompletionListener {
        TucsonAgentId agentId;

        public StopListener(TucsonAgentId tucsonAgentId) {
            this.agentId = tucsonAgentId;
        }

        @Override
        public void operationCompleted(AbstractTupleCentreOperation op) {
            if (op.isResultSuccess()) {
                stop = true;
                System.out.println("[primeCalc]: Stop");
                // LogicTuple tupleRes = LogicTuple.parse("mystop(" + agentId
                // + ")");
                // ITucsonOperation opRes = acc.out(tid, tupleRes, null);
                // Out out = new Out(tid, tupleRes);
                // aqm.add(out, null);
            }
        }

        @Override
        public void operationCompleted(ITucsonOperation op) {
        }
    }
}
