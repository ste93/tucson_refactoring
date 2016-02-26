package it.unibo.mok.gui.impl.model.mok;

import it.unibo.mok.gui.impl.model.Molecule2D;
import java.awt.Graphics2D;

public class MoKMolecule extends Molecule2D {

    public MoKMolecule(final String id, final float concentration) {
        super(id, concentration);
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.getConcentration() > 0) {
            super.draw(g);
        }
    }

}
