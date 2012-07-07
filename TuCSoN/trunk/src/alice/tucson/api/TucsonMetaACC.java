/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tucson.api;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.UnknownACCException;
import alice.tucson.service.ACCProxyAgentSide;

/**
 * TuCSoN Meta Agent Coordination Context.
 * It is exploited by TuCSoN agents to obtain an ACC with
 * which to interact with the TuCSoN node.
 */
public class TucsonMetaACC{
	
	/**
	 * Gets the available most-comprehensive ACC from the TuCSoN Node Service
	 * active on the specified pair node:port where node is the ip address.
	 * 
	 * @param agentId Who demand for the ACC
	 * @param node The ip address of the target TuCSoN Node Service
	 * @param port The listening port of the target TuCSoN Node Service
	 * 
	 * @return The DefaultACC (which is the most powerful at the moment)
	 * @throws UnknownACCException 
	 */
	public static EnhancedACC getContext(Object agentId, String node, int port){
		EnhancedACC acc = null;
		try {
			acc = new ACCProxyAgentSide(agentId, node, port);
			((TucsonAgentId)agentId).assignUUID();
		} catch (TucsonInvalidAgentIdException e) {
			System.err.println("[Tucson-MetaACC]: " + e);
			e.printStackTrace();
			return null;
		}
		return acc;
	}
	
	/**
	 * Gets the available most-comprehensive ACC from the TuCSoN Node Service
	 * active on the default host ("localhost") on the default port (20504).
	 * 
	 * @param agentId Who demand for the ACC
	 * 
	 * @return The DefaultACC (which is the most powerful at the moment)
	 * @throws UnknownACCException 
	 */
	public static EnhancedACC getContext(Object agentId){
		return getContext(agentId, "localhost", 20504);
	}

	public static String getVersion(){
		return "TuCSoN-1.10.0.0205";
	}

}
