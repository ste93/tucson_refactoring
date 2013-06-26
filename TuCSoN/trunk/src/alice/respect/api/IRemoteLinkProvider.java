package alice.respect.api;

import alice.tuplecentre.api.TupleCentreId;

public interface IRemoteLinkProvider {

    ILinkContext getRemoteLinkContext(TupleCentreId id);

}
