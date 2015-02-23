package alice.tucson.api;
import java.security.NoSuchAlgorithmException;

import alice.logictuple.exceptions.InvalidTupleArgumentException;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.respect.api.exceptions.OperationNotAllowedException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.RBAC;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public interface MetaACC extends EnhancedACC {

	void add(RBAC rbac) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException, OperationNotAllowedException, NoSuchAlgorithmException ;
	void add(RBAC rbac, Long l, String node, int port) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException, InvalidTupleArgumentException, OperationNotAllowedException, NoSuchAlgorithmException ;
	void remove(RBAC rbac) throws OperationNotAllowedException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException;
	void remove(RBAC rbac, Long l, String node, int port) throws OperationNotAllowedException, TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidVarNameException;
}
