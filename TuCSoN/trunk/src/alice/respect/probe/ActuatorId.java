package alice.respect.probe;


/**
 * Actuator's identifier class
 * 
 * @author Steven Maraldi
 * 
 */
public class ActuatorId extends ProbeId{

	private static final long serialVersionUID = 4412594143980641593L;

	public ActuatorId( String id ){
		super( id );
	}
	
	@Override
	public boolean isSensor() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActuator() {
		// TODO Auto-generated method stub
		return true;
	}

}
