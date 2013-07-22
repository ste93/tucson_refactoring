package alice.casestudies.wanderaround;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class NXT_ServoMotorActuator implements ISimpleProbe{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	private NxtSimulatorGUI gui;
	
	private int angle = 0, power = 0;
	
	public NXT_ServoMotorActuator( ProbeId id ){
		this.id = id;
		this.gui = NxtSimulatorGUI.getNxtSimulatorGUI();
	}
	
	@Override
	public boolean writeValue(String key, int value) {
		System.err.println("WRITE REQUEST ( "+key+", "+value+" )");
		// TODO Auto-generated method stub
		if( key.equals("power") ){
			this.power = value;
			if( id.getLocalName().equals("servoMotorActuatorLeft") ){
				gui.setMotorParameters("left", "power", power);
			}else if( id.getLocalName().equals("servoMotorActuatorRight") ){
				gui.setMotorParameters("right", "power", power);
			}
			return true;	
		}else if( key.equals("angle") ){
			this.angle = value;
			if( id.getLocalName().equals("servoMotorActuatorLeft") ){
				gui.setMotorParameters("left", "angle", angle);
			}else if( id.getLocalName().equals("servoMotorActuatorRight") ){
				gui.setMotorParameters("right", "angle", angle);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		try {
			if( key.equals("power") ){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( key, power );
				return true;
			}else if( key.equals("angle") ){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( key, angle );
				return true;
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
}
