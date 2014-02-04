package it.unibo.sd.jade.coordination;

import java.util.List;
import java.util.Map;

import alice.tucson.service.TucsonOpCompletionEvent;
import alice.tucson.service.TucsonOperation;

/**
 * AsyncOpResultData: classe usata per rappresentare la struttura dati con la
 * quale controllare se il risultato dell'operazione di coordinazione in
 * modalità sincrona con tecnica di polling è stata completata o è pendente
 * 
 * @author lucasangiorgi
 * 
 */
public class AsyncOpResultData {

    private List<TucsonOpCompletionEvent> events;
    private Map<Long, TucsonOperation> mapOpPend;
    private long opId;

    /**
     * 
     * @param o
     *            the TuCSoN operation id
     * @param e
     *            the TuCSoN operation completion event
     * @param p
     *            the map of pending operations
     */
    public AsyncOpResultData(final long o,
            final List<TucsonOpCompletionEvent> e,
            final Map<Long, TucsonOperation> p) {
        this.opId = o;
        this.events = e;
        this.mapOpPend = p;
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
    public TucsonOpCompletionEvent getEventComplete(final long o) {
        TucsonOpCompletionEvent ev = null;
        synchronized (this.events) {
            final boolean trovato = false;
            for (int i = 0; (i < this.events.size()) && !trovato; i++) {
                if (this.events.get(i).getOpId().getId() == o) { // operazione
                                                                 // richiesta
                                                                 // trovata
                    // prelevo l'operazione completata dalla coda dei messaggi
                    // completati
                    ev = this.events.remove(i);
                }
            }
        }
        return ev;
    }

    /**
     * 
     * @return the List of TuCSoN completion events
     */
    public List<TucsonOpCompletionEvent> getListEvents() {
        return this.events;
    }

    /**
     * 
     * @return the Map of pending operations
     */
    public Map<Long, TucsonOperation> getMapOpPend() {
        return this.mapOpPend;
    }

    /**
     * 
     * @return the operation id
     */
    public long getOpId() {
        return this.opId;
    }

    /**
     * controlla se l'operazione richiesta è ancora pendente o meno
     * 
     * @param o
     *            id dell'operazione che si vuole controllare
     * @return true se è nella lista delle operazioni pendenti, false se non lo
     *         è
     */
    public boolean isPendant(final long o) {
        synchronized (this.mapOpPend) {
            final TucsonOperation op1 = this.mapOpPend.get(o);
            if (op1 != null) { // operazione nella lista delle operazioni
                               // pendenti
                return false;
            }
            return true;
        }
    }

    /**
     * 
     * @param e
     *            the List of completion events
     */
    public void setListEvents(final List<TucsonOpCompletionEvent> e) {
        this.events = e;
    }

    /**
     * 
     * @param p
     *            the map of pending operations
     */
    public void setMapOpPend(final Map<Long, TucsonOperation> p) {
        this.mapOpPend = p;
    }

    /**
     * 
     * @param id
     *            the operation id
     */
    public void setOpId(final long id) {
        this.opId = id;
    }
}
