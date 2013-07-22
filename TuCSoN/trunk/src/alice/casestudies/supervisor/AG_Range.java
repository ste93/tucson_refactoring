package alice.casestudies.supervisor;

import alice.logictuple.LogicTuple;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

/**
 * 
 * Range agent ( LIGHT SUPERVISOR CASE STUDY )
 * 
 * @author Steven Maraldi
 *
 */

public class AG_Range extends TucsonAgent{

	private boolean iteraction = true;
	
	public AG_Range(String aid) throws TucsonInvalidAgentIdException {
		super(aid);
	}

	@Override
	protected void main() {
		// TODO Auto-generated method stub
			SynchACC acc = getContext();
			TucsonTupleCentreId tc_intensity = null;
			TucsonTupleCentreId tc_range = null;
			int intensity, min, max;
			LogicTuple t;
			
			try {
				tc_intensity = new TucsonTupleCentreId("tc_intensity","localhost",""+20504);
				tc_range = new TucsonTupleCentreId("tc_range","localhost",""+20504);
			} catch (TucsonInvalidTupleCentreIdException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(iteraction){
				try{
					t = LogicTuple.parse("range(Min,Max)");
					t = acc.rdp(tc_range, t, null).getLogicTupleResult();
					min = t.getArg(0).intValue();
					max = t.getArg(1).intValue();
					t = LogicTuple.parse("intensity(X)");
					intensity = acc.inp(tc_intensity, t, null).getLogicTupleResult().getArg(0).intValue();
					
					if( intensity < min )
						t = LogicTuple.parse("data(intensity_to_set("+min+"))");
					else if( intensity > max )
						t = LogicTuple.parse("data(intensity_to_set("+max+"))");
					else
						t = LogicTuple.parse("data(intensity_to_set("+intensity+"))");
					
					acc.out(tc_intensity, t, null);
				}catch(Exception e){
					
				}
			}
		
	}
	
	public void stopAgent(){
		iteraction = false;
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}

}
