package alice.tucson.examples.asynchAgent;

import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Inp;
import it.unibo.sd.jade.operations.ordinary.Out;
import java.util.Random;
import java.util.concurrent.Semaphore;
import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.asynchQueueManager.AsynchQueueManager;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
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
    int inpResult;
    Semaphore waitResponse;
    AsynchQueueManager aqm;
    int nPrimeCalc;

    /**
     * the code executed on the last operation response
     * 
     * @author Consalici Drudi
     *
     */
    class Response50 implements TucsonOperationCompletionListener {
        AsynchQueueManager aqm;
        TucsonTupleCentreId tid;
        
        public Response50(AsynchQueueManager aqm, TucsonTupleCentreId tid) {
            this.aqm = aqm;
            this.tid = tid;
        }

        @Override
        public void operationCompleted(AbstractTupleCentreOperation op) {
            // waitResponse.release();
            LogicTuple tuple;
            try {
                tuple = LogicTuple.parse("firstloop");
                Out out = new Out(tid, tuple);
                aqm.add(out, null);
            } catch (InvalidTupleException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void operationCompleted(ITucsonOperation op) {
        }
    }

    /**
     * the code executed on the operation response
     * 
     * @author Consalici Drudi
     *
     */
    class Response implements TucsonOperationCompletionListener {
        @Override
        public void operationCompleted(AbstractTupleCentreOperation op) {
            if (op.isResultSuccess()) {
                try {
                    LogicTuple tuple = (LogicTuple) op.getTupleResult();
                    int number = Integer.parseInt(tuple.getArg(0).toString());
                    int prime = Integer.parseInt(tuple.getArg(1).toString());
                    int in = aqm.getCompletedQueue()
                            .getAllTypedOperation(In.class).getAllSuccessOp()
                            .size();
                    int inp = aqm.getCompletedQueue()
                            .getAllTypedOperation(Inp.class).getAllSuccessOp()
                            .size();
                    int total = in + inp;
                    System.out.println("The prime number until " + number
                            + " are " + prime);
                    System.out.println("OP " + total + " of 50");
                } catch (NumberFormatException | InvalidOperationException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }

        @Override
        public void operationCompleted(ITucsonOperation op) {
        }
    }

    public static void main(String[] args) {
        try {
            new MasterAgent("master", 3).go();
        } catch (TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
    }

    public MasterAgent(String id, int nPrimeCalc)
            throws TucsonInvalidAgentIdException {
        super(id);
        inpResult = 0;
        waitResponse = new Semaphore(0);
        this.nPrimeCalc = nPrimeCalc;
        aqm = new AsynchQueueManager("aqm" + this.getTucsonAgentId());
    }

    @Override
    public void operationCompleted(AbstractTupleCentreOperation op) {
    }

    @Override
    public void operationCompleted(ITucsonOperation op) {
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
                aqm.add(out, null);
            }
            System.out.println("I send 50 operation to PrimeCalculator");
            Inp inp;
            for (int i = 0; i < 50; i++) {
                tuple = LogicTuple.parse("prime(X,Y)");
                inp = new Inp(tid, tuple);
                if (i == 49) {
                    // r is the code to execute after the response operation
                    Response50 r = new Response50(aqm, tid);
                    aqm.add(inp, r);
                } else {
                    Response r = new Response();
                    aqm.add(inp, r);
                }
            }
            System.out
                    .println("Now I could do whatever i want... i want sleep until the last inp answers");
            final EnhancedSynchACC accSynch = this.getContext();
            LogicTuple firstLoopTuple = LogicTuple.parse("firstloop");
            In firstLoopIn = new In(tid, firstLoopTuple);
            firstLoopIn.executeSynch(accSynch, null);
            // waitResponse.acquire();
            // **4**
            // inspect the status of operations
            // operation in cascade to completedQueue on aqm
            // 1 get all Inp operation
            // 2 get all success operation
            // 3 get size
            inpResult = aqm.getCompletedQueue().getAllTypedOperation(Inp.class)
                    .getAllSuccessOp().size();
            System.out.println("I send 50 inp to PrimeCalculator. I received "
                    + inpResult + " result");
            In in;
            aqm.getCompletedQueue().removeAllSuccessOperation();
            for (int i = 0; i < (50 - inpResult); i++) {
                tuple = LogicTuple.parse("prime(X,Y)");
                in = new In(tid, tuple);
                Response r = new Response();
                aqm.add(in, r);
            }
            int inResult = 0;
            boolean stop = false;
            while (!stop) {
                Thread.sleep(5000);
                inResult = aqm.getCompletedQueue()
                        .getAllTypedOperation(In.class).getAllSuccessOp()
                        .size();
                System.out.println("InResult=" + inResult + " InpResult="
                        + inpResult + " TOT=" + (inResult + inpResult));
                if ((inResult + inpResult) == 50) {
                    stop = true;
                }
            }
            System.out
                    .println("I Finish all operation, now i stop the prime calculator");
            System.out.println("send STOP to all prime calculator");
            for (int i = 0; i < nPrimeCalc; i++) {
                // tuple = LogicTuple.parse("stop(factorialcalculator" + i +
                // ")");
                tuple = LogicTuple.parse("stop(primecalc)");
                out = new Out(tid, tuple);
                aqm.add(out, null);
            }
            // **5**
            // Terminate the Aqm
            //
            aqm.shutdown();
            Thread.sleep(3000);
        } catch (TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (InvalidTupleException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (UnreachableNodeException e) {
            e.printStackTrace();
        } catch (OperationTimeOutException e) {
            e.printStackTrace();
        }
    }
}
