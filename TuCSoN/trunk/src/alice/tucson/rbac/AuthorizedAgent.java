package alice.tucson.rbac;

import java.io.Serializable;

public interface AuthorizedAgent extends Serializable {

	public String getAgentName();
	
	public String getAgentClass();
	
	public void setUsername(String username);
	public String getUsername();
	public void setPassword(String password);
	public String getPassword();
}
