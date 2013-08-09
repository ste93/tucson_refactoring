package alice.respect.api;

import alice.tuplecentre.core.InputEvent;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 27/giu/2013
 * 
 */
public interface IEnvironmentContext {

    /**
     * 
     * @return the current time
     */
    long getCurrentTime();

    /**
     * 
     * @param ev
     *            the input environment event to notify
     */
    void notifyInputEnvEvent(InputEvent ev);

    /**
     * 
     * @param ev
     *            the input event to notify
     */
    void notifyInputEvent(InputEvent ev);

}
