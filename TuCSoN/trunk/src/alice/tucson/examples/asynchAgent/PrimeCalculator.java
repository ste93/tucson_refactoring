package alice.tucson.examples.asynchAgent;

import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Inp;
import it.unibo.sd.jade.operations.ordinary.Out;

import java.math.BigInteger;
import java.util.Random;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
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
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class PrimeCalculator extends AbstractTucsonAgent{
	protected boolean stop;
	public PrimeCalculator(String id) throws TucsonInvalidAgentIdException {
		super(id);
		stop=false;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void operationCompleted(AbstractTupleCentreOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}
	
	int getPrimeNumbers(int n) {
		boolean[] primi = new boolean[n];
		primi[0] = primi[1] = false;
		for (int i = 2; i < n; i++) {
			primi[i] = true;
			for (int j = 2; j < i; j++) if (i % j == 0) {
				primi[i] = false; break;
			}
		}
		int p = 0;
		for (int j = 2; j < n; j++) if (primi[j]) p++;
		return p;
		}
	
	@Override
	protected void main() {		
		try {
        	
		 	final EnhancedAsynchACC acc = this.getContext();
			final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
			        "localhost", "20504");
			AsynchQueueManager aqm=new AsynchQueueManager("aqm"+this.getTucsonAgentId());
			while(!stop){
				LogicTuple tuple = LogicTuple.parse("calcprime(X)");
				Inp inp = new Inp(tid, tuple);
				aqm.add(inp, new InpListener(acc,tid,aqm));
		        LogicTuple stopTuple = LogicTuple.parse("stop("+this.getTucsonAgentId()+")");
	                Inp inpStop = new Inp(tid, stopTuple);
	                aqm.add(inpStop, new StopListener());
			}
			aqm.shutdownNow();
		} catch (TucsonInvalidTupleCentreIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTupleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TucsonInvalidAgentIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class InpListener implements TucsonOperationCompletionListener{
		EnhancedAsynchACC acc;
		TucsonTupleCentreId tid;
		AsynchQueueManager aqm;
		public InpListener(EnhancedAsynchACC acc,TucsonTupleCentreId tid, AsynchQueueManager aqm ){
			this.acc=acc;
			this.tid=tid;
			this.aqm=aqm;
		}
		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			// TODO Auto-generated method stub
			if (op.isResultSuccess()) {
	            try {
	            	LogicTuple res = null;
					LogicTuple tupleRes;
		            res = (LogicTuple)op.getTupleResult();
		            int number = Integer.parseInt(res.getArg(0).toString());
		            long primeN = getPrimeNumbers(number);
					tupleRes = LogicTuple.parse("prime("+number+","+primeN+")");
					//ITucsonOperation opRes = acc.out(tid, tupleRes, null);
					Out out=new Out(tid, tupleRes);
					aqm.add(out, null);
				} catch (InvalidTupleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else {
	        }
		}

		@Override
		public void operationCompleted(ITucsonOperation op) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class StopListener implements TucsonOperationCompletionListener{

		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			// TODO Auto-generated method stub
			if(op.isResultSuccess()){
				System.out.println("MI FERMOOOOOOOOOOOOOOOOOO");
				stop=true;
			}
		}

		@Override
		public void operationCompleted(ITucsonOperation op) {
			// TODO Auto-generated method stub
			if(op.isResultSuccess()){
				System.out.println("MI FERMOOOOOOOOOOOOOOOOOO");
				stop=true;
			}
		}
		
	}
}
