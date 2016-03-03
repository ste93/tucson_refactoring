package it.unibo.mok.gui.interfaces;

public interface Executable extends Runnable {

    void setGUI(GUI gui);

    boolean setup();

    void stop();

	void setFilter(String filter);

}
