package it.unibo.sd.jade.coordination;

import java.util.List;
import java.util.Map;
import alice.tucson.service.TucsonOpCompletionEvent;
import alice.tucson.service.TucsonOperation;

/**
 * AsynchTucsonOpResult. Data structure representing results of asynchronous
 * operations. Such results are handled by the caller JADE agent in
 * "polling mode".
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class AsynchTucsonOpResult {
    private long opId;
    private Map<Long, TucsonOperation> pendingOps;
    private List<TucsonOpCompletionEvent> tucsonCompletionEvents;

    /**
     * 
     * @param o
     *            the TuCSoN operation id
     * @param e
     *            the TuCSoN operation completion event
     * @param p
     *            the map of pending operations
     */
    public AsynchTucsonOpResult(final long o,
            final List<TucsonOpCompletionEvent> e,
            final Map<Long, TucsonOperation> p) {
        this.opId = o;
        this.tucsonCompletionEvents = e;
        this.pendingOps = p;
    }

    /**
     * 
     * @return the operation id
     */
    public long getOpId() {
        return this.opId;
    }

    /**
     * 
     * @return the Map of pending operations
     */
    public Map<Long, TucsonOperation> getPendingOperations() {
        return this.pendingOps;
    }

    /**
     * Given a TuCSoN operation id, its completion is retrieved (if available)
     * 
     * @param o
     *            id of the TuCSoN operation to look for
     * @return TucsonOpCompletionEvent if available, @code{null} otherwise
     */
    public TucsonOpCompletionEvent getTucsonCompletionEvent(final long o) {
        TucsonOpCompletionEvent ev = null;
        synchronized (this.tucsonCompletionEvents) {
            final boolean trovato = false;
            for (int i = 0; i < this.tucsonCompletionEvents.size() && !trovato; i++) {
                if (this.tucsonCompletionEvents.get(i).getOpId().getId() == o) {
                    // completion found
                    ev = this.tucsonCompletionEvents.remove(i);
                }
            }
        }
        return ev;
    }

    /**
     * 
     * @return the List of TuCSoN completion events
     */
    public List<TucsonOpCompletionEvent> getTucsonCompletionEvents() {
        return this.tucsonCompletionEvents;
    }

    /**
     * Checks wether given operation (through its id) is still pending
     * 
     * @param o
     *            id of the TuCSoN operation to look for
     * @return @code{true} if still pending, @code{false} if not
     */
    public boolean isPending(final long o) {
        synchronized (this.pendingOps) {
            final TucsonOperation op1 = this.pendingOps.get(o);
            if (op1 != null) {
                return false;
            }
            return true;
        }
    }

    /**
     * 
     * @param id
     *            the operation id
     */
    public void setOpId(final long id) {
        this.opId = id;
    }

    /**
     * 
     * @param p
     *            the map of pending operations
     */
    public void setPendingOperations(final Map<Long, TucsonOperation> p) {
        this.pendingOps = p;
    }

    /**
     * 
     * @param e
     *            the List of completion events
     */
    public void setTucsonCompletionEvents(final List<TucsonOpCompletionEvent> e) {
        this.tucsonCompletionEvents = e;
    }
}
