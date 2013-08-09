package alice.respect.probe;

/**
 * Actuator's identifier class
 * 
 * @author Steven Maraldi
 * 
 */
public class ActuatorId extends ProbeId {

    private static final long serialVersionUID = 4412594143980641593L;

    public ActuatorId(final String i) {
        super(i);
    }

    @Override
    public boolean isActuator() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isSensor() {
        // TODO Auto-generated method stub
        return false;
    }

}
