package alice.tucson.rbac;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

public interface Role extends Serializable{

	String getRoleName();
	void setRoleName(String roleName);
	
	Policy getPolicy();	
	void setPolicy(Policy policy);
	
	boolean getCredentialsRequired();	
	void setCredentials(String user, String pass);
	
	String getEncryptedCredentials() throws NoSuchAlgorithmException;
}
