package alice.tucson.rbac;

import alice.tucson.utilities.Utils;

public class TucsonRole implements Role {

    private static final long serialVersionUID = 1L;

    protected String agentClass;
    protected Policy policy;
    protected String roleDescription;
    protected String roleName;

    public TucsonRole() {
        this("");
    }

    public TucsonRole(final String roleName) {
        this(roleName, "substitute");
    }

    public TucsonRole(final String roleName, final String agentClass) {
        this.setRoleName(roleName);
        this.setAgentClass(agentClass);
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
    public void setAgentClass(final String agentClass) {
        this.agentClass = Utils.decapitalize(agentClass);
    }

    @Override
    public void setDescription(final String roleDescription) {
        this.roleDescription = roleDescription;
    }

    @Override
    public void setPolicy(final Policy policy) {
        this.policy = policy;
    }

    @Override
    public void setRoleName(final String roleName) {
        if (roleName == null || roleName.equals("")) {
            return;
        }

        this.roleName = Utils.decapitalize(roleName);
    }
}
