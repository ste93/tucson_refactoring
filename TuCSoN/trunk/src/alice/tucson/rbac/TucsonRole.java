package alice.tucson.rbac;

public class TucsonRole implements Role{

	private static final long serialVersionUID = 1L;
	
	protected String roleName;
	protected Policy policy;
	
	public TucsonRole(){}
	
	public TucsonRole(String roleName){
		setRoleName(roleName);
	}
	
	@Override
	public String getRoleName() {
		return roleName;
	}

	@Override
	public void setRoleName(String roleName) {
		if(roleName==null || roleName.equals(""))
			return;
		
		this.roleName = roleName;
	}

	@Override
	public Policy getPolicy() {
		return policy;
	}

	@Override
	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	@Override
	public void addPermission(Permission permission) {
		if(policy==null)
			policy = new TucsonPolicy();
		policy.addPermission(permission);
	}

	@Override
	public void removePermission(Permission permission) {
		if(policy!=null)
			policy.removePermission(permission);
	}

}
