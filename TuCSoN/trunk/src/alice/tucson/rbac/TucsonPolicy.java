package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.List;
import alice.logictuple.TupleArgument;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public class TucsonPolicy implements Policy {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static Policy createPolicy(final String nomePolicy,
            final TupleArgument[] permissionsArr) {
        final Policy returnPolicy = new TucsonPolicy(nomePolicy);
        for (final TupleArgument permTuple : permissionsArr) {
            final Permission newPermission = TucsonPermission
                    .createPermission(permTuple.getName());
            returnPolicy.addPermission(newPermission);
        }
        return returnPolicy;
    }

    private String policyName;

    /**
     * 
     */
    protected List<Permission> permissions;

    public TucsonPolicy(final String polName) {
        this(polName, new ArrayList<Permission>());
    }

    public TucsonPolicy(final String polName, final List<Permission> perms) {
        this.policyName = polName;
        this.permissions = perms;
    }

    @Override
    public void addPermission(final Permission permission) {
        if (this.permissions == null) {
            this.permissions = new ArrayList<Permission>();
        }
        if (!this.permissions.contains(permission)) {
            this.permissions.add(permission);
        }
    }

    @Override
    public List<Permission> getPermissions() {
        return this.permissions;
    }

    @Override
    public String getPolicyName() {
        return this.policyName;
    }

    @Override
    public boolean hasPermissions(final List<String> permissionsId) {
        for (final String perm : permissionsId) {
            if (!this.hasPermission(perm)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void removePermission(final Permission permission) {
        if (this.permissions != null) {
            this.permissions.remove(permission);
        }
    }

    @Override
    public void setPermissions(final List<Permission> perms) {
        this.permissions = perms;
    }

    @Override
    public void setPolicyName(final String polName) {
        this.policyName = polName;
    }

    private boolean hasPermission(final String permissionid) {
        for (final Permission perm : this.permissions) {
            if (perm.getPermissionName().equalsIgnoreCase(permissionid)) {
                return true;
            }
        }

        return false;
    }
}
