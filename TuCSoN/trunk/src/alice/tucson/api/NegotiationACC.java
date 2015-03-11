package alice.tucson.api;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.exceptions.AgentNotAllowedException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Role;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public interface NegotiationACC {
	
	EnhancedACC activateRole(String roleName) throws TucsonOperationNotPossibleException, InvalidVarNameException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException, AgentNotAllowedException;
	EnhancedACC activateRole(String roleName, Long l) throws TucsonOperationNotPossibleException, InvalidVarNameException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException, AgentNotAllowedException;

	EnhancedACC activateRoleWithPermission(List<String> permissionsId) throws InvalidVarNameException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException, AgentNotAllowedException;
	EnhancedACC activateRoleWithPermission(List<String> permissionsId, Long l) throws InvalidVarNameException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException, AgentNotAllowedException;

	EnhancedACC activateDefaultRole() throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException;

	List<Role> listActivableRoles() throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException;

	boolean login(String username, String password) throws NoSuchAlgorithmException, InvalidVarNameException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;

	
}
