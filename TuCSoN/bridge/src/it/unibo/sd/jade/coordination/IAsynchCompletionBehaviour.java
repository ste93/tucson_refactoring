package it.unibo.sd.jade.coordination;

import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * Interface given to JADE developers to specify which behaviour should handle
 * asynchronous operations' completions (in "interrupt mode")
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public interface IAsynchCompletionBehaviour {
    /**
     * 
     * @param ev
     *            the TuCSoN operation completion event to handle
     */
    void setTucsonOpCompletionEvent(TucsonOpCompletionEvent ev);
}
