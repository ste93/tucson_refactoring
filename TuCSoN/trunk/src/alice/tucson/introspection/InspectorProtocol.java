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

import java.io.Serializable;

import alice.logictuple.LogicTuple;

public class InspectorProtocol implements Serializable {

    /** don't observe */
    public static final byte NO_OBSERVATION = 1;

    /** observe continuosly */
    public static final byte PROACTIVE_OBSERVATION = 3;

    /** observe only when asked by inspector */
    public static final byte REACTIVE_OBSERVATION = 2;

    private static final long serialVersionUID = -6842026459178793395L;

    /** defining W set observation */
    public byte pendingQueryObservType = InspectorProtocol.NO_OBSERVATION;

    /** defining T set observation */
    public byte reactionsObservType = InspectorProtocol.NO_OBSERVATION;

    /** desired tracing state for the vm */
    public boolean tracing = false;

    /** filter for tuple observed */
    public LogicTuple tsetFilter = null;

    /** defining T set observation */
    public byte tsetObservType = InspectorProtocol.NO_OBSERVATION;

    /** filter for query observed */
    public LogicTuple wsetFilter = null;

}
