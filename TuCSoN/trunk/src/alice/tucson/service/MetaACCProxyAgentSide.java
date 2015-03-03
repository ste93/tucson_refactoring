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
		
		LogicTuple defaultClassTuple = new LogicTuple("set_default_agent_class", new Value(rbac.getDefaultAgentClass()));;
		op = inp(tid, defaultClassTuple, l);
		
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
	
	//TODO: implementare remove RBAC
	@Override
	public void removeRBAC(Long l, String node, int port) throws OperationNotAllowedException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException {
		/*if(rbac == null)
			return;*/
		
		if(!admin_authorized)
			throw new OperationNotAllowedException();
		inp(tid, new LogicTuple("disinstall_rbac"), (Long)null);
		/*
		LogicTuple roleTuple = new LogicTuple("role", new Var("Nome"), new Var("Desc"), new Var("Livello"));
		LogicTuple policyTuple = new LogicTuple("policy",new Var("NomePolicy"),new Var("Permessi"));
		LogicTuple rolePolicyTuple = new LogicTuple("role_policy", new Var("NomeRuolo"), new Var("NomePolicy"));
		LogicTuple authorizedTuple = new LogicTuple("authorized_agent", new Var("NomeAgente"));
		inAll(tid, roleTuple, (Long)l);
		inAll(tid, policyTuple, (Long)l);
		inAll(tid, rolePolicyTuple, (Long)l);
		inAll(tid, authorizedTuple, (Long)l);*/
		
	}
	
	// Passiamo da una fase in cui RBAC non è supportato ad una fase nella quale va tutto come progettato
	// admin si aggiunge agli utenti autorizzati
	private void installRBACSupport() throws IOException, TucsonOperationNotPossibleException, TucsonInvalidSpecificationException, TucsonInvalidLogicTupleException, UnreachableNodeException, OperationTimeOutException, OperationNotAllowedException{
		
		if(!admin_authorized)
			throw new OperationNotAllowedException();
		
		inp(tid, new LogicTuple("install_rbac"), (Long)null);
		//TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), aid, tid, new LogicTuple("install_rbac"));
		//set(tid, new LogicTuple("[]"), (Long)null); // Rimozione delle tuple precedenti
		
		/*final InputStream is = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(
                        MetaACCProxyAgentSide.RBAC_BOOT_SPEC_FILE);
        final String spec = alice.util.Tools.loadText(new BufferedInputStream(is));
        
		setS(tid, spec, (Long)null);*/
		//TODO: Vedere se serve inserire l'agente autorizzato
        LogicTuple adminAuthorized = new LogicTuple("authorized_agent",new Value(aid.toString()));
        out(tid, adminAuthorized, (Long)null);
        //TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), aid, tid, adminAuthorized);
        
        //out(tid, new LogicTuple("boot"), (Long)null);
	}
	
	
	private void addRole(Role role, Long l) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, NoSuchAlgorithmException{
		
		LogicTuple roleTuple = new LogicTuple("role",
				new Value(role.getRoleName()),
				new Value(role.getDescription()),
				new Value(role.getAgentClass()));
		
		ITucsonOperation op = out(tid, roleTuple, l);
		//TODO: rimozione credenziali di un ruolo
		if(role.getCredentialsRequired())
			op = out(tid, new LogicTuple("role_credentials", new Value(role.getRoleName()), new Value(role.getEncryptedCredentials())), l);
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
			//TucsonTupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
			LogicTuple template = new LogicTuple("role_activation_request",
					new Value(this.aid.toString()),
					new Value(this.getUUID().toString()),
					new Value("admin_role"),
					new Value(getUsername()+":"+TucsonACCTool.encrypt(getPassword())),
					new Var("Result"));
			ITucsonOperation op = inp(tid, template, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				if(res!=null && res.getArg(4).getName().equalsIgnoreCase("ok")){
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
	public void removeRole(String roleName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(Policy policy) throws InvalidVarNameException, InvalidTupleArgumentException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		this.addPolicy(policy, (Long)null);
	}

	@Override
	public void removePolicy(String policyName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(AuthorizedAgent agent) throws NoSuchAlgorithmException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		this.addAuthorizedAgent(agent, (Long)null);
	}

	@Override
	public void remove(String agentName) {
		// TODO Auto-generated method stub
		
	}

	
}
