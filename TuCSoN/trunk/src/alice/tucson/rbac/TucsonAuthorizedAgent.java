package alice.tucson.rbac;

import java.security.NoSuchAlgorithmException;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.tucson.service.tools.TucsonACCTool;

public class TucsonAuthorizedAgent implements AuthorizedAgent {

	private String agentAid;
	private String agentClass;
	
	private String username;
	private String password;
	
	public TucsonAuthorizedAgent(String agentClass, String username, String password){
		this.agentClass = agentClass;
		this.username = username;
		this.password = password;
	}

	@Override
	public String getAgentName() {
		return agentAid;
	}
	
	public static LogicTuple getLogicTuple(AuthorizedAgent agent) throws NoSuchAlgorithmException{
		return new LogicTuple("authorized_agent",
				new Value(agent.getUsername()+":"+TucsonACCTool.encrypt(agent.getPassword())),
				new Value(agent.getAgentClass()));
	}

	@Override
	public String getAgentClass() {
		return this.agentClass;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setPassword(String pass) {
		this.password = pass;
	}

	@Override
	public String getPassword() {
		return this.password;
	}
}
