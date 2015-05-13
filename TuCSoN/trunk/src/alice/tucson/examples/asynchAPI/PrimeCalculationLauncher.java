package alice.tucson.examples.asynchAPI;

import java.util.logging.Logger;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

/**
 * An example of usage of TuCSoN asynchSupport API. A master agent (MasterAgent)
 * delegates to nOfWorkers other worker agents (PrimeCalculator) computationally
 * expensive operations (prime numbers calculations). The master uses TuCSoN
 * AsynchOpsHelper to request in, inp and out operations in asynchronous mode:
 * in this way he can do whatever he wants while calculations progress, without
 * having to synchronously wait the end of all operations. The master makes
 * MasterAgent.REQUESTS out of tuples like "calcprime(N)". The worker agents
 * requests inp and calculate primes up to N.
 *
 * @author Consalici-Drudi
 *
 */
public class PrimeCalculationLauncher {

    public static void main(final String[] args) {
        try {
            final int nOfWorkers = 3;
            Logger.getLogger("PrimeCalculationLauncher").info(
                    "Starting Master Agent");
            new MasterAgent("master", nOfWorkers).go();
            for (int i = 0; i < nOfWorkers; i++) {
                Logger.getLogger("PrimeCalculationLauncher").info(
                        "Starting Prime Calculator n. " + i);
                new PrimeCalculator("worker-" + i).go();
            }
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }

    }

}
