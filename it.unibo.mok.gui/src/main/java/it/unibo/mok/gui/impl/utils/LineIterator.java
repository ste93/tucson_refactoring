package it.unibo.mok.gui.impl.utils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;

public class LineIterator implements Iterator<Point2D> {

    private final static double DEFAULT_PRECISION = 1.0;
    private final double dx, dy;
    private final Line2D line;
    private final double sx, sy;
    private double x, y, error;

    public LineIterator(final Line2D line) {
        this(line, LineIterator.DEFAULT_PRECISION);
    }

    public LineIterator(final Line2D line, final double precision) {
        this.line = line;
        this.sx = line.getX1() < line.getX2() ? precision : -1 * precision;
        this.sy = line.getY1() < line.getY2() ? precision : -1 * precision;
        this.dx = Math.abs(line.getX2() - line.getX1());
        this.dy = Math.abs(line.getY2() - line.getY1());
        this.error = this.dx - this.dy;
        this.y = line.getY1();
        this.x = line.getX1();
    }

    @Override
    public boolean hasNext() {
        return Math.abs(this.x - this.line.getX2()) > 0.9
                || Math.abs(this.y - this.line.getY2()) > 0.9;
    }

    @Override
    public Point2D next() {
        final Point2D ret = new Point2D.Double(this.x, this.y);
        final double e2 = 2 * this.error;
        if (e2 > -this.dy) {
            this.error -= this.dy;
            this.x += this.sx;
        }
        if (e2 < this.dx) {
            this.error += this.dx;
            this.y += this.sy;
        }
        return ret;
    }

    @Override
    public void remove() {
        throw new AssertionError();
    }

}
