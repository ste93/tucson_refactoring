package alice.tucson.api;

import alice.logictuple.LogicTuple;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.core.OperationTimeOutException;

public interface UniformSynchACC extends RootACC{

	ITucsonOperation uin(Object tid, LogicTuple tuple, Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	ITucsonOperation urd(Object tid, LogicTuple tuple, Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	ITucsonOperation uinp(Object tid, LogicTuple tuple, Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	ITucsonOperation urdp(Object tid, LogicTuple tuple, Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
}
