package alice.tucson.rbac;

import java.io.Serializable;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public interface Permission extends Serializable {

    String getPermissionName();

    void setPermissionName(String permissionName);
}
