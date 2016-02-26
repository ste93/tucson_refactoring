package it.unibo.mok.inspector.executors;

import it.unibo.mok.gui.interfaces.GUI;

public class LinkRunnable implements Runnable {

    private final GUI gui;
    private final int n;
    private final String node1;
    private final String node2;

    public LinkRunnable(final int n, final String node1, final String node2,
            final GUI gui) {
        this.n = n;
        this.node1 = node1;
        this.node2 = node2;
        this.gui = gui;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
                break;
            }

            this.gui.transferMolecule(this.n + "", this.node1, this.node2, 1);

            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
                break;
            }

            this.gui.transferMolecule(this.n + "", this.node2, this.node1, 1);

        }

    }

}
