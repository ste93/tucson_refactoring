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

import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Agent Coordination Context enabling interaction with the Ordinary Tuple Space (basic Linda-like
 * primitives) and enacting a BLOCKING behavior from the agent's perspective.
 * This means that whichever is the Linda operation invoked (either suspensive or predicative) the
 * agent stub will block waiting for its completion (communicated by the node side).
 */
public interface OrdinarySynchACC extends RootACC{
	
	/**
	 * Out Linda primitive, synchronous version. Inserts the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation out(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;

	/**
	 * In Linda primitive, synchronous version. Retrieves the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation in(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;

	/**
	 * Rd Linda primitive, synchronous version. Reads (w/o removing) the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation rd(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * Inp Linda primitive, synchronous version. Retrieves the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation inp(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * Rdp Linda primitive, synchronous version. Reads (w/o removing) the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation rdp(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * No Linda primitive, synchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation no(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * Nop Linda primitive, synchronous version. Checks absence of the specified tuple
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if a tuple is found to match the given template,
	 * a failure completion answer is forwarded to the TuCSoN Agent exploiting this proxy
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
	ITucsonOperation nop(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * Set TuCSoN primitive, synchronous version. Inserts all the tuples in the
	 * specified list in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation set(Object tid, LogicTuple tuple, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * Get TuCSoN primitive, synchronous version. Reads (w/o removing) all the tuples
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: is the tuple space is empty, a failure
	 * completion answer is forwarded to the TuCSoN Agent exploiting this proxy
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
	ITucsonOperation get(Object tid, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;

}
