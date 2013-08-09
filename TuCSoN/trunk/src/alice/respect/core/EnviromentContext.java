package alice.respect.core;

import alice.respect.api.IEnvironmentContext;
import alice.tuplecentre.core.InputEvent;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 01/lug/2013
 * 
 */
public class EnviromentContext implements IEnvironmentContext {

    private final RespectVMContext vm;

    /**
     * 
     * @param rvm
     *            the ReSpecT VM this context refers to
     */
    public EnviromentContext(final RespectVMContext rvm) {
        this.vm = rvm;
    }

    public long getCurrentTime() {
        return this.vm.getCurrentTime();
    }

    public void notifyInputEnvEvent(final InputEvent ev) {
        this.vm.notifyInputEnvEvent(ev);
    }

    public void notifyInputEvent(final InputEvent ev) {
        this.vm.notifyInputEvent(ev);
    }

}
