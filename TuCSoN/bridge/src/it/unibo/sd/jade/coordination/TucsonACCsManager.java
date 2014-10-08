package it.unibo.sd.jade.coordination;

import jade.core.AID;
import jade.core.Agent;
import java.util.LinkedHashMap;
import java.util.Map;
import alice.tucson.api.EnhancedACC;

/**
 * TucsonACCsManager. Responsible for tracking JADE agent - TuCSoN ACC mappings.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 */
public enum TucsonACCsManager {
    /** the singleton instance of this ACCs manager */
    INSTANCE;
    private Map<AID, EnhancedACC> mAccs;

    private TucsonACCsManager() {
        this.mAccs = new LinkedHashMap<AID, EnhancedACC>();
    }

    /**
     * 
     * @param agent
     *            the JADE agent holding the TuCSoN ACC
     * @param acc
     *            the ACC held by the JADE agent
     */
    public void addAcc(final Agent agent, final EnhancedACC acc) {
        this.mAccs.put(agent.getAID(), acc);
    }

    /**
     * 
     * @param agent
     *            the JADE agent willing to get an ACC
     * @return the ACC released
     */
    public EnhancedACC getAcc(final Agent agent) {
        return this.mAccs.get(agent.getAID());
    }

    /**
     * 
     * @param agent
     *            the JADE agent querying about held ACCs
     * @return wether the given JADE agent currently holds any ACC
     */
    public boolean hasAcc(final Agent agent) {
        return this.mAccs.containsKey(agent.getAID());
    }

    /**
     * 
     * @param agent
     *            the JADE agent whose ACCs should be removed
     */
    public void removeAcc(final Agent agent) {
        this.mAccs.remove(agent.getAID());
    }
}
