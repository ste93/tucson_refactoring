/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.respect.api;

import alice.respect.api.exceptions.InvalidAgentIdException;
import alice.respect.core.AgentIdOperatorManager;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tuprolog.InvalidTermException;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * Agent identifier.
 * 
 * @author aricci
 */
public class AgentId implements alice.tuplecentre.api.AgentId,
        java.io.Serializable {

    private static AgentIdOperatorManager opManager =
            new AgentIdOperatorManager();
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    protected Term id;

    /**
     * Constructs an agent identifier
     * 
     * The Agent identifier must be a ground valid logic term
     * 
     * @param sid
     *            is the string representation of the identifier
     * @throws InvalidAgentIdException
     *             if it is not a valid identifier
     */
    public AgentId(final String sid) throws InvalidAgentIdException {
        if (sid.indexOf(':') != -1) {
            sid.substring(0, sid.indexOf(':'));
        }
        try {
            this.id = Term.createTerm(sid, AgentId.opManager);
        } catch (final InvalidTermException e) {
            // TODO Properly handle Exception
            throw new InvalidAgentIdException();
        }
        if (!this.id.isGround()) {
            throw new InvalidAgentIdException();
        }
    }

    public AgentId(final String name, final TucsonTupleCentreId tcId) {
        this.id = new Struct(name, tcId.toTerm());
    }

    /**
     * Constructs an agent identifier
     * 
     * The Agent identifier must be a ground logic term
     * 
     * @param tid
     *            the identifier as tuProlog term
     * @throws InvalidAgentIdException
     *             if it is not a valid identifier
     */
    public AgentId(final Term tid) throws InvalidAgentIdException {
        try {
            this.id = tid.getTerm();
        } catch (final Exception ex) {
            throw new InvalidAgentIdException();
        }
        if (!this.id.isGround()) {
            throw new InvalidAgentIdException();
        }
    }

    public String getLocalName() {
        if (this.id.isCompound()) {
            return ((Struct) this.id).getArg(0).toString();
        }
        return this.id.toString();
    }

    public boolean isAgent() {
        return true;
    }

    public boolean isEnv() {
        return false;
    }

    public boolean isTC() {
        return false;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }

    /**
     * Provides the logic term representation of the identifier
     * 
     * @return the term representing the identifier
     */
    public Term toTerm() {
        return this.id;
    }

}
