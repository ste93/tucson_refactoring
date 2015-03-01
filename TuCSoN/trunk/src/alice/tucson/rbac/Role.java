package alice.tucson.rbac;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

public interface Role extends Serializable{

	String getRoleName();
	void setRoleName(String roleName);
	
	String getDescription();
	void setDescription(String roleDescription);
	
	String getAgentClass();
	void setAgentClass(String agentClass);
	
	Policy getPolicy();	
	void setPolicy(Policy policy);
	
	// DA RIMUOVERE PROBABILMENTE
	boolean getCredentialsRequired();	
	void setCredentials(String user, String pass);
	String getEncryptedCredentials() throws NoSuchAlgorithmException;
}
