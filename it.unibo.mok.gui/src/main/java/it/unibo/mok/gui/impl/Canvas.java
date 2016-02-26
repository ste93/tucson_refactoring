package it.unibo.mok.gui.impl;

import it.unibo.mok.gui.impl.animators.TransferAnimator;
import it.unibo.mok.gui.impl.swing.LogPanel;
import it.unibo.mok.gui.impl.utils.CircularPointGenerator;
import it.unibo.mok.gui.impl.utils.MathUtils;
import it.unibo.mok.gui.impl.utils.Pair;
import it.unibo.mok.gui.interfaces.GUI;
import it.unibo.mok.gui.interfaces.Link;
import it.unibo.mok.gui.interfaces.Molecule;
import it.unibo.mok.gui.interfaces.Node;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Canvas extends JPanel implements MouseListener,
        MouseMotionListener, MouseWheelListener, ChangeListener,
        ComponentListener {

    private static final long serialVersionUID = 2041001903442144220L;

    /*
     * **************************************************
     * Fields *************************************************
     */

    private Pair axisOrigin;
    private final CircularPointGenerator circularPointGenerator;
    private final Properties config;
    private final int CONFIG_MAX_ZOOM_SIZE;
    private final int CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE;
    private Pair currentGenCenterPoint;
    private float currentZoomRealValue;
    private int currentZoomValue;
    private final Database database;
    private Node draggedNode;
    private final Semaphore drawCompletedLock;
    private int fullscreenCounter;
    private Pair genCenterPoint;
    private final GUI gui;
    private int lastMouseX;
    private int lastMouseY;
    private final Semaphore lock;
    private final LogPanel logPanel;
    private final Semaphore panLock;
    private JSlider slider;

    private final List<TransferAnimator> transfers;

    /*
     * **************************************************
     * Constructors *************************************************
     */

    public Canvas(final GUI gui, final Database database, final Semaphore lock,
            final Semaphore panLock, final Properties config,
            final LogPanel logPanel) {
        this.gui = gui;
        this.database = database;
        this.lock = lock;
        this.panLock = panLock;
        this.transfers = new ArrayList<>();
        this.config = config;
        this.logPanel = logPanel;
        this.drawCompletedLock = new Semaphore(0);
        this.CONFIG_MAX_ZOOM_SIZE = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_ZOOM_MAX_SIZE));
        this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE));
        this.currentZoomValue = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE));
        this.currentZoomRealValue = MathUtils.zoomValue(this.currentZoomValue,
                this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE);
        final int compSize = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_NODE_SIZE_FOR_GENERATION));
        final int ringRadius = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_NODE_RINGS_RADIUS));
        this.circularPointGenerator = new CircularPointGenerator(ringRadius,
                compSize);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.setLayout(new BorderLayout());
        this.addComponentListener(this);
        this.initSlider();
        this.initLog();
        this.startCycle();
    }

    /*
     * **************************************************
     * Setup *************************************************
     */

    public void addNode(final Node node) {
        node.setOriginalCenter(this.circularPointGenerator
                .generateNext(this.genCenterPoint));
        node.setAxisCenter(this.axisOrigin);
        node.zoom(this.currentZoomRealValue);
    }

    public void clear() {
        this.circularPointGenerator.clear();
        this.transfers.clear();
    }

    /*
     * **************************************************
     * Drawing thread cycle *************************************************
     */

    @Override
    public void componentHidden(final ComponentEvent e) {
    }

    /*
     * **************************************************
     * Commands *************************************************
     */

    @Override
    public void componentMoved(final ComponentEvent e) {
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        this.fullscreenCounter++;
        // TODO Needs to be checked: different behavior on Win - Mac OS
        if (this.fullscreenCounter <= 2
                && (this.axisOrigin == null || this.axisOrigin.getSecond() < 0)) {
            this.axisOrigin = new Pair(this.getWidth() / 2,
                    this.getHeight() / 2);
            this.genCenterPoint = new Pair(this.axisOrigin.getFirst(),
                    this.axisOrigin.getSecond());
            this.currentGenCenterPoint = this.genCenterPoint;
        }
    }

    @Override
    public void componentShown(final ComponentEvent e) {
    }

    public float getZoomRealValue() {
        return this.currentZoomRealValue;
    }

    /*
     * **************************************************
     * Drawing *************************************************
     */

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        final Pair offset = new Pair(e.getX() - this.lastMouseX, e.getY()
                - this.lastMouseY);
        this.lock.acquireUninterruptibly();
        if (this.draggedNode != null) {
            this.draggedNode.simulateMove(offset);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            this.currentGenCenterPoint = MathUtils.move(
                    this.currentGenCenterPoint, offset);
            for (final Node node : this.database.getNodes()) {
                node.simulateMove(offset);
            }
        }
        this.lock.release();
        this.lastMouseX = e.getX();
        this.lastMouseY = e.getY();
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    /*
     * **************************************************
     * Mouse events callbacks *************************************************
     */

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        if (this.draggedNode != null || this.lastMouseX != 0) {
            return; // To prevent deadlock when one is dragging
        }
        this.panLock.acquireUninterruptibly();
        this.lock.acquireUninterruptibly();
        final Pair clickedPoint = new Pair(e.getX(), e.getY());
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (final Node node : this.database.getNodes()) {
                if (node.contains(clickedPoint)) {
                    this.draggedNode = node;
                    this.lastMouseX = e.getX();
                    this.lastMouseY = e.getY();
                    break;
                }
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            this.lastMouseX = e.getX();
            this.lastMouseY = e.getY();
        }
        this.lock.release();
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        this.lock.acquireUninterruptibly();
        if (this.draggedNode != null) {
            this.draggedNode.moveEnded();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            for (final Node node : this.database.getNodes()) {
                node.moveEnded();
            }
            this.genCenterPoint = MathUtils.originalPointFromZoomed(
                    this.currentGenCenterPoint, this.axisOrigin,
                    this.currentZoomRealValue);
            this.currentGenCenterPoint = MathUtils.zoomedPointFromOriginal(
                    this.genCenterPoint, this.axisOrigin,
                    this.currentZoomRealValue);
        }
        this.lock.release();
        this.panLock.release();
        this.draggedNode = null;
        this.lastMouseX = 0;
        this.lastMouseY = 0;
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        if (this.lastMouseX == 0) {
            this.currentZoomValue -= e.getWheelRotation();
            if (this.currentZoomValue > this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE
                    * this.CONFIG_MAX_ZOOM_SIZE) {
                this.currentZoomValue = this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE
                        * this.CONFIG_MAX_ZOOM_SIZE;
            } else if (this.currentZoomValue < 0) {
                this.currentZoomValue = 0;
            }
            this.slider.setValue(this.currentZoomValue);
            this.zoom();
        }
    }

    /*
     * **************************************************
     * Slider callback *************************************************
     */

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        this.lock.acquireUninterruptibly();
        this.drawLinks(g2);
        this.drawNodes(g2);
        this.drawMovingMolecules(g2);
        this.lock.release();
        this.drawCompletedLock.release();
    }

    /*
     * **************************************************
     * Canvas resize callback *************************************************
     */

    @Override
    public void stateChanged(final ChangeEvent e) {
        final JSlider source = (JSlider) e.getSource();
        this.currentZoomValue = source.getValue();
        this.currentZoomRealValue = MathUtils.zoomValue(this.currentZoomValue,
                this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE);
        this.zoom();
    }

    /*
     * **************************************************
     * Properties *************************************************
     */

    public void transferMolecule(final Molecule molecule,
            final Molecule movingMolecule, final Node source,
            final Node destination, final Link link) {
        movingMolecule.injectConfig(this.config);
        movingMolecule.setOriginalCenter(source.getCenter());
        movingMolecule.setAxisCenter(this.axisOrigin);
        movingMolecule.zoom(this.currentZoomRealValue);
        this.transfers.add(new TransferAnimator(movingMolecule, source,
                destination, this.config, link, this));
    }

    /*
     * **************************************************
     * Unused *************************************************
     */

    private void drawLinks(final Graphics2D g2) {
        for (final Link link : this.database.getLinks()) {
            link.draw(g2);
        }
    }

    private void drawMovingMolecules(final Graphics2D g2) {
        final Iterator<TransferAnimator> iter = this.transfers.iterator();
        while (iter.hasNext()) {
            iter.next().getMovingMolecule().draw(g2);
        }
    }

    private void drawNodes(final Graphics2D g2) {
        final Iterator<Node> iter = this.database.getNodes().iterator();
        while (iter.hasNext()) {
            iter.next().draw(g2);
        }
    }

    private void initLog() {
        final JPanel dummy = new JPanel();
        dummy.setOpaque(false);
        dummy.setLayout(new FlowLayout(FlowLayout.LEFT));
        dummy.setBorder(new EmptyBorder(15, 0, 0, 40));
        dummy.add(this.logPanel);
        this.add(dummy, BorderLayout.EAST);
    }

    private void initSlider() {
        this.slider = new JSlider(SwingConstants.VERTICAL, 0,
                this.CONFIG_MAX_ZOOM_SIZE
                        * this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE,
                this.currentZoomValue);
        this.slider.setFocusable(false);
        this.slider.setOpaque(false);
        this.slider.setMajorTickSpacing(this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE);
        this.slider.setMinorTickSpacing(1);
        this.slider.setPaintTicks(true);
        this.slider.setPaintLabels(true);
        this.slider.addChangeListener(this);
        final Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
        labels.put(0, new JLabel("0.1x"));
        for (int i = 1; i <= this.CONFIG_MAX_ZOOM_SIZE; i++) {
            labels.put(i * this.CONFIG_ZOOM_TICKS_TO_DOUBLE_SIZE, new JLabel(i
                    + "x"));
        }
        this.slider.setLabelTable(labels);
        final JPanel dummy = new JPanel();
        dummy.setOpaque(false);
        dummy.setLayout(new FlowLayout(FlowLayout.LEFT));
        dummy.add(this.slider);
        this.add(dummy, BorderLayout.SOUTH);
    }

    private void startCycle() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final int FPS = Integer.parseInt(Canvas.this.config
                        .getProperty(MoKGUI.CONFIG_FPS));
                final int DANCE_ANIMATION_FPS = Integer.parseInt(Canvas.this.config
                        .getProperty(MoKGUI.CONFIG_FPS_DANCE_ANIMATION));
                int framesAfterLastDanceAnimation = 0;
                final int delay = 1000 / FPS;
                final int danceSkip = DANCE_ANIMATION_FPS > 0 ? FPS
                        / DANCE_ANIMATION_FPS : 0;
                long thisSecondStartingTime = System.currentTimeMillis();
                int fpsThisSecond = 0;
                while (!Thread.interrupted()) {
                    final long start = System.currentTimeMillis();
                    Canvas.this.drawCompletedLock.acquireUninterruptibly();
                    Canvas.this.lock.acquireUninterruptibly();
                    fpsThisSecond++;
                    /* Dance animations */
                    if (DANCE_ANIMATION_FPS > 0) {
                        framesAfterLastDanceAnimation++;
                        if (framesAfterLastDanceAnimation > danceSkip) {
                            framesAfterLastDanceAnimation = 0;
                            for (final Node node : Canvas.this.database
                                    .getNodes()) {
                                node.animate();
                            }
                        }
                    }
                    /* Transfer animations */
                    for (int i = 0; i < Canvas.this.transfers.size(); i++) {
                        if (!Canvas.this.transfers.get(i).ended()) {
                            Canvas.this.transfers.get(i).animate();
                        } else {
                            Canvas.this.gui.transferAnimationCompleted(
                                    Canvas.this.transfers.get(i)
                                            .getMovingMolecule(),
                                    Canvas.this.transfers.get(i)
                                            .getDestination(),
                                    Canvas.this.transfers.get(i).getLink());
                            Canvas.this.transfers.remove(i);
                        }
                    }
                    Canvas.this.logPanel
                            .setMovingMoleculesNumber(Canvas.this.transfers
                                    .size());
                    Canvas.this.repaint();
                    Canvas.this.lock.release();
                    final long now = System.currentTimeMillis();
                    final long timeElapsed = now - start;
                    if (now - thisSecondStartingTime > 1000) {
                        Canvas.this.logPanel.setFps(fpsThisSecond);
                        thisSecondStartingTime = now;
                        fpsThisSecond = 0;
                    }
                    if (timeElapsed < delay) {
                        try {
                            Thread.sleep(delay - timeElapsed);
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();
    }

    private void zoom() {
        this.lock.acquireUninterruptibly();
        this.currentGenCenterPoint = MathUtils
                .zoomedPointFromOriginal(this.genCenterPoint, this.axisOrigin,
                        this.currentZoomRealValue);
        for (final Node node : this.database.getNodes()) {
            node.zoom(this.currentZoomRealValue);
        }
        this.lock.release();
    }

}
