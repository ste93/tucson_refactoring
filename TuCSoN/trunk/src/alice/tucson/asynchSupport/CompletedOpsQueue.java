package alice.tucson.asynchSupport;

/**
 * {@link alice.tucson.asynchSupport.SearchableOpsQueue} specialised for handling
 * completed operations.
 *
 * @author Fabio Consalici, Riccardo Drudi
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * 
 */
public class CompletedOpsQueue extends SearchableOpsQueue {

    private static final long serialVersionUID = 1L;

    /**
     * Gets all successfully completed operations
     *
     * @return the queue of successfully completed operations
     */
    public CompletedOpsQueue getSuccessfulOps() {
        final CompletedOpsQueue successful = new CompletedOpsQueue();
        for (TucsonOpWrapper tow : this) {
            if (tow.getOp().isResultSuccess()) {
                successful.add(tow);
            }
        }
        return successful;
    }

    @Override
    public CompletedOpsQueue getMatchingOps(final Class<?> optype) {
        final CompletedOpsQueue matching = new CompletedOpsQueue();
        for (TucsonOpWrapper tow : this) {
            if (tow.getAction().getClass().equals(optype)) {
                matching.add(tow);
            }
        }
        return matching;
    }

    /**
     * Gets all failed operations
     *
     * @return the queue of failed operations
     */
    public CompletedOpsQueue getFailedOps() {
        final CompletedOpsQueue failed = new CompletedOpsQueue();
        for (TucsonOpWrapper tow : this) {
            if (!tow.getOp().isResultSuccess()) {
                failed.add(tow);
            }
        }
        return failed;
    }

    /**
     * Removes all successfully completed operations
     */
    public void removeSuccessfulOps() {
        for (TucsonOpWrapper tow : this) {
            if (tow.getOp().isResultSuccess()) {
                this.remove(tow);
            }
        }
    }

    /**
     * Removes all failed operations
     */
    public void removeFailedOps() {
        for (TucsonOpWrapper tow : this) {
            if (!tow.getOp().isResultSuccess()) {
                this.remove(tow);
            }
        }
    }

}
