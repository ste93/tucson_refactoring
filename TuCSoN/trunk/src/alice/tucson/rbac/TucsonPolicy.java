package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.List;

import alice.logictuple.TupleArgument;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

public class TucsonPolicy implements Policy{

	protected List<Permission> permissions;
	private String policyName;
	
	public TucsonPolicy(String policyName){
		this.policyName = policyName;
	}
	
	public TucsonPolicy(String policyName, List<Permission> perms){
		this(policyName);
		this.permissions = perms;
	}
	
	public String getPolicyName(){
		return policyName;
	}
	
	public void setPolicyName(String policyName){
		this.policyName = policyName;
	}
	
	@Override
	public List<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public void addPermission(Permission permission) {
		if(permissions==null)
			permissions = new ArrayList<Permission>();
		if(!permissions.contains(permission))
			permissions.add(permission);
	}

	@Override
	public void removePermission(Permission permission) {
		if(permissions!=null)
			permissions.remove(permission);
	}
	
	

	@Override
	public boolean hasPermissions(List<String> permissionsId) {
		for(String perm : permissionsId){
			if(!hasPermission(perm))
				return false;
		}
		return true;
	}
	
	private boolean hasPermission(String permissionid){
		for(Permission perm : permissions){
			if(perm.getPermissionName().equalsIgnoreCase(permissionid))
				return true;
		}
		
		return false;
	}

	public static Policy createPolicy(String nomePolicy, TupleArgument[] permissionsArr){
		Policy returnPolicy = new TucsonPolicy(nomePolicy);
		for(TupleArgument permTuple : permissionsArr){
			Permission newPermission = TucsonPermission.createPermission(permTuple.getName());
			returnPolicy.addPermission(newPermission);
		}
		return returnPolicy;
	}
}
