package alice.tucson.service;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.MetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.RBAC;
import alice.tucson.rbac.Role;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;


public class MetaACCProxyAgentSide extends ACCProxyAgentSide implements MetaACC{

	@Override
	public ITucsonOperation activateAdminRole() throws InvalidVarNameException, TucsonOperationNotPossibleException, TucsonInvalidTupleCentreIdException, UnreachableNodeException, OperationTimeOutException {
		return this.activateAdminRole(null);
	}

	@Override
	public ITucsonOperation activateAdminRole(Long l) throws InvalidVarNameException, TucsonOperationNotPossibleException, TucsonInvalidTupleCentreIdException, UnreachableNodeException, OperationTimeOutException {
		return this.activateRole("admin_role", l);
	}

	
	public MetaACCProxyAgentSide(Object id)
			throws TucsonInvalidAgentIdException {
		super(id);
	}
	
	public MetaACCProxyAgentSide(Object aid, String node, int port) throws TucsonInvalidAgentIdException{
		super(aid, node, port);
	}

	@Override
	public void add(RBAC rbac) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		
		this.add(rbac, null, node, port);
	}
	
	@Override
	public void add(RBAC rbac, Long l, String node, int port) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		if(rbac == null)
			return;
		
		TupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);//getTid(node, port);
		
		LogicTuple template = new LogicTuple("organisation_name",
				new Value(rbac.getOrgName()));
		ITucsonOperation op = out(tid, template, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("addRBAC: "+res);
		}
		else
			log("problem with addRBAC: "+rbac.getOrgName());
		
		for(Role role : rbac.getRoles()){
			this.addRole(role, l, tid);
		}
		
		for(Policy policy : rbac.getPolicies())
			this.addPolicy(policy, l, tid);
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
	
	
	private void addRole(Role role, Long l, TupleCentreId tid) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		
		LogicTuple roleTuple = new LogicTuple("role",
				new Value(role.getRoleName()),
				new Value("'ruolo " + role.getRoleName() + "'"));
		ITucsonOperation op = out(tid, roleTuple, l);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
			log("addRole: "+res);
		}
		else
			log("problem with addRole: "+role.getRoleName());
		
		this.addRolePolicy(role.getPolicy(), role.getRoleName(), l, tid);
	}
	
	private void addPolicy(Policy policy, Long l, TupleCentreId tid) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		String permissions = "";
		for(Permission perm : policy.getPermissions()){
			permissions += perm.getPermissionName() + ",";
		}
		permissions = permissions.substring(0, permissions.length() - 1);
		LogicTuple policyTuple = new LogicTuple("policy",
				new Value(policy.getPolicyName()),
				new Value("[" + permissions + "]"));
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
