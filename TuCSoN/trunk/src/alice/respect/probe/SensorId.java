package alice.respect.probe;


/**
 * Sensor's identifier class
 * 
 * @author Steven Maraldi
 *
 */
public class SensorId extends ProbeId{

	private static final long serialVersionUID = -2977090935649268889L;

	public SensorId( String id ){
		super( id );
	}
	
	@Override
	public boolean isSensor() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isActuator() {
		// TODO Auto-generated method stub
		return false;
	}

}
