package alice.tucson.examples.asynchAPI;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
/**
 * An Example of the use of AsynchQueueManager. An Agent(master) delegate to
 * three other agent (PrimeCalculator) heavy operation. The master use the aqm
 * to make in, inp and out operation in asynch mode. In this way he can do
 * whatever he want and don't have to wait synchronously the end of all
 * operations. the master make 50 out (throught AsynchQueueManager) of tuple
 * like calcprime(N). the slave agent make inp and calculate prime-counting(N).
 * then they make out with the result.
 * 
 * @author Consalici-Drudi
 *
 */
public class StartPrimeCalcSystem {

	public static void main(String[] args) {
		try {
		        int nPrimeCalc=3;
			new MasterAgent("master", nPrimeCalc).go();
			PrimeCalculator fc;
			for(int i = 0; i<nPrimeCalc; i++){
				System.out.println("parte il "+i);
				new PrimeCalculator("factorialcalculator"+i).go();
			}
		} catch (TucsonInvalidAgentIdException e) {
			e.printStackTrace();
		}
		
	}

}
