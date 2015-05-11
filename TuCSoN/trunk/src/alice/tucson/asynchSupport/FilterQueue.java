package alice.tucson.asynchSupport;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import alice.tucson.asynchSupport.operations.AbstractTucsonAction;

/**
 * Queue for all operation added in AsynchQueueManager.
 *
 * @author Consalici Drudi
 *
 */
public class FilterQueue extends
        LinkedBlockingQueue<TucsonOperationForAsynchManager> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * return a list of all operations who match the arguments
     * 
     * @param optype
     *            the type of the operations to retrieve
     * @return the queue where the operation result will be put
     */
    public FilterQueue getAllTypedOperation(final Class<?> optype) {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        final FilterQueue typedOp = new FilterQueue();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (elem.getAction().getClass().equals(optype)) {
                typedOp.add(elem);
            }
        }
        return typedOp;
    }

    /**
     * remove the specific action to list
     *
     * @param action
     *            the TuCSoN action to be removed
     * @return {@code true} or {@code false} depending on whether removal was
     *         successful or not
     */
    public boolean removeAbstractTucsonAction(final AbstractTucsonAction action) {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (elem.getAction().equals(action)) {
                elem.setDeleted(true);
                return this.remove(elem);
            }
        }
        return false;
    }

    /**
     * remove all the operations match with argument
     *
     * @param optype
     *            the type of the operations to remove
     */
    public void removeAllTypedOperation(final Class<?> optype) {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (elem.getAction().getClass().equals(optype)) {
                elem.setDeleted(true);
                this.remove(elem);
            }
        }
    }

    /**
     * remove the operation identified by ID to list
     *
     * @param id
     *            the ID of the operation to remove
     * @return {@code true} or {@code false} depending on whether removal was
     *         successful or not
     */
    public boolean removeOperationById(final long id) {
        final Iterator<TucsonOperationForAsynchManager> it = this.iterator();
        while (it.hasNext()) {
            final TucsonOperationForAsynchManager elem = it.next();
            if (elem.getOp().getId() == id) {
                elem.setDeleted(true);
                return this.remove(elem);
            }
        }
        return false;
    }

}
