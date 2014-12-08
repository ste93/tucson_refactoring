package alice.tucson.rbac;

import java.io.Serializable;

public interface Role extends Serializable{

	String getRoleName();
	void setRoleName(String roleName);
	
	Policy getPolicy();	
	void setPolicy(Policy policy);
	
	/*void addPermission(Permission permission);
	void removePermission(Permission permission);*/
}
