package alice.tucson.service;

import java.util.Map;

import alice.tucson.api.RootACC;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it)
 * 
 */
public class RootACCProxy implements RootACC {

	public void exit() throws TucsonOperationNotPossibleException {
		/*
         * 
         */
	}

	@Override
	public Map<Long, TucsonOperation> getPendingOperationsMap() {
		return null;
	}

}
