package alice.tucson.rbac;


public class TucsonPermission implements Permission{
	
	protected String permissionName;
	
	public TucsonPermission(){};
	public TucsonPermission(String permissionName){
		setPermissionName(permissionName);
	}

	@Override
	public String getPermissionName() {
		return permissionName;
	}

	@Override
	public void setPermissionName(String permissionName) {
		if(permissionName == null || permissionName.equals(""))
			return;
		this.permissionName = permissionName;
	}
}
