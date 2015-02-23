package alice.tucson.service.tools;

import java.security.MessageDigest;
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
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRole;
import alice.tucson.service.ACCProxyAgentSide;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public final class TucsonACCTool {

	public static boolean activateContext(String agentAid, UUID agentUUID, TupleCentreId tid, EnhancedACC acc){
		try {
			LogicTuple template = new LogicTuple("context_request",
					new Value(agentAid),
					new Var("Result"),
					new Value(agentUUID.toString()));
			ITucsonOperation op = acc.inp(tid, template, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				if(res!=null && res.getArg(1).getName().equalsIgnoreCase("ok")){
					return true;
				} else if(res!=null && res.getArg(1).getName().equalsIgnoreCase("failed") && 
						res.getArg(1).getArg(0).toString().equalsIgnoreCase("agent_already_present"))
					return true;
			}
		} catch (InvalidVarNameException | TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static Role activateRole(String agentAid, UUID accUUID, String roleName, TupleCentreId tid, EnhancedACC acc){
		if(!activateContext(agentAid, accUUID, tid, acc))
			return null;
		
		Role newRole = null;
		try {
			LogicTuple template = new LogicTuple("role_activation_request",
					new Value(agentAid.toString()),
					new Value(accUUID.toString()),
					new Value(roleName),
					new Var("Result"));
			ITucsonOperation op = acc.inp(tid, template, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				if(res!=null && res.getArg(3).getName().equalsIgnoreCase("ok")){
					String policyName = res.getArg(3).getArg(0).toString();
					TupleArgument[] permissionsList = res.getArg(3).getArg(1).toArray();
					Policy newPolicy = TucsonPolicy.createPolicy(policyName, permissionsList);
					newRole = new TucsonRole(roleName);
					newRole.setPolicy(newPolicy);
				}
			}
		} catch (InvalidVarNameException | TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) {
			e.printStackTrace();
		}
		
		return newRole;
	}
	
	public static Role activateRoleWithPolicy(String agentAid, UUID accUUID, Policy policy, TupleCentreId tid, EnhancedACC acc){
		if(!activateContext(agentAid, accUUID, tid, acc))
			return null;
		
		Role newRole = null;
		
		try {
			LogicTuple rolePolicyTemplate = new LogicTuple("policy_role_request",
				new Value(policy.getPolicyName()),
				new Var("Result"));
			ITucsonOperation op = acc.inp(tid, rolePolicyTemplate, (Long)null);
			if(op.isResultSuccess()){
				LogicTuple res = op.getLogicTupleResult();
				String roleName = res.getArg(1).toString();
				
				LogicTuple template = new LogicTuple("role_activation_request",
						new Value(agentAid.toString()),
						new Value(accUUID.toString()),
						new Value(roleName),
						new Var("Credenziali"),
						new Var("Result"));
				op = acc.inp(tid, template, (Long)null);
				if(op.isResultSuccess()){
					newRole = new TucsonRole(roleName);
					newRole.setPolicy(policy);
				}
			}	
		} catch (InvalidVarNameException | TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newRole;
	}
	
	public static List<Policy> getPolicyList(String agentAid, TupleCentreId tid, EnhancedACC acc){
		List<Policy> policies = new ArrayList<Policy>();
		try {
			LogicTuple policyListTuple = new LogicTuple("policy_list_request",
					new Value(agentAid.toString()),
					new Var("Result"));
			
			ITucsonOperation op = acc.inp(tid, policyListTuple, (Long)null);
			
			if(op.isResultSuccess()){
	    		LogicTuple res = op.getLogicTupleResult();
	    		
	    		TupleArgument[] policiesList = res.getArg(1).getArg(0).toArray();
	        	for(TupleArgument term : policiesList){
	        		TupleArgument[] permissionsTuples = term.getArg(1).toArray();

	        		Policy newPolicy = TucsonPolicy.createPolicy(term.getArg(0).toString(), permissionsTuples);
	        		policies.add(newPolicy);
	        	}
			}
		} catch (InvalidVarNameException | TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) {
			e.printStackTrace();
		}
		
		return policies;
	}
	
	public static String encrypt(String password) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());
		
		byte byteData[] = md.digest();
		
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}
}
