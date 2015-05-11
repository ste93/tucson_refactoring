package alice.tucson.asynchSupport;

import java.util.Iterator;

/**
 * A specialization of FilterQueue who manage the completed operations
 *
 * @author Consalici Drudi
 *
 */
public class CompletedFilterQueue extends FilterQueue {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * return a list of all success operations
     *
     * @return the queue whhere the operations successfully completed are put
     */
    public CompletedFilterQueue getAllSuccessOp() {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        final CompletedFilterQueue typedOp = new CompletedFilterQueue();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (elem.getOp().isResultSuccess()) {
                typedOp.add(elem);
            }
        }
        return typedOp;
    }

    /**
     * return a list of all operations who match the arguments
     */
    @Override
    public CompletedFilterQueue getAllTypedOperation(final Class<?> optype) {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        final CompletedFilterQueue typedOp = new CompletedFilterQueue();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (elem.getAction().getClass().equals(optype)) {
                typedOp.add(elem);
            }
        }
        return typedOp;
    }

    /**
     * return a list of all unsuccess operations
     *
     * @return the queue where all the failed operations are put
     */
    public CompletedFilterQueue getAllUnsuccessOp() {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        final CompletedFilterQueue typedOp = new CompletedFilterQueue();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (!elem.getOp().isResultSuccess()) {
                typedOp.add(elem);
            }
        }
        return typedOp;
    }

    /**
     * remove from the completedQueue the success operations
     */
    public void removeAllSuccessOperation() {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (elem.getOp().isResultSuccess()) {
                this.remove(elem);
            }
        }
    }

    /**
     * remove from the completedQueue the unsuccess operations
     */
    public void removeAllUnsuccessOperation() {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (!elem.getOp().isResultSuccess()) {
                this.remove(elem);
            }
        }
    }

}
