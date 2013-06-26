package alice.respect.api;

import java.util.Map;

import alice.respect.api.exceptions.InstantiationNotPossibleException;

public interface ITCRegistry {

    void addTC(IRespectTC tc);

    Map<String, ? extends IRespectTC> getMap();

    IRespectTC getTC(TupleCentreId id) throws InstantiationNotPossibleException;

}
