package it.unibo.mok.gui.impl;

import it.unibo.mok.gui.interfaces.Executable;
import it.unibo.mok.gui.interfaces.GUI;

public class Executor {

    private Executable executable;
    private final GUI gui;
    private boolean isExecuting = false;
    private Thread thread;

    public Executor(final GUI gui) {
        this.gui = gui;
    }

    public boolean execute(final Executable executable) {
        if (!this.isExecuting) {
            this.gui.clear();
            this.executable = executable;
            executable.setGUI(this.gui);
            if (executable.setup()) {
                this.thread = new Thread(executable);
                this.thread.start();
                this.isExecuting = true;
                return true;
            }
        }
        return false;
    }

    public void stopCurrentExecutable() {
        if (this.thread != null) {
            this.isExecuting = false;
            this.thread.interrupt();
            this.executable.stop();
        }
    }

}
