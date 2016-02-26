package it.unibo.mok.gui.impl.model.mok;

import it.unibo.mok.gui.impl.model.Node2D;
import it.unibo.mok.gui.interfaces.Molecule;

public class MoKCompartment extends Node2D {

    private final String address;
    private final int port;

    public MoKCompartment(final String name, final String address,
            final int port) {
        super(name);
        this.address = address;
        this.port = port;
    }

    @Override
    public boolean addMolecule(final Molecule moleculeToAdd) {
        final Molecule molecule = this.getMolecule(moleculeToAdd.getId());
        if (molecule != null) {
            molecule.setConcentration(moleculeToAdd.getConcentration()
                    + molecule.getConcentration());
        } else {
            super.addMolecule(moleculeToAdd);
        }
        return true;
    }

    @Override
    protected String getDisplayedName() {
        return new StringBuilder(this.id).append(" @ ").append(this.address)
                .append(" : ").append(this.port).append(" [")
                .append(this.molecules.size()).append("]").toString();
    }

}
