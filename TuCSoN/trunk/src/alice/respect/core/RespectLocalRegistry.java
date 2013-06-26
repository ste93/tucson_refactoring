package alice.respect.core;

import java.util.HashMap;
import java.util.Map;

import alice.respect.api.IRespectTC;
import alice.respect.api.ITCRegistry;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InstantiationNotPossibleException;

public class RespectLocalRegistry implements ITCRegistry {

    /**
     * internal representation of the registry, keys are tuple centre id (as
     * String)
     */
    private final Map<String, IRespectTC> reg;

    public RespectLocalRegistry() {
        this.reg = new HashMap<String, IRespectTC>();
    }

    /**
     * Add a tuple centre to the registry. WARNING: If a tuple centre with the
     * same name yet exists the specified tuple centre is not added to the
     * registry.
     * 
     * @param tc
     *            the tuple centre to be added to the registry
     */
    public void addTC(final IRespectTC tc) {
        if (!this.reg.containsKey(tc.getId().getName())) {
            this.reg.put(tc.getId().getName(), tc);
        }
    }

    public Map<String, IRespectTC> getMap() {
        return this.reg;
    }

    public int getSize() {
        return this.reg.size();
    }

    public IRespectTC getTC(final TupleCentreId id)
            throws InstantiationNotPossibleException {
        if (!this.reg.containsKey(id.getName())) {
            throw new InstantiationNotPossibleException();
        }
        return this.reg.get(id.getName());
    }

}
