package alice.tucson.examples.asynchAPI;

import java.util.concurrent.Semaphore;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
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
 * The master agent send operation in the tuple space and wait for response
 * asynchronously
 *
 * @author Consalici-Drudi
 *
 */
public class MasterAgent extends AbstractTucsonAgent {

    /**
     * the code executed on the operation response
     *
     * @author Consalici Drudi
     *
     */
    class Response implements TucsonOperationCompletionListener {

        @Override
        public void operationCompleted(final AbstractTupleCentreOperation op) {
            if (op.isResultSuccess()) {
                try {
                    final LogicTuple tuple = (LogicTuple) op.getTupleResult();
                    final int number = Integer.parseInt(tuple.getArg(0)
                            .toString());
                    final int prime = Integer.parseInt(tuple.getArg(1)
                            .toString());
                    final int in = MasterAgent.this.aqm.getCompletedQueue()
                            .getAllTypedOperation(In.class).getAllSuccessOp()
                            .size();
                    final int inp = MasterAgent.this.aqm.getCompletedQueue()
                            .getAllTypedOperation(Inp.class).getAllSuccessOp()
                            .size();
                    final int total = in + inp;
                    System.out.println("The prime number until " + number
                            + " are " + prime);
                    System.out.println("OP " + total + " of 50");
                } catch (NumberFormatException | InvalidOperationException e) {
                    e.printStackTrace();
                }
            } else {
                /*
                 * log something
                 */
            }
        }

        @Override
        public void operationCompleted(final ITucsonOperation op) {
            /*
             * Not used atm
             */
        }
    }

    /**
     * the code executed on the last operation response
     *
     * @author Consalici Drudi
     *
     */
    class Response50 implements TucsonOperationCompletionListener {

        AsynchQueueManager manager;
        TucsonTupleCentreId tid;

        public Response50(final AsynchQueueManager aqm,
                final TucsonTupleCentreId tid) {
            this.manager = aqm;
            this.tid = tid;
        }

        @Override
        public void operationCompleted(final AbstractTupleCentreOperation op) {
            // waitResponse.release();
            LogicTuple tuple;
            try {
                tuple = LogicTuple.parse("firstloop");
                final Out out = new Out(this.tid, tuple);
                this.manager.add(out, null);
            } catch (final InvalidLogicTupleException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void operationCompleted(final ITucsonOperation op) {
            /*
             * Not used atm
             */
        }
    }

    public static void main(final String[] args) {
        try {
            new MasterAgent("master", 3).go();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
    }

    AsynchQueueManager aqm;

    int inpResult;

    int nPrimeCalc;

    Semaphore waitResponse;

    public MasterAgent(final String id, final int nPrimeCalc)
            throws TucsonInvalidAgentIdException {
        super(id);
        this.inpResult = 0;
        this.waitResponse = new Semaphore(0);
        this.nPrimeCalc = nPrimeCalc;
        this.aqm = new AsynchQueueManager("aqm" + this.getTucsonAgentId());
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
        // **1**
        // Create TUPLE CENTRE ID and ASYNCHQUEUEMANAGER
        //
        try {
            Out out;
            LogicTuple tuple = null;
            final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
                    "localhost", "20504");
            // Send Operation to PrimeCalculator
            int number = 5000;
            // **2**
            // Operation to send to aqm
            //
            for (int i = 0; i < 50; i++) {
                tuple = LogicTuple.parse("calcprime(" + number + ")");
                number += 2000;
                out = new Out(tid, tuple);
                // **3**
                // send Op to aqm to be executed
                //
                this.aqm.add(out, null);
            }
            System.out.println("I send 50 operation to PrimeCalculator");
            Inp inp;
            for (int i = 0; i < 50; i++) {
                tuple = LogicTuple.parse("prime(X,Y)");
                inp = new Inp(tid, tuple);
                if (i == 49) {
                    // r is the code to execute after the response operation
                    final Response50 r = new Response50(this.aqm, tid);
                    this.aqm.add(inp, r);
                } else {
                    final Response r = new Response();
                    this.aqm.add(inp, r);
                }
            }
            System.out
                    .println("Now I could do whatever i want... i want sleep until the last inp answers");
            final EnhancedSynchACC accSynch = this.getContext();
            final LogicTuple firstLoopTuple = LogicTuple.parse("firstloop");
            final In firstLoopIn = new In(tid, firstLoopTuple);
            firstLoopIn.executeSynch(accSynch, null);
            // waitResponse.acquire();
            // **4**
            // inspect the status of operations
            // operation in cascade to completedQueue on aqm
            // 1 get all Inp operation
            // 2 get all success operation
            // 3 get size
            this.inpResult = this.aqm.getCompletedQueue()
                    .getAllTypedOperation(Inp.class).getAllSuccessOp().size();
            System.out.println("I send 50 inp to PrimeCalculator. I received "
                    + this.inpResult + " result");
            In in;
            this.aqm.getCompletedQueue().removeAllSuccessOperation();
            for (int i = 0; i < 50 - this.inpResult; i++) {
                tuple = LogicTuple.parse("prime(X,Y)");
                in = new In(tid, tuple);
                final Response r = new Response();
                this.aqm.add(in, r);
            }
            int inResult = 0;
            boolean stop = false;
            while (!stop) {
                Thread.sleep(5000);
                inResult = this.aqm.getCompletedQueue()
                        .getAllTypedOperation(In.class).getAllSuccessOp()
                        .size();
                System.out.println("InResult=" + inResult + " InpResult="
                        + this.inpResult + " TOT="
                        + (inResult + this.inpResult));
                if (inResult + this.inpResult == 50) {
                    stop = true;
                }
            }
            System.out
                    .println("I Finish all operation, now i stop the prime calculator");
            System.out.println("send STOP to all prime calculator");
            for (int i = 0; i < this.nPrimeCalc; i++) {
                // tuple = LogicTuple.parse("stop(factorialcalculator" + i +
                // ")");
                tuple = LogicTuple.parse("stop(primecalc)");
                out = new Out(tid, tuple);
                this.aqm.add(out, null);
            }
            // **5**
            // Terminate the Aqm
            //
            this.aqm.shutdown();
            Thread.sleep(3000);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }
    }
}
