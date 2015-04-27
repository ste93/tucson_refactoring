package alice.tucson.rbac;

import java.io.Serializable;

/*
 * 		Rappresenta un permesso per una politica
 */
public interface Permission extends Serializable {

    public String getPermissionName();

    public void setPermissionName(String permissionName);
}
