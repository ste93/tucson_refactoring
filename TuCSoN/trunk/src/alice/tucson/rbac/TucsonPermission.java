package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.List;

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

    protected String permissionName;

    public TucsonPermission() {
    }

    public TucsonPermission(final String permissionName) {
        this.setPermissionName(permissionName);
    }

    @Override
    public String getPermissionName() {
        return this.permissionName;
    }

    @Override
    public void setPermissionName(final String permissionName) {
        if (permissionName == null || permissionName.equals("")) {
            return;
        }
        this.permissionName = permissionName;
    }
}
