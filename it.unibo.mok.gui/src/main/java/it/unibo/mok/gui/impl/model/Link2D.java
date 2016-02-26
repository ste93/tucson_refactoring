package it.unibo.mok.gui.impl.model;

import it.unibo.mok.gui.impl.MoKGUI;
import it.unibo.mok.gui.interfaces.Link;
import it.unibo.mok.gui.interfaces.Node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Properties;
import java.util.concurrent.Semaphore;

public class Link2D implements Link {

    /*
     * **************************************************
     * Fields *************************************************
     */

    private final Node first;
    private Color lineColor;
    private int lineThickness;
    private final Node second;
    private final Semaphore transferCompleted;
    private final Semaphore transferLock;

    /*
     * **************************************************
     * Constructor *************************************************
     */

    public Link2D(final Node first, final Node second) {
        this.first = first;
        this.second = second;
        this.transferLock = new Semaphore(1);
        this.transferCompleted = new Semaphore(0);
    }

    /*
     * **************************************************
     * Commands *************************************************
     */

    @Override
    public void draw(final Graphics2D g) {
        g.setStroke(new BasicStroke(this.lineThickness));
        g.setPaint(this.lineColor);
        g.drawLine(this.first.getCenter().getFirst(), this.first.getCenter()
                .getSecond(), this.second.getCenter().getFirst(), this.second
                .getCenter().getSecond());
    }

    @Override
    public Node getFirst() {
        return this.first;
    }

    /*
     * **************************************************
     * Properties *************************************************
     */

    @Override
    public Node getSecond() {
        return this.second;
    }

    @Override
    public Semaphore getTransferCompleted() {
        return this.transferCompleted;
    }

    @Override
    public Semaphore getTransferLock() {
        return this.transferLock;
    }

    @Override
    public void injectConfig(final Properties config) {
        this.lineColor = Color.decode(config
                .getProperty(MoKGUI.CONFIG_LINK_COLOR));
        this.lineThickness = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_LINK_THICKNESS));
    }

    @Override
    public String toString() {
        return "Link(first=" + this.first + ", second=" + this.second + ")";
    }

}
