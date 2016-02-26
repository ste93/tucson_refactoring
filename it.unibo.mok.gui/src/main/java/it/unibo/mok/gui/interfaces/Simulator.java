package it.unibo.mok.gui.interfaces;

public interface Simulator {

    void addLink();

    void addMolecule();

    void addNode();

    void clear();

    void moveMolecule();

    void removeLink();

    void removeMolecule();

    void removeNode();

    void setGUI(GUI gui);

    void setMoleculeConc();

}
