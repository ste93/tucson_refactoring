package alice.tucson.service;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidTupleArgumentException;
import alice.logictuple.exceptions.InvalidVarNameException;
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
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.RBAC;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonAuthorizedAgent;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Term;


public class MetaACCProxyAgentSide extends ACCProxyAgentSide implements MetaACC{
	
	private static final String RBAC_BOOT_SPEC_FILE = "alice/tucson/service/config/boot_spec_rbac.rsp";
	
	public MetaACCProxyAgentSide(Object id)
			throws TucsonInvalidAgentIdException {
		super(id);
	}
	
	public MetaACCProxyAgentSide(Object aid, String node, int port) throws TucsonInvalidAgentIdException{
		super(aid, node, port);
	}

	@Override
	public void add(RBAC rbac) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException {		
		this.add(rbac, null, node, port);
	}
	
	@Override
	public void add(RBAC rbac, Long l, String node, int port) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException {
		if(rbac == null)
			return;
		
		TucsonTupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);//getTid(node, port);
		
		try {
			installRBACSupport(tid);
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
		
		for(Role role : rbac.getRoles())
			this.addRole(role, l, tid);
		
		for(Policy policy : rbac.getPolicies())
			this.addPolicy(policy, l, tid);
		
		for(String authAgent : rbac.getAuthorizedAgents())
			this.addAuthorizedAgent(authAgent, l, tid);
		
		LogicTuple inspectorsTuple = null;
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
	public void remove(RBAC rbac) {
		this.remove(rbac, null, node, port);
	}
	
	//TODO: implementare remove RBAC
	@Override
	public void remove(RBAC rbac, Long l, String node, int port) {
		if(rbac == null)
			return;
	}
	
	// Passiamo da una fase in cui RBAC non è supportato ad una fase nella quale va tutto come progettato
	// admin si aggiunge agli utenti autorizzati
	private void installRBACSupport(TucsonTupleCentreId tid) throws IOException, TucsonOperationNotPossibleException, TucsonInvalidSpecificationException, TucsonInvalidLogicTupleException, UnreachableNodeException, OperationTimeOutException{
		//RIMOZIONE TUTTE TUPLE SPEC PRECEDENTI
		
		//RIMOZIONE TUTTE TUPLE PRECEDENTITupleCentreContainer.doBlockingOperation(TucsonOperation.inAllCode(), aid, tid, null);
		set(tid, new LogicTuple("asd(A)"), (Long)null);
		
		final InputStream is = Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(
                        MetaACCProxyAgentSide.RBAC_BOOT_SPEC_FILE);
        final String spec = alice.util.Tools
                .loadText(new BufferedInputStream(is));
        final LogicTuple specTuple = new LogicTuple("spec", new Value(spec));
        TupleCentreContainer.doBlockingSpecOperation(TucsonOperation.setSCode(), aid, tid, specTuple);
        
        LogicTuple adminAuthorized = new LogicTuple("authorized_agent",new Value(aid.toString()));
        TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), aid, tid, adminAuthorized);
        
        TupleCentreContainer.doNonBlockingOperation(
                TucsonOperation.outCode(), aid, tid,
                new LogicTuple("boot"), null);
	}
	
	
	private void addRole(Role role, Long l, TupleCentreId tid) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException{
		
		LogicTuple roleTuple = new LogicTuple("role",
				new Value(role.getRoleName()),
				new Value("ruolo_" + role.getRoleName()));
		
		ITucsonOperation op = out(tid, roleTuple, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("addRole: "+res);
		}
		else
			log("problem with addRole: "+role.getRoleName());
		
		this.addRolePolicy(role.getPolicy(), role.getRoleName(), l, tid);
	}
	
	private void addPolicy(Policy policy, Long l, TupleCentreId tid) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException{
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
	
	private void addRolePolicy(Policy policy, String roleName, Long l, TupleCentreId tid) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		
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
	
	private void addAuthorizedAgent(String agentName, Long l, TupleCentreId tid) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple authTuple = TucsonAuthorizedAgent.getLogicTuple(agentName);
		ITucsonOperation op = out(tid, authTuple, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("Authorized agent added: "+res);
		} else {
			log("Problem with addAuthorizedAgent: " + agentName);
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

	

	

	
}
