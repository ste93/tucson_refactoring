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
package alice.tucson.service;

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonOpId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.*;
import alice.tucson.parsing.MyOpManager;

import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.TCCycleResult.Outcome;

import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.lib.InvalidObjectIdException;

import java.io.*;
import java.util.*;

/**
 * Active part of the Default Agent Coordination Context.
 * 
 * It implements the underlying behavior needed by every TuCSoN Agent
 * {@link alice.tucson.api.TucsonAgent user} to fruitfully interact with the
 * TuCSoN Node Service {@link alice.tucson.service.TucsonNodeService TuCSoN}.
 * Essentially, it implements every method exposed in the Default ACC Interface
 * {@link alice.tucson.api.DefaultACC default} offered to the agent, maps
 * each of them into TuCSoN Request Messages {@link alice.tucson.network.TucsonMsgRequest req},
 * then waits for TuCSoN Node Services replies {@link alice.tucson.network.TucsonMsgReply reply}
 * forwarding them to the agent.
 * 
 * It also is in charge of establishing the first connection toward the TuCSoN
 * Node Service and the specific tuplecentre inside it as soon as needed (that is,
 * upon the first invocation of any method belonging to the ACC Interface).
 * 
 * It is created from the TuCSoN Agent class. In it, an internal thread
 * is responsible to obtain the choosen ACC (the Default is the only at the moment)
 * by invoking the {@link alice.tucson.api.TucsonMetaACC#getContext getContext}
 * static method from the TuCSoN Meta ACC class {@link alice.tucson.api.TucsonMetaACC metaACC}.
 * The acquisition of such ACC triggers this proxy creation and execution.
 * 
 * @see alice.tucson.api.TucsonAgent TucsonAgent
 * @see alice.tucson.service.TucsonNodeService TucsonNodeService
 * @see alice.tucson.api.DefaultACC DefaultACC
 * @see alice.tucson.network.TucsonMsgRequest TucsonMsgRequest
 * @see alice.tucson.network.TucsonMsgReply TucsonMsgReply
 * @see alice.tucson.api.TucsonMetaACC TucsonMetaACC
 */
public class ACCProxyAgentSide implements EnhancedACC{
	
	/**
	 * TuCSoN Agent Identifier
	 */
	protected TucsonAgentId aid;
	/**
	 * TuCSoN Node Service ip address
	 */
	protected String node;
	/**
	 * TuCSoN Node Service listening port
	 */
	protected int port;
	/**
	 * Current ACC session description
	 */
	protected ACCDescription profile;
	/**
	 * TuCSoN requests completion events (node replies events)
	 */
	protected LinkedList<TucsonOpCompletionEvent> events;
	/**
	 * Active sessions toward different nodes
	 */
	protected HashMap<String, ControllerSession> controllerSessions;
	/**
	 * Requested TuCSoN operations
	 */
	protected HashMap<Long, TucsonOperation> operations;
	/**
	 * Expired TuCSoN operations
	 */
	protected ArrayList<Long> operationExpired;
	
	
	/**
	 * Complete constructor: starts the named TuCSoN Agent on the specific TuCSoN
	 * node listening on the specified port
	 * 
	 * @param aid TuCSoN Agent identifier
	 * @param node TuCSoN node ip address
	 * @param port TuCSoN node listening port
	 * 
	 * @throws TucsonInvalidAgentIdException
	 */
	public ACCProxyAgentSide(Object aid, String node, int port) throws TucsonInvalidAgentIdException{
	
		if(aid.getClass().getName().equals("alice.tucson.api.TucsonAgentId"))
			this.aid = (TucsonAgentId) aid;
		else
			this.aid = new TucsonAgentId(aid);
		this.node = node;
		this.port = port;

		profile = new ACCDescription();
		events = new LinkedList<TucsonOpCompletionEvent>();
		controllerSessions = new HashMap<String, ControllerSession>();
		operations = new HashMap<Long, TucsonOperation>();
		operationExpired = new ArrayList<Long>();
		
	}
	
	/**
	 * Default constructor: exploits the default port (20504) in the "localhost"
	 * TuCSoN Node Service
	 * 
	 * @param aid TuCSoN Agent identifier
	 * 
	 * @throws TucsonInvalidAgentIdException
	 */
	public ACCProxyAgentSide(Object aid) throws TucsonInvalidAgentIdException{
		this(aid, "localhost", 20504);
	}
	
