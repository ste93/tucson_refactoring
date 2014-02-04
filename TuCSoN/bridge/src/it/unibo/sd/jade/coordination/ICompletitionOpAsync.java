package it.unibo.sd.jade.coordination;

import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * Interfaccia utilizzata per poter "ignettare" nel comportamento che implementa
 * questa interfaccia il risultato dell'operazione di coordinazione sospensiva
 * in modalit� asincrona.
 * 
 * @author lucasangiorgi
 * 
 */
public interface ICompletitionOpAsync {

    /**
     * 
     * @param ev
     *            the TuCSoN operation completion event to handle
     */
    void setTucsonOpCompletionEvent(TucsonOpCompletionEvent ev);
}
