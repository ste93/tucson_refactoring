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
package alice.tucson.api;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Agent Coordination Context enabling interaction with the Ordinary Tuple Space
 * and enacting a BLOCKING behavior from the agent's perspective. This means
 * that whichever is the Linda operation invoked (either suspensive or
 * predicative) the agent proxy WILL block waiting for its completion (either
 * success or failure).
 *
 * @see alice.tucson.service.ACCProxyAgentSide ACCProxyAgentSide
 * @see alice.tucson.service.ACCProxyNodeSide ACCProxyNodeSide
 *
 * @author ste (mailto: s.mariani@unibo.it)
 */
public interface OrdinarySynchACC extends RootACC {

    /**
     * <code>get</code> TuCSoN primitive, reads (w/o removing) all the tuples in
     * the given target tuplecentre.
     *
     * Semantics is NOT SUSPENSIVE: if the tuple space is empty, an empty list
     * is returned to the TuCSoN Agent exploiting this ACC.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation get(TupleCentreId tid, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>in</code> Linda primitive, retrieves the specified tuple from the
     * given target tuplecentre.
     *
     * Notice that the primitive semantics is SUSPENSIVE: until no tuple is
     * found to match the given template, no success completion answer is
     * forwarded to the TuCSoN Agent exploiting this ACC, which then is blocked
     * waiting.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the tuple to be retrieved from the target tuplecentre
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation in(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>inp</code> Linda primitive, retrieves the specified tuple from the
     * given target tuplecentre.
     *
     * This time the primitive semantics is NOT SUSPENSIVE: if no tuple is found
     * to match the given template, a failure completion answer is forwarded to
     * the TuCSoN Agent exploiting this ACC.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the tuple to be retrieved from the target tuplecentre
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation inp(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>no</code> TuCSoN primitive, checks absence of the specified tuple
     * in the given target tuplecentre.
     *
     * Notice that the primitive semantics is SUSPENSIVE: until any tuple is
     * found to match the given template, no success completion answer is
     * forwarded to the TuCSoN Agent exploiting this ACC, which then is blocked
     * waiting.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the tuple to be checked for absence from the target
     *            tuplecentre
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation no(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>nop</code> TuCSoN primitive, checks absence of the specified tuple
     * in the given target tuplecentre.
     *
     * This time the primitive semantics is NOT SUSPENSIVE: if any tuple is
     * found to match the given template, a failure completion answer is
     * forwarded to the TuCSoN Agent exploiting this ACC.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the tuple to be checked for absence from the target
     *            tuplecentre
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation nop(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>out</code> Linda primitive, inserts the specified tuple in the
     * given target tuplecentre.
     *
     * Notice that TuCSoN out primitive assumes the ORDERED version of this
     * primitive, hence the tuple is SUDDENLY injected in the target space (if
     * the primitive successfully completes)
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the tuple to be emitted in the target tuplecentre
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation out(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>rd</code> Linda primitive, reads (w/o removing) the specified tuple
     * from the given target tuplecentre.
     *
     * Notice that the primitive semantics is SUSPENSIVE: until no tuple is
     * found to match the given template, no success completion answer is
     * forwarded to the TuCSoN Agent exploiting this ACC, which then is blocked
     * waiting.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the tuple to be read from the target tuplecentre
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation rd(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>rdp</code> Linda primitive, reads (w/o removing) the specified
     * tuple from the given target tuplecentre.
     *
     * This time the primitive semantics is NOT SUSPENSIVE: if no tuple is found
     * to match the given template, a failure completion answer is forwarded to
     * the TuCSoN Agent exploiting this ACC.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the tuple to be read from the target tuplecentre
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation rdp(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>set</code> TuCSoN primitive, to replace all the tuples in the given
     * target tuplecentre with that specified in the given list.
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param tuple
     *            the Prolog list of all the tuples to be injected (overwriting
     *            space)
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation set(TupleCentreId tid, Tuple tuple, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * <code>spawn</code> TuCSoN primitive, starts a parallel computational
     * activity within the target node.
     *
     * Notice that semantics is still NOT SUSPENSIVE: as soon as the parallel
     * activity has been started by the node, the completion is returned to the
     * TuCSoN Agent exploiting this ACC. This is due to very nature of the spawn
     * primitive, which is exactly meant to ASYNCHRONOUSLY start a PARALLEL
     * computational activity (despite this ACC being synchronous).
     *
     * @param tid
     *            the target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param toSpawn
     *            the tuple storing the activity to spawn as a parallel
     *            computation. Must be a Prolog term with functor name
     *            <code>exec/solve</code>, storing either a Java qualified class
     *            name (dotted-list of packages and <code>.class</code>
     *            extension too) or the filepath to a valid Prolog theory and a
     *            valid Prolog goal to be checked. E.g.:
     *            <code>exec('list.of.packages.YourClass.class')</code> OR
     *            <code>solve('path/to/Prolog/Theory.pl', yourGoal)</code>
     * @param timeout
     *            the maximum waiting time for completion tolerated by the
     *            TuCSoN agent behind this ACC. Notice that reaching the timeout
     *            just unblocks the agent, but the request IS NOT REMOVED from
     *            TuCSoN node pending requests (will still be served at sometime
     *            in the future).
     *
     * @return the interface to access the data about TuCSoN operations outcome.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tuplecentre.api.TupleCentreId TupleCentreId
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    ITucsonOperation spawn(TupleCentreId tid, Tuple toSpawn, Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;
}
