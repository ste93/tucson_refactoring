package alice.tucson.rbac;

import java.io.Serializable;

public interface AuthorizedAgent extends Serializable {

    public String getAgentClass();

    public String getPassword();

    public String getUsername();

    public void setPassword(String password);

    public void setUsername(String username);
}
