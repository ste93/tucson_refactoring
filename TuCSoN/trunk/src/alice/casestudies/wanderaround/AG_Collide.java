package alice.casestudies.wanderaround;

import alice.logictuple.LogicTuple;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

public class AG_Collide extends TucsonAgent{

	private boolean iteraction = true;
	
	public AG_Collide(String aid) throws TucsonInvalidAgentIdException {
		super(aid);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void main() {
		// TODO Auto-generated method stub
			TucsonTupleCentreId tc_motor = null;
			TucsonTupleCentreId tc_sonar = null;
			SynchACC acc = getContext();
			LogicTuple t;
			
			try{
				tc_motor = new TucsonTupleCentreId("tc_motor","localhost",""+20504);
				tc_sonar = new TucsonTupleCentreId("tc_sonar","localhost",""+20504);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			while( iteraction ){
				try{
					t = LogicTuple.parse("distance(front,X)");
					t = acc.rdp(tc_sonar, t, null).getLogicTupleResult();
					
					if( t.getArg(1).intValue()< 15 ){
						t = LogicTuple.parse("data(halt)");
						acc.out(tc_motor, t, null);
					}	

				}catch( Exception e ){
					System.err.println(e.toString());
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
