package alice.tucson.service;

import java.util.ArrayList;
import java.util.List;

import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Role;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class RoleACCProxyAgentSide extends ACCProxyAgentSide{
	
	private List<String> permissions;
	private Role role;
	
	public RoleACCProxyAgentSide(Object aid, String perm)
			throws TucsonInvalidAgentIdException {
		super(aid);
		
		permissions = new ArrayList<String>();
		addPermission(perm);
	}
	
	public RoleACCProxyAgentSide(Object aid, final String n, final int p) throws TucsonInvalidAgentIdException{
		super(aid, n, p);
		permissions = new ArrayList<String>();
		//addPermission(perm);
	}
	
	public void setRole(Role role){
		this.role = role;
	}

	private void addPermission(String perm){
		if(!permissions.contains(perm))
			permissions.add(perm);
	}
	
	private void checkPermission(String perm) throws TucsonOperationNotPossibleException{
		if(permissions.isEmpty() || !permissions.contains(perm))
			throw new TucsonOperationNotPossibleException();
	}
	
	@Override
    public ITucsonOperation rd(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
		
		checkPermission("rd");
		return this.rd(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rd(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
    	
    	checkPermission("rd");
    	return this.rd(tid, tuple, l);
    }
}
