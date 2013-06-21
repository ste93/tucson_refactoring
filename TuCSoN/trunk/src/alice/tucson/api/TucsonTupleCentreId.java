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

import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tuprolog.Term;

public class TucsonTupleCentreId implements
        alice.tuplecentre.api.TupleCentreId, Serializable {

    private static final long serialVersionUID = -4503481713163088789L;
    private Object tid;

    public TucsonTupleCentreId(final Object id)
            throws TucsonInvalidTupleCentreIdException {
        if (id.getClass().getName().equals("alice.respect.api.TupleCentreId")) {
            this.tid = id;
        } else {
            try {
                this.tid = new TupleCentreId((String) id);
            } catch (final InvalidTupleCentreIdException e) {
                throw new TucsonInvalidTupleCentreIdException();
            }
        }
    }

    public TucsonTupleCentreId(final String tcName, final String netid,
            final String portno) throws TucsonInvalidTupleCentreIdException {
        try {
            this.tid = new TupleCentreId(tcName, netid, portno);
        } catch (final InvalidTupleCentreIdException e) {
            // TODO Properly handle Exception
            throw new TucsonInvalidTupleCentreIdException();
        }
    }

    public Object getInternalTupleCentreId() {
        return this.tid;
    }

    public String getName() {
        return ((TupleCentreId) this.tid).getName();
    }

    public String getNode() {
        return ((TupleCentreId) this.tid).getNode();
    }

    public int getPort() {
        return ((TupleCentreId) this.tid).getPort();
    }

    public boolean isAgent() {
        return false;
    }

    public boolean isEnv() {
        return false;
    }

    public boolean isTC() {
        return true;
    }

    @Override
    public String toString() {
        return ((TupleCentreId) this.tid).toString();
    }

    public Term toTerm() {
        return alice.tuprolog.Term.createTerm(this.tid.toString());
    }

}
