package alice.tucson.rbac;

import java.security.NoSuchAlgorithmException;

import alice.tucson.service.tools.TucsonACCTool;
import alice.tucson.utilities.Utils;

public class TucsonRole implements Role{

	private static final long serialVersionUID = 1L;
	
	protected String roleName;
	protected String roleDescription;
	protected Policy policy;
	protected String agentClass;

	
	public TucsonRole(){
		this("");
	}
	
	public TucsonRole(String roleName){
		this(roleName, "substitute");
	}
	
	public TucsonRole(String roleName, String agentClass){
		this.setRoleName(roleName);
		this.setAgentClass(agentClass);
	}

	@Override
	public String getRoleName() {
		return roleName;
	}

	@Override
	public void setRoleName(String roleName) {
		if(roleName==null || roleName.equals(""))
			return;
		
		this.roleName = Utils.decapitalize(roleName);
	}
	
	@Override
	public String getDescription() {
		return (roleDescription != null && !roleDescription.equalsIgnoreCase(""))? roleDescription : "ruolo_"+roleName;
	}

	@Override
	public void setDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	
	@Override
	public String getAgentClass() {
		if(agentClass == null || agentClass.equalsIgnoreCase(""))
			return "substitute";
		return agentClass;
	}

	@Override
	public void setAgentClass(String agentClass) {
		this.agentClass = Utils.decapitalize(agentClass);
	}

	@Override
	public Policy getPolicy() {
		return policy;
	}

	@Override
	public void setPolicy(Policy policy) {
		this.policy = policy;
	}
}
