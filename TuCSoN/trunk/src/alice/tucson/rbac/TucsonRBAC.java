package alice.tucson.rbac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public class TucsonRBAC implements RBAC {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final List<AuthorizedAgent> authorizedAgents;
    private String baseAgentClass;
    private boolean inspectorsAuthorized;
    private boolean loginRequired;

    private String orgName;
    private final Map<String, Policy> policies;
    private final Map<String, Role> roles;

    public TucsonRBAC(final String org) {
        this.roles = new HashMap<String, Role>();
        this.policies = new HashMap<String, Policy>();
        this.authorizedAgents = new ArrayList<AuthorizedAgent>();
        this.orgName = org;
        this.loginRequired = false;
        this.inspectorsAuthorized = false;
        this.baseAgentClass = "baseAgentClass";
    }

    @Override
    public void addAuthorizedAgent(final AuthorizedAgent agent) {
        if (!this.authorizedAgents.contains(agent)) {
            this.authorizedAgents.add(agent);
        }
    }

    @Override
    public void addPolicy(final Policy policy) {
        if (policy == null) {
            return;
        }

        if (this.policies.get(policy.getPolicyName()) != null) {
            return;
        }

        this.policies.put(policy.getPolicyName(), policy);
    }

    @Override
    public void addRole(final Role role) {
        if (role == null) {
            return;
        }

        if (this.roles.get(role.getRoleName()) != null) {
            return; // Gi� presente
        }

        this.roles.put(role.getRoleName(), role);
    }

    @Override
    public List<AuthorizedAgent> getAuthorizedAgents() {
        return this.authorizedAgents;
    }

    @Override
    public boolean getAuthorizedInspectors() {
        return this.inspectorsAuthorized;
    }

    @Override
    public String getBaseAgentClass() {
        return this.baseAgentClass;
    }

    @Override
    public boolean getLoginRequired() {
        return this.loginRequired;
    }

    @Override
    public String getOrgName() {
        return this.orgName;
    }

    @Override
    public List<Policy> getPolicies() {
        final List<Policy> policyList = new ArrayList<Policy>(
                this.policies.values());
        return policyList;
    }

    @Override
    public List<Role> getRoles() {
        final List<Role> rolesList = new ArrayList<Role>(this.roles.values());
        return rolesList;
    }

    @Override
    public void removeAuthorizedAgent(final AuthorizedAgent agent) {
        if (this.authorizedAgents.contains(agent)) {
            this.authorizedAgents.remove(agent);
        }
    }

    @Override
    public void removePolicy(final Policy policy) {
        if (policy != null && this.policies.containsKey(policy.getPolicyName())) {
            this.policies.remove(policy.getPolicyName());
        }
    }

    @Override
    public void removePolicy(final String policyName) {
        if (this.policies.containsKey(policyName)) {
            this.policies.remove(policyName);
        }
    }

    @Override
    public void removeRole(final Role role) {
        if (role != null && this.roles.containsKey(role.getRoleName())) {
            this.roles.remove(role.getRoleName());
        }
    }

    @Override
    public void removeRole(final String roleName) {
        this.roles.remove(roleName);
    }

    @Override
    /**
     * Set the the rule whether to authorize inspectors or not
     *
     * @param auth
     * 			If inspectors have to be authorized or not.
     */
    public void setAuthorizedInspectors(final boolean auth) {
        this.inspectorsAuthorized = auth;
    }

    @Override
    public void setBaseAgentClass(final String agentClass) {
        this.baseAgentClass = agentClass;
    }

    @Override
    public void setLoginRequired(final boolean loginReq) {
        this.loginRequired = loginReq;
    }

    @Override
    public void setOrgName(final String org) {
        this.orgName = org;
    }

}
