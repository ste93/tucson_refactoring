package alice.tucson.examples.asynchAgent;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
/**
 * An Example of the use of AsynchQueueManager.
 * An Agent(master) delegate to three other agent (PrimeCalculator) heavy operation.
 * The master use the aqm to make in, inp and out operation in asynch mode.
 * In this way he can do whatever he want and don't have to wait synchronously the end of all operations.
 * the master make 50 out (throught AsynchQueueManager) of tuple like calcprime(N).
 * the slave agent make inp and calculate prime-counting(N).
 * then they make out with the result.
 * @author Consalici-Drudi
 *
 */
public class StartFactorialSystem {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new MasterAgent("master").go();
			for(int i = 0; i<3; i++){
				System.out.println("parte il "+i);
				new PrimeCalculator("factorialcalculator"+i).go();
			}
		} catch (TucsonInvalidAgentIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
