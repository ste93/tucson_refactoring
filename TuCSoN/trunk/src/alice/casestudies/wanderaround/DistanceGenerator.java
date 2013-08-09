package alice.casestudies.wanderaround;

import java.util.ArrayList;

public class DistanceGenerator extends Thread {

    private final NxtSimulatorGUI gui;
    private final boolean iteraction = true;
    private final ArrayList<ISensorEventListener> listeners;

    public DistanceGenerator() {
        this.listeners = new ArrayList<ISensorEventListener>();
        this.gui = NxtSimulatorGUI.getNxtSimulatorGUI();
        this.start();
    }

    public void addListener(final ISensorEventListener l) {
        if (!this.listeners.contains(l)) {
            this.listeners.add(l);
        }
    }

    public void removeListener(final ISensorEventListener l) {
        if (this.listeners.contains(l)) {
            this.listeners.remove(l);
        }
    }

    @Override
    public void run() {
        while (this.iteraction) {
            try {
                for (int i = 0; i < this.listeners.size(); i++) {
                    if (this.listeners.get(i).getListenerName()
                            .equals("ultrasonicSensorFront")) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("front"));
                    } else if (this.listeners.get(i).getListenerName()
                            .equals("ultrasonicSensorRight")) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("right"));
                    } else if (this.listeners.get(i).getListenerName()
                            .equals("ultrasonicSensorBack")) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("back"));
                    } else if (this.listeners.get(i).getListenerName()
                            .equals("ultrasonicSensorLeft")) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("left"));
                    }
                }

                Thread.sleep(1000);
            } catch (final Exception e) {
                // e.printStackTrace();
            }
        }
    }
}
