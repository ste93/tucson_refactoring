package it.unibo.sd.jade.examples.myex;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

public class startProducerConsumer {
	
	public static void log(String msg) {
		System.out.println(msg);	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log("[Starter]: START");
		
			try {
				for(int i=0;i<2;i++){
					//new AsynchProducer("producer"+1).go();
					new AsynchConsumer("consumer"+i).go();
					Thread.sleep(5000);
					new AsynchProducer("producer"+i).go();
					//new AsynchConsumer("consumer"+1).go();
				}
				
				
			} catch (TucsonInvalidAgentIdException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	

}
