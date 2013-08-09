package alice.respect.probe;

import alice.respect.transducer.TransducerId;

/**
 * Simple interface for a generic probe.
 * 
 * @author Steven Maraldi
 * 
 */
public interface ISimpleProbe {

    /**
     * Get the probe's identifier
     * 
     * @return probe's identifier
     */
    public ProbeId getIdentifier();

    /**
     * Gets the transducer which the probe is communicating with.
     * 
     * @return the transducer's identifier
     */
    public TransducerId getTransducer();

    /**
     * Reads the probe's value. A read request should force an event from the
     * device
     * 
     * @param key
     *            the parameter to read
     * 
     * @return true if success
     */
    public boolean readValue(String key);

    /**
     * Sets the transducer which the probe will communicate with.
     * 
     * @param tId
     *            transducer's identifier
     */
    public void setTransducer(TransducerId tId);

    /**
     * Set the specific value of a parameter of the probe
     * 
     * @param key
     *            the parameter to set
     * @param value
     *            value to be set.
     * @return true if the operation has been successfully executed.
     */
    public boolean writeValue(String key, int value);
}
