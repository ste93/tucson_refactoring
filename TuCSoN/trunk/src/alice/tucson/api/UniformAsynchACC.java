package alice.tucson.api;

import alice.logictuple.LogicTuple;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

public interface UniformAsynchACC extends RootACC{
	
	ITucsonOperation uin(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	ITucsonOperation urd(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	ITucsonOperation uinp(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	ITucsonOperation urdp(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException, UnreachableNodeException;

}
