package alice.tucson.api;

import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

import alice.tuplecentre.core.OperationTimeOutException;

public interface BulkSynchACC extends RootACC{

	ITucsonOperation in_all(Object tid, LogicTuple tuple, Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	ITucsonOperation rd_all(Object tid, LogicTuple tuple, Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
}
