package alice.respect.api;

import alice.tuplecentre.api.TupleCentreId;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 28/giu/2013
 * 
 */
public interface IRemoteLinkProvider {

    /**
     * 
     * @param id
     *            the target tuple centre to link with
     * @return the linking context toward the target tuple centre
     */
    ILinkContext getRemoteLinkContext(TupleCentreId id);

}
