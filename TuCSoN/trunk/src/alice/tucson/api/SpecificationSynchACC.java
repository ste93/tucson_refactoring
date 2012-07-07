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

import alice.logictuple.InvalidTupleOperationException;
import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

import alice.tuplecentre.core.OperationTimeOutException;

/**
 * Agent Coordination Context enabling interaction with the Specification Tuple Space (storing ReSpecT
 * reactions that is the coordination laws) and enacting a BLOCKING behavior from the agent's
 * perspective.
 * This means that whichever is the operation invoked (either suspensive or predicative) the
 * agent stub will block waiting for its completion (communicated by the node side).
 */
public interface SpecificationSynchACC extends RootACC{
	
	/**
	 * Out_s Specification primitive, synchronous version. Adds the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * Again, this TuCSoN out_s primitive assumes the ORDERED semantics,
	 * hence the reaction specification is SUDDENLY injected in the target space
	 * (if the primitive successfully completes)
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody
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
	ITucsonOperation out_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	/**
	 * In_s Specification primitive, synchronous version. Retrieves the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody
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
	ITucsonOperation in_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	/**
	 * Rd_s Specification primitive, synchronous version. Reads (w/o removing) the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * waiting the completion answer from the TuCSoN node for a maximum time
	 * specified in ms timeunit.
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody
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
	ITucsonOperation rd_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
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
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody
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
	ITucsonOperation inp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
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
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody
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
	ITucsonOperation rdp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	/**
	 * No_s Specification primitive, synchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody
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
	ITucsonOperation no_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	/**
	 * Nop_s Specification primitive, synchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if a Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody
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
	ITucsonOperation nop_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			Long ms) throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	/**
	 * Set_s TuCSoN primitive, synchronous version. Replace the specification space
	 * with the newly specified (in the form of a string) in the given target
	 * tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation set_s(Object tid, String spec, Long ms) throws TucsonOperationNotPossibleException,
	UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * Set_s TuCSoN primitive, synchronous version. Replace the specification space
	 * with the newly specified (in the form of a string) in the given target
	 * tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
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
	ITucsonOperation set_s(Object tid, LogicTuple spec, Long ms)
		throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;
	
	/**
	 * Get_s TuCSoN primitive, synchronous version. Reads (w/o removing) all the Reaction Specifications
	 * in the given target tuplecentre, waiting the completion answer from
	 * the TuCSoN node for a maximum time specified in ms timeunit.
	 * 
	 * Semantics is NOT SUSPENSIVE: if the specification space is empty, a failure
	 * completion answer is forwarded to the TuCSoN Agent exploiting this proxy
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
	ITucsonOperation get_s(Object tid, Long ms) throws TucsonOperationNotPossibleException,
	UnreachableNodeException, OperationTimeOutException, InvalidTupleOperationException;
	
}
