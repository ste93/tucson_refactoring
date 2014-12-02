package alice.tucson.rbac;


public class TucsonPermission implements Permission{
	
	protected String permissionName;
	
	public TucsonPermission(){};
	public TucsonPermission(String permissionName){
		setPermission(permissionName);
	}

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
