/*
 * Tuple Centre media - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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
package alice.tuplecentre.core;

import java.util.HashMap;
import java.util.Map;

import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;

/**
 * Represents events of the tuple centre virtual machine
 *
 * An event is always related to the operation executed
 * by some agent.
 * 
 * @author aricci
 */
abstract public class Event implements java.io.Serializable {

	private static final long serialVersionUID = 5233628097824741218L;
	
	/** the entitiy executing the operation **/
	private IId source;
	/** the operation (primitive + tuple) associated with this event **/
	private TupleCentreOperation simpleTCEvent;
	/** the current tuple centre (VM) where this event is managed **/
	private TupleCentreId reactingTC;
	/** represent the target entity that could be an agent or a TC **/
	private IId target;
	/** time at which this event occurs*/
	private long time;
	
	private HashMap<String, String> ev_prop;
	
	public Event(IId s, TupleCentreOperation op, TupleCentreId tc, long t){
		source = s;
		simpleTCEvent = op;
		target = tc;
		reactingTC = tc;
		time = t;
		ev_prop = new HashMap<String, String>();
	}

	public Event(IId s, TupleCentreOperation op, TupleCentreId tc, long t, 
			Map<String, String> prop){
		this(s, op, tc, t);
		ev_prop.putAll(prop);
	}
	
	public long getTime(){
		return time;
	}
	
	/**
	 * Gets the operation which directly or not caused the event
	 */
	public TupleCentreOperation getSimpleTCEvent(){
		return simpleTCEvent;
	}
	
	public void setSimpleTCEvent(TupleCentreOperation op){
		simpleTCEvent = op;
	}
	
	public TupleCentreId getReactingTC(){
		return reactingTC;
	}
	
	public void setReactingTC(TupleCentreId tc){
		reactingTC = tc;
	}
	
	/**
	 * Gets the executor of the operation which caused directly or indirectly
	 * this event.
	 * 
	 * @return the id of the executor  
	 */
	public IId getSource(){
		return source;
	}
	
	public void setSource(IId s){
		source = s;
	}
	
	public IId getTarget(){
		return target;
	}
	
	public void setTarget(IId t){
		target = t;
	}
	
	public Tuple getTuple(){
		return simpleTCEvent.getTupleArgument();
		
	}

	public String getEventProp(String key){
		return ev_prop.get(key);
	}
	
	/**
	 * Tests if it is an input event
	 * 
	 * @return true if it is an input event
	 */
	public abstract boolean isInput();
	
	/**
	 * Tests if it is an output event
	 * 
	 * @return true if it is an output event
	 */
	public abstract boolean isOutput();

	/**
	 * Tests if it is an internal event
	 * 
	 * @return true if it is an internal event
	 */
	public abstract boolean isInternal();

}
