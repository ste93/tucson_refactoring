package alice.tucson.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.NegotiationACC;
import alice.tucson.api.RootACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonPermission;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRole;
import alice.tucson.service.tools.TucsonACCTool;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Term;

public class NegotiationACCProxyAgentSide implements NegotiationACC{

	private EnhancedACC internalACC;
	protected static final String tcOrg = "'$ORG'";
	protected static final String tcAgent = "negotAgent";
	protected String node;
	protected int port;
	private Object agentAid;
	
	private String agentClass;
	
	private final int OKNUMBER = 1;
	
	public NegotiationACCProxyAgentSide(Object aid) throws TucsonInvalidAgentIdException{
		this(aid, "localhost", 20504);
	}
	
	public NegotiationACCProxyAgentSide(Object aid, String node, int port) throws TucsonInvalidAgentIdException{
		internalACC = new ACCProxyAgentSide(tcAgent, node, port);
		this.node = node;
		this.port = port;
		this.agentAid = aid;
		setDefaultAgentClass();
	}

	@Override
	public EnhancedACC activateRole(String roleName)
			throws TucsonOperationNotPossibleException,
			TucsonInvalidTupleCentreIdException, InvalidVarNameException,
			UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException {
		return this.activateRole(roleName, null);
	}

	@Override
	public EnhancedACC activateRole(String roleName, Long l)
			throws TucsonOperationNotPossibleException,
			TucsonInvalidTupleCentreIdException, InvalidVarNameException,
			UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException {
		
		TupleCentreId tid = new TucsonTupleCentreId(tcOrg,"'"+node+"'", ""+port);
		
		if(!isRBACInstalled(tid)){
			return new ACCProxyAgentSide(agentAid, node, port);
		}
		
		RoleACCProxyAgentSide newACC = new RoleACCProxyAgentSide(agentAid.toString(), node, port);
		Role newRole = TucsonACCTool.activateRole(agentAid.toString(), newACC.getUUID(), roleName, tid, internalACC);

		if(newRole != null){			
			newACC.setRole(newRole);
			return newACC;
		} else
			return null;
	}
	
	@Override
	public EnhancedACC activateRoleWithPermission(List<String> permissionsId) throws InvalidVarNameException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException {
		return this.activateRoleWithPermission(permissionsId, null);
	}
    
	/*
	 * 	Il metodo cerca di attivare un ruolo con i privilegi minimi necessari.
	 * 	1 - Ottiene la lista delle policy 
	 *  2 - Viene scelta la policy migliore.
	 *  3 - Viene ricercato il ruolo che detiene la policy, ed attivato.
	 */
    @Override
	public synchronized EnhancedACC activateRoleWithPermission(List<String> permissionsId, Long l) throws InvalidVarNameException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException {
		
    	TupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
    	
    	if(!isRBACInstalled(tid))
    		return new ACCProxyAgentSide(agentAid, node, port);
    	
    	List<Policy> policies = TucsonACCTool.getPolicyList(agentAid.toString(), tid, internalACC);

		Policy policy = choosePolicy(policies, permissionsId);
		
		RoleACCProxyAgentSide newACC = new RoleACCProxyAgentSide(agentAid, node, port);
		Role newRole = TucsonACCTool.activateRoleWithPolicy(agentAid.toString(), newACC.getUUID(), policy, tid, internalACC);
		
		if(newACC != null)
			newACC.setRole(newRole);
		
		return newACC;
	}
    
    /*
     * 	Attiva un ACC fuori dalla struttura RBAC nel caso questa non sia stata installata nel nodo.
     */
    @Override
	public EnhancedACC activateDefaultRole() throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException {
    	TupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
    	if(!isRBACInstalled(tid)){
    		return new ACCProxyAgentSide(agentAid, node, port);
    	}
    	return null;
	}
    
    private Policy choosePolicy(List<Policy> policies, List<String> permissionsId){
    	Policy bestPolicy = null;
    	int bestPermissionsArity = 10000;
    	
    	for(Policy pol : policies)
    		if(pol.hasPermissions(permissionsId))
    			if(pol.getPermissions().size() < bestPermissionsArity){
    				bestPermissionsArity = pol.getPermissions().size();
    				bestPolicy = pol;
    			}
	
    	return bestPolicy;    	
    }
    
    private boolean isRBACInstalled(TupleCentreId tid) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
    	LogicTuple rbacInstalled = null;
		try {
			rbacInstalled = new LogicTuple("rbac_installed", new Var("Result"));
		} catch (InvalidVarNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ITucsonOperation op = internalACC.rd(tid, rbacInstalled, (Long)null);
    	if(op.isResultSuccess()){
    		LogicTuple res = op.getLogicTupleResult();
    		if(res.getArg(0).toString().equals("yes"))
    			return true;
    	}
    	return false;
    }
    
    protected void log(final String msg) {
        System.out.println("[ACCProxyAgentSide]: " + msg);
    }

	@Override
	public void listAllRoles() throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException {
		TupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
		ITucsonOperation op = internalACC.inp(tid, new LogicTuple("role_list_request", new Var("Result")), (Long)null);
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
		}
	}

	@Override
	public boolean login(String username, String password) throws NoSuchAlgorithmException, TucsonInvalidTupleCentreIdException, InvalidVarNameException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException {
		TupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
		LogicTuple loginTuple = new LogicTuple("login_request",
				new Value(username+":"+TucsonACCTool.encrypt(password)),
				new Var("Result"));
		
		ITucsonOperation op = internalACC.inp(tid, loginTuple, (Long)null);
		if(op.isResultSuccess()){
			TupleArgument response = op.getLogicTupleResult().getArg(1);
			this.setAgentClass(response.getArg(0).toString());
			if(response.getName().equals("ok")){
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void setAgentClass(String agentClass) {
		this.agentClass = agentClass;
	}

	@Override
	public String getAgentClass() {
		return this.agentClass;
	}

	private void setDefaultAgentClass() {
		try {
			TupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
			LogicTuple defaultClassTuple = new LogicTuple("get_default_agent_class", new Var("Response"));
			ITucsonOperation op = internalACC.inp(tid, defaultClassTuple, (Long)null);
			if(op.isResultSuccess()){
				String defaultClass = op.getLogicTupleResult().getArg(0).toString();
				this.setAgentClass(defaultClass);
			}
		} catch (TucsonInvalidTupleCentreIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidVarNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
