package alice.tucson.rbac;

import alice.tucson.utilities.Utils;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public class TucsonRole implements Role {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    protected String agentClass;
    /**
     * 
     */
    protected Policy policy;
    /**
     * 
     */
    protected String roleDescription;
    /**
     * 
     */
    protected String roleName;

    public TucsonRole() {
        this("");
    }

    public TucsonRole(final String rn) {
        this(rn, "substitute");
    }

    public TucsonRole(final String rn, final String ac) {
        this.setRoleName(rn);
        this.setAgentClass(ac);
    }

    @Override
    public String getAgentClass() {
        if (this.agentClass == null || this.agentClass.equalsIgnoreCase("")) {
            return "substitute";
        }
        return this.agentClass;
    }

    @Override
    public String getDescription() {
        return this.roleDescription != null
                && !this.roleDescription.equalsIgnoreCase("") ? this.roleDescription
                        : "ruolo_" + this.roleName;
    }

    @Override
    public Policy getPolicy() {
        return this.policy;
    }

    @Override
    public String getRoleName() {
        return this.roleName;
    }

    @Override
    public void setAgentClass(final String ac) {
        this.agentClass = Utils.decapitalize(ac);
    }

    @Override
    public void setDescription(final String rd) {
        this.roleDescription = rd;
    }

    @Override
    public void setPolicy(final Policy p) {
        this.policy = p;
    }

    @Override
    public void setRoleName(final String rn) {
        if (rn == null || rn.equals("")) {
            return;
        }

        this.roleName = Utils.decapitalize(rn);
    }
}
