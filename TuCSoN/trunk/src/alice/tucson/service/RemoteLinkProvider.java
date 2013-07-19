package alice.tucson.service;

import java.util.HashMap;
import java.util.Map;

import alice.respect.api.ILinkContext;
import alice.respect.api.IRemoteLinkProvider;
import alice.tuplecentre.api.TupleCentreId;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 17/lug/2013
 * 
 */
public class RemoteLinkProvider implements IRemoteLinkProvider {

    // FIXME How to fix this?
    private static Map<String, InterTupleCentreACCProvider> remoteRegistry;

    /**
     * 
     */
    public RemoteLinkProvider() {
        RemoteLinkProvider.remoteRegistry =
                new HashMap<String, InterTupleCentreACCProvider>();
    }

    public ILinkContext getRemoteLinkContext(final TupleCentreId id) {
        // id è il tuplecentre target (nome completo xchè toString?)
        InterTupleCentreACCProvider helper =
                RemoteLinkProvider.remoteRegistry.get(id.toString());
        if (helper == null) {
            helper = new InterTupleCentreACCProvider(id);
            RemoteLinkProvider.remoteRegistry.put(id.toString(), helper);
        }
        return helper;
    }

}
