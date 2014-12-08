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
	
	List<Role> getRoles();
	List<Policy> getPolicies();
}
