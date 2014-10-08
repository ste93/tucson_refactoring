package it.unibo.sd.jade.service;

import jade.core.Service.Slice;

/**
 * TucsonSlice. Requested by JADE kernel services extension mechanism.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public interface TucsonSlice extends Slice {
    /**
     * 
     */
    String EXECUTE_ASYNCH = "Execute-Asynch";
    /**
     * 
     */
    String EXECUTE_SYNCH = "Execute-Synch";
}
