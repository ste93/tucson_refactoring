package it.unibo.sd.jade.example.classic;

import java.util.Random;

import it.unibo.sd.jade.examples.myex.AsynchProducer;
import it.unibo.sd.jade.examples.myex.AsynchQueueManager;
import it.unibo.sd.jade.operations.ordinary.Out;
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
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class Producer extends AbstractTucsonAgent{

	public Producer(String id) throws TucsonInvalidAgentIdException {
		super(id);
	}

	@Override
	public void operationCompleted(AbstractTupleCentreOperation op) {
		
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		
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
	            

		        
		        for(int i=0;i<10000;i++){

		            
			        int color=r.nextInt(4);
			        //log("result="+getColor(color));
			        tuple = LogicTuple.parse("color("+getColor(color)+")");
			        out=new Out(tid,tuple);
			        OutList oList=new OutList();
			        //int sleep=r.nextInt(100);
			        //Thread.sleep(sleep);
			        out.executeAsynch(acc,oList);
			        
		        }
		        log("[producer]io muoio");

	            
	            
	            
	           

	        } catch (final TucsonInvalidTupleCentreIdException e) {
	            e.printStackTrace();
	        }  catch (InvalidTupleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnreachableNodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		
	}
	public void log(String msg) {
		System.out.println(msg);
		
	}
	 public static void main(final String[] args) {
		    String aid = null;
	        if (args.length == 1) {
	            aid = args[0];
	        } else {
	            aid = "ProducerClassic";
	        }
	        try {
				new Producer(aid).go();
			} catch (TucsonInvalidAgentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

}
