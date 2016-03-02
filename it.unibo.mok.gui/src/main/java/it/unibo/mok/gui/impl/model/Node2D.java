package it.unibo.mok.gui.impl.model;

import it.unibo.mok.gui.impl.MoKGUI;
import it.unibo.mok.gui.impl.utils.CircularPointGenerator;
import it.unibo.mok.gui.impl.utils.MathUtils;
import it.unibo.mok.gui.impl.utils.Pair;
import it.unibo.mok.gui.interfaces.Molecule;
import it.unibo.mok.gui.interfaces.Node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Node2D implements Node {

    private static Pair calculateTopLeftPoint(final Pair center,
            final int diameter) {
        final int topLeftPointX = center.getFirst() - diameter / 2;
        final int topLeftPointY = center.getSecond() - diameter / 2;
        return new Pair(topLeftPointX, topLeftPointY);
    }

    private static Ellipse2D generateCircle(final Pair center,
            final int diameter) {
        final Pair topLeft = Node2D.calculateTopLeftPoint(center, diameter);
        return new Ellipse2D.Float(topLeft.getFirst(), topLeft.getSecond(),
                diameter, diameter);
    }

    private Pair axisCenter;
    private Ellipse2D backgroundCircle;
    private Color backgroundColor;
    private Ellipse2D borderCircle;
    private Color borderColor;
    private int borderThickness;
    private CircularPointGenerator circularPointGenerator;
    private Pair currentCenter;
    private int diameter;
    private final Font font;
    private int moleculeRingRadius;
    private int moleculeSize;
    private Pair originalCenter;
    private float zoomValue;
    protected final String id;
    protected final List<Molecule> molecules;
	private float pieChartZoomThreshold;

    public Node2D(final String id) {
        this.molecules = new ArrayList<>();
        this.id = id;
        this.font = new Font("Calibri", Font.BOLD, 15);
    }

    @Override
    public boolean addMolecule(final Molecule molecule) {
        this.molecules.add(molecule);
        molecule.setOriginalCenter(this.circularPointGenerator
                .generateNext(this.originalCenter));
        molecule.setAxisCenter(this.axisCenter);
        molecule.zoom(this.zoomValue);
        this.diameter = this.nodeDiameter();
        this.regenerate();
        return true;
    }

    @Override
    public void animate() {
        for (int i = 0; i < this.molecules.size(); i++) {
            if (this.molecules.get(i) != null) {
                this.molecules.get(i).animate();
            }
        }
        this.regenerate();
    }

    @Override
    public boolean contains(final Pair point) {
        return this.backgroundCircle.contains(point.getFirst(),
                point.getSecond());
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setStroke(new BasicStroke(this.borderThickness));
        g.setPaint(this.backgroundColor);
        g.fill(this.backgroundCircle);
        // Draw pie chart
        if (this.zoomValue < this.pieChartZoomThreshold) {
        	double curValue = 0.0D;
        	int startAngle = 0;
        	List<Molecule> pieSlices = new ArrayList<>();
        	int total = findPieSlices(pieSlices);
        	for (Molecule mol : pieSlices) {
        		startAngle = (int) (curValue * 360 / total);
        		int arcAngle = (int) (mol.getConcentration() * 360 / total);
        		g.setColor(mol.getBackgroundColor());
        		g.fillArc((int)backgroundCircle.getX(), (int)backgroundCircle.getY(), (int)backgroundCircle.getWidth(), (int)backgroundCircle.getHeight(), startAngle, arcAngle);
        		curValue += mol.getConcentration();
        	}
        } 
        // Draw molecules
        else {
            if (this.borderThickness > 0) {
                g.setPaint(this.borderColor);
                g.draw(this.borderCircle);
            }
	        for (int i = 0; i < this.molecules.size(); i++) {
	            if (this.molecules.get(i) != null) {
	                this.molecules.get(i).draw(g);
	            }
	        }
        }
        this.drawId(g);
    }

    private int findPieSlices(List<Molecule> list) {
    	if (this.molecules.size() == 0) return 0;
    	int total = 0;
    	int totalNodeConc = 0;
    	for (Molecule mol : this.molecules) {
    		totalNodeConc += mol.getConcentration();
    	}
    	int nodeMediumConc = totalNodeConc/this.molecules.size();
    	for (Molecule mol : this.molecules) {
    		if (mol.getConcentration() >= nodeMediumConc) {
    			list.add(mol);
    			total += mol.getConcentration();
    		}
    	}
    	return total;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Node
                && this.getId().equals(((Node) obj).getId());
    }

    @Override
    public Pair getCenter() {
        return this.currentCenter;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Molecule getMolecule(final String moleculeId) {
        for (final Molecule molecule : this.molecules) {
            if (molecule.getId().equals(moleculeId)) {
                return molecule;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public void injectConfig(final Properties config) {
        this.backgroundColor = Color.decode(config
                .getProperty(MoKGUI.CONFIG_NODE_BACKGROUND_COLOR));
        this.borderColor = Color.decode(config
                .getProperty(MoKGUI.CONFIG_NODE_BORDER_COLOR));
        this.borderThickness = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_NODE_BORDER_THICKNESS));
        this.moleculeRingRadius = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_MOLECULE_RINGS_RADIUS));
        this.moleculeSize = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_MOLECULE_SIZE_FOR_GENERATION));
        this.pieChartZoomThreshold = Float.parseFloat(config
                .getProperty(MoKGUI.CONFIG_NODE_PIE_CHART_ZOOM_THRESHOLD));
        this.circularPointGenerator = new CircularPointGenerator(
                this.moleculeRingRadius, this.moleculeSize);
        this.diameter = this.nodeDiameter();
    }

    @Override
    public void moveEnded() {
        this.originalCenter = MathUtils.originalPointFromZoomed(
                this.currentCenter, this.axisCenter, this.zoomValue);
        this.currentCenter = MathUtils.zoomedPointFromOriginal(
                this.originalCenter, this.axisCenter, this.zoomValue);
        for (int i = 0; i < this.molecules.size(); i++) {
            if (this.molecules.get(i) != null) {
                this.molecules.get(i).moveEnded();
            }
        }
        this.regenerate();
    }

    @Override
    public boolean removeMolecule(final Molecule molecule) {
        return this.molecules.remove(molecule);
    }

    /*
     * **************************************************
     * Properties *************************************************
     */

    @Override
    public boolean removeMolecule(final String moleculeId) {
        boolean deleted = false;
        for (int i = 0; i < this.molecules.size(); i++) {
            if (this.molecules.get(i).getId().equals(moleculeId)) {
                this.molecules.remove(this.molecules.get(i));
                deleted = true;
            }
        }
        return deleted;
    }

    @Override
    public void setAxisCenter(final Pair axisCenter) {
        this.axisCenter = axisCenter;
        for (int i = 0; i < this.molecules.size(); i++) {
            if (this.molecules.get(i) != null) {
                this.molecules.get(i).setAxisCenter(axisCenter);
            }
        }
    }

    @Override
    public void setOriginalCenter(final Pair center) {
        this.originalCenter = center;
        this.currentCenter = center;
        this.regenerate();
    }

    @Override
    public void simulateMove(final Pair offset) {
        this.currentCenter = MathUtils.move(this.currentCenter, offset);
        for (int i = 0; i < this.molecules.size(); i++) {
            if (this.molecules.get(i) != null) {
                this.molecules.get(i).simulateMove(offset);
            }
        }
        this.regenerate();
    }

    @Override
    public String toString() {
        return "Node(id=" + this.id + ")";
    }

    @Override
    public void zoom(final float zoomValue) {
        this.zoomValue = zoomValue;
        this.currentCenter = MathUtils.zoomedPointFromOriginal(
                this.originalCenter, this.axisCenter, zoomValue);
        for (int i = 0; i < this.molecules.size(); i++) {
            if (this.molecules.get(i) != null) {
                this.molecules.get(i).zoom(zoomValue);
            }
        }
        this.regenerate();
    }

    private void drawId(final Graphics2D g) {
        final int TEXT_GAP = 10;
        g.setStroke(new BasicStroke(1));
        g.setFont(this.font);
        final FontMetrics fm = g.getFontMetrics();
        final String displayedName = this.getDisplayedName();
        final int displayedNameWidth = fm.stringWidth(displayedName);
        final int displayedNameHeight = fm.getAscent();
        final int x = (int) this.backgroundCircle.getCenterX()
                - displayedNameWidth / 2;
        final int y = (int) this.backgroundCircle.getMaxY()
                + displayedNameHeight + this.borderThickness;
        final int rectWidth = displayedNameWidth + TEXT_GAP * 2;
        final int rectHeight = displayedNameHeight + TEXT_GAP;
        g.setColor(Color.white);
        g.fillRect(x - TEXT_GAP, (int) this.backgroundCircle.getMaxY()
                + this.borderThickness, rectWidth, rectHeight);
        g.setColor(Color.black);
        g.drawString(displayedName, x, y + TEXT_GAP / 3f);
        g.draw(new RoundRectangle2D.Double(x - TEXT_GAP,
                (int) this.backgroundCircle.getMaxY() + this.borderThickness,
                rectWidth, rectHeight, 10, 10));
    }

    /*
     * **************************************************
     * Utils *************************************************
     */

    private int nodeDiameter() {
        return this.moleculeRingRadius
                * this.circularPointGenerator.getCurrentRing() * 2
                + this.moleculeSize * 2;
    }

    protected String getDisplayedName() {
        return this.id;
    }

    protected void regenerate() {
        this.backgroundCircle = Node2D.generateCircle(this.currentCenter,
                (int) (this.zoomValue * this.diameter) - this.borderThickness);
        if (this.borderThickness > 0) {
            this.borderCircle = Node2D.generateCircle(this.currentCenter,
                    (int) (this.zoomValue * this.diameter));
        }
    }

}
