package alice.respect.api;

import java.util.HashMap;

import alice.respect.api.exceptions.InstantiationNotPossibleException;

public interface ITCRegistry {

    public void addTC(IRespectTC tc);

    public HashMap<String, ? extends IRespectTC> getHashmap();

    public IRespectTC getTC(TupleCentreId id)
            throws InstantiationNotPossibleException;

}
