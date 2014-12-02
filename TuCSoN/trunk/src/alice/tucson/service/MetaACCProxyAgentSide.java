package alice.tucson.service;


import alice.tucson.api.MetaACC;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.rbac.RBAC;


public class MetaACCProxyAgentSide extends ACCProxyAgentSide implements MetaACC{

	public MetaACCProxyAgentSide(String id)
			throws TucsonInvalidAgentIdException {
		super(id);
	}

	@Override
	public void add(RBAC rbac) {
		if(rbac == null)
			return;
		
	}

	@Override
	public void remove(RBAC rbac) {
		// TODO Auto-generated method stub
		
	}

	
}
