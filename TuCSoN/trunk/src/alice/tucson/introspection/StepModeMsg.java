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
package alice.tucson.introspection;

import alice.tucson.api.TucsonAgentId;

/**
 * @author Roberto D'Elia
 *
 */
public class StepModeMsg extends NodeMsg {

    private static final long serialVersionUID = -6748034977696183466L;

    /**
     * @param id
     *            the agent id of the sender
     */
    public StepModeMsg(final TucsonAgentId id) {
        super(id, "stepMode");
    }
}
