package alice.tucson.service;

import alice.respect.api.ILinkContext;
import alice.respect.api.IRemoteLinkProvider;

import alice.tuplecentre.api.TupleCentreId;

import java.util.HashMap;

/**
 * 
 */
public class RemoteLinkProvider implements IRemoteLinkProvider{
	
	private static HashMap<String, InterTupleCentreACCProvider> remoteRegistry;

	public RemoteLinkProvider(){
		remoteRegistry = new HashMap<String, InterTupleCentreACCProvider>();
	}

	public ILinkContext getRemoteLinkContext(TupleCentreId id){
		// id il tuplecentre target (nome completo xche' toString?)
		InterTupleCentreACCProvider helper = remoteRegistry.get(id.toString());
		if(helper == null){
			helper = new InterTupleCentreACCProvider(id);
			remoteRegistry.put(id.toString(), helper);
		}
		return helper;
	}
	
}
