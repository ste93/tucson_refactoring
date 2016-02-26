package it.unibo.mok.gui.interfaces;

import java.awt.Graphics2D;
import java.util.Properties;

public interface Drawable {

    void draw(Graphics2D g);

    void injectConfig(Properties config);

}
