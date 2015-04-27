package alice.tucson.rbac;

import java.io.Serializable;
import java.util.List;

/*
 * 		Rappresenta la politica di un ruolo
 */
public interface Policy extends Serializable {

    void addPermission(Permission permission);

    List<Permission> getPermissions();

    String getPolicyName();

    boolean hasPermissions(List<String> permissionsId);

    void removePermission(Permission permission);

    void setPermissions(List<Permission> permissions);

    void setPolicyName(String policyName);
}
