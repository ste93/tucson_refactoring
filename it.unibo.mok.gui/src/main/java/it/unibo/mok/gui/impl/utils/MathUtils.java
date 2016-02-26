package it.unibo.mok.gui.impl.utils;

public class MathUtils {

    public static Pair move(final Pair point, final Pair offset) {
        return new Pair(point.getFirst() + offset.getFirst(), point.getSecond()
                + offset.getSecond());
    }

    public static Pair originalPointFromZoomed(final Pair zoomedPoint,
            final Pair AXIS_ORIGIN, final float zoomValue) {
        return new Pair(
                (int) ((zoomedPoint.getFirst() - AXIS_ORIGIN.getFirst())
                        / zoomValue + AXIS_ORIGIN.getFirst()),
                (int) ((zoomedPoint.getSecond() - AXIS_ORIGIN.getSecond())
                        / zoomValue + AXIS_ORIGIN.getSecond()));
    }

    public static Pair zoomedPointFromOriginal(final Pair originalPoint,
            final Pair AXIS_ORIGIN, final float zoomValue) {
        return new Pair(
                (int) ((originalPoint.getFirst() - AXIS_ORIGIN.getFirst())
                        * zoomValue + AXIS_ORIGIN.getFirst()),
                (int) ((originalPoint.getSecond() - AXIS_ORIGIN.getSecond())
                        * zoomValue + AXIS_ORIGIN.getSecond()));
    }

    public static float zoomValue(final int zoomValue,
            final int zoomTicksToDoubleSize) {
        float zoomPercentage = zoomValue / (float) zoomTicksToDoubleSize;
        if (zoomPercentage == 0) {
            zoomPercentage = 0.1f;
        }
        return zoomPercentage;
    }

}
