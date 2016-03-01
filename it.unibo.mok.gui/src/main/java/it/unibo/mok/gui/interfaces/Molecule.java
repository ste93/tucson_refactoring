package it.unibo.mok.gui.interfaces;

import java.awt.Color;

import it.unibo.mok.gui.impl.utils.Pair;

public interface Molecule extends Drawable {

    void animate();

    float getConcentration();

    String getId();

    Molecule lazyCopy();

    void moveEnded();

    void setAxisCenter(Pair axisCenter);

    void setConcentration(float concentration);

    void setOriginalCenter(Pair center);

    void setVisible(boolean b);

    void setZoom(float realZoomValue);

    void simulateMove(Pair offset);

    void zoom(float zoomValue);

	Color getBackgroundColor();

}
