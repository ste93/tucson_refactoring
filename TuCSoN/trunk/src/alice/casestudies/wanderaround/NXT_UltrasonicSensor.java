package alice.casestudies.wanderaround;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class NXT_UltrasonicSensor implements ISimpleProbe, ISensorEventListener{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	
	private int distance = 0;
	
	public NXT_UltrasonicSensor( ProbeId id ){
		this.id = id;
		DistanceGenerator resource = new DistanceGenerator();
		resource.addListener(this);
	}
	
	@Override
	public boolean writeValue(String key, int value) {
		// TODO Auto-generated method stub
		speakErr("I'm a sensor, I can't set values. Try asking to an actuator next time!!");
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		try {
			if( key.equals("distance") ){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( key, distance );
			}
			return true;
		} catch (TucsonOperationNotPossibleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ProbeId getIdentifier() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setTransducer(TransducerId tId) {
		// TODO Auto-generated method stub
		this.tId = tId;
	}

	@Override
	public TransducerId getTransducer() {
		// TODO Auto-generated method stub
		return tId;
	}
	
	private void speakErr( String msg ){
		System.err.println("[**ENVIRONMENT**][RESOURCE "+id.getLocalName()+"] "+msg);
	}

	@Override
	public void notifyEvent(String key, int value) {
		// TODO Auto-generated method stub
		try {
			if( key.equals("distance") ){
				this.distance = value;
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( key, distance );
			}
		} catch (TucsonOperationNotPossibleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getListenerName() {
		// TODO Auto-generated method stub
		return id.getLocalName();
	}
}
