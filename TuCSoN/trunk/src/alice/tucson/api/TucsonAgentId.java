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

import java.io.Serializable;
import java.util.UUID;

import alice.respect.api.AgentId;
import alice.respect.api.exceptions.InvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

public class TucsonAgentId implements alice.tuplecentre.api.AgentId,
        Serializable {

    private static final long serialVersionUID = -5788843633820003843L;

    private static String dropMinus(final UUID uuid) {
        final String uuids = uuid.toString();
        String res = "";
        int j = 0;
        for (int i = 0; i < uuids.length(); i++) {
            if (uuids.charAt(i) == '-') {
                res += uuids.substring(j, i);
                j = i + 1;
            }
        }
        res += uuids.substring(j, uuids.length());
        return res;
    }

    private Object aid;

    private UUID uuid;

    public TucsonAgentId(final String id) throws TucsonInvalidAgentIdException {
        try {
            this.aid = new AgentId(id);
            this.uuid = null;
        } catch (final InvalidAgentIdException e) {
            throw new TucsonInvalidAgentIdException();
        }
    }

    public TucsonAgentId(final String name, final TucsonTupleCentreId tcId) {
        this.aid = new AgentId(name, tcId);
    }

    public Object getAgentId() {
        return this.aid;
    }

    public String getAgentName() {
        return ((AgentId) this.aid).getLocalName();
    }

    public String getAgentUUID() {
        return this.uuid.toString();
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
        return ((AgentId) this.aid).toString();
    }

    boolean assignUUID() throws TucsonInvalidAgentIdException {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
            try {
                this.aid =
                        new AgentId(this.aid + ":uuid"
                                + TucsonAgentId.dropMinus(this.uuid));
            } catch (final InvalidAgentIdException e) {
                throw new TucsonInvalidAgentIdException();
            }
        }
        return true;
    }

}
