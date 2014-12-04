package alice.tucson.rbac;

import java.io.Serializable;
import java.util.List;

public interface RBAC extends Serializable {

	String getOrgName();
	void setOrgName(String orgName);
	
	void addRole(Role role);
	void removeRole(String roleName);
	void removeRole(Role role);
	
	List<Role> getRoles();
}
