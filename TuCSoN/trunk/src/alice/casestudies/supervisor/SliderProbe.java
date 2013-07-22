package alice.casestudies.supervisor;

import alice.respect.core.ISerialEventListener;
import alice.respect.core.SerialComm;
import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class SliderProbe implements ISerialEventListener, ISimpleProbe{

	private ProbeId id;
	private TransducerId tId;
	private TransducerStandardInterface transducer;
	private int sliderValue = 50;
	
	public SliderProbe(ProbeId id){
		this.id = id;
		SerialComm.getSerialComm().addListener(this);
	}

	@Override
	public boolean writeValue(String key, int value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean readValue(String key) {
		// TODO Auto-generated method stub
		if( key.equals("intensity") ){
			String msg = "RI"; // RD means Read Distance
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
		String[] key_value = value.split("/");
		sliderValue = Integer.parseInt(key_value[1]);
//		valueNormalized = sliderValue/10;
		try{
			if( transducer == null ){
				transducer = TransducerManager.getTransducerManager().getTransducer( tId.getAgentName() );
			}
			transducer.notifyEnvEvent( key_value[0], sliderValue );
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public String getListenerName() {
		// TODO Auto-generated method stub
		return id.getLocalName();
	}

}
