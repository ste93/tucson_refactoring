package alice.tucson.service;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidTupleArgumentException;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.respect.api.exceptions.*;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.MetaACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidSpecificationException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.AuthorizedAgent;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.RBAC;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonAuthorizedAgent;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRole;
import alice.tucson.service.tools.TucsonACCTool;
import alice.tucson.utilities.Utils;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Term;


public class MetaACCProxyAgentSide extends ACCProxyAgentSide implements MetaACC{
	
	private boolean admin_authorized;
	private TupleCentreId tid;
	
	public MetaACCProxyAgentSide(Object id)
			throws TucsonInvalidAgentIdException, TucsonInvalidTupleCentreIdException {
		this(id, "localhost", 20504);
	}
	
	public MetaACCProxyAgentSide(Object aid, String node, int port) throws TucsonInvalidAgentIdException, TucsonInvalidTupleCentreIdException{
		this(aid, node, port, "","");
	}
	
	public MetaACCProxyAgentSide(Object aid, String node, int port, String username, String password) throws TucsonInvalidAgentIdException, TucsonInvalidTupleCentreIdException{
		super(aid, node, port);
		setUsername(username);
		setPassword(password);
		admin_authorized = false;
		tid = getTid(node, port);// new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
		try {
			activateAdminRole();
		} catch (InvalidVarNameException | NoSuchAlgorithmException
				| TucsonOperationNotPossibleException
				| TucsonInvalidTupleCentreIdException
				| UnreachableNodeException | OperationTimeOutException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add(RBAC rbac) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException, OperationNotAllowedException, NoSuchAlgorithmException {		
		this.add(rbac, null, node, port);
	}
	
	@Override
	public void add(RBAC rbac, Long l, String node, int port) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException, OperationNotAllowedException, NoSuchAlgorithmException {
		if(rbac == null)
			return;
		
		if(!admin_authorized)
			throw new OperationNotAllowedException();
		
		try {
			installRBACSupport();
		} catch (IOException | TucsonInvalidSpecificationException
				| TucsonInvalidLogicTupleException e) {
			e.printStackTrace();
		}
		
		LogicTuple template = new LogicTuple("organisation_name",
				new Value(rbac.getOrgName()));
		ITucsonOperation op = out(tid, template, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("addRBAC: "+res);
		}
		else
			log("problem with addRBAC: "+rbac.getOrgName());
		
		LogicTuple baseClassTuple = new LogicTuple("set_base_agent_class", new Value(rbac.getBaseAgentClass()));;
		op = inp(tid, baseClassTuple, l);
		
		for(Role role : rbac.getRoles())
			this.addRole(role, l);
		
		for(Policy policy : rbac.getPolicies())
			this.addPolicy(policy, l);
		
		for(AuthorizedAgent authAgent : rbac.getAuthorizedAgents())
			this.addAuthorizedAgent(authAgent, l);
		
		LogicTuple loginTuple = new LogicTuple("set_login", new Value((rbac.getLoginRequired())? "yes" : "no"));
		/*if(rbac.getLoginRequired()){
			loginTuple = new LogicTuple("set_login", new Value("yes"));
		} else {
			loginTuple = new LogicTuple("set_login", new Value("no"));
		}*/
		
		op = inp(tid, loginTuple, l);
		
		LogicTuple inspectorsTuple = new LogicTuple("authorize_inspectors", new Value((rbac.getAuthorizedInspectors())? "yes" : "no"));
		if(rbac.getAuthorizedInspectors()){
			inspectorsTuple = new LogicTuple("authorize_inspectors", new Value("yes"));
		} else {
			inspectorsTuple = new LogicTuple("authorize_inspectors", new Value("no"));
		}
		
		op = inp(tid, inspectorsTuple, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("Inspectors activation: "+res.getArg(0));
		}
		else
			log("Inspectors activation: ERROR");
	}

	@Override
	public void removeRBAC() throws OperationNotAllowedException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException {
		this.removeRBAC(null, node, port);
	}
	
	@Override
	public void removeRBAC(Long l, String node, int port) throws OperationNotAllowedException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException {

		if(!admin_authorized)
			throw new OperationNotAllowedException();
		inp(tid, new LogicTuple("disinstall_rbac"), (Long)null);	
	}
	
	// Passiamo da una fase in cui RBAC non è supportato ad una fase nella quale va tutto come progettato
	// admin si aggiunge agli utenti autorizzati
	private void installRBACSupport() throws IOException, TucsonOperationNotPossibleException, TucsonInvalidSpecificationException, TucsonInvalidLogicTupleException, UnreachableNodeException, OperationTimeOutException, OperationNotAllowedException{
		
		if(!admin_authorized)
			throw new OperationNotAllowedException();
		
		inp(tid, new LogicTuple("install_rbac"), (Long)null);
		//TODO: Serve admin autorizzato? Se non fosse autorizzato non potrebbe farlo!
        LogicTuple adminAuthorized = new LogicTuple("authorized_agent",new Value(aid.toString()));
        out(tid, adminAuthorized, (Long)null);
	}
	
	
	private void addRole(Role role, Long l) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, NoSuchAlgorithmException{
		
		LogicTuple roleTuple = new LogicTuple("role",
				new Value(role.getRoleName()),
				new Value(role.getDescription()),
				new Value(role.getAgentClass()));
		
		ITucsonOperation op = out(tid, roleTuple, l);
		
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("addRole: "+res);
		}
		else
			log("problem with addRole: "+role.getRoleName());
		
		this.addRolePolicy(role.getPolicy(), role.getRoleName(), l);
	}
	
	private void addPolicy(Policy policy, Long l) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException{
		String permissions = "[";
		for(Permission perm : policy.getPermissions()){
			permissions += perm.getPermissionName() + ",";
		}

		permissions = permissions.substring(0, permissions.length() - 1);
		permissions += "]";
		LogicTuple policyTuple = new LogicTuple("policy",
				new Value(policy.getPolicyName()),
				TupleArgument.parse(permissions));
		ITucsonOperation op = out(tid, policyTuple, l);
		
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("addPolicy: "+res);
		}
		else
			log("problem with addPolicy: "+policy.getPolicyName());
		
	}
	
