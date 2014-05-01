/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.api;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.service.ACCProxyAgentSide;

/**
 * TuCSoN Meta Agent Coordination Context. It is exploited by TuCSoN agents to
 * obtain an ACC with which to interact with the TuCSoN node.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * 
 */
public final class TucsonMetaACC {
    private static final int DEFAULT_PORT = 20504;

    /**
     * Gets the available most-comprehensive ACC from the TuCSoN Node Service
     * active on the default host ("localhost") on the default port (20504).
     * 
     * @param aid
     *            Who demand for the ACC
     * 
     * @return The DefaultACC (which is the most powerful at the moment)
     */
    public static EnhancedACC getContext(final TucsonAgentId aid) {
        return TucsonMetaACC.getContext(aid, "localhost",
                TucsonMetaACC.DEFAULT_PORT);
    }

    /**
     * Gets the available most-comprehensive ACC from the TuCSoN Node Service
     * active on the specified pair node:port where node is the ip address.
     * 
     * @param aid
     *            Who demand for the ACC
     * @param netid
     *            The ip address of the target TuCSoN Node Service
     * @param portno
     *            The listening port of the target TuCSoN Node Service
     * 
     * @return The DefaultACC (which is the most powerful at the moment)
     */
    public static EnhancedACC getContext(final TucsonAgentId aid,
            final String netid, final int portno) {
        EnhancedACC acc = null;
        try {
            acc = new ACCProxyAgentSide(aid.toString(), netid, portno);
            aid.assignUUID();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
            return null;
        }
        return acc;
    }

    /**
     * 
     * @return the current version of the TuCSoN Coordination Infrastructure
     */
    public static String getVersion() {
        return "TuCSoN-1.10.7.0208";
    }

    private TucsonMetaACC() {
        /*
         * 
         */
    }
}
