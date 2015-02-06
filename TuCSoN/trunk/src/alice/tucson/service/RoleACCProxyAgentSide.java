package alice.tucson.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Role;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class RoleACCProxyAgentSide extends ACCProxyAgentSide{
	
	private List<String> permissions;
	private Role role;
	
	public RoleACCProxyAgentSide(Object aid)
			throws TucsonInvalidAgentIdException {
		super(aid);
		
		permissions = new ArrayList<String>();
	}
	
	public RoleACCProxyAgentSide(Object aid, final String n, final int p) throws TucsonInvalidAgentIdException{
		super(aid, n, p);
		permissions = new ArrayList<String>();
	}
	
	public void setRole(Role role){
		this.role = role;
		setPermissions();
	}
	
	public UUID getUUID(){
		return this.executor.agentUUID;
	}
	
	private void setPermissions(){
		permissions = new ArrayList<String>();
		List<Permission> perms = role.getPolicy().getPermissions();
		for(Permission perm : perms){
			permissions.add(perm.getPermissionName());
		}
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
		return super.rd(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rd(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
    	
    	checkPermission("rd");
    	return super.rd(tid, tuple, l);
    }
    
    @Override
    public ITucsonOperation rdp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
    	
    	checkPermission("rdp");
    	return super.rdp(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rdp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
    	
    	checkPermission("rdp");
        return super.rdp(tid, tuple, l);
    }
    
    @Override
    public ITucsonOperation in(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        
    	checkPermission("in");
    	return super.in(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation in(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
    	
    	checkPermission("in");
    	return super.in(tid, tuple, l);
    }
    
    @Override
    public ITucsonOperation inp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
    	
        checkPermission("inp");
        return super.inp(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation inp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
    	
        checkPermission("inp");
        return super.inp(tid, tuple, l);
    }
    
    @Override
    public ITucsonOperation out(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
    	
    	checkPermission("out");
    	return super.out(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation out(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
    	checkPermission("out");
    	return super.out(tid, tuple, l);
    }
}
