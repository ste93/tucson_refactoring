package alice.respect.core;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 11/ago/2013
 * 
 */
public interface ISerialEventListener {

    /**
     * 
     * @return the String representation of the listener name
     */
    String getListenerName();

    /**
     * 
     * @param value
     *            the String representation of the event to notify
     */
    void notifyEvent(String value);
}
