package alice.tucson.rbac;


public class TucsonPermission implements Permission{
	
	protected String permissionName;

	@Override
	public String getPermission() {
		return permissionName;
	}

	@Override
	public void setPermission(String permissionName) {
		if(permissionName == null || permissionName.equals(""))
			return;
		this.permissionName = permissionName;
	}
}
