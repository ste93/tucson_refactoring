package alice.tucson.service.tools;

import java.util.ArrayList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRole;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public final class TucsonACCTool {

	public static Role activateRole(String agentAid, String roleName, TupleCentreId tid, EnhancedACC acc){
		Role newRole = null;
		try {
			LogicTuple template = new LogicTuple("role_activation_request",
					new Value(agentAid.toString()),
					new Value(roleName),
					new Var("Result"));
			ITucsonOperation op = acc.inp(tid, template, (Long)null);
			if(op.isResultSuccess()){
				newRole = new TucsonRole(roleName);
			}
		} catch (InvalidVarNameException | TucsonOperationNotPossibleException | UnreachableNodeException | OperationTimeOutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newRole;
	}
	
	public static Role activateRoleWithPolicy(String agentAid, Policy policy, TupleCentreId tid, EnhancedACC acc){
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
						new Value(roleName),
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
	
	public static List<Policy> getPolicyList(String agentAid, TucsonTupleCentreId tid, EnhancedACC acc){
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
}
