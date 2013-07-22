package alice.casestudies.supervisor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class RangeProbe implements ActionListener, ISimpleProbe{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	private SupervisorGUI gui;
	
	public RangeProbe(ProbeId id){
		this.id = id;
		gui = SupervisorGUI.getLightGUI();
		gui.addRangeButtonActionListener(this);
	}

	@Override
	public boolean writeValue(String key, int value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		try{	
			if( key.equals("min") ){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( key, Integer.parseInt(gui.getMinValue()) );
				return true;
			}else if( key.equals("max")){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( key, Integer.parseInt(gui.getMaxValue()) );
				return true;
			}
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
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		try{
			if( ((javax.swing.JButton)arg0.getSource()).getName().equals("btnMin") ){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( "min", Integer.parseInt(gui.getMinValue()) );
			}else if( ((javax.swing.JButton)arg0.getSource()).getName().equals("btnMax") ){
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( "max", Integer.parseInt(gui.getMaxValue()) );
			}
		}catch( Exception e ){
			e.printStackTrace();
		}
	}

}
