package alice.casestudies.wanderaround;

import alice.tuplecentre.api.TupleCentreId;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.Transducer;
import alice.respect.transducer.TransducerId;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.core.TupleCentreOperation;

public class TransducerSens extends Transducer {

	public TransducerSens( TransducerId id, TupleCentreId tcId, ProbeId probeId ) throws TucsonInvalidAgentIdException {
		super( id, tcId, probeId );
	}

	@Override
	public boolean getEnv( String key ){
		try{
			boolean allReadsOk = false;
			Object[] keySet = probes.keySet().toArray();
			for( int i=0; i<keySet.length; i++ ){
				//value = ((ISimpleProbe)probes.get( (ProbeId)keySet[i] )).readValue( key );
				allReadsOk = ((ISimpleProbe)probes.get( (ProbeId)keySet[i] )).readValue( key );
			}
			
			return allReadsOk;
		}catch( Exception ex ){
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean setEnv(String key, int value) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operationCompleted(TupleCentreOperation op) {
		// TODO Auto-generated method stub
		
	}

}

