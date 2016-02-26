package it.unibo.mok.gui.interfaces;

import java.util.concurrent.Semaphore;

public interface Link extends Drawable {

    Node getFirst();

    Node getSecond();

    Semaphore getTransferCompleted();

    Semaphore getTransferLock();

}
