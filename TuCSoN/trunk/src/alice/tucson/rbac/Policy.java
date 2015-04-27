package alice.tucson.rbac;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
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
