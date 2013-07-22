package alice.casestudies.supervisor;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class SliderProbeSw implements ChangeListener, ISimpleProbe{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	private int sliderValue = 50;
	
	public SliderProbeSw(ProbeId id){
		this.id = id;
		SupervisorGUI.getLightGUI().addSliderChangeListener(this);
		readValue("intensity");
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JSlider source = (JSlider) e.getSource();
//		if (!source.getValueIsAdjusting()) {
			sliderValue = (int)source.getValue();
//			valueNormalized = sliderValue/10;
			try{
				if( transducer == null ){
					transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
				}
				transducer.notifyEnvEvent( "intensity", sliderValue );
			}catch(Exception ex){
				ex.printStackTrace();
			}
//		}
	}

	@Override
	public boolean writeValue(String key, int value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		if( !key.equals("intensity") )
			return false;
		try{
			if( transducer == null ){
				transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
			}
			transducer.notifyEnvEvent( key, sliderValue );
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

}
