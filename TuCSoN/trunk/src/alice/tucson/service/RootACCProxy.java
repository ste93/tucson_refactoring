package alice.tucson.service;

import java.util.Map;
import alice.tucson.api.RootACC;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it)
 * 
 */
public class RootACCProxy implements RootACC {
    @Override
    public void exit() {
        /*
         * 
         */
    }

    @Override
    public Map<Long, TucsonOperation> getPendingOperationsMap() {
        return null;
    }
}
