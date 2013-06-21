package alice.respect.api;

import alice.tuplecentre.api.TupleCentreId;

public interface IRemoteLinkProvider {

    public ILinkContext getRemoteLinkContext(TupleCentreId id);

}
