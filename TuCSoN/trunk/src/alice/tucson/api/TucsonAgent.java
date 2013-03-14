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

import java.util.HashMap;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.service.TucsonOpCompletionEvent;

import alice.tuplecentre.core.TupleCentreOperation;

/**
 * Base class to extend to implement TuCSoN Agents.
 * Once created, the method {@link alice.tucson.api.TucsonAgent#spawn spawn()}
 * gets TuCSoN Default ACC (the most comprehensive at the moment) and trigger
 * Agent's main execution cyle, that is the method {@link alice.tucson.api.TucsonAgent#main
 * main}.
 */
public abstract class TucsonAgent implements TucsonOperationCompletionListener{

	private EnhancedACC context;
	private TucsonAgentId aid;
	private String node;
	private int port;
	protected HashMap <TucsonOpId, TucsonOpCompletionEvent> events = null;

	/**
	 * Most complete constructor, allows to specify the ip address where the TuCSoN
	 * node to whom ask for an ACC resides and its listening port.
	 * 
	 * @param aid The TucsonAgent Identifier
	 * @param netid The ip address of the TuCSoN Node to contact
	 * @param port The listening port of the TuCSoN Node to contact
	 */
	private TucsonAgent(TucsonAgentId aid, String netid, int port){
		this.aid = aid;
		this.node = netid;
		this.port = port;
	}
	
	/**
	 * Same as first one, but takes a String in place of a TucsonAgentId that is
	 * created from scratch using such string.
	 * 
	 * @param aid The String to use to build the TucsonAgentIdentifier
	 * @param netid The ip address of the TuCSoN Node to contact
	 * @param port The listening port of the TuCSoN Node to contact
	 * 
	 * @throws TucsonInvalidAgentIdException
	 */
	public TucsonAgent(String aid, String netid, int port) throws TucsonInvalidAgentIdException{
		this(new TucsonAgentId(aid), netid, port);
	}
	
	/**
	 * Again we assume default port (which is 20504), so we skip that parameter
	 * (String aid version).
	 * 
	 * @param aid The String to use to build the TucsonAgentIdentifier
	 * @param netid The ip address of the TuCSoN Node to contact
	 * 
	 * @throws TucsonInvalidAgentIdException
	 */
	public TucsonAgent(String aid, String netid) throws TucsonInvalidAgentIdException{
		this(new TucsonAgentId(aid), netid, 20504);
	}
	
	/**
	 * Same as before, this time using the passed String to create the TucsonAgentId
	 * from scratch
	 * 
	 * @param aid The String to use to build the TucsonAgentIdentifier
	 * 
	 * @throws TucsonInvalidAgentIdException
	 */
	public TucsonAgent(String aid) throws TucsonInvalidAgentIdException{
		this(new TucsonAgentId(aid), "localhost", 20504);
	}
	
	/**
	 * Starts main execution cycle {@link alice.tucson.api.TucsonAgent@main main}
	 */
	final public void go(){
		new AgentThread(this).start();
	}

	/**
	 * Main execution cycle, user-defined.
	 */
	abstract protected void main();

	/**
	 * Getter for the TucsonAgent identifier
	 * 
	 * @return The TucsonAgentId for this agent
	 */
	public final TucsonAgentId getTucsonAgentId(){
		return aid;
	}
	
	/**
	 * Returns local agent name
	 * 
	 * @return The String name of the agent
	 */
	public final String myName(){
		return aid.getAgentName();
	}
	
	/**
	 * Returns agent default node.
	 * 
	 * @return The default node of the agent
	 */
	public final String myNode(){
		return this.node;
	}
	
	/**
	 * Returns agent default port
	 * 
	 * @return The default port of the agent
	 */
	public final int myport(){
		return this.port;
	}
	
	/**
	 * Getter for the ACC.
	 * At the moment the TucsonAgent base class always ask for the most
	 * comprehensive ACC (that is the DefaultACC): it's up to the user agent
	 * wether to use a more restrictive one (properly declaring its reference)
	 * 
	 * @return The DefaultACC
	 */
	protected EnhancedACC getContext(){
		return context;
	}
	
	/**
	 * Setter for the ACC.
	 * Takes the most comprehensive one, hence even a less-powerful can be
	 * passed.
	 * 
	 * @param context The ACC to use
	 */
	protected void setContext(EnhancedACC context){
		this.context = context;
	}
	
	public abstract void operationCompleted(ITucsonOperation op);

	@Override
	public final void operationCompleted(TupleCentreOperation op) {}
	
	/**
	 * Internal Thread responsible for ACC acquisition and main cycle execution.
	 * Notice that the ACC is demanded to the TuCSoN Node Service hosted at the
	 * construction-time defined ip address and listening on the construction-time
	 * defined port.
	 */
	final class AgentThread extends Thread{
		TucsonAgent agent;
		AgentThread(TucsonAgent agent){
			this.agent = agent;
		}
		final public void run(){
				try {
					agent.setContext(TucsonMetaACC.getContext(agent.getTucsonAgentId(), node, port));
					agent.main();
					agent.getContext().exit();
				} catch (TucsonOperationNotPossibleException e) {
					System.err.println("[TucsonAgent] AgentThread: " + e);
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Utility method to print on standard output the user agent activity.
	 * 
	 * @param msg The message to print
	 */
	protected void say(String msg){
		System.out.println("["+getTucsonAgentId().getAgentName()+"]: " + msg);
	}
	
}
