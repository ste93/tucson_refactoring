package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.List;

public class TucsonPolicy implements Policy{

	protected List<Permission> permissions;
	
	
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

}
