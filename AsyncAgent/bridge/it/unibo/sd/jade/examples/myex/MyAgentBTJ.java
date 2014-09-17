package it.unibo.sd.jade.examples.myex;

import it.unibo.sd.jade.examples.bookTrading.BookBuyerAgent;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Inp;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import alice.logictuple.LogicTuple;
import alice.respect.api.AbstractAgent;
import alice.respect.api.AgentId;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.AsynchACC;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.examples.helloWorld.HelloWorldAgent;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;


public class MyAgentBTJ extends AbstractTucsonAgent  {
	
	public MyAgentBTJ(String id) throws TucsonInvalidAgentIdException {
		super(id);
		// TODO Auto-generated constructor stub
	}

	private class InList implements TucsonOperationCompletionListener{
		String name;
		public InList(String name){
			this.name=name;
		}
		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			if(op.isResultSuccess()){
				log(name+" result succes");
			}else{
				log(name+" result failde");
			}
			
		}

		@Override
		public void operationCompleted(ITucsonOperation op) {
			// TODO Auto-generated method stub
		}
		
		
	}
	private class OutList implements TucsonOperationCompletionListener{

		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			if(op.isResultSuccess()){
				log("out result succes");
			}else{
				log("out result failde");
			}
			
		}

		@Override
		public void operationCompleted(ITucsonOperation op) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	public void log(String msg) {
		System.out.println(msg);
		
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
		 final EnhancedAsynchACC acc = this.getContext();
		 try {
	            final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
	                    "localhost", "20504");
	            final LogicTuple tuple = LogicTuple.parse("hello(world)");
	            
	            AsynchQueueManager aqm=new AsynchQueueManager("aqm");
	            

	            In in1 = new In(tid, tuple);
	            MyL l1=new MyL();
	            aqm.add(in1,l1);
	            aqm.add(in1,l1);
	            aqm.add(in1,l1);
	            aqm.add(in1,l1);
	            aqm.add(in1,l1);
	            Out o = new Out(tid, tuple);
	            MyL l2=new MyL();
				aqm.add(o,l2);
				aqm.add(o,l2);
				aqm.add(o,l2);
				aqm.add(o,l2);
	            log("aggiunta???:");
	            Thread.sleep(3000);
	            aqm.shutdownNow();
	            
	            
	            
	           

	        } catch (final TucsonInvalidTupleCentreIdException e) {
	            e.printStackTrace();
	        } catch (final InvalidTupleException e) {

	            e.printStackTrace();
	        }  catch (TucsonInvalidAgentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}

	  public static void main(final String[] args) {
		    String aid = null;
	        if (args.length == 1) {
	            aid = args[0];
	        } else {
	            aid = "AsynqQueue";
	        }
	        try {
				new MyAgentBTJ("btj").go();
			} catch (TucsonInvalidAgentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	  private class MyL implements TucsonOperationCompletionListener{

		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			System.out.println("op fatta successo??:"+op.isResultSuccess());
		}

		@Override
		public void operationCompleted(ITucsonOperation op) {
			// TODO Auto-generated method stub
			
		}
		  
	  }
	

}
