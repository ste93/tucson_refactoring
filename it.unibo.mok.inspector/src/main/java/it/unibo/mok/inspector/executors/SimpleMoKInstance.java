package it.unibo.mok.inspector.executors;

import it.unibo.mok.gui.impl.model.mok.MoKCompartment;
import it.unibo.mok.gui.impl.model.mok.MoKMolecule;
import it.unibo.mok.gui.interfaces.Executable;
import it.unibo.mok.gui.interfaces.GUI;

public class SimpleMoKInstance implements Executable {

    private GUI gui;
    private Thread t1;
    private Thread t2;

    @Override
    public void run() {

        /* Topology setup */
        this.gui.addNode(new MoKCompartment("comp0", "127.0.0.1", 8027));
        this.gui.addNode(new MoKCompartment("comp1", "127.0.0.1", 8040));
        this.gui.addNode(new MoKCompartment("comp2", "127.0.0.1", 8055));
        this.gui.addLink("comp0", "comp1");
        this.gui.addLink("comp0", "comp2");

        /* Initial molecules */
        this.gui.addMolecule(new MoKMolecule("paperino", 10), "comp0");
        this.gui.addMolecule(new MoKMolecule("paperino", 10), "comp0"); // Same
                                                                        // molecule
                                                                        // name,
                                                                        // concentrations
                                                                        // are
                                                                        // added
        this.gui.addMolecule(new MoKMolecule("pippo", 21), "comp0");
        this.gui.addMolecule(new MoKMolecule("pluto", 99), "comp0");
        this.gui.addMolecule(new MoKMolecule("paperino", 2), "comp1");
        this.gui.setMoleculeConcentration("pippo", "comp0", 9); // Overrides
                                                                // previous
                                                                // concentration

        /*
         * To achieve a proper GUI responsiveness, one thread should be made for
         * every link. This is because transfer calls are blocking-by-link: if
         * the link that must be used to transfer is already used, they block
         * until the link is not used anymore.
         */

        this.t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        Thread.sleep(1000);
                        SimpleMoKInstance.this.gui.transferMolecule("paperino",
                                "comp0", "comp1", 2);
                        SimpleMoKInstance.this.gui.transferMolecule("pluto",
                                "comp0", "comp1", 1);
                        Thread.sleep(1000);
                        SimpleMoKInstance.this.gui.transferMolecule("paperino",
                                "comp1", "comp0", 2);
                    }
                } catch (final InterruptedException e) {
                }
            }
        });

        this.t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        Thread.sleep(1000);
                        SimpleMoKInstance.this.gui.transferMolecule("pippo",
                                "comp0", "comp2", 4);
                        SimpleMoKInstance.this.gui.transferMolecule("pippo",
                                "comp0", "comp2", 3);
                        Thread.sleep(1000);
                        SimpleMoKInstance.this.gui.transferMolecule("pippo",
                                "comp2", "comp0", 4);
                        SimpleMoKInstance.this.gui.transferMolecule("pippo",
                                "comp2", "comp0", 3);
                    }
                } catch (final InterruptedException e) {
                }

            }
        });

        this.t1.start();
        this.t2.start();

    }

    @Override
    public void setGUI(final GUI gui) {
        this.gui = gui;
    }

    @Override
    public boolean setup() {
        return true;
    }

    @Override
    public void stop() {
        this.t1.interrupt();
        this.t2.interrupt();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
