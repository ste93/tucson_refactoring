package alice.casestudies.wanderaround;

import alice.logictuple.LogicTuple;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class AG_Wander extends TucsonAgent{

	private boolean iteraction = true;
	
	public AG_Wander(String aid) throws TucsonInvalidAgentIdException {
		super(aid);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void main() {
		// TODO Auto-generated method stub
			SynchACC acc = getContext();
			TucsonTupleCentreId tc_avoid = null;
			int angle, speed;
			LogicTuple t;
			
			try {
				tc_avoid = new TucsonTupleCentreId("tc_avoid","localhost",""+20504);
			} catch (TucsonInvalidTupleCentreIdException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(iteraction){
				try{
					angle = (int)(Math.random() * 360);
					speed = (int)(Math.random() * 100);
					t = LogicTuple.parse("data(random_direction("+angle+","+speed+"))");
					acc.out(tc_avoid, t, null);
				}catch(Exception e){
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
