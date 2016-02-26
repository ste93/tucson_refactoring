package it.unibo.mok.gui.impl.model;

import it.unibo.mok.gui.impl.MoKGUI;
import it.unibo.mok.gui.impl.animators.DanceAnimator;
import it.unibo.mok.gui.impl.utils.MathUtils;
import it.unibo.mok.gui.impl.utils.Pair;
import it.unibo.mok.gui.interfaces.Molecule;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Locale;
import java.util.Properties;

public class Molecule2D implements Molecule {

    /*
     * **************************************************
     * Fields *************************************************
     */

    private static Pair calculateTopLeftPoint(final Pair center,
            final int diameter) {
        final int topLeftPointX = center.getFirst() - diameter / 2;
        final int topLeftPointY = center.getSecond() - diameter / 2;
        return new Pair(topLeftPointX, topLeftPointY);
    }

    private static int fontDimension(final double moleculeRadius) {
        return (int) (moleculeRadius * 1 / 2);
    }

    private static Color fromStringToColor(final String string) {
        final String hexColor = String.format("#%06X",
                0xFFFFFF & string.hashCode());
        return Color.decode(hexColor);
    }

    private static Ellipse2D generateCircle(final Pair center,
            final int diameter) {
        final Pair topLeft = Molecule2D.calculateTopLeftPoint(center, diameter);
        return new Ellipse2D.Float(topLeft.getFirst(), topLeft.getSecond(),
                diameter, diameter);
    }

    private final DanceAnimator animator;
    private Pair axisCenter;
    private Ellipse2D backgroundCircle;
    private final Color backgroundColor;
    private Ellipse2D borderCircle;
    private Color borderColor;
    private int borderThickness;
    private float concentration;
    private Pair currentCenter;
    private int diameter;
    private Pair originalCenter;
    private boolean showId;

    /*
     * **************************************************
     * Constructor *************************************************
     */

    private final Color textColor;

    /*
     * **************************************************
     * Constructor *************************************************
     */

    private boolean visible;

    private float zoomValue;

    protected final String id;

    public Molecule2D(final String id, final float concentration) {
        this.id = id;
        this.animator = new DanceAnimator(1);
        this.concentration = concentration;
        this.textColor = this.fromBackgroundColorToTextColor(Molecule2D
                .fromStringToColor(id));
        this.backgroundColor = Molecule2D.fromStringToColor(id);
        this.visible = true;
    }

    @Override
    public void animate() {
        this.currentCenter = this.animator.animate(this.currentCenter);
        this.regenerate();
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.visible) {
            g.setStroke(new BasicStroke(this.borderThickness));
            if (this.borderThickness > 0) {
                g.setPaint(this.borderColor);
                g.draw(this.borderCircle);
            }
            g.setPaint(this.backgroundColor);
            g.fill(this.backgroundCircle);
            if (this.backgroundCircle.getHeight() > 15) {
                g.setFont(new Font("Calibri", Font.BOLD, Molecule2D
                        .fontDimension(this.backgroundCircle.getHeight())));
                g.setColor(this.textColor);
                final FontMetrics fm = g.getFontMetrics();
                final String conc = this.getStringToShow();
                final int displayedNameWidth = fm.stringWidth(conc);
                final int displayedNameHeight = fm.getAscent();
                final int x = this.currentCenter.getFirst()
                        - displayedNameWidth / 2;
                final int y = this.currentCenter.getSecond()
                        + displayedNameHeight / 2;
                g.drawString(conc, x, y);
            }
        }
    }

    @Override
    public float getConcentration() {
        return this.concentration;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void injectConfig(final Properties config) {
        this.borderColor = Color.decode(config
                .getProperty(MoKGUI.CONFIG_MOLECULE_BORDER_COLOR));
        this.borderThickness = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_MOLECULE_BORDER_THICKNESS));
        this.diameter = Integer.parseInt(config
                .getProperty(MoKGUI.CONFIG_MOLECULE_SIZE_FOR_GENERATION));
        this.showId = Boolean
                .parseBoolean(config
                        .getProperty(MoKGUI.CONFIG_MOLECULE_SHOW_ID_INSTEAD_OF_CONCENTRATION));
    }

    @Override
    public Molecule lazyCopy() {
        return new Molecule2D(this.id, this.concentration);
    }

    @Override
    public void moveEnded() {
        this.originalCenter = MathUtils.originalPointFromZoomed(
                this.currentCenter, this.axisCenter, this.zoomValue);
        this.currentCenter = MathUtils.zoomedPointFromOriginal(
                this.originalCenter, this.axisCenter, this.zoomValue);
        this.regenerate();
    }

    @Override
    public void setAxisCenter(final Pair axisCenter) {
        this.axisCenter = axisCenter;
    }

    @Override
    public void setConcentration(final float concentration) {
        this.concentration = concentration;
    }

    @Override
    public void setOriginalCenter(final Pair center) {
        this.originalCenter = center;
        this.currentCenter = center;
        this.regenerate();
    }

    /*
     * **************************************************
     * Properties *************************************************
     */

    @Override
    public void setVisible(final boolean b) {
        this.visible = b;
    }

    @Override
    public void setZoom(final float realZoomValue) {
        this.zoomValue = realZoomValue; // TODO
    }

    @Override
    public void simulateMove(final Pair offset) {
        this.currentCenter = MathUtils.move(this.currentCenter, offset);
        this.regenerate();
    }

    /*
     * **************************************************
     * Utils *************************************************
     */

    @Override
    public String toString() {
        return "Molecule(id=" + this.id + ", concentration="
                + this.concentration + ")";
    }

    @Override
    public void zoom(final float zoomValue) {
        this.zoomValue = zoomValue;
        this.currentCenter = MathUtils.zoomedPointFromOriginal(
                this.originalCenter, this.axisCenter, zoomValue);
        this.regenerate();
    }

    private Color fromBackgroundColorToTextColor(final Color c) {
        final float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(),
                c.getBlue(), null);
        final float brightness = hsb[2];
        if (brightness > 0.5) {
            return Color.black;
        } else {
            return Color.white;
        }
    }

    private void regenerate() {
        this.backgroundCircle = Molecule2D.generateCircle(this.currentCenter,
                (int) (this.zoomValue * this.diameter) - this.borderThickness);
        if (this.borderThickness > 0) {
            this.borderCircle = Molecule2D.generateCircle(this.currentCenter,
                    (int) (this.zoomValue * this.diameter));
        }
    }

    protected String getStringToShow() {
        if (this.showId) {
            return this.id;
        } else {
            return String.format(Locale.US, "%.1f", this.concentration);
        }
    }

}
