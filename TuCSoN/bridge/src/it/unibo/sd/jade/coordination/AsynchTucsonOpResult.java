package it.unibo.sd.jade.coordination;

import java.util.List;
import java.util.Map;
import alice.tucson.service.TucsonOpCompletionEvent;
import alice.tucson.service.TucsonOperation;

/**
 * AsyncOpResultData: classe usata per rappresentare la struttura dati con la
 * quale controllare se il risultato dell'operazione di coordinazione in
 * modalità asincrona con tecnica di polling è stata completata o è pendente
 * 
 * @author lucasangiorgi
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
     * cerca e restituisce in modo mutuamente esclusivo l'evento riguardante
     * l'operazione richiesta
     * 
     * @param o
     *            id dell'operazione che si vuole cercare
     * @return TucsonOpCompletionEvent se l'operazione è stata completata,
     *         altrimenti null
     */
    public TucsonOpCompletionEvent getTucsonCompletionEvent(final long o) {
        TucsonOpCompletionEvent ev = null;
        synchronized (this.tucsonCompletionEvents) {
            final boolean trovato = false;
            for (int i = 0; i < this.tucsonCompletionEvents.size() && !trovato; i++) {
                if (this.tucsonCompletionEvents.get(i).getOpId().getId() == o) { // operazione
                    // richiesta
                    // trovata
                    // prelevo l'operazione completata dalla coda dei messaggi
                    // completati
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
     * controlla se l'operazione richiesta è ancora pendente o meno
     * 
     * @param o
     *            id dell'operazione che si vuole controllare
     * @return true se è nella lista delle operazioni pendenti, false se non lo
     *         è
     */
    public boolean isPending(final long o) {
        synchronized (this.pendingOps) {
            final TucsonOperation op1 = this.pendingOps.get(o);
            if (op1 != null) { // operazione nella lista delle operazioni
                               // pendenti
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
