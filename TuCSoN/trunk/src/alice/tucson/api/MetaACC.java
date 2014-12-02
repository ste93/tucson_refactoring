package alice.tucson.api;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.RBAC;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public interface MetaACC extends EnhancedACC {

	void add(RBAC rbac) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	void add(RBAC rbac, Long l, String node, int port) throws TucsonInvalidTupleCentreIdException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	void remove(RBAC rbac);
	void remove(RBAC rbac, Long l, String node, int port);
}
