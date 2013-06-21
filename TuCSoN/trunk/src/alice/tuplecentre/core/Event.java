/*
 * Tuple Centre media - Copyright (C) 2001-2002 aliCE team at deis.unibo.it This
 * library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package alice.tuplecentre.core;

import java.util.HashMap;
import java.util.Map;

import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;

/**
 * Represents events of the tuple centre virtual machine
 * 
 * An event is always related to the operation executed by some agent.
 * 
 * @author aricci
 */
abstract public class Event implements java.io.Serializable {

    private static final long serialVersionUID = 5233628097824741218L;

    private final HashMap<String, String> ev_prop;
    /** the current tuple centre (VM) where this event is managed **/
    private TupleCentreId reactingTC;
    /** the operation (primitive + tuple) associated with this event **/
    private TupleCentreOperation simpleTCEvent;
    /** the entitiy executing the operation **/
    private IId source;
    /** represent the target entity that could be an agent or a TC **/
    private IId target;

    /** time at which this event occurs */
    private final long time;

    public Event(final IId s, final TupleCentreOperation op,
            final TupleCentreId tc, final long t) {
        this.source = s;
        this.simpleTCEvent = op;
        this.target = tc;
        this.reactingTC = tc;
        this.time = t;
        this.ev_prop = new HashMap<String, String>();
    }

    public Event(final IId s, final TupleCentreOperation op,
            final TupleCentreId tc, final long t, final Map<String, String> prop) {
        this(s, op, tc, t);
        this.ev_prop.putAll(prop);
    }

    public String getEventProp(final String key) {
        return this.ev_prop.get(key);
    }

    public TupleCentreId getReactingTC() {
        return this.reactingTC;
    }

    /**
     * Gets the operation which directly or not caused the event
     */
    public TupleCentreOperation getSimpleTCEvent() {
        return this.simpleTCEvent;
    }

    /**
     * Gets the executor of the operation which caused directly or indirectly
     * this event.
     * 
     * @return the id of the executor
     */
    public IId getSource() {
        return this.source;
    }

    public IId getTarget() {
        return this.target;
    }

    public long getTime() {
        return this.time;
    }

    public Tuple getTuple() {
        return this.simpleTCEvent.getTupleArgument();

    }

    /**
     * Tests if it is an input event
     * 
     * @return true if it is an input event
     */
    public abstract boolean isInput();

    /**
     * Tests if it is an internal event
     * 
     * @return true if it is an internal event
     */
    public abstract boolean isInternal();

    /**
     * Tests if it is an output event
     * 
     * @return true if it is an output event
     */
    public abstract boolean isOutput();

    public void setReactingTC(final TupleCentreId tc) {
        this.reactingTC = tc;
    }

    public void setSimpleTCEvent(final TupleCentreOperation op) {
        this.simpleTCEvent = op;
    }

    public void setSource(final IId s) {
        this.source = s;
    }

    public void setTarget(final IId t) {
        this.target = t;
    }

}
