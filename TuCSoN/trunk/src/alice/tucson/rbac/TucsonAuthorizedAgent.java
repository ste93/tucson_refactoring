package alice.tucson.rbac;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;

public class TucsonAuthorizedAgent implements AuthorizedAgent {

	private String agentAid;
	
	public TucsonAuthorizedAgent(String agentName){
		this.agentAid = agentName;
	}

	@Override
	public String getAgentName() {
		return agentAid;
	}
	
	public static LogicTuple getLogicTuple(String agentName){
		return new LogicTuple("authorized_agent",
				new Value(agentName));
	}
}