	private void addRolePolicy(Policy policy, String roleName, Long l) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		
		LogicTuple policyTuple = new LogicTuple("role_policy",
				new Value(roleName),
				new Value(policy.getPolicyName()));
		ITucsonOperation op = out(tid, policyTuple, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("addRolePolicy: "+res);
		}
		else
			log("problem with addRolePolicy: "+policy.getPolicyName());
	}
	
	private void addAuthorizedAgent(AuthorizedAgent agent, Long l) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, NoSuchAlgorithmException{
		LogicTuple authTuple = TucsonAuthorizedAgent.getLogicTuple(agent);
		ITucsonOperation op = out(tid, authTuple, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("Authorized agent added: "+res);
		} else {
			log("Problem with addAuthorizedAgent.");
		}
	}

	private TupleCentreId getTid(String node, int port) throws TucsonInvalidTupleCentreIdException{
		String tmpNode;
		int tmpPort;
		if(node!=null && !node.equals("")) {
			tmpNode = node;
			tmpPort = port;
		}
		else {
			tmpNode = this.node;
			tmpPort = this.port;
		}

		if(node.equalsIgnoreCase("localhost") || node.equalsIgnoreCase("127.0.0.1") || node.equalsIgnoreCase("'127.0.0.1'")) {
			InetAddress localhost;
			try {
				localhost = InetAddress.getLocalHost();
				String local_node_address = localhost.getHostAddress();
				tmpNode = local_node_address;
			} catch (UnknownHostException e) {
				tmpNode = node;
				return new TucsonTupleCentreId(tcOrg, "'"+tmpNode+"'", ""+tmpPort);
			}
		}
		else
			tmpNode = node;
		
		return new TucsonTupleCentreId(tcOrg, "'"+tmpNode+"'", ""+tmpPort);
	}


	private void activateAdminRole() throws InvalidVarNameException,
			TucsonOperationNotPossibleException,
			TucsonInvalidTupleCentreIdException, UnreachableNodeException,
			OperationTimeOutException, NoSuchAlgorithmException {
		
		try {
			LogicTuple template = new LogicTuple("admin_login_request",
					new Value(this.getUsername()+":"+TucsonACCTool.encrypt(this.getPassword())),
					new Var("Result"));
			ITucsonOperation op = inp(tid, template, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				if(res!=null && res.getArg(1).getName().equalsIgnoreCase("ok")){
					admin_authorized = true;
				}
			}
		} catch (InvalidVarNameException | TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add(Role role) throws InvalidVarNameException, NoSuchAlgorithmException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		this.addRole(role, (Long)null);
	}

	@Override
	public void removeRole(String roleName) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		LogicTuple roleDestructionTuple = new LogicTuple("remove_role", new Value(Utils.decapitalize(roleName)));
		inp(tid, roleDestructionTuple, (Long)null);
	}

	@Override
	public void add(Policy policy) throws InvalidVarNameException, InvalidTupleArgumentException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		this.addPolicy(policy, (Long)null);
	}

	@Override
	public void removePolicy(String policyName) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		LogicTuple policyDestructionTuple = new LogicTuple("remove_policy", new Value(policyName));
		inp(tid, policyDestructionTuple, (Long)null);
	}

	@Override
	public void add(AuthorizedAgent agent) throws NoSuchAlgorithmException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		this.addAuthorizedAgent(agent, (Long)null);
	}

	@Override
	public void remove(String agentName) {
		//TODO
	}

	@Override
	public void setBaseAgentClass(String newBaseAgentClass) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		LogicTuple newClassTuple = new LogicTuple("set_base_agent_class",
				new Value(newBaseAgentClass));
		
		ITucsonOperation op = inp(tid, newClassTuple, (Long)null);
		if(op.isResultSuccess()){
			log("[MetaACC]: changed base agent class to " + newBaseAgentClass);
		} else {
			log("[MetaACC]: failure in changing base agent class");
		}
	}

	@Override
	public void setRolePolicy(String roleName, String policyName) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		LogicTuple setPolicyTuple;
		try {
			setPolicyTuple = new LogicTuple("set_role_policy",
					new Value(roleName),
					new Value(policyName),
					new Var("Result"));
			ITucsonOperation op = inp(tid, setPolicyTuple, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				if(res.getArg(2).getName().equalsIgnoreCase("ok"))
					log("[" + this.aid.toString() + "]: policy of role " + roleName + " changed to " + policyName + ".");
				else if(res.getArg(2).getName().equalsIgnoreCase("failed")){
					String failReason = res.getArg(2).getArg(0).toString();
					log("[" + this.aid.toString() + "]: policy of role " + roleName + " not changed because " + failReason + ".");
				}
			}
		} catch (InvalidVarNameException e) {
			e.printStackTrace();
		}
	}

	//TODO: Non funziona l'inserimento in lista
	@Override
	public void addPermissionToPolicy(Permission permission, String policyName) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		
		try {
			LogicTuple setPermissionTuple = new LogicTuple("add_permission",
					new Value(policyName),
					new Value(permission.getPermissionName()),
					new Var("Result"));
			ITucsonOperation op = inp(tid, setPermissionTuple, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				if(res.getArg(2).getName().equalsIgnoreCase("ok"))
					log("[" + this.aid.toString() + "]: permission " + permission.getPermissionName() + " added to " + policyName + ".");
				else if(res.getArg(2).getName().equalsIgnoreCase("failed")){
					String failReason = res.getArg(2).getArg(0).toString();
					log("[" + this.aid.toString() + "]: permission " + permission.getPermissionName() + " not added because " + failReason + ".");
				}
			}
		} catch (InvalidVarNameException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setRoleAgentClass(String roleName, String agentClass) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		LogicTuple setRoleClassTuple;
		try {
			setRoleClassTuple = new LogicTuple("set_role_class",
					new Value(roleName),
					new Value(agentClass),
					new Var("Result"));
			ITucsonOperation op = inp(tid, setRoleClassTuple, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				if(res.getArg(2).getName().equalsIgnoreCase("ok"))
					log("[" + this.aid.toString() + "]: agent class of role " + roleName + " changed to " + agentClass + ".");
				else if(res.getArg(2).getName().equalsIgnoreCase("failed")){
					String failReason = res.getArg(2).getArg(0).toString();
					log("[" + this.aid.toString() + "]: agent class of role " + roleName + " changed because " + failReason + ".");
				}
			}
		} catch (InvalidVarNameException e) {
			e.printStackTrace();
		}
	}
}
