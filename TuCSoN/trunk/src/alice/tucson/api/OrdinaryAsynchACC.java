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

import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

/**
 * Agent Coordination Context enabling interaction with the Ordinary Tuple Space (basic Linda-like
 * primitives) and enacting a NON-BLOCKING behavior from the agent's perspective.
 * This means that whichever is the Linda operation invoked (either suspensive or predicative) the
 * agent stub will NOT block waiting for its completion but will be asynchronously notified (by the
 * node side).
 */
public interface OrdinaryAsynchACC extends RootACC{

	/**
	 * Out Linda primitive, asynchronous version. Inserts the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
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
	ITucsonOperation out(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;

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
	ITucsonOperation in(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;

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
	ITucsonOperation rd(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
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
	ITucsonOperation inp(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;

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
	ITucsonOperation rdp(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;

	/**
	 * No Linda primitive, asynchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Notice that the primitive semantics is still SUSPENSIVE: until any tuple is found
	 * to match the given template, no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy (but thanks to asynchronous behaviour,
	 * TuCSoN Agent could do something else instead of getting stuck)
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
	ITucsonOperation no(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	/**
	 * Nop Linda primitive, asynchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * This time the primitive semantics is NOT SUSPENSIVE: if a tuple is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
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
	ITucsonOperation nop(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	/**
	 * Set TuCSoN primitive, asynchronous version. Inserts all the tuples in the
	 * specified list in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
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
	ITucsonOperation set(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	/**
	 * Get TuCSoN primitive, asynchronous version. Reads (w/o removing) all the tuples
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: is the tuple space is empty, a failure
	 * completion answer is forwarded to the TuCSoN Agent exploiting this proxy
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
	ITucsonOperation get(Object tid, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
	ITucsonOperation spawn(Object tid, LogicTuple tuple, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException;
	
}
