package it.unibo.mok.inspector.tucson;

import it.unibo.mok.gui.interfaces.GUI;

public class LinkTransferMonitor {

    private final String first;
    private final GUI gui;
    private final String second;

    public LinkTransferMonitor(final String first, final String second,
            final GUI gui) {
        this.first = first;
        this.second = second;
        this.gui = gui;
    }

    public synchronized void doTransfer(final String tuple,
            final boolean firstToSecond) {
        System.out.println("TRANSFER");
        if (firstToSecond) {
            this.gui.transferMolecule(tuple, this.first, this.second, 1);
        } else {
            this.gui.transferMolecule(tuple, this.second, this.first, 1);
        }
    }

    public String getFirst() {
        return this.first;
    }

    public String getSecond() {
        return this.second;
    }

}
