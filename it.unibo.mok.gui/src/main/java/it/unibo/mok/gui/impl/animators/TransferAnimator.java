package it.unibo.mok.gui.impl.animators;

import it.unibo.mok.gui.impl.Canvas;
import it.unibo.mok.gui.impl.MoKGUI;
import it.unibo.mok.gui.impl.utils.LineIterator;
import it.unibo.mok.gui.impl.utils.Pair;
import it.unibo.mok.gui.interfaces.Link;
import it.unibo.mok.gui.interfaces.Molecule;
import it.unibo.mok.gui.interfaces.Node;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.util.Properties;

public class TransferAnimator {

    private final Canvas canvas;
    private final Node destination;
    private boolean ended = false;
    private LineIterator it;
    private final Link link;
    private final Molecule movingMolecule;
    private final int pointsPerFrame;
    private final Node source;
    private int step;
	private boolean addToDest;

    public TransferAnimator(final Molecule movingMolecule, final Node source,
            final Node destination, final Properties config, final Link link,
            final Canvas canvas, final boolean addToDest) {
        this.source = source;
        this.addToDest = addToDest;
        this.destination = destination;
        this.canvas = canvas;
        this.movingMolecule = movingMolecule;
        this.link = link;
        this.pointsPerFrame = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_TRANSFER_POINTS_PER_SECOND))
                / Integer.parseInt(config.getProperty(MoKGUI.CONFIG_FPS));
        movingMolecule.setVisible(false);
    }

    public void animate() {
        final float zoomedPointsPerFrame = this.pointsPerFrame
                * this.canvas.getZoomRealValue();
        // if (zoomedPointsPerFrame < 1) zoomedPointsPerFrame = 1;

        final Double line = new Line2D.Double(this.source.getCenter()
                .getFirst(), this.source.getCenter().getSecond(),
                this.destination.getCenter().getFirst(), this.destination
                        .getCenter().getSecond());
        this.it = new LineIterator(line);
        Point2D p = null;
        this.step++;

        for (int i = 0; i < this.step * zoomedPointsPerFrame; i++) {
            if (this.it.hasNext()) {
                p = this.it.next();
            } else {
                this.movingMolecule.setVisible(false);
                return;
            }
        }

        if (p == null) {
            this.movingMolecule.setVisible(false);
            return;
        }
        final Pair point = new Pair((int) p.getX(), (int) p.getY());
        if (p == null || this.source.contains(point)) {
            return;
        }
        if (p != null && !this.destination.contains(point)) {
            this.movingMolecule.setVisible(true);
            this.movingMolecule.setOriginalCenter(point);
            this.movingMolecule.setZoom(this.canvas.getZoomRealValue());
        } else {
            this.ended = true;
            this.movingMolecule.setVisible(false);
        }
    }

    public boolean ended() {
        return this.ended || this.it != null && !this.it.hasNext();
    }

    public Node getDestination() {
        return this.destination;
    }

    public Link getLink() {
        return this.link;
    }

    public Molecule getMovingMolecule() {
        return this.movingMolecule;
    }

	public boolean getAddToDest() {
		return addToDest;
	}

}
