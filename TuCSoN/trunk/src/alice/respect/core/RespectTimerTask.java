package alice.respect.core;

import java.util.TimerTask;

import alice.tuplecentre.core.InputEvent;

public class RespectTimerTask extends TimerTask {

    private final RespectOperation op;
    private final RespectVMContext vm;

    public RespectTimerTask(final RespectVMContext rvm,
            final RespectOperation rop) {
        super();
        this.vm = rvm;
        this.op = rop;
    }

    @Override
    public void run() {
        this.vm.notifyInputEvent(new InputEvent(this.vm.getId(), this.op,
                this.vm.getId(), this.vm.getCurrentTime()));
    }

}
