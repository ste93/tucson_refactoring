package alice.tucson.api;

import java.util.List;

import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public interface NegotiationACC {

	RootACC activateRole(String roleId) throws TucsonOperationNotPossibleException, TucsonInvalidTupleCentreIdException, InvalidVarNameException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException;
	RootACC activateRole(String roleId, Long l) throws TucsonOperationNotPossibleException, TucsonInvalidTupleCentreIdException, InvalidVarNameException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException;

	RootACC activateRoleWithPermission(List<String> permissionsId) throws InvalidVarNameException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException;
	RootACC activateRoleWithPermission(List<String> permissionsId, Long l) throws InvalidVarNameException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, TucsonInvalidAgentIdException;

}
