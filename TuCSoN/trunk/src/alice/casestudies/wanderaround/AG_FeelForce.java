package alice.casestudies.wanderaround;

import alice.logictuple.LogicTuple;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class AG_FeelForce extends TucsonAgent{

	private boolean iteraction = true;
	
	private int angle, speed;
	
	public AG_FeelForce(String aid) throws TucsonInvalidAgentIdException {
		super(aid);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void main() {
		// TODO Auto-generated method stub
			SynchACC acc = getContext();
			TucsonTupleCentreId tc_sonar = null;
			TucsonTupleCentreId tc_runaway = null;
			TucsonTupleCentreId tc_avoid = null;
			int front, right, back, left;
			LogicTuple t;
			
			try {
				tc_sonar = new TucsonTupleCentreId("tc_sonar","localhost",""+20504);
				tc_runaway = new TucsonTupleCentreId("tc_runaway","localhost",""+20504);
				tc_avoid = new TucsonTupleCentreId("tc_avoid","localhost",""+20504);
			} catch (TucsonInvalidTupleCentreIdException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(iteraction){
				try{
					t = LogicTuple.parse("distance(front,X)");
					front = acc.rdp(tc_sonar, t, null).getLogicTupleResult().getArg(1).intValue();
					t = LogicTuple.parse("distance(right,X)");
					right = acc.rdp(tc_sonar, t, null).getLogicTupleResult().getArg(1).intValue();
					t = LogicTuple.parse("distance(back,X)");
					back = acc.rdp(tc_sonar, t, null).getLogicTupleResult().getArg(1).intValue();
					t = LogicTuple.parse("distance(left,X)");
					left = acc.rdp(tc_sonar, t, null).getLogicTupleResult().getArg(1).intValue();
	
//					System.out.println("[AG_FEELFORCE] F: "+front+" | R: "+right+" | B: "+back+" | L: "+left);
					computeDirection(front, right, back, left, angle, speed);
//					System.out.println("[AG_FEELFORCE] New direction: A: "+angle+" | S: "+speed);
					
					t = LogicTuple.parse("data(direction("+angle+","+speed+"))");
					acc.out(tc_runaway, t, null);
					acc.out(tc_avoid, t, null);
					
					Thread.sleep(1000);
				}catch(Exception e){
					System.err.println(e.toString());
				}
			}
		
	}
	
	private void computeDirection(int front, int right, int back, int left, int a, int s){
		this.angle = (int) (Math.random() * 360);
		this.speed = (int) (Math.random() * 100);
	}
	
	public void stopAgent(){
		iteraction = false;
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}

}
