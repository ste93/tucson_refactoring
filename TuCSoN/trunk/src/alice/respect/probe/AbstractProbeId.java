package alice.respect.probe;

import alice.respect.api.EnvId;
import alice.tuprolog.Term;

/**
 * Probe identifier.
 * 
 * @author Steven Maraldi
 */
public abstract class AbstractProbeId extends EnvId implements java.io.Serializable {

    private static final long serialVersionUID = -7709792820397648780L;
    /**
     * 
     */
    protected Term id;

    /**
     * Constructs a probe identifier
     * 
     * @param i
     *            the resource's identifier
     */
    public AbstractProbeId(final String i) {
        super(i);
    }

    /**
     * Checks if the resource is an actuator.
     * 
     * @return true if the resource is an actuatore one.
     */
    public abstract boolean isActuator();

    /**
     * Checks if the resource is a sensor.
     * 
     * @return true if the resource is a sensor one.
     */
    public abstract boolean isSensor();
}
