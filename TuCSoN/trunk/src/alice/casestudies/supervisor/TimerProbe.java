package alice.casestudies.supervisor;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class TimerProbe extends Thread implements ISimpleProbe{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	private SupervisorGUI gui;
	private int time = 0;
	
	public TimerProbe( ProbeId id ){
		this.id = id;
		gui = SupervisorGUI.getLightGUI();
	}
	
	@Override
	public boolean writeValue(String key, int value) {
		speak("WRITE REQUEST ( "+key+", "+value+" )");
		// TODO Auto-generated method stub
		if( key.equals("time") ){
			if( value == 0 ){
				time = value;
				gui.setTimeValue(0);
			}else{
				time++;
				gui.setTimeValue( ((int) time) );
			}
			
			return true;
		}
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		if( key.equals("time") ){
			int intensity = Integer.parseInt(gui.getIntensityValue());
			if( transducer == null ){
				transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
			}
			try {
				transducer.notifyEnvEvent( key, intensity );
			} catch (TucsonOperationNotPossibleException
					| UnreachableNodeException | OperationTimeOutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
}
