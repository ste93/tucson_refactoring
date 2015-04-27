package alice.tucson.api;

import java.security.NoSuchAlgorithmException;
import alice.logictuple.exceptions.InvalidTupleArgumentException;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.respect.api.exceptions.OperationNotAllowedException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.AuthorizedAgent;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.RBAC;
import alice.tucson.rbac.Role;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public interface MetaACC extends EnhancedACC {

    void add(AuthorizedAgent agent) throws NoSuchAlgorithmException,
    TucsonOperationNotPossibleException, UnreachableNodeException,
    OperationTimeOutException;

    void add(Policy policy) throws InvalidVarNameException,
    InvalidTupleArgumentException, TucsonOperationNotPossibleException,
    UnreachableNodeException, OperationTimeOutException;

    void add(RBAC rbac) throws TucsonInvalidTupleCentreIdException,
    TucsonOperationNotPossibleException, UnreachableNodeException,
    OperationTimeOutException, InvalidVarNameException,
    InvalidTupleArgumentException, OperationNotAllowedException,
    NoSuchAlgorithmException;

    void add(RBAC rbac, Long l, String node, int port)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException, InvalidVarNameException,
            InvalidTupleArgumentException, OperationNotAllowedException,
            NoSuchAlgorithmException;

    void add(Role role) throws InvalidVarNameException,
    NoSuchAlgorithmException, TucsonOperationNotPossibleException,
    UnreachableNodeException, OperationTimeOutException;

    void addPermissionToPolicy(Permission permission, String policyName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    void remove(String agentName);

    void removePolicy(String policyName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    void removeRBAC() throws OperationNotAllowedException,
    TucsonInvalidTupleCentreIdException,
    TucsonOperationNotPossibleException, UnreachableNodeException,
    OperationTimeOutException, InvalidVarNameException;

    void removeRBAC(Long l, String node, int port)
            throws OperationNotAllowedException,
            TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException, InvalidVarNameException;

    void removeRole(String roleName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    void setBaseAgentClass(String newBaseAgentClass)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    void setRoleAgentClass(String roleName, String agentClass)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    void setRolePolicy(String roleName, String policyName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;
}
