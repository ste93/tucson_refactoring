package alice.tucson.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.RootACC;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it)
 * 
 */
public class RootACCProxy implements RootACC {
    @Override
    public void exit() {
        /*
         * 
         */
    }

    @Override
    public Map<Long, TucsonOperation> getPendingOperationsMap() {
        return null;
    }

	@Override
	public void enterACC() throws UnreachableNodeException,
			TucsonOperationNotPossibleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPassword(String password) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUsername(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isACCEntered() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UUID getUUID() {
		// TODO Auto-generated method stub
		return null;
	}
}
