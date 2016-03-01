package it.unibo.mok.gui.impl;

import it.unibo.mok.gui.impl.swing.CanvasContainer;
import it.unibo.mok.gui.impl.swing.ControlPanel;
import it.unibo.mok.gui.impl.swing.LogPanel;
import it.unibo.mok.gui.impl.swing.StatusBar;
import it.unibo.mok.gui.interfaces.Executable;
import it.unibo.mok.gui.interfaces.GUI;
import it.unibo.mok.gui.interfaces.Link;
import it.unibo.mok.gui.interfaces.Molecule;
import it.unibo.mok.gui.interfaces.Node;
import it.unibo.mok.gui.interfaces.Simulator;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class MoKGUI implements GUI {

    public static final String CONFIG_FPS = "FPS";
    public static final String CONFIG_FPS_DANCE_ANIMATION = "DANCE_ANIMATION_FPS";
    public static final String CONFIG_LINK_COLOR = "LINK_COLOR";
    public static final String CONFIG_LINK_THICKNESS = "LINK_THICKNESS";
    public static final String CONFIG_MOLECULE_BORDER_COLOR = "MOLECULE_BORDER_COLOR";
    public static final String CONFIG_MOLECULE_BORDER_THICKNESS = "MOLECULE_BORDER_THICKNESS";
    public static final String CONFIG_MOLECULE_RINGS_RADIUS = "MOLECULE_RINGS_RADIUS";
    public static final String CONFIG_MOLECULE_SHOW_ID_INSTEAD_OF_CONCENTRATION = "MOLECULE_SHOW_ID_INSTEAD_OF_CONCENTRATION";
    public static final String CONFIG_MOLECULE_SIZE_FOR_GENERATION = "MOLECULE_SIZE_FOR_GENERATION";
    public static final String CONFIG_NODE_BACKGROUND_COLOR = "NODE_BACKGROUND_COLOR";
    public static final String CONFIG_NODE_BORDER_COLOR = "NODE_BORDER_COLOR";
    public static final String CONFIG_NODE_BORDER_THICKNESS = "NODE_BORDER_THICKNESS";
    public static final String CONFIG_NODE_RINGS_RADIUS = "NODE_RINGS_RADIUS";
    public static final String CONFIG_NODE_SIZE_FOR_GENERATION = "NODE_SIZE_FOR_GENERATION";
    public static final String CONFIG_TRANSFER_POINTS_PER_SECOND = "TRANSFER_POINTS_PER_SECOND";
    public static final String CONFIG_ZOOM_MAX_SIZE = "ZOOM_MAX_SIZE";
    public static final String CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE = "ZOOM_TICKS_TO_DOUBLE_SIZE";
    public static final String CONFIG_NODE_PIE_CHART_ZOOM_THRESHOLD = "NODE_PIE_CHART_ZOOM_THRESHOLD";
    private static final String CONFIG_FILE = "gui.config";

    /*
     * **************************************************
     * Fields *************************************************
     */

    private final Canvas canvas;
    private final Properties config;
    private final ControlPanel controlPanel;
    private final Database database;
    private final Executor executor;
    private final JFrame frame;
    private final Semaphore lock;
    private final LogPanel logPanel;
    private final Semaphore panLock;

    /*
     * **************************************************
     * Constructors *************************************************
     */

    public MoKGUI(final Simulator simulator) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception e) {
            System.err.println("Cannot load SystemLookAndFeel");
        }
        this.config = new Properties();
        this.loadConfig();
        this.database = new Database();
        this.lock = new Semaphore(1);
        this.panLock = new Semaphore(1);
        this.frame = new JFrame("MoK GUI");
        this.logPanel = new LogPanel(this.config);
        this.canvas = new Canvas(this, this.database, this.lock, this.panLock,
                this.config, this.logPanel);
        this.executor = new Executor(this);
        this.controlPanel = new ControlPanel(simulator, this.executor);
        simulator.setGUI(this);
        this.configureGUI();
    }

    /*
     * **************************************************
     * Setup *************************************************
     */

    @Override
    public void addExecutable(final Executable executable) {
        this.controlPanel.addExecutable(executable);
        this.frame.repaint();
    }

    @Override
    public boolean addLink(final String firstNode, final String secondNode) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        final Link link = this.database.addLink(firstNode, secondNode);
        if (link != null) {
            link.injectConfig(this.config);
            this.logPanel.setLinksNumber(this.database.getLinks().size());
        }
        this.lock.release();
        this.panLock.release();
        return link != null;
    }

    @Override
    public boolean addMolecule(final Molecule molecule, final String node) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        boolean result = false;
        final Node container = this.database.getNode(node);
        if (container != null) {
            molecule.injectConfig(this.config);
            result = container.addMolecule(molecule);
        }
        this.lock.release();
        this.panLock.release();
        return result;
    }

    /*
     * **************************************************
     * Commands *************************************************
     */

    @Override
    public boolean addNode(final Node node) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        final boolean added = this.database.addNode(node);
        if (added) {
            node.injectConfig(this.config);
            this.canvas.addNode(node);
            this.logPanel.setNodesNumber(this.database.getNodes().size());
        }
        this.lock.release();
        this.panLock.release();
        return added;
    }

    @Override
    public void clear() {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        this.database.clear();
        this.logPanel.clear();
        this.canvas.clear();
        this.lock.release();
        this.panLock.release();
    }

    @Override
    public boolean removeLink(final String firstNode, final String secondNode) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        final boolean result = this.database.removeLink(firstNode, secondNode);
        if (result) {
            this.logPanel.setLinksNumber(this.database.getLinks().size());
        }
        this.lock.release();
        this.panLock.release();
        return result;
    }

    @Override
    public boolean removeMolecule(final String moleculeId, final String node) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        boolean result = false;
        final Node container = this.database.getNode(node);
        if (container != null) {
            result = container.removeMolecule(moleculeId);
        }
        this.lock.release();
        this.panLock.release();
        return result;
    }

    @Override
    public boolean removeNode(final String nodeIdentifier) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        final boolean removed = this.database.removeNode(nodeIdentifier);
        if (removed) {
            this.database.removeLinks(nodeIdentifier);
            this.logPanel.setNodesNumber(this.database.getNodes().size());
            this.logPanel.setLinksNumber(this.database.getLinks().size());
        }
        this.lock.release();
        this.panLock.release();
        return removed;
    }

    @Override
    public boolean setMoleculeConcentration(final String moleculeId,
            final String node, final float concentration) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        boolean result = false;
        final Node container = this.database.getNode(node);
        if (container != null) {
            final Molecule molecule = container.getMolecule(moleculeId);
            if (molecule != null) {
                molecule.setConcentration(concentration);
                result = true;
            }
        }
        this.lock.release();
        this.panLock.release();
        return result;
    }

    @Override
    public void show() {
        this.frame.setVisible(true);
    }

    @Override
    public void transferAnimationCompleted(final Molecule movingMolecule,
            final Node destination, final Link link, final boolean addToDest) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                MoKGUI.this.panLock.acquireUninterruptibly();
                MoKGUI.this.lock.acquireUninterruptibly();
                if (addToDest) {
                	destination.addMolecule(movingMolecule);
                }
                movingMolecule.setVisible(true);
                MoKGUI.this.lock.release();
                MoKGUI.this.panLock.release();
                /* Comunicate to the blocked call that transfer is completed */
                link.getTransferCompleted().release();
            }
        }).start();
    }

    @Override
    public boolean transferMolecule(final String moleculeId,
            final String sourceNode, final String destinationNode,
            final float concentrationToTransfer, final boolean addToDest) {
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        final Node source = this.database.getNode(sourceNode);
        final Node destination = this.database.getNode(destinationNode);
        if (source != null && destination != null) {
            final Link link = this.database
                    .getLink(sourceNode, destinationNode);
            if (link != null) {
                final Molecule molecule = source.getMolecule(moleculeId);
                if (molecule != null) {
                    return this.doTransfer(molecule, source, destination, link,
                            concentrationToTransfer, addToDest);
                }
            }
        }
        this.lock.release();
        this.panLock.release();
        return false;
    }

    private void configureGUI() {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        this.frame.getContentPane().setLayout(new BorderLayout());
        this.frame.getContentPane().add(this.controlPanel, BorderLayout.NORTH);
        this.frame.getContentPane().add(new CanvasContainer(this.canvas),
                BorderLayout.CENTER);
        this.frame.getContentPane().add(new StatusBar(), BorderLayout.SOUTH);
        this.frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                MoKGUI.this.executor.stopCurrentExecutable();
            }
        });
        this.canvas.setOpaque(false);
    }

    private boolean doTransfer(final Molecule molecule, final Node sourceNode,
            final Node destinationNode, final Link link,
            final float concentrationToTransfer, final boolean addToDest) {
        /* Release lock semaphores and wait for link to be usable */
        this.lock.release();
        this.panLock.release();
        link.getTransferLock().acquireUninterruptibly();
        /* Now the link is usable, acquire locks again and start the transfer */
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        final float concRemaining = molecule.getConcentration()
                - concentrationToTransfer;
        if (concRemaining < 0) {
            sourceNode.removeMolecule(molecule);
        } else {
            molecule.setConcentration(concRemaining);
        }
        final Molecule movingMolecule = molecule.lazyCopy();
        movingMolecule.setConcentration(concentrationToTransfer);
        this.canvas.transferMolecule(molecule, movingMolecule, sourceNode,
                destinationNode, link, addToDest);
        /* Transfer command has been sent. Wait for transfer completed lock */
        this.lock.release();
        this.panLock.release();
        link.getTransferCompleted().acquireUninterruptibly();
        /*
         * Now, transfer has been fully completed, so we can release the lock
         * again and return the original blocked call
         */
        link.getTransferLock().release();
        return true;
    }

    private void loadConfig() {
        try {
            final FileInputStream input = new FileInputStream(
                    MoKGUI.CONFIG_FILE);
            this.config.load(input);
        } catch (final IOException e) {
            System.err
                    .println("Could not load config properties file. Generating default one..");
            this.loadDefaultConfig();
        }
    }

    private void loadDefaultConfig() {
        this.config.put(MoKGUI.CONFIG_FPS, "60");
        this.config.put(MoKGUI.CONFIG_FPS_DANCE_ANIMATION, "3");
        this.config.put(MoKGUI.CONFIG_NODE_SIZE_FOR_GENERATION, "300");
        this.config.put(MoKGUI.CONFIG_NODE_RINGS_RADIUS, "300");
        this.config.put(MoKGUI.CONFIG_NODE_BACKGROUND_COLOR, "#E3F2FD");
        this.config.put(MoKGUI.CONFIG_NODE_BORDER_COLOR, "#0D47A1");
        this.config.put(MoKGUI.CONFIG_NODE_BORDER_THICKNESS, "2");
        this.config.put(MoKGUI.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE, "6");
        this.config.put(MoKGUI.CONFIG_ZOOM_MAX_SIZE, "3");
        this.config.put(MoKGUI.CONFIG_MOLECULE_RINGS_RADIUS, "25");
        this.config.put(MoKGUI.CONFIG_MOLECULE_SIZE_FOR_GENERATION, "20");
        this.config.put(MoKGUI.CONFIG_MOLECULE_BORDER_COLOR, "#0D47A1");
        this.config.put(MoKGUI.CONFIG_MOLECULE_BORDER_THICKNESS, "2");
        this.config.put(MoKGUI.CONFIG_LINK_COLOR, "#2196F3");
        this.config.put(MoKGUI.CONFIG_LINK_THICKNESS, "2");
        this.config.put(MoKGUI.CONFIG_TRANSFER_POINTS_PER_SECOND, "120");
        this.config
                .put(MoKGUI.CONFIG_MOLECULE_SHOW_ID_INSTEAD_OF_CONCENTRATION,
                        "true");
        this.config
        		.put(MoKGUI.CONFIG_NODE_PIE_CHART_ZOOM_THRESHOLD,
                "1.5");
        try {
            final FileOutputStream output = new FileOutputStream(
                    MoKGUI.CONFIG_FILE);
            this.config.store(output, null);
        } catch (final IOException e) {
        }
    }

}
