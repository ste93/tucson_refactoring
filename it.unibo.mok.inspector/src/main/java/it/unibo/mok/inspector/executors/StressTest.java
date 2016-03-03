package it.unibo.mok.inspector.executors;

import it.unibo.mok.gui.impl.model.mok.MoKCompartment;
import it.unibo.mok.gui.impl.model.mok.MoKMolecule;
import it.unibo.mok.gui.interfaces.Executable;
import it.unibo.mok.gui.interfaces.GUI;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StressTest implements Executable {

    public static int[] askUserParams() {
        final JTextField comp = new JTextField("10", 5);
        final JTextField mol = new JTextField("100", 5);
        final JLabel l1 = new JLabel("N. compartments");
        final JLabel l2 = new JLabel("N: mols in each");
        final Dimension d = new Dimension(120, 20);
        l1.setPreferredSize(d);
        l2.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(comp);
        final JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.add(l2);
        secondRow.add(mol);
        final JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(firstRow);
        p.add(secondRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Insert number", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        try {
            if (result == JOptionPane.OK_OPTION
                    && Integer.parseInt(comp.getText()) > 0
                    && Integer.parseInt(mol.getText()) > 0) {
                return new int[] { Integer.parseInt(comp.getText()),
                        Integer.parseInt(mol.getText()) };
            }
        } catch (final Exception e) {
        }
        return null;
    }

    private GUI gui;
    private Thread[] linksThreads;
    private int N_COMPARTMENTS;

    private int N_MOLECULES;

    @Override
    public void run() {
        final Random random = new Random();

        for (int i = 0; i < this.N_COMPARTMENTS; i++) {
            this.gui.addNode(new MoKCompartment("comp" + i, "127.0.0.1",
                    8027 + i));

            // Maglia completa
            for (int k = i - 1; k >= 0; k--) {
                this.gui.addLink("comp" + i, "comp" + k);
            }

            for (int m = 0; m < this.N_MOLECULES; m++) {
                this.gui.addMolecule(new MoKMolecule(i * this.N_MOLECULES + m
                        + "", random.nextFloat() * 100 + 1), "comp" + i);
            }
        }

        final int linksNumber = this.N_COMPARTMENTS * (this.N_COMPARTMENTS - 1)
                / 2;
        this.linksThreads = new Thread[linksNumber];

        int z = 0;
        for (int i = 0; i < this.N_COMPARTMENTS; i++) {
            for (int k = i + 1; k < this.N_COMPARTMENTS; k++) {
                final LinkRunnable l = new LinkRunnable(i * this.N_MOLECULES,
                        "comp" + i, "comp" + k, this.gui);
                this.linksThreads[z] = new Thread(l);
                this.linksThreads[z].start();
                z++;
            }
        }

    }

    @Override
    public void setGUI(final GUI gui) {
        this.gui = gui;
    }

    @Override
    public boolean setup() {
        final int[] data = StressTest.askUserParams();
        if (data == null) {
            return false;
        } else {
            this.N_COMPARTMENTS = data[0];
            this.N_MOLECULES = data[1];
            return true;
        }
    }

    @Override
    public void stop() {
        for (final Thread linksThread : this.linksThreads) {
            if (linksThread != null) { // Check needed because execution could
                                       // be stopped before threads generation
                linksThread.interrupt();
            }
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

	@Override
	public void setFilter(String filter) {
		// TODO nothing here		
	}

}
