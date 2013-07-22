package alice.respect.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EventMonitor {

    private final Condition canCompute;
    private boolean hasEvent;
    private final ReentrantLock lock;

    public EventMonitor() {
        this.hasEvent = false;
        this.lock = new ReentrantLock();
        this.canCompute = this.lock.newCondition();
    }

    public void awaitEvent() throws InterruptedException {
        this.lock.lock();
        try {
            while (!this.hasEvent) {
                this.canCompute.await();
            }
            this.hasEvent = false;
            return;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean hasEvent() {
        this.lock.lock();
        try {
            return this.hasEvent;
        } finally {
            this.lock.unlock();
        }
    }

    public void signalEvent() {
        this.lock.lock();
        try {
            this.hasEvent = true;
            this.canCompute.signal();
        } finally {
            this.lock.unlock();
        }
    }

}
