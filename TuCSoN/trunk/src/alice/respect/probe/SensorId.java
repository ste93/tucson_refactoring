package alice.respect.probe;

/**
 * Sensor's identifier class
 * 
 * @author Steven Maraldi
 * 
 */
public class SensorId extends ProbeId {

    private static final long serialVersionUID = -2977090935649268889L;

    public SensorId(final String i) {
        super(i);
    }

    @Override
    public boolean isActuator() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSensor() {
        // TODO Auto-generated method stub
        return true;
    }

}
