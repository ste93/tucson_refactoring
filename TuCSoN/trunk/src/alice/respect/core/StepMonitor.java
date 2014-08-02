package alice.respect.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class StepMonitor {
    private final Condition canCompute;
    private final ReentrantLock lock;

    /**
     * 
     */
    public StepMonitor() {
        this.lock = new ReentrantLock();
        this.canCompute = this.lock.newCondition();
    }

    /**
     * 
     * @throws InterruptedException
     *             if the synchronisation wait gets interrupted
     */
    public void awaitEvent() throws InterruptedException {
        this.lock.lock();
        try {
                this.canCompute.await();
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * 
     */
    public void signalEvent() {
        this.lock.lock();
        try {
            this.canCompute.signal();
        } finally {
            this.lock.unlock();
        }
    }
}