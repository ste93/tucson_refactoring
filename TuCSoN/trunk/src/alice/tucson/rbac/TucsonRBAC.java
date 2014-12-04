package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

public class TucsonRBAC implements RBAC{

	private String orgName;
	
	private Map<String,Role> roles;
	
	public TucsonRBAC(String orgName){
		roles = new HashMap<String,Role>();
		this.orgName = orgName;
	}
	
	@Override
	public String getOrgName() {
		return orgName;
	}

	@Override
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	@Override
	public void addRole(Role role) {
		if(role==null)
			return;
		
		if(roles.get(role.getRoleName())!=null) {
			return; //Già presente
		}
		
		roles.put(role.getRoleName(), role);
	}

	@Override
	public void removeRole(String roleName) {
		roles.remove(roleName);
	}

	@Override
	public void removeRole(Role role) {
		if(role != null && roles.containsKey(role.getRoleName()))
			roles.remove(role.getRoleName());
	}

	@Override
	public List<Role> getRoles() {
		List<Role> rolesList = new ArrayList<Role>(roles.values());
		return rolesList;
	}

}
