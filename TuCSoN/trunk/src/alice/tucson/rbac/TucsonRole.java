package alice.tucson.rbac;

import java.security.NoSuchAlgorithmException;

import alice.tucson.service.tools.TucsonACCTool;

public class TucsonRole implements Role{

	private static final long serialVersionUID = 1L;
	
	protected String roleName;
	protected Policy policy;
	protected boolean credentialsRequired;
	
	private String username;
	private String password;
	
	public TucsonRole(){
		this("");
	}
	
	public TucsonRole(String roleName){
		this(roleName, false);
	}
	
	public TucsonRole(String roleName, boolean credentialsReq){
		setRoleName(roleName);
		this.credentialsRequired = credentialsReq;
	}

	@Override
	public String getRoleName() {
		return roleName;
	}

	@Override
	public void setRoleName(String roleName) {
		if(roleName==null || roleName.equals(""))
			return;
		
		this.roleName = roleName;
	}

	@Override
	public Policy getPolicy() {
		return policy;
	}

	@Override
	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	@Override
	public boolean getCredentialsRequired() {
		return credentialsRequired;
	}

	public void setCredentialsRequired(boolean credentialsReq){
		this.credentialsRequired = credentialsReq;
	}

	@Override
	public void setCredentials(String user, String pass) {
		this.username = user;
		this.password = pass;
	}

	@Override
	public String getEncryptedCredentials() throws NoSuchAlgorithmException {
		return username+":"+TucsonACCTool.encrypt(password);
	}
	
	

}
