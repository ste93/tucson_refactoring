package it.unibo.sd.jade.example.classic;

import java.util.Random;

import it.unibo.sd.jade.examples.myex.AsynchConsumer;
import it.unibo.sd.jade.examples.myex.AsynchQueueManager;
import it.unibo.sd.jade.operations.ordinary.In;
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
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class Consumer extends AbstractTucsonAgent{
	int blue=0;
	int yellow=0;
	int green=0;
	int red=0;
	int black=0;
	public int count=0;
	public Consumer(String id) throws TucsonInvalidAgentIdException {
		super(id);
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
	public synchronized  void addColor(String color){
		switch(color){
		case "blue": 
			blue++;
			break;
		case "green":
			green++;
			break;
		case "yellow":
			yellow++;
			break;
		case "red":
			red++;
			break;
		default: black++;
		}
	}
	@Override
	protected void main() {
		 try {
	            In in;
	   		 	final EnhancedAsynchACC acc = this.getContext();
	   		 	LogicTuple tuple;
	            final TucsonTupleCentreId tid = new TucsonTupleCentreId("default",
	                    "localhost", "20504");
	            Random r=new Random();



		        
		        for(int i=0;i<5000;i++){
				        tuple = LogicTuple.parse("color(X)");
				        in=new In(tid,tuple);
				        InList iList=new InList();
				        //int sleep=r.nextInt(100);
				        //Thread.sleep(sleep);
				        in.executeAsynch(acc, iList);
		        }
		        log(""+this.getTucsonAgentId()+"green="+green+" blue="+blue+" yellow="+yellow+" red="+red+" e count="+count+" e failedFOrTime:");
		        Thread.sleep(5000);
		        log(""+this.getTucsonAgentId()+"io muoio");

	            
	            
	            
	           

	        } catch (final TucsonInvalidTupleCentreIdException e) {
	            e.printStackTrace();
	        } catch (InvalidTupleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TucsonOperationNotPossibleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnreachableNodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}
	 public static void main(final String[] args) {
		    /*String aid = null;
	        if (args.length == 1) {
	            aid = args[0];
	        } else {
	            aid = "asynqqueue";
	        }*/
	        try {
				new Consumer("consumeruno").go();
				new Consumer("consumerdue").go();
				/*new AsynchConsumer("consumertre").go();
				new AsynchConsumer("consumerquattro").go();
				new AsynchConsumer("consumercinque").go();
				new AsynchConsumer("consumersei").go();
				new AsynchConsumer("consumersette").go();
				new AsynchConsumer("consumerotto").go();*/
			} catch (TucsonInvalidAgentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	 private class InList implements TucsonOperationCompletionListener{
			@Override
			public void operationCompleted(AbstractTupleCentreOperation op) {
				count++;
				if(op.isResultSuccess()){
					LogicTuple tuple = (LogicTuple) op.getTupleResult();
					try {
						String color=tuple.getArg(0).toString();
						addColor(color);
						log("[consumer listener]green="+green+" blue="+blue+" yellow="+yellow+" red="+red);
						
					} catch (InvalidOperationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					log("[consumer listener]out result failde:");
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

}
