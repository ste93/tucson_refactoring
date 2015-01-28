package alice.tucson.service;

import java.util.ArrayList;
import java.util.List;

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
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
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
		//TupleCentreId tid2 = new TucsonTupleCentreId(tcDefault, "'"+node+"'", ""+port);
		
		Role newRole = TucsonACCTool.activateRole(agentAid.toString(), roleName, tid, internalACC);

		if(newRole != null){
			RoleACCProxyAgentSide newACC = new RoleACCProxyAgentSide(agentAid.toString(), node, port);
			newACC.setRole(newRole);
			/*return newACC;*/

			//EnhancedACC newACC2 = new ACCProxyAgentSide(agentAid, node, port);
			try {
				newACC.rdp(tid, LogicTuple.parse("ciao(A)"), (Long)null);
			} catch (InvalidLogicTupleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		
    	TucsonTupleCentreId tid = new TucsonTupleCentreId(tcOrg, "'"+node+"'", ""+port);
    	List<Policy> policies = TucsonACCTool.getPolicyList(agentAid.toString(), tid, internalACC);

		Policy policy = choosePolicy(policies, permissionsId);
		
		Role newRole = TucsonACCTool.activateRoleWithPolicy(agentAid.toString(), policy, tid, internalACC);
		RoleACCProxyAgentSide newACC = new RoleACCProxyAgentSide(agentAid, node, port);
		
		if(newACC != null)
			newACC.setRole(newRole);
		
		return newACC;
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
    
    protected void log(final String msg) {
        System.out.println("[ACCProxyAgentSide]: " + msg);
    }
}
