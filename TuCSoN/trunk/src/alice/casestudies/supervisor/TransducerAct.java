package alice.casestudies.supervisor;

import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.Transducer;
import alice.respect.transducer.TransducerId;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.core.TupleCentreOperation;

public class TransducerAct extends Transducer{
	
	public TransducerAct(TransducerId id, TupleCentreId tcId, ProbeId probeId ) throws TucsonInvalidAgentIdException{
		super(id, tcId, probeId );
		// TODO Auto-generated constructor stub
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationCompleted(TupleCentreOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getEnv(String key){
		// TODO Auto-generated method stub
		try{
			boolean allReadsOk = false;
			Object[] keySet = probes.keySet().toArray();
			for( int i=0; i<keySet.length; i++ )
				allReadsOk = ((ISimpleProbe)probes.get( (ProbeId)keySet[i] )).readValue( key );
			
			return allReadsOk;
		}catch( Exception ex ){
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean setEnv(String key, int value) {
		// TODO Auto-generated method stub
		try{
			boolean resultIsOk = false;
			Object[] keySet = probes.keySet().toArray();
			for( int i=0; i<keySet.length; i++ ){
				resultIsOk = ((ISimpleProbe)probes.get( (ProbeId)keySet[i] )).writeValue(key, value);
			}
			return resultIsOk;
		}catch( Exception ex ){
			//ex.printStackTrace();
			return false;
		}
	}

}
