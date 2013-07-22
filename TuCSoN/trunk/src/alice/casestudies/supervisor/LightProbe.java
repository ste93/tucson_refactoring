package alice.casestudies.supervisor;

import alice.respect.core.ISerialEventListener;
import alice.respect.core.SerialComm;
import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * Probe used for communication from/to the ligh actuator hardware
 * 
 * @author Steven Maraldi
 *
 */
public class LightProbe extends Thread implements ISimpleProbe, ISerialEventListener{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	
	public LightProbe( ProbeId id ){
		this.id = id;
		SerialComm.getSerialComm().addListener(this);
	}
	
	@Override
	public boolean writeValue(String key, int value) {
		speak("WRITE REQUEST ( "+key+", "+value+" )");
		// TODO Auto-generated method stub
		if( key.equals("intensity") ){
			String msg = "LI"; // Light intensity
			try {
				SerialComm.getSerialComm().getOutputStream().write( msg.getBytes() );
				SerialComm.getSerialComm().getOutputStream().write( value );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SerialComm.getSerialComm().close();
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		if( key.equals("intensity") ){
			String msg = "RI"; // RP means Read Power
			try {
				SerialComm.getSerialComm().getOutputStream().write( msg.getBytes() );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SerialComm.getSerialComm().close();
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public ProbeId getIdentifier() {
		// TODO Auto-generated method stub
		return id;
	}

	private void speak( String msg ){
		System.out.println("[**ENVIRONMENT**][RESOURCE "+id.getLocalName()+"] "+msg);
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

	@Override
	public void notifyEvent(String value) {
		// TODO Auto-generated method stub
		try {
			String[] key_value = value.split("/");
			if( key_value[0].equals("intensity") ){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( key_value[0], Integer.parseInt( key_value[1] ) );
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
