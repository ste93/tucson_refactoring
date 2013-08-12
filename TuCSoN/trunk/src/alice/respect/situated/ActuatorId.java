package alice.respect.situated;

/**
 * Actuator's identifier class
 * 
 * @author Steven Maraldi
 * 
 */
public class ActuatorId extends AbstractProbeId {

    private static final long serialVersionUID = 4412594143980641593L;

    /**
     * 
     * @param i
     *            the String representation of the actuator identifier
     */
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
