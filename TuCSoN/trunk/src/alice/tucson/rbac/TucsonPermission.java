package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.List;


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
	
	public static TucsonPermission createPermission(String permName){
		return new TucsonPermission(permName);
	}
	
	public static List<Permission> createPermissionsFromStrings(List<String> permissions){
		List<Permission> perms = new ArrayList<Permission>();
		for(String perm : permissions){
			perms.add(new TucsonPermission(perm));
		}
		return perms;
	}
}
