package alice.tucson.examples.asynchAgent;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

public class startProducerConsumer {
	
	public static void log(String msg) {
		System.out.println(msg);	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log("[Starter]: START");
		
			try {
				for(int i=0;i<1;i++){
					//new AsynchProducer("producer"+1).go();
					new AsynchConsumer("consumer"+i,1000).go();
					Thread.sleep(10000);
					new AsynchProducer("producer"+i,1000).go();
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
