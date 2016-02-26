package it.unibo.mok.gui.interfaces;

import it.unibo.mok.gui.impl.utils.Pair;

public interface Node extends Drawable {

    boolean addMolecule(Molecule molecule);

    void animate();

    boolean contains(Pair point);

    Pair getCenter();

    String getId();

    Molecule getMolecule(String moleculeId);

    void moveEnded();

    boolean removeMolecule(Molecule molecule);

    boolean removeMolecule(String moleculeId);

    void setAxisCenter(Pair axisCenter);

    void setOriginalCenter(Pair center);

    void simulateMove(Pair offset);

    void zoom(float currentZoomRealValue);

}
