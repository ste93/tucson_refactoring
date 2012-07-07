package alice.tucson.api;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

public interface RootACC {
	
	/**
	 * 
	 * @throws TucsonOperationNotPossibleException
	 */
	void exit() throws TucsonOperationNotPossibleException;

}
