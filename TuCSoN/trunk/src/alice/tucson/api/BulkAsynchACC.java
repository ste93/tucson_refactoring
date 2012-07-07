package alice.tucson.api;

import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

/**
 * BulkACC with asynchronous behavior. Bulk primitives are global primitives that allows to
 * collects all the tuples matching a given template in one shot.
 */
public interface BulkAsynchACC extends RootACC{

	ITucsonOperation in_all(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	ITucsonOperation rd_all(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
}
