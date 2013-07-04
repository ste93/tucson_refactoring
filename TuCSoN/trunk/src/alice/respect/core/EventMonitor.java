package alice.respect.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EventMonitor {

	private ReentrantLock lock;
	private Condition canCompute;
	private boolean hasEvent;

	public EventMonitor() {
		hasEvent = false;
		lock = new ReentrantLock();
		canCompute = lock.newCondition();
	}

	public void signalEvent() {
		lock.lock();
		try {
			hasEvent = true;
			canCompute.signal();
		} finally {
			lock.unlock();
		}
	}

	public void awaitEvent() throws InterruptedException {
		lock.lock();
		try {
			while (!hasEvent) {
				canCompute.await();
			}
			hasEvent = false;
			return;
		} finally {
			lock.unlock();
		}
	}

	public boolean hasEvent() {
		lock.lock();
		try {
			return hasEvent;
		} finally {
			lock.unlock();
		}
	}

}
