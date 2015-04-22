package alice.respect.core;

import java.util.HashMap;
import java.util.Map;
import alice.respect.api.IRespectTC;
import alice.respect.api.ITCRegistry;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InstantiationNotPossibleException;

/**
 * 
 * @author Alessandro Ricci
 * 
 */
public class RespectLocalRegistry implements ITCRegistry {

    /**
     * internal representation of the registry, keys are tuple centre id (as
     * String)
     */
    private final Map<String, IRespectTC> reg;

    /**
     * 
     */
    public RespectLocalRegistry() {
        this.reg = new HashMap<String, IRespectTC>();
    }

    @Override
    public void addTC(final IRespectTC tc) {
        if (!this.reg.containsKey(tc.getId().toString())) {
            this.reg.put(tc.getId().toString(), tc);
        }
    }

    @Override
    public Map<String, IRespectTC> getMap() {
        return this.reg;
    }

    /**
     * 
     * @return the size of the ReSpecT local registry
     */
    public int getSize() {
        return this.reg.size();
    }

    @Override
    public IRespectTC getTC(final TupleCentreId id)
            throws InstantiationNotPossibleException {
        if (!this.reg.containsKey(id.toString())) {
            throw new InstantiationNotPossibleException("The string "
                    + id.toString() + " is not contained in the registry");
        }
        System.out.println("....[RespectLocalRegistry]: Got "
                + this.reg.get(id.toString()).getId());
        return this.reg.get(id.toString());
    }
}
