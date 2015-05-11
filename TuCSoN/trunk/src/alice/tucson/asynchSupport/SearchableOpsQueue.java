package alice.tucson.asynchSupport;

import java.util.concurrent.LinkedBlockingQueue;
import alice.tucson.asynchSupport.operations.AbstractTucsonAction;

/**
 * Queue storing pending operations delegated to
 * {@link alice.tucson.asynchSupport.AsynchOpsHelper}.
 *
 * @author Fabio Consalici, Riccardo Drudi
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 *
 */
public class SearchableOpsQueue extends LinkedBlockingQueue<TucsonOpWrapper> {

    private static final long serialVersionUID = 1L;

    /**
     * Gets all the operations whose type matches the given type
     * 
     * @param optype
     *            the operation type to look for
     *
     * @return the queue of operations of the same input type
     */
    public SearchableOpsQueue getMatchingOps(final Class<?> optype) {
        final SearchableOpsQueue matching = new SearchableOpsQueue();
        for (TucsonOpWrapper tow : this) {
            if (tow.getAction().getClass().equals(optype)) {
                matching.add(tow);
            }
        }
        return matching;
    }

    /**
     * Removes the specific action from the queue
     *
     * @param action
     *            the TuCSoN action to be removed
     * @return {@code true} or {@code false} depending on whether removal was
     *         successful or not
     */
    public boolean removeOp(final AbstractTucsonAction action) {
        for (TucsonOpWrapper tow : this) {
            if (tow.getAction().equals(action)) {
                tow.setHasBeenRemoved(true);
                return this.remove(tow);
            }
        }
        return false;
    }

    /**
     * Removse all the operations whose type matches the given type
     *
     * @param optype
     *            the operation type whose operations should be removed
     */
    public void removeMatchingOps(final Class<?> optype) {
        for (TucsonOpWrapper tow : this) {
            if (tow.getAction().getClass().equals(optype)) {
                tow.setHasBeenRemoved(true);
                this.remove(tow);
            }
        }
    }

    /**
     * Removes the operation identified by the given ID
     *
     * @param id
     *            the ID of the operation to remove
     * @return {@code true} or {@code false} depending on whether removal was
     *         successful or not
     */
    public boolean removeOpById(final long id) {
        for (TucsonOpWrapper tow : this) {
            if (tow.getOp().getId() == id) {
                tow.setHasBeenRemoved(true);
                return this.remove(tow);
            }
        }
        return false;
    }

}
