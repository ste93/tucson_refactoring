package alice.tucson.rbac;

import java.io.Serializable;

public interface Role extends Serializable {

    String getAgentClass();

    String getDescription();

    Policy getPolicy();

    String getRoleName();

    void setAgentClass(String agentClass);

    void setDescription(String roleDescription);

    void setPolicy(Policy policy);

    void setRoleName(String roleName);
}
