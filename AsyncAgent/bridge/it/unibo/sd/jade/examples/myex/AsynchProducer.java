package it.unibo.sd.jade.examples.myex;


import java.util.Random;

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


public class AsynchProducer extends AbstractTucsonAgent  {
	
	public AsynchProducer(String id) throws TucsonInvalidAgentIdException {
		super(id);
	}

	private class OutList implements TucsonOperationCompletionListener{

		@Override
		public void operationCompleted(AbstractTupleCentreOperation op) {
			if(op.isResultSuccess()){
			}else{
				log("[producer]out result failde");
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
	public String getColor(int color){
		switch(color){
			case 0: 
				return "blue";
			case 1:
				return "green";
			case 2:
				return "yellow";
			case 3:
				return "red";
			default: return "black";
		}
	}
	@Override
	protected void main() {
		 try {
	            Out out;
	   		 	final EnhancedAsynchACC acc = this.getContext();
	   		 	LogicTuple tuple;
	            final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
	                    "localhost", "20504");
	            Random r=new Random();
	            
	            AsynchQueueManager aqm=new AsynchQueueManager("aqm"+this.getTucsonAgentId());

		        
		        for(int i=0;i<5000;i++){

		            
			        int color=r.nextInt(4);
			        //log("result="+getColor(color));
			        tuple = LogicTuple.parse("color("+getColor(color)+")");
			        out=new Out(tid,tuple);
			        OutList oList=new OutList();
			        //int sleep=r.nextInt(100);
			        //Thread.sleep(sleep);
			        aqm.add(out, oList);
		        }
		        aqm.shutdown();
		        log("["+this.getTucsonAgentId()+"]io muoio");

	            
	            
	            
	           

	        } catch (final TucsonInvalidTupleCentreIdException e) {
	            e.printStackTrace();
	        } catch (TucsonInvalidAgentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidTupleException e) {
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
				new AsynchProducer("producer").go();
			} catch (TucsonInvalidAgentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

}
