package alice.tucson.examples.asynchAgent;

import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Inp;
import it.unibo.sd.jade.operations.ordinary.Out;

import java.util.concurrent.Semaphore;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.asynchQueueManager.AsynchQueueManager;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class MasterAgent extends AbstractTucsonAgent{
	int inpResult;
	Semaphore waitResponse;
	AsynchQueueManager aqm;
	/**
	 * the code executed on the last operation response
	 * @author Consalici Drudi
	 *
	 */
        class Response50 implements TucsonOperationCompletionListener{
    	AsynchQueueManager aqm;
    	public Response50(AsynchQueueManager aqm){
    		this.aqm=aqm;
    	}
		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			// TODO Auto-generated method stub
			waitResponse.release();
		}

		@Override
		public void operationCompleted(ITucsonOperation op) {
			// TODO Auto-generated method stub
			
		}
    	
    }
	
    /**
     * the code executed on the operation response
     * @author Consalici Drudi
     *
     */
    class Response implements TucsonOperationCompletionListener{

		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			// TODO Auto-generated method stub
			if(op.isResultSuccess()){
				try {
					LogicTuple tuple = (LogicTuple) op.getTupleResult();
					int number = Integer.parseInt(tuple.getArg(0).toString());
					int prime = Integer.parseInt(tuple.getArg(1).toString());
					int in=aqm.getCompletedQueue().getAllTypedOperation(In.class).getAllSuccessOp().size();
					int inp=aqm.getCompletedQueue().getAllTypedOperation(Inp.class).getAllSuccessOp().size();
					int total=in+inp;
					System.out.println("The prime number until "+number+" are "+prime);
					System.out.println("OP "+total+" of 50");
				} catch (NumberFormatException | InvalidOperationException e) {
					// TODO Auto-generated catch block
					System.out.println("throw Exception on response listener:"+e.getMessage());
					//e.printStackTrace();
				}
			}else{
				System.out.println("operation not result success");
			}
		}

		@Override
		public void operationCompleted(ITucsonOperation op) {
			// TODO Auto-generated method stub
		}
    	
    }
	
    public static void main(String[] args){
    	try {
			new MasterAgent("master").go();
		} catch (TucsonInvalidAgentIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public MasterAgent(String id) throws TucsonInvalidAgentIdException {
		super(id);
		inpResult=0;
		waitResponse = new Semaphore(0);
	}

	@Override
	public void operationCompleted(AbstractTupleCentreOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void main() {
        try {
                //      **1**
                //Create TUPLE CENTRE ID and ASYNCHQUEUEMANAGER
                //
                final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
		"localhost", "20504");
                aqm=new AsynchQueueManager("aqm"+this.getTucsonAgentId());
	        //Send Operation to PrimeCalculator
	        int number=8000;
	        //       **2**
                //Operation to send to aqm
                //
	        Out out;
    		LogicTuple tuple = null;
	        for(int i = 0; i<50; i++){
	        	tuple = LogicTuple.parse("calcprime("+ number +")");
	        	number+=2000;
	        	out = new Out(tid, tuple);
	        	//      **3**
	        	//send Op to aqm to be executed
	        	//
	        	aqm.add(out, null);
	        }
	        System.out.println("I send 50 operation to PrimeCalculator");
	        Inp inp;
	        for(int i = 0; i<50; i++){
	        	tuple = LogicTuple.parse("prime(X,Y)");
        		inp = new Inp(tid, tuple);
	        	if(i==49){
	        	        // r is the code to execute after the response operation
	        		Response50 r = new Response50(aqm);
	        		aqm.add(inp, r);
	        	}else{
	        		Response r = new Response();
	        		aqm.add(inp, r);
	        	}
	        }
	        System.out.println("Now I could do whatever i want... i want sleep until the last inp answers");
	        waitResponse.acquire();
	        //     **4**
	        // inspect the status of operations
	        //operation in cascade to completedQueue on aqm
	        //1 get all Inp operation
	        //2 get all success operation
	        //3 get size
	        inpResult=aqm.getCompletedQueue().getAllTypedOperation(Inp.class).getAllSuccessOp().size();
			System.out.println("I send 50 inp to PrimeCalculator. I received "+inpResult+" result");
	        In in;
		for(int i=0;i<(50-inpResult);i++){
			tuple = LogicTuple.parse("prime(X,Y)");
        		in = new In(tid, tuple);
	        	Response r = new Response();
	        	aqm.add(in, r);
	        }
		int inResult;
		boolean stop=false;
		while(!stop){
			Thread.sleep(5000);
			inResult = aqm.getCompletedQueue().getAllTypedOperation(In.class).getAllSuccessOp().size();
			System.out.println("InResult="+inResult+" InpResult="+inpResult+" TOT="+(inResult+inpResult));
			if((inResult+inpResult)==50){
				stop=true;
			}
		}
		System.out.println("I Finish all operation, now i stop the prime calculator");
	        for(int i = 0; i<3; i++){
	        	System.out.println("MANDO STOP");
	        	tuple = LogicTuple.parse("stop(factorialcalculator"+i+")");
	        	out = new Out(tid, tuple);
	        	aqm.add(out, null);
	        }
	        //     **5**
	        //Terminate the Aqm
	        //
	        aqm.shutdown();
	        Thread.sleep(3000);
	        
		} catch (TucsonInvalidTupleCentreIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTupleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TucsonInvalidAgentIdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

}
