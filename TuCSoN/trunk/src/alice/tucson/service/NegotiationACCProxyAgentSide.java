package alice.tucson.service;

import java.util.ArrayList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
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
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRole;
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
	
	private final int OKNUMBER = 1;
	
	public NegotiationACCProxyAgentSide(Object aid) throws TucsonInvalidAgentIdException{
		this(aid, "localhost", 20504);
	}
	
	public NegotiationACCProxyAgentSide(Object aid, String node, int port) throws TucsonInvalidAgentIdException{
		internalACC = new ACCProxyAgentSide(tcAgent, node, port);
		this.node = node;
		this.port = port;
		this.agentAid = aid;
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
		
		LogicTuple template = new LogicTuple("role_activation_request",
    			new Value(tcAgent),
    			new Value(roleName),
    			new Var("Result"));
		
		ITucsonOperation op = internalACC.inp(tid, template, l);
		
		if(op.isResultSuccess()){
			LogicTuple res = op.getLogicTupleResult();
    		if(res!=null && res.getArg(2).getName().equalsIgnoreCase("ok")){
    			RoleACCProxyAgentSide newACC = new RoleACCProxyAgentSide(agentAid, node, port);
    			return newACC;
    			/*Role roleReceived = createRole(res);
    			if(roleReceived != null){
    				profile.addRole(roleReceived);
    				log("Activated the role: " + res);
    			} else {
    				log("Activation request failed: " + res);
    			}*/
    		} else {
				//log("Activation request failed: " + res);
				return null;
			}
    	} else{
			//log("Activation request failed: " + roleName);
			return null;
		}
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
		
    	TucsonTupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
    	
    	LogicTuple permissionTemplate = new LogicTuple("policy_list_request",
    			new Value(agentAid.toString()),
    			//new Value(permissionId),
    			new Var("Result"));
    	
    	ITucsonOperation op = internalACC.inp(tid, permissionTemplate, l);
    	
    	if(op.isResultSuccess()){
    		LogicTuple res = op.getLogicTupleResult();
    		if(res!=null && res.getArg(OKNUMBER).getName().equalsIgnoreCase("ok")){
    			Policy policy = choosePolicy(res, permissionsId);
    			LogicTuple rolePolicyTemplate = new LogicTuple("policy_role_request",
    					new Value(policy.getPolicyName()),
    					new Var("Result"));
    			
    			ITucsonOperation op2 = internalACC.inp(tid, rolePolicyTemplate, l);
    			
    			if(op2.isResultSuccess()){
    				res = op2.getLogicTupleResult();
    				String roleName = res.getArg(1).toString();
    				LogicTuple template = new LogicTuple("role_activation_request",
    		    			new Value(agentAid.toString()),
    		    			new Value(roleName),
    		    			new Var("Result"));
    				op2 = internalACC.inp(tid, template, l);
    				if(op2.isResultSuccess()){
    					Role newRole = new TucsonRole(roleName);
        				newRole.setPolicy(policy);
        				RoleACCProxyAgentSide newACC = new RoleACCProxyAgentSide(agentAid, node, port);
        				newACC.setRole(newRole);
        				return newACC;
    				}
    			}
    		} else {
				log("Activation request failed: " + res);
			}
    	} else{
			log("Activation request failed for permission: " + permissionsId.toString());
		}
    	
    	return null;
	}
    
    
    private Policy choosePolicy(LogicTuple res, List<String> permissionsId){
    	TupleArgument[] policiesList = res.getArg(OKNUMBER).getArg(0).toArray();
    	List<Policy> policies = new ArrayList<Policy>();
    	for(TupleArgument term : policiesList){
    		TupleArgument[] permissionsTuples = term.getArg(1).toArray();

    		Policy newPolicy = TucsonPolicy.createPolicy(term.getArg(0).toString(), permissionsTuples);
    		policies.add(newPolicy);
    		/*String arg1 = term.getArg(0).toString();
    		String permissions = ((Struct)term.getArg(1)).getArg(0).toString();
    		Struct structPermissions = (Struct) Term.createTerm(permissions);*/
    	}
    	
    	Policy bestPolicy = null;
    	int bestPermissionsArity = 10000;
    	
    	for(Policy pol : policies){
    		if(pol.hasPermissions(permissionsId)){
    			if(pol.getPermissions().size() < bestPermissionsArity){
    				bestPermissionsArity = pol.getPermissions().size();
    				bestPolicy = pol;
    			}
    		}
    	}
    	
    	return bestPolicy;    	
    }
    
    protected void log(final String msg) {
        System.out.println("[ACCProxyAgentSide]: " + msg);
    }
}
