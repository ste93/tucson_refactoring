package it.unibo.mok.inspector;

import it.unibo.mok.gui.impl.MoKGUI;
import it.unibo.mok.gui.impl.MoKSimulator;
import it.unibo.mok.gui.interfaces.GUI;
import it.unibo.mok.inspector.executors.SimpleMoKInstance;
import it.unibo.mok.inspector.executors.StressTest;
import it.unibo.mok.inspector.tucson.TucsonInspector;

public class Main {

    public static void main(final String args[]) {
        final GUI gui = new MoKGUI(new MoKSimulator());
        // gui.addExecutable(new MyTucson());
        gui.addExecutable(new TucsonInspector());
        gui.addExecutable(new SimpleMoKInstance());
        gui.addExecutable(new StressTest());
        gui.show();
    }

}
