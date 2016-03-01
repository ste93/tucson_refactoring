package it.unibo.mok.gui.impl;

import it.unibo.mok.gui.impl.model.mok.MoKCompartment;
import it.unibo.mok.gui.impl.model.mok.MoKMolecule;
import it.unibo.mok.gui.interfaces.GUI;
import it.unibo.mok.gui.interfaces.Simulator;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MoKSimulator implements Simulator {

    private int compartmentsCounter;
    private GUI gui;
    private final Random random;

    public MoKSimulator() {
        this.random = new Random();
    }

    @Override
    public void addLink() {
        final String[] data = this.askUserLinkData();
        if (data != null) {
            final boolean result = this.gui.addLink(data[0], data[1]);
            if (!result) {
                JOptionPane
                        .showMessageDialog(null,
                                "One or more compartment does not exists or link already exists");
            }
        }
    }

    @Override
    public void addMolecule() {
        final String[] data = this.askUserMoleculeData();
        if (data != null) {
            final boolean result = this.gui.addMolecule(new MoKMolecule(
                    data[0], Float.parseFloat(data[1])), data[2]);
            if (!result) {
                JOptionPane.showMessageDialog(null, "Node does not exists");
            }
        }
    }

    @Override
    public void addNode() {
        final String[] data = this.askUserCompartmentData();
        if (data != null) {
            final boolean result = this.gui.addNode(new MoKCompartment(data[0],
                    data[1], Integer.parseInt(data[2])));
            if (result) {
                this.compartmentsCounter++;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Compartment already exists");
            }
        }
    }

    @Override
    public void clear() {
        this.gui.clear();
        this.compartmentsCounter = 0;
    }

    @Override
    public void moveMolecule() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final String[] data = MoKSimulator.this
                        .askUserMoleculeTransfer();
                if (data != null) {
                    final boolean result = MoKSimulator.this.gui
                            .transferMolecule(data[0], data[1], data[2],
                                    Integer.parseInt(data[3]), true);
                    if (!result) {
                        JOptionPane.showMessageDialog(null, "Transfer failed");
                    }
                }
            }
        }).start();
    }

    @Override
    public void removeLink() {
        final String[] data = this.askUserLinkData();
        if (data != null) {
            final boolean result = this.gui.removeLink(data[0], data[1]);
            if (!result) {
                JOptionPane.showMessageDialog(null, "Link does not exists");
            }
        }
    }

    @Override
    public void removeMolecule() {
        final String[] data = this.askUserMoleculeAndCompartment();
        if (data != null) {
            final boolean result = this.gui.removeMolecule(data[0], data[1]);
            if (!result) {
                JOptionPane.showMessageDialog(null, "Molecule " + data[0]
                        + " does not exists in node " + data[1]);
            }
        }
    }

    @Override
    public void removeNode() {
        final String compName = this.askUserCompartmentName();
        if (compName != null) {
            final boolean result = this.gui.removeNode(compName);
            if (!result) {
                JOptionPane.showMessageDialog(null,
                        "Compartment does not exists");
            }
        }
    }

    @Override
    public void setGUI(final GUI gui) {
        this.gui = gui;
    }

    @Override
    public void setMoleculeConc() {
        final String[] data = this.askUserMoleculeData();
        if (data != null) {
            final boolean result = this.gui.setMoleculeConcentration(data[0],
                    data[2], Float.parseFloat(data[1]));
            if (!result) {
                JOptionPane.showMessageDialog(null,
                        "Change concentration failed");
            }
        }
    }

    private String[] askUserCompartmentData() {
        final JTextField name = new JTextField("comp"
                + this.compartmentsCounter, 20);
        final JTextField address = new JTextField("127.0.0.1", 20);
        final JTextField port = new JTextField(this.random.nextInt(10000) + "",
                5);
        final JLabel l1 = new JLabel("Name:");
        final JLabel l2 = new JLabel("Address:");
        final JLabel l3 = new JLabel("Port:");
        final Dimension d = new Dimension(60, 20);
        l1.setPreferredSize(d);
        l2.setPreferredSize(d);
        l3.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(name);
        final JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.add(l2);
        secondRow.add(address);
        final JPanel thirdRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thirdRow.add(l3);
        thirdRow.add(port);
        final JPanel p = new JPanel(new GridLayout(3, 1));
        p.add(firstRow);
        p.add(secondRow);
        p.add(thirdRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Create new compartment", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        try {
            if (result == JOptionPane.OK_OPTION
                    && Integer.parseInt(port.getText()) > 0) {
                return new String[] { name.getText(), address.getText(),
                        port.getText() };
            }
        } catch (final Exception e) {
        }
        return null;
    }

    private String askUserCompartmentName() {
        final JTextField name = new JTextField("comp", 20);
        final JLabel l1 = new JLabel("Name:");
        final Dimension d = new Dimension(60, 20);
        l1.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(name);
        final JPanel p = new JPanel(new GridLayout(1, 1));
        p.add(firstRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Remove compartment", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        try {
            if (result == JOptionPane.OK_OPTION) {
                return name.getText();
            }
        } catch (final Exception e) {
        }
        return null;
    }

    private String[] askUserLinkData() {
        final JTextField first = new JTextField("comp", 20);
        final JTextField second = new JTextField("comp", 20);
        final JLabel l1 = new JLabel("First compartment:");
        final JLabel l2 = new JLabel("Second compartment:");
        final Dimension d = new Dimension(140, 20);
        l1.setPreferredSize(d);
        l2.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(first);
        final JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.add(l2);
        secondRow.add(second);
        final JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(firstRow);
        p.add(secondRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Create new link", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        try {
            if (result == JOptionPane.OK_OPTION) {
                return new String[] { first.getText(), second.getText() };
            }
        } catch (final Exception e) {
        }
        return null;
    }

    private String[] askUserMoleculeAndCompartment() {
        final JTextField name = new JTextField("", 20);
        final JTextField node = new JTextField("comp", 20);
        final JLabel l1 = new JLabel("Name:");
        final JLabel l2 = new JLabel("Node container:");
        final Dimension d = new Dimension(100, 20);
        l1.setPreferredSize(d);
        l2.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(name);
        final JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.add(l2);
        secondRow.add(node);
        final JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(firstRow);
        p.add(secondRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Remove molecule", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        try {
            if (result == JOptionPane.OK_OPTION) {
                return new String[] { name.getText(), node.getText() };
            }
        } catch (final Exception e) {
        }
        return null;
    }

    private String[] askUserMoleculeData() {
        final JTextField name = new JTextField("", 20);
        final JTextField conc = new JTextField(this.random.nextInt(20) + "", 5);
        final JTextField node = new JTextField("comp"
                + (this.compartmentsCounter - 1), 20);
        final JLabel l1 = new JLabel("Name:");
        final JLabel l2 = new JLabel("Concentration:");
        final JLabel l3 = new JLabel("Node container:");
        final Dimension d = new Dimension(100, 20);
        l1.setPreferredSize(d);
        l2.setPreferredSize(d);
        l3.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(name);
        final JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.add(l2);
        secondRow.add(conc);
        final JPanel thirdRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thirdRow.add(l3);
        thirdRow.add(node);
        final JPanel p = new JPanel(new GridLayout(3, 1));
        p.add(firstRow);
        p.add(secondRow);
        p.add(thirdRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Molecule data", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        try {
            if (result == JOptionPane.OK_OPTION
                    && Float.parseFloat(conc.getText()) > 0) {
                return new String[] { name.getText(), conc.getText(),
                        node.getText() };
            }
        } catch (final Exception e) {
        }
        return null;
    }

    private String[] askUserMoleculeTransfer() {
        final JTextField name = new JTextField("", 20);
        final JTextField source = new JTextField("comp", 20);
        final JTextField dest = new JTextField("comp", 20);
        final JTextField conc = new JTextField("1", 5);
        final JLabel l1 = new JLabel("Name:");
        final JLabel l2 = new JLabel("Source node:");
        final JLabel l3 = new JLabel("Destination node:");
        final JLabel l4 = new JLabel("Concentration to move:");
        final Dimension d = new Dimension(120, 20);
        l1.setPreferredSize(d);
        l2.setPreferredSize(d);
        l3.setPreferredSize(d);
        l4.setPreferredSize(d);
        final JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.add(l1);
        firstRow.add(name);
        final JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.add(l2);
        secondRow.add(source);
        final JPanel thirdRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thirdRow.add(l3);
        thirdRow.add(dest);
        final JPanel forthRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        forthRow.add(l4);
        forthRow.add(conc);
        final JPanel p = new JPanel(new GridLayout(4, 1));
        p.add(firstRow);
        p.add(secondRow);
        p.add(thirdRow);
        p.add(forthRow);
        final int result = JOptionPane.showConfirmDialog(null, p,
                "Transfer molecule", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        try {
            if (result == JOptionPane.OK_OPTION
                    && Integer.parseInt(conc.getText()) > 0) {
                return new String[] { name.getText(), source.getText(),
                        dest.getText(), conc.getText() };
            }
        } catch (final Exception e) {
        }
        return null;
    }

}
