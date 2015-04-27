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
public interface RBAC extends Serializable {

    void addAuthorizedAgent(AuthorizedAgent agent);

    void addPolicy(Policy policy);

    void addRole(Role role);

    List<AuthorizedAgent> getAuthorizedAgents();

    boolean getAuthorizedInspectors();

    String getBaseAgentClass();

    boolean getLoginRequired();

    String getOrgName();

    List<Policy> getPolicies();

    List<Role> getRoles();

    void removeAuthorizedAgent(AuthorizedAgent agent);

    void removePolicy(Policy policy);

    void removePolicy(String policyName);

    void removeRole(Role role);

    void removeRole(String roleName);

    void setAuthorizedInspectors(boolean auth);

    void setBaseAgentClass(String agentClass);

    void setLoginRequired(boolean loginReq);

    void setOrgName(String orgName);
}
