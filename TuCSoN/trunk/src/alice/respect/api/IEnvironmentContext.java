package alice.respect.api;

import alice.tuplecentre.core.InputEvent;

public interface IEnvironmentContext {

    long getCurrentTime();

    void notifyInputEnvEvent(InputEvent ev);

    void notifyInputEvent(InputEvent ev);

}
