package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public class TucsonPermission implements Permission {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static TucsonPermission createPermission(final String permName) {
        return new TucsonPermission(permName);
    }

    public static List<Permission> createPermissionsFromStrings(
            final List<String> permissions) {
        final List<Permission> perms = new ArrayList<Permission>();
        for (final String perm : permissions) {
            perms.add(new TucsonPermission(perm));
        }
        return perms;
    }

    /**
     * 
     */
    protected String permissionName;

    public TucsonPermission() {
    }

    public TucsonPermission(final String perm) {
        this.setPermissionName(perm);
    }

    @Override
    public String getPermissionName() {
        return this.permissionName;
    }

    @Override
    public void setPermissionName(final String perm) {
        if (perm == null || perm.equals("")) {
            return;
        }
        this.permissionName = perm;
    }
}
