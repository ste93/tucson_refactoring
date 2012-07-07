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

import alice.logictuple.InvalidLogicTupleException;
import alice.logictuple.InvalidTupleOperationException;
import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

/**
 * Agent Coordination Context enabling interaction with the Specification Tuple Space (storing ReSpecT
 * reactions that is the coordination laws) and enacting a NON-BLOCKING behavior from the agent's
 * perspective.
 * This means that whichever is the operation invoked (either suspensive or predicative) the
 * agent stub will NOT block waiting for its completion but will be asynchronously notified (by the
 * node side).
 */
public interface SpecificationAsynchACC extends RootACC{
	
	/**
	 * Out_s Specification primitive, asynchronous version. Adds the specified Reaction
	 * Specification (wrapped in a Logic Tuple) in the given target tuplecentre,
	 * WITHOUT waiting the completion answer from the TuCSoN node. The TuCSoN agent
	 * this proxy instance refers to will be asynchronously notified upon need
	 * (that is, when completion reply arrives).
	 * 
	 * Again, this TuCSoN out_s primitive assumes the ORDERED semantics,
	 * hence the reaction specification is SUDDENLY injected in the target space
	 * (if the primitive successfully completes)
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody 
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
	ITucsonOperation out_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException,
			UnreachableNodeException;
	
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
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody 
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
	ITucsonOperation in_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException,
			UnreachableNodeException;
	
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
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody 
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
	ITucsonOperation rd_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException,
			UnreachableNodeException;

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
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody 
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
	ITucsonOperation inp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException,
			UnreachableNodeException;
		
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
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody 
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
	ITucsonOperation rdp_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException,
			UnreachableNodeException;
	
	/**
	 * No_s Specification primitive, asynchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Notice that the primitive semantics is still SUSPENSIVE: until any Reaction Specification
	 * is found to match the given template, no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy (but thanks to asynchronous behaviour,
	 * TuCSoN Agent could go something else instead of getting stuck)
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody 
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
	ITucsonOperation no_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException,
			UnreachableNodeException;
	
	/**
	 * Nop_s Specification primitive, asynchronous version. Checks absence of the
	 * specified Reaction Specification (wrapped in a Logic Tuple)
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: if a Reaction Specification is found
	 * to match the given template, a failure completion answer is forwarded to
	 * the TuCSoN Agent exploiting this proxy
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param event 
	 * @param guards 
	 * @param reactionBody 
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
	ITucsonOperation nop_s(Object tid, LogicTuple event, LogicTuple guards, LogicTuple reactionBody,
			TucsonOperationCompletionListener l) throws TucsonOperationNotPossibleException,
			UnreachableNodeException;
	
	/**
	 * Set_s TuCSoN primitive, asynchronous version. Replace the specification space
	 * with the newly specified (in the form of a string) in the given target
	 * tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
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
	ITucsonOperation set_s(Object tid, String spec, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException,
			InvalidLogicTupleException;
	
	/**
	 * Get_s TuCSoN primitive, asynchronous version. Reads (w/o removing) all the Reaction Specifications
	 * in the given target tuplecentre, WITHOUT waiting the completion answer from
	 * the TuCSoN node. The TuCSoN agent this proxy instance refers to will be
	 * asynchronously notified upon need (that is, when completion reply arrives).
	 * 
	 * Semantics is NOT SUSPENSIVE: is the specification space is empty, a failure
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
	 * @throws InvalidTupleOperationException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.respect.api.RespectSpecification RespectSpecification
	 */
	ITucsonOperation get_s(Object tid, TucsonOperationCompletionListener l)
			throws TucsonOperationNotPossibleException, UnreachableNodeException,
			InvalidTupleOperationException;
	
}
