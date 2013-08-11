package alice.casestudies.wanderaround;

import java.util.ArrayList;
import java.util.List;

public class DistanceGenerator extends Thread {

    private static final boolean ITERATE = true;
    private final NxtSimulatorGUI gui;
    private final List<ISensorEventListener> listeners;

    public DistanceGenerator() {
        super();
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
        while (DistanceGenerator.ITERATE) {
            try {
                for (int i = 0; i < this.listeners.size(); i++) {
                    if ("ultrasonicSensorFront".equals(this.listeners.get(i)
                            .getListenerName())) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("front"));
                    } else if ("ultrasonicSensorRight".equals(this.listeners
                            .get(i).getListenerName())) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("right"));
                    } else if ("ultrasonicSensorBack".equals(this.listeners
                            .get(i).getListenerName())) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("back"));
                    } else if ("ultrasonicSensorLeft".equals(this.listeners
                            .get(i).getListenerName())) {
                        this.listeners.get(i).notifyEvent("distance",
                                this.gui.getDistance("left"));
                    }
                }

                Thread.sleep(1000);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
}