	/**
	 * Out Linda primitive, asynchronous version. Inserts the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * Notice that TuCSoN out primitive assumes the ORDERED version of this primitive,
	 * hence the tuple is SUDDENLY injected in the target space (if the primitive
	 * successfully completes)
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be emitted in the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation out(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.outCode(), tid, tuple, l);
	}
	
	/**
	 * In Linda primitive, asynchronous version. Retrieves the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Notice that the primitive semantics is still SUSPENSIVE: until no tuple is found
	 * to match the given template, no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy (but thanks to asynchronous behaviour,
	 * TuCSoN Agent could go something else instead of getting stuck)
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be retrieved from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation in(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.inCode(), tid, tuple, l);
	}
	
	/**
	 * Rd Linda primitive, asynchronous version. Reads (w/o removing) the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Notice that the primitive semantics is still SUSPENSIVE: until no tuple is found
	 * to match the given template, no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy (but thanks to asynchronous behaviour,
	 * TuCSoN Agent could go something else instead of getting stuck)
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be read from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation rd(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.rdCode(), tid, tuple, l);
	}
	
	/**
	 * Inp Linda primitive, asynchronous version. Retrieves the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * This time the primitive semantics is NOT SUSPENSIVE: if no tuple is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be retrieved from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation inp(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.inpCode(), tid, tuple, l);
	}
	
	/**
	 * Rdp Linda primitive, asynchronous version. Reads (w/o removing) the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * This time the primitive semantics is NOT SUSPENSIVE: if no tuple is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be read from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation rdp(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.rdpCode(), tid, tuple, l);
	}
	
	/**
	 * No Linda primitive, asynchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * This time the primitive semantics is NOT SUSPENSIVE: if a tuple is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be checked for absence from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation no(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.noCode(), tid, tuple, l);
	}
	
	/**
	 * No Linda primitive, asynchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * This time the primitive semantics is NOT SUSPENSIVE: if a tuple is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be checked for absence from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation nop(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.nopCode(), tid, tuple, l);
	}
	
	/**
	 * Get TuCSoN primitive, asynchronous version. Reads (w/o removing) all the tuples
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: is the tuple space is empty, a failure
	 * completion answer is forwarded to the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation get(Object tid, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.get_Code(), tid, null, l);
	}
	
	/**
	 * Set TuCSoN primitive, asynchronous version. Inserts all the tuples in the
	 * specified list in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple The logic tuple containing the list of all the tuples to be injected
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation set(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.set_Code(), tid, tuple, l);
	}
	
	
	
	/**
	 * SPAWN
	 */
	public ITucsonOperation spawn(Object tid, LogicTuple toSpawn, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(TucsonOperation.spawnCode(), tid, toSpawn, l);
	}
	
	
	
