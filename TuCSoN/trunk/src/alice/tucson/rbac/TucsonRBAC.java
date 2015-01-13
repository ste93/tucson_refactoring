package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry.Entry;

public class TucsonRBAC implements RBAC{

	private String orgName;
	
	private Map<String,Role> roles;
	private Map<String,Policy> policies;
	
	public TucsonRBAC(String orgName){
		roles = new HashMap<String,Role>();
		policies = new HashMap<String,Policy>();
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

	@Override
	public void addPolicy(Policy policy) {
		if(policy == null)
			return;
		
		if(policies.get(policy.getPolicyName()) != null)
			return;
		
		policies.put(policy.getPolicyName(), policy);
	}

	@Override
	public void removePolicy(String policyName) {
		policies.remove(policyName);
	}

	@Override
	public void removePolicy(Policy policy) {
		if(policy != null && policies.containsKey(policy.getPolicyName()))
			policies.remove(policy).getPolicyName();
	}

	@Override
	public List<Policy> getPolicies() {
		List<Policy> policyList = new ArrayList<Policy>(policies.values());
		return policyList;
	}

}
