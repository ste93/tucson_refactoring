package alice.casestudies.supervisor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class SwitchProbe implements ActionListener, ISimpleProbe{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	private boolean lock = false;
	
	public SwitchProbe(ProbeId id){
		this.id = id;
		SupervisorGUI.getLightGUI().addSwitchActionListener(this);
	}

	@Override
	public boolean writeValue(String key, int value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		if( !key.equals("lock") )
			return false;
		try{
			if( transducer == null ){
				transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
			}
			if( lock )
				transducer.notifyEnvEvent( key, 1 );
			else
				transducer.notifyEnvEvent( key, 0 );
		}catch(Exception e){
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		lock = !lock;
		try{
			if( transducer == null ){
				transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
			}
			if( lock )
				transducer.notifyEnvEvent( "lock", 1 );
			else
				transducer.notifyEnvEvent( "lock", 0 );
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
