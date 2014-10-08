package it.unibo.sd.jade.coordination;

import java.util.LinkedList;
import java.util.List;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * TucsonOpResult. Object wrapping TuCSoN coordination operation results to JADE
 * agents.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class TucsonOpResult {
    /*
     * next result to provide. Used to give the result to requesting agents and
     * test if operation is still to be requested
     */
    private int nextRes;
    /*
     * used to know if the operation result is ready
     */
    private boolean ready;
    private final List<TucsonOpCompletionEvent> tucsonCompletionEvents;

    /**
     * 
     */
    public TucsonOpResult() {
        this.tucsonCompletionEvents = new LinkedList<TucsonOpCompletionEvent>();
        this.nextRes = 0;
        this.ready = false;
    }

    /**
     * @return the next result
     */
    public int getNextRes() {
        return this.nextRes;
    }

    /**
     * @return the list of completion events
     */
    public List<TucsonOpCompletionEvent> getTucsonCompletionEvents() {
        return this.tucsonCompletionEvents;
    }

    /**
     * @return wether the operation result is ready
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * @param n
     *            the next result to set
     */
    public void setNextRes(final int n) {
        this.nextRes = n;
    }

    /**
     * @param r
     *            wether the operation result is ready
     */
    public void setReady(final boolean r) {
        this.ready = r;
    }
}
