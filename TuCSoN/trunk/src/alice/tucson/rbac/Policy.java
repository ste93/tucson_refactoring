package alice.tucson.rbac;

import java.io.Serializable;
import java.util.List;

/*
 * 		Rappresenta la politica di un ruolo
 */
public interface Policy extends Serializable {

	List<Permission> getPermissions();
	void setPermissions(List<Permission> permissions);
	void addPermission(Permission permission);
	void removePermission(Permission permission);
}
