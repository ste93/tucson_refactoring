package alice.tucson.rbac;

import java.io.Serializable;
import java.util.List;

public interface RBAC extends Serializable {

	String getOrgName();
	void setOrgName(String orgName);
	
	void addRole(Role role);
	void removeRole(String roleName);
	void removeRole(Role role);
	
	void addPolicy(Policy policy);
	void removePolicy(String policyName);
	void removePolicy(Policy policy);
	
	void addAuthorizedAgent(AuthorizedAgent agent);
	void removeAuthorizedAgent(String agentName);
	void removeAuthorizedAgent(AuthorizedAgent agent);
	
	List<Role> getRoles();
	List<Policy> getPolicies();
	List<AuthorizedAgent> getAuthorizedAgents();
	
	void setAuthorizedInspectors(boolean auth);
	boolean getAuthorizedInspectors();
	
	void setLoginRequired(boolean loginReq);
	boolean getLoginRequired();
	
	void setBaseAgentClass(String agentClass);
	String getBaseAgentClass();
}