	public ITucsonOperation uin(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.uinCode(), tid, tuple, l);
	}

	public ITucsonOperation urd(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.urdCode(), tid, tuple, l);
	}
	
	public ITucsonOperation uno(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.unoCode(), tid, tuple, l);
	}

	public ITucsonOperation uinp(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.uinpCode(), tid, tuple, l);
	}

	public ITucsonOperation urdp(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.urdpCode(), tid, tuple, l);
	}
	
	public ITucsonOperation unop(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.unopCode(), tid, tuple, l);
	}
	
	public ITucsonOperation out_all(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.out_allCode(), tid, tuple, l);
	}

	public ITucsonOperation in_all(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.in_allCode(), tid, tuple, l);
	}

	public ITucsonOperation rd_all(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.rd_allCode(), tid, tuple, l);
	}
	
	public ITucsonOperation no_all(Object tid, LogicTuple tuple,
			TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException {
		return doNonBlockingOperation(TucsonOperation.no_allCode(), tid, tuple, l);
	}
	
	
	
	/**
	 * Out_s Specification primitive, asynchronous version. Adds the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * WITHOUT waiting the completion answer from the TuCSoN node. The TuCSoN agent
	 * this proxy instance refers to will be asynchronously notified upon need
	 * (that is, when completion reply arrives).
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * Again, this TuCSoN out_s primitive assumes the ORDERED semantics,
	 * hence the reaction specification is SUDDENLY injected in the target space
	 * (if the primitive successfully completes)
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be emitted in the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation out_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doNonBlockingOperation(TucsonOperation.out_sCode(), tid, tuple, l);
	}
	
	/**
	 * In_s Specification primitive, asynchronous version. Retrieves the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * WITHOUT waiting the completion answer from the TuCSoN node. The TuCSoN agent
	 * this proxy instance refers to will be asynchronously notified upon need
	 * (that is, when completion reply arrives).
	 * 
	 * Notice that the primitive semantics is still SUSPENSIVE: until no Reaction Specification
	 * is found to match the given template, no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy (but thanks to asynchronous behaviour,
	 * TuCSoN Agent could go something else instead of getting stuck)
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be retrieved from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation in_s(Object tid, LogicTuple event, LogicTuple guards,
			LogicTuple reactionBody, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doNonBlockingOperation(TucsonOperation.in_sCode(), tid, tuple, l);
	}
	
	/**
	 * Rd_s Specification primitive, asynchronous version. Reads (w/o removing) the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * WITHOUT waiting the completion answer from the TuCSoN node. The TuCSoN agent
	 * this proxy instance refers to will be asynchronously notified upon need
	 * (that is, when completion reply arrives).
	 * 
	 * Notice that the primitive semantics is still SUSPENSIVE: until no Reaction Specification
	 * is found to match the given template, no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy (but thanks to asynchronous behaviour,
	 * TuCSoN Agent could go something else instead of getting stuck)
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be read from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation rd_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doNonBlockingOperation(TucsonOperation.rd_sCode(), tid, tuple, l);
	}
	
	/**
	 * Inp_s Specification primitive, asynchronous version. Retrieves the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * WITHOUT waiting the completion answer from the TuCSoN node. The TuCSoN agent
	 * this proxy instance refers to will be asynchronously notified upon need
	 * (that is, when completion reply arrives).
	 * 
	 * This time the primitive semantics is NOT SUSPENSIVE: if no Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be retrieved from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation inp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doNonBlockingOperation(TucsonOperation.inp_sCode(), tid, tuple, l);
	}
	
	/**
	 * Rdp_s Specification primitive, asynchronous version. Reads (w/o removing) the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * WITHOUT waiting the completion answer from the TuCSoN node. The TuCSoN agent
	 * this proxy instance refers to will be asynchronously notified upon need
	 * (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: if no Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be read from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation rdp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doNonBlockingOperation(TucsonOperation.rdp_sCode(), tid, tuple, l);
	}
	
	/**
	 * No_s Specification primitive, asynchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: if a Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be checked for absence from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation no_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doNonBlockingOperation(TucsonOperation.no_sCode(), tid, tuple, l);
	}
	
	/**
	 * No_s Specification primitive, asynchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: if a Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be checked for absence from the target tuplecentre
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation nop_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doNonBlockingOperation(TucsonOperation.nop_sCode(), tid, tuple, l);
	}
	
	/**
	 * Get_s TuCSoN primitive, asynchronous version. Reads (w/o removing) all the Reaction Specifications
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: is the specification space is empty, a failure
	 * completion answer is forwarded to the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws InvalidTupleOperationException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation get_s(Object tid, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, InvalidTupleOperationException{
		LogicTuple spec = new LogicTuple("spec");		
		return doNonBlockingOperation(TucsonOperation.get_sCode(), tid, spec, l);
//		return tupleRes.getArg(0).getName().replace('\'', ' ').trim();
	}
	
	/**
	 * Set_s TuCSoN primitive, asynchronous version. Replace the specification space
	 * with the newly specified (in the form of a string) in the given target
	 * tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param spec The new specification space to replace the current one
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws InvalidLogicTupleException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation set_s(Object tid, String spec, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, InvalidLogicTupleException{
		LogicTuple specT = new LogicTuple("spec", new Value(spec));
		return doNonBlockingOperation(TucsonOperation.set_sCode(), tid, specT, l);
	}
	
	/**
	 * Private method that takes in charge execution of all the Asynchronous
	 * primitives listed above. It simply forwards real execution to another private
	 * method {@link alice.tucson.api doOperation doOp} (notice that in truth
	 * there is no real execution at this point: we are just packing primitives
	 * invocation into TuCSoN messages, then send them to the Node side)
	 * 
	 * @param type TuCSoN operation type (internal integer code)
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param t The Logic Tuple involved in the requested operation
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	protected ITucsonOperation doNonBlockingOperation(int type, Object tid, LogicTuple t,
			TucsonOperationCompletionListener l)
					throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(type, tid, t, null, l);
	}
	
	protected ITucsonOperation doNonBlockingOperation(int type, Object tid, LogicTuple t,
			List<LogicTuple> list, TucsonOperationCompletionListener l)
					throws TucsonOperationNotPossibleException, UnreachableNodeException{
		
		TucsonTupleCentreId tcid = null;
		if(tid.getClass().getName().equals("alice.tucson.api.TucsonTupleCentreId")){
			tcid = (TucsonTupleCentreId) tid;
		}else{
			try{
				tcid = new TucsonTupleCentreId(tid);
			}catch(TucsonInvalidTupleCentreIdException ex){
				System.err.println("[ACCProxyAgentSide]: " + ex);
				return null;
			}
		}
		
		try{
			return doOperation(tcid, type, t, list, l);
		}catch(TucsonOperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}catch(UnreachableNodeException e){
			throw new UnreachableNodeException();
		}
		
	}
	
	/**
	 * Out Linda primitive, synchronous version. Inserts the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * Notice that TuCSoN out primitive assumes the ORDERED version of this primitive,
	 * hence the tuple is SUDDENLY injected in the target space (if the primitive
	 * successfully completes)
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be emitted in the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation out(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.outCode(), tid, tuple, ms);
	}
	
	/**
	 * In Linda primitive, synchronous version. Retrieves the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be retrieved from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation in(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.inCode(), tid, tuple, ms);
	}
	
	/**
	 * Rd Linda primitive, synchronous version. Reads (w/o removing) the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be read from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation rd(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.rdCode(), tid, tuple, ms);
	}
	
	/**
	 * Inp Linda primitive, synchronous version. Retrieves the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be retrieved from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation inp(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.inpCode(), tid, tuple, ms);
	}
	
	/**
	 * Rdp Linda primitive, synchronous version. Reads (w/o removing) the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be read from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation rdp(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.rdpCode(), tid, tuple, ms);
	}
	
	/**
	 * No Linda primitive, synchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if a tuple is found to match the given template,
	 * a failure completion answer is forwarded to the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be checked for absence from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation no(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.noCode(), tid, tuple, ms);
	}
	
	/**
	 * No Linda primitive, synchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if a tuple is found to match the given template,
	 * a failure completion answer is forwarded to the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Tuple to be checked for absence from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation nop(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.nopCode(), tid, tuple, ms);
	}
	
	/**
	 * Get TuCSoN primitive, synchronous version. Reads (w/o removing) all the tuples
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: is the tuple space is empty, a failure
	 * completion answer is forwarded to the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The list of Logic Tuples resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation get(Object tid, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.get_Code(), tid, null, ms);
	}
	
	/**
	 * Set TuCSoN primitive, synchronous version. Inserts all the tuples in the
	 * specified list in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple The logic tuple containing the list of all the tuples to be injected
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	public ITucsonOperation set(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(TucsonOperation.set_Code(), tid, tuple, ms);
	}
	
	
	
	/**
	 * SPAWN: BEWARE THE TRICK: this is the synch ACC, but I do a NONBLOCKING call!!
	 */
	public ITucsonOperation spawn(Object tid, LogicTuple toSpawn, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doNonBlockingOperation(TucsonOperation.spawnCode(), tid, toSpawn, null);
	}
	
	
	
	public ITucsonOperation uin(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.uinCode(), tid, tuple, ms);
	}

	public ITucsonOperation urd(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.urdCode(), tid, tuple, ms);
	}
	
	public ITucsonOperation uno(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.unoCode(), tid, tuple, ms);
	}

	public ITucsonOperation uinp(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.uinpCode(), tid, tuple, ms);
	}

	public ITucsonOperation urdp(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.urdpCode(), tid, tuple, ms);
	}
	
	public ITucsonOperation unop(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.unopCode(), tid, tuple, ms);
	}
	
	public ITucsonOperation out_all(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.out_allCode(), tid, tuple, ms);
	}

	public ITucsonOperation in_all(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.in_allCode(), tid, tuple, ms);
	}

	public ITucsonOperation rd_all(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.rd_allCode(), tid, tuple, ms);
	}
	
	public ITucsonOperation no_all(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException,
			UnreachableNodeException, OperationTimeOutException {
		return doBlockingOperation(TucsonOperation.no_allCode(), tid, tuple, ms);
	}
	
	
	
	/**
	 * Out_s Specification primitive, synchronous version. Adds the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * Again, this TuCSoN out_s primitive assumes the ORDERED semantics,
	 * hence the reaction specification is SUDDENLY injected in the target space
	 * (if the primitive successfully completes)
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be emitted in the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation out_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doBlockingOperation(TucsonOperation.out_sCode(), tid, tuple, ms);
	}
	
	/**
	 * In_s Specification primitive, synchronous version. Retrieves the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be retrieved from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation in_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doBlockingOperation(TucsonOperation.in_sCode(), tid, tuple, ms);
	}
	
	/**
	 * Rd_s Specification primitive, synchronous version. Reads (w/o removing) the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be read from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation rd_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doBlockingOperation(TucsonOperation.rd_sCode(), tid, tuple, ms);
	}
	
	/**
	 * Inp_s Specification primitive, synchronous version. Retrieves the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * This time the primitive semantics is NOT SUSPENSIVE: if no Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be retrieved from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation inp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doBlockingOperation(TucsonOperation.inp_sCode(), tid, tuple, ms);
	}
	
	/**
	 * Rdp_s Specification primitive, synchronous version. Reads (w/o removing) the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if no Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest
	 * to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be read from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation rdp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doBlockingOperation(TucsonOperation.rdp_sCode(), tid, tuple, ms);
	}
	
	/**
	 * No_s Specification primitive, synchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if a Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be checked for absence from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation no_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doBlockingOperation(TucsonOperation.no_sCode(), tid, tuple, ms);
	}
	
	/**
	 * No_s Specification primitive, synchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if a Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple Reaction Specification to be checked for absence from the target tuplecentre
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation nop_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm("reaction("+event+","+guards+","+reactionBody+")", new MyOpManager()));
		return doBlockingOperation(TucsonOperation.nop_sCode(), tid, tuple, ms);
	}
	
	/**
	 * Get_s TuCSoN primitive, synchronous version. Reads (w/o removing) all the Reaction Specifications
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if the specification space is empty, a failure
	 * completion answer is forwarded to the TuCSoN Agent exploiting this proxy
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The String representing the obtained Reaction Specification space
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation get_s(Object tid, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException, InvalidTupleOperationException{		
		LogicTuple spec = new LogicTuple("spec", new Var("S"));
		return doBlockingOperation(TucsonOperation.get_sCode(), tid, spec, ms);
//		LogicTuple tupleRes = (LogicTuple) doBlockingOperation(TucsonOperation.get_sCode(), tid, spec, ms);
//		return tupleRes.getArg(0).getName().replace('\'', ' ').trim();
	}
	
	/**
	 * Set_s TuCSoN primitive, synchronous version. Replace the specification space
	 * with the newly specified (in the form of a string) in the given target
	 * tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param spec The new specification space to replace the current one
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation set_s(Object tid, String spec, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{		
		if(spec.equals("") || spec.equals("''") || spec.equals("'.'"))
			throw new TucsonOperationNotPossibleException();
		LogicTuple specT = new LogicTuple("spec", new Value(spec));
		return doBlockingOperation(TucsonOperation.set_sCode(), tid, specT, ms);
	}
	
	/**
	 * Set_s TuCSoN primitive, synchronous version. Replace the specification space
	 * with the newly specified (in the form of a string) in the given target
	 * tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Such operation is delegated to a private method {@link alice.tucson.api.doBlockingOperation
	 * blocking} which maps it on a TucsonMsgRequest to send it to the TuCSoN Node Service for processing
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param spec The new specification space to replace the current one
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return The Logic Tuple resulting from the completion of the primitive
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	public ITucsonOperation set_s(Object tid, LogicTuple spec, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{		
//		if(spec.equals("") || spec.equals("''") || spec.equals("'.'"))
//			throw new TucsonOperationNotPossibleException();
//		LogicTuple specT = new LogicTuple("spec", new Value(spec));
		return doBlockingOperation(TucsonOperation.set_sCode(), tid, spec, ms);
	}
	
	/**
	 * Private method that takes in charge execution of all the Synchronous
	 * primitives listed above. It simply forwards real execution to another private
	 * method {@link alice.tucson.api doOperation doOp} (notice that in truth
	 * there is no real execution at this point: we are just packing primitives
	 * invocation into TuCSoN messages, then send them to the Node side)
	 * 
	 * The difference w.r.t. the previous method {@link alice.tucson.api.doNonBlockingOperation
	 * nonBlocking} is that here we explicitly wait for completion a time specified
	 * in the timeout input parameter.
	 * 
	 * @param type TuCSoN operation type (internal integer code)
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param t The Logic Tuple involved in the requested operation
	 * @param ms Maximum waiting time tolerated by the callee TuCSoN Agent
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 */
	protected ITucsonOperation doBlockingOperation(int type, Object tid, LogicTuple t, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(type, tid, t, null, ms);
	}
	
	protected ITucsonOperation doBlockingOperation(int type, Object tid, LogicTuple t, List<LogicTuple> list, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{		
		
		TucsonTupleCentreId tcid = null;
		if(tid.getClass().getName().equals("alice.tucson.api.TucsonTupleCentreId")){
			tcid = (TucsonTupleCentreId) tid;
		}else{
			try{
				tcid = new TucsonTupleCentreId(tid);
			}catch(TucsonInvalidTupleCentreIdException ex){
				System.err.println("[ACCProxyAgentSide]: " + ex);
				return null;
			}
		}
		
		ITucsonOperation op = null;
		try{			
			op = doOperation(tcid, type, t, list, null);
		}catch(TucsonOperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}catch(UnreachableNodeException e){
			throw new UnreachableNodeException();
		}
		try{
			if(ms == null)
				op.waitForOperationCompletion();
			else
				op.waitForOperationCompletion(ms);
		}catch(OperationTimeOutException e){
			throw new OperationTimeOutException();
		}
		return op;

	}
	
	/**
	 * Method to track expired operations, that is operations whose completion
	 * has not been received before specified timeout expiration 
	 * 
	 * @param id Unique Identifier of the expired operation
	 */
	public void addOperationExpired(long id){
		this.operationExpired.add(id);
	}
	
	/**
	 * This method is the real responsible of TuCSoN operations execution.
	 * 
	 * First, it takes the target tuplecentre and checks wether this proxy has
	 * ever established a connection toward it: if it did, the already opened
	 * connection is retrieved and used, otherwise a new connection is opened
	 * and stored for later use {@link alice.tucson.service.ACCProxyAgentSide#getSession getSession}.
	 * 
	 * Then, a Tucson Operation {@link alice.tucson.service.TucsonOperation op}
	 * storing any useful information about the TuCSoN primitive invocation
	 * is created and packed into a Tucson Message Request {@link alice.tucson.network.TucsonMsgRequest}
	 * to be possibly sent over the wire toward the target tuplecentre.
	 * 
	 * Notice that a listener is needed, who is the proxy itself, wichever was the
	 * requested operation (inp, in, etc.) and despite its (a-)synchronous behavior.
	 * This is because of the distributed very nature of TuCSoN: we couldn't expect
	 * to block on a socket waiting for a reply. Instead, requested operations should be
	 * dispatched toward the TuCSoN Node Service, which in turn will take them in
	 * charge and notify the requestor upon completion.
	 * 
	 * @param tcid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param type TuCSoN operation type (internal integer code)
	 * @param t The Logic Tuple involved in the requested operation
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.tucson.service.TucsonOperation TucsonOperation
	 */
	protected synchronized ITucsonOperation doOperation(TucsonTupleCentreId tcid,
			int type, LogicTuple t, List<LogicTuple> list, TucsonOperationCompletionListener l)
					throws TucsonOperationNotPossibleException, UnreachableNodeException{				

		int nTry = 0;
		boolean exception;
		
		do{
			
			nTry++;
			exception = false;
			
			TucsonProtocol session = null;
			try{
				session = getSession(tcid);
			}catch(UnreachableNodeException ex2){
				exception = true;
				throw new UnreachableNodeException();
			}
			ObjectOutputStream outStream = session.getOutputStream();		

			TucsonOperation op = null;					
			if((type == TucsonOperation.outCode()) || (type == TucsonOperation.out_sCode())
					|| (type == TucsonOperation.set_sCode()) || (type == TucsonOperation.set_Code())
					|| type == TucsonOperation.out_allCode() || type == TucsonOperation.spawnCode())
				op = new TucsonOperation(type, (Tuple) t, l, this);
			else
				op = new TucsonOperation(type, (TupleTemplate) t, l, this);
			operations.put(op.getId(), op);
			
			TucsonMsgRequest msg = new TucsonMsgRequest(op.getId(), op.getType(), tcid.toString(),
					op.getLogicTupleArgument());
//			log("requesting op " + msg.getType() + ", " + msg.getTuple() + ", " + msg.getTid());
			
			try{
				TucsonMsgRequest.write(outStream, msg);
				outStream.flush();
			}catch(IOException ex){
				exception = true;
				System.err.println("[ACCProxyAgentSide]: " + ex);
			}
			
			if(!exception)
				return op;
			
		}while(nTry < 3);
		
		throw new UnreachableNodeException();
		
	}

	/**
	 * Method to release the current ACC held by the TuCSoN Agent behind this
	 * proxy: it takes all the opened connection (we could have been interacting
	 * with different tuplecentres on the same node, hence interfaced by the same ACC)
	 * to quit them, sending a proper message to the TuCSoN Node involved.
	 * 
	 * @throws TucsonOperationNotPossibleException
	 */
	public synchronized void exit() throws TucsonOperationNotPossibleException{
		
		Iterator<ControllerSession> it = controllerSessions.values().iterator();
		ControllerSession cs;
		TucsonProtocol info;
		Controller contr;
		ObjectOutputStream outStream;
		TucsonOperation op;
		TucsonMsgRequest exit;
		
		while(it.hasNext()){
			
			cs = (ControllerSession) it.next();
			info = cs.getSession();
			contr = cs.getController();
			contr.setStop();
			outStream = info.getOutputStream();
			
			op = new TucsonOperation(TucsonOperation.exitCode(), (TupleTemplate) null, null, this);				
			operations.put(op.getId(), op);
			
			exit = new TucsonMsgRequest((int) op.getId(), op.getType(), null,
					op.getLogicTupleArgument());
			try{
				TucsonMsgRequest.write(outStream, exit);
				outStream.flush();
			}catch(IOException ex){
				System.err.println("[ACCProxyAgentSide]: " + ex);
			}
			
		}
		
	}

	/**
	 * This method is responsible to setup, store and retrieve connections toward
	 * all the tuplecentres ever contacted by the TuCSoN Agent behind this proxy.
	 * 
	 * If a connection toward the given target tuplecentre already exists, it is
	 * retrieved and used. If not, the new connection is setup then stored for later use.
	 * 
	 * It is worth noting a couple of things. Why don't we setup connections
	 * once and for all as soon as the TuCSoN Agent is booted? The reason is that
	 * new tuplecentres can be created at run-time as TuCSoN Agents please, thus for every
	 * TuCSoN Operation request we should check wether a new tuplecentre has to be 
	 * created and booted. If a new tuplecentre has to be booted the correspondant
	 * proxy node side is dinamically triggered and booted {@link alice.tucson.service.ACCProxyNodeSide
	 * nodeProxy} 
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * 
	 * @return The open session toward the given target tuplecentre
	 * 
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.network.TucsonProtocol TucsonProtocol
	 * @see alice.tucson.service.ACCProxyNodeSide ACCProxyNodeSide
	 */
	protected TucsonProtocol getSession(TucsonTupleCentreId tid) throws UnreachableNodeException{				
		
		String opNode = alice.util.Tools.removeApices(tid.getNode());
		int port = tid.getPort();
//		log(opNode+":"+port);
		ControllerSession tc = controllerSessions.get(opNode+":"+port);
		if(tc != null)
			return tc.getSession();
		else{
			if(opNode.equals("localhost"))
				tc = controllerSessions.get("127.0.0.1:"+port);
			if(opNode.equals("127.0.0.1"))
				tc = controllerSessions.get("localhost:"+port);
			if(tc != null)
				return tc.getSession();
		}
			
		profile.setProperty("agent-identity", aid.toString());
		profile.setProperty("agent-role", "user");

		TucsonProtocol dialog = null;
		boolean isEnterReqAcpt = false;
		try{
			dialog = new TucsonProtocolTCP(opNode, port);
			dialog.sendEnterRequest(profile);
			dialog.receiveEnterRequestAnswer();
			if(dialog.isEnterRequestAccepted())
				isEnterReqAcpt = true;
		}catch(Exception ex){
			throw new UnreachableNodeException();
		}								

		if(isEnterReqAcpt){
			ObjectInputStream din = dialog.getInputStream();
			Controller contr = new Controller(din);
			ControllerSession cs = new ControllerSession(contr, dialog);
			controllerSessions.put(opNode+":"+port, cs);
			contr.start();
			return dialog;
		}
		
		return null;
		
	}

	/**
	 * Method to add a TuCSoN Operation Completion Event {@link alice.tucson.service.TucsonOpCompletionEvent
	 * event} to the internal queue of pending completion event to process
	 * 
	 * @param ev Completion Event to be added to pending queue
	 * 
	 * @see alice.tucson.service.TucsonOpCompletionEvent TucsonOpCompletionEvent
	 */
	protected void postEvent(TucsonOpCompletionEvent ev){
		synchronized(events){
			events.addLast(ev);
		}
	}

	/**
	 * Method internally used to log proxy activity (could be used for debug)
	 * 
	 * @param msg String to display on the standard output
	 */
	protected void log(String msg){
		System.out.println("[ACCProxyAgentSide]: " + msg);
	}

	/**
	 * 
	 */
	protected class Controller extends Thread{
		
		private boolean stop;
		private ObjectInputStream in;
		private final Prolog p = new Prolog();

		/**
		 * 
		 * @param in
		 */
		Controller(ObjectInputStream in){
			
			this.in = in;
			stop = false;
			this.setDaemon(true);
			
			alice.tuprolog.lib.JavaLibrary jlib = (alice.tuprolog.lib.JavaLibrary) p.getLibrary("alice.tuprolog.lib.JavaLibrary");
			try{
				jlib.register(new alice.tuprolog.Struct("config"), this);
			}catch(InvalidObjectIdException ex){
				System.err.println("[ACCProxyAgentSide] Controller: " + ex);
				ex.printStackTrace();
			}
			
		}

		/**
		 * 
		 */
		@SuppressWarnings("unchecked")
		public void run(){
			
			TucsonOpCompletionEvent ev = null;
			while(!isStopped()){
				
				for(Long operation : operationExpired)
					operations.remove(operation);
				
				TucsonMsgReply msg = null;
				try{
					msg = TucsonMsgReply.read(in);
				}catch(EOFException e){
					log("TuCSoN node service unavailable, nothing I can do");
					setStop();
					break;
				}catch(Exception ex){
					setStop();
					System.err.println("[ACCProxyAgentSide] Controller: " + ex);
				}

				boolean ok = msg.isAllowed();
				if(ok){
					
					int type = msg.getType();
					if(type == TucsonOperation.uinCode() || type == TucsonOperation.uinpCode()
							|| type == TucsonOperation.urdCode() || type == TucsonOperation.urdpCode()
							|| type == TucsonOperation.unoCode() || type == TucsonOperation.unopCode()
							|| type == TucsonOperation.noCode() || type == TucsonOperation.no_sCode()
							|| type == TucsonOperation.nopCode() || type == TucsonOperation.nop_sCode()
							|| type == TucsonOperation.inCode() || type == TucsonOperation.rdCode()
							|| type == TucsonOperation.inpCode() || type == TucsonOperation.rdpCode()
							|| type == TucsonOperation.in_sCode() || type == TucsonOperation.rd_sCode()
							|| type == TucsonOperation.inp_sCode() || type == TucsonOperation.rdp_sCode()){

						boolean succeeded = msg.isSuccess();
						if(succeeded){
							
							LogicTuple tupleReq = msg.getTupleRequested();
							LogicTuple tupleRes = (LogicTuple) msg.getTupleResult();
//							log("tupleReq="+tupleReq+", tupleRes="+tupleRes);
							LogicTuple res = unify(tupleReq, tupleRes);
							ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, true, res);
							
						}else{
							ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, false);
						}
						
					}else if(type == TucsonOperation.set_Code() || type == TucsonOperation.set_sCode()
							|| type == TucsonOperation.outCode() || type == TucsonOperation.out_sCode()
							|| type == TucsonOperation.out_allCode() || type == TucsonOperation.spawnCode()){
						ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, msg.isSuccess());
					}else if(type == TucsonOperation.in_allCode() || type == TucsonOperation.rd_allCode()
							|| type == TucsonOperation.no_allCode()
							|| type == TucsonOperation.get_Code() || type == TucsonOperation.get_sCode()){
						List<LogicTuple> tupleSetRes = (List<LogicTuple>) msg.getTupleResult();
						ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, msg.isSuccess(), tupleSetRes);
					}else if(type == TucsonOperation.exitCode()){
						setStop();
						break;
					}
					
				}else{
					ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), false, false);
				}
				
				TucsonOperation op = operations.remove(msg.getId());
				if(op.isNoAll() || op.isInAll() || op.isRdAll() || op.isGet() || op.isSet() || op.isGet_s()
						|| op.isSet_s() || op.isOutAll()){
					op.setLogicTupleListResult((List<LogicTuple>) msg.getTupleResult());
				}else{
					op.setTupleResult((LogicTuple) msg.getTupleResult());
				}
				if(msg.isResultSuccess())
					op.setOpResult(Outcome.SUCCESS);
				else
					op.setOpResult(Outcome.FAILURE);
				op.notifyCompletion(ev.operationSucceeded(), msg.isAllowed());
				postEvent(ev);
				
			}
			
		}

		/**
		 * 
		 * @return
		 */
		synchronized boolean isStopped(){
			return stop;
		}

		/**
		 * 
		 */
		synchronized void setStop(){
			stop = true;
		}

		/**
		 * 
		 * @param template
		 * @param tuple
		 * @return
		 */
		LogicTuple unify(TupleTemplate template, Tuple tuple){
			boolean res = template.propagate(p, tuple);
			if(res)
				return (LogicTuple) template;
			else
				return null;

		}
		
	}

	/**
	 * 
	 */
	protected class ControllerSession{
		
		private Controller controller;
		private TucsonProtocol session;

		/**
		 * 
		 * @param c
		 * @param s
		 */
		ControllerSession(Controller c, TucsonProtocol s){
			controller = c;
			session = s;
		}

		/**
		 * 
		 * @return
		 */
		public Controller getController(){
			return controller;
		}

		/**
		 * 
		 * @return
		 */
		public TucsonProtocol getSession(){
			return session;
		}
		
	}

}
