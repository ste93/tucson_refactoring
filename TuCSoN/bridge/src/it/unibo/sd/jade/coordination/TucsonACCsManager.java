/*
 * Copyright 1999-2014 Alma Mater Studiorum - Universita' di Bologna
 *
 * This file is part of TuCSoN4JADE <http://tucson4jade.apice.unibo.it>.
 *
 *    TuCSoN4JADE is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published
 *    by the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    TuCSoN4JADE is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with TuCSoN4JADE.  If not, see
 *    <https://www.gnu.org/licenses/lgpl.html>.
 *
 */
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
