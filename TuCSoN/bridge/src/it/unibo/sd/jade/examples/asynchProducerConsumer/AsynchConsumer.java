package it.unibo.sd.jade.examples.asynchProducerConsumer;

import java.util.Random;



import it.unibo.sd.jade.asynchAgent.AsynchQueueManager;
import it.unibo.sd.jade.operations.ordinary.In;
import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * Asynchronous Consumer Example
 * 
 * @author Consalici Drudi
 */

public class AsynchConsumer extends AbstractTucsonAgent  {

    private int blue = 0;
    private int yellow = 0;
    private int green = 0;
    private int red = 0;
    private int black = 0;
    public int count = 0;
    public int failedForTime=0;
    public AsynchConsumer(String id) throws TucsonInvalidAgentIdException {
       super(id);
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
                    e.printStackTrace();
                }
            }else{
                failedForTime++;
                log("[consumer listener]out result failde:"+failedForTime);
            }
        }
        @Override
        public void operationCompleted(ITucsonOperation op) {

        }
    }
	public void log(String msg) {
        System.out.println(msg);
    }
    @Override
    public void operationCompleted(AbstractTupleCentreOperation op) {
 
    }
    @Override
    public void operationCompleted(ITucsonOperation op) {
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

		            AsynchQueueManager aqm=new AsynchQueueManager("aqm"+this.getTucsonAgentId());

			        
			        for(int i=0;i<5000;i++){
					        tuple = LogicTuple.parse("color(X)");
					        in=new In(tid,tuple);
					        InList iList=new InList();
					        //int sleep=r.nextInt(100);
					        //Thread.sleep(sleep);
					        aqm.add(in, iList,100);
			        }
			        aqm.shutdown();
			        log("["+this.getTucsonAgentId()+"]: green="+green+" blue="+blue+" yellow="+yellow+" red="+red+" e count="+count+" e failedFOrTime:"+failedForTime);
			        log("["+this.getTucsonAgentId()+"]: io muoio");

		            
		            
		            
		           

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
		            aid = "asynqqueue";
		        }
		        try {
					new AsynchConsumer("consumer").go();
				} catch (TucsonInvalidAgentIdException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }

	}
