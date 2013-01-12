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

import alice.logictuple.BioTuple;
import alice.logictuple.LogicTuple;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public interface UniformSynchACC extends RootACC{

	/**
	 * <code>in</code> Linda primitive, retrieves the specified bio tuple from the given target
	 * tuplecentre. The internal multiplicity value of the bio tuple is a constant or a variable.
	 * In the first case, will be retrieves from the space an amount of the tuple equal to the value
	 * of the constant; while in the second case, will be retrives the entire tuple.
	 * 
	 * If two or more tuples match the specified template, one is removed and returned with 
	 * probability given by its multiplicity. 
	 * 
	 * Notice that the primitive semantics is SUSPENSIVE: until no bio tuple is found
	 * to match the given template(with multiplicity greater than or equal to the value
	 * of the constant, if it is specified), no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this ACC, which then is blocked waiting.
	 * 
	 * @param tid the target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple the bio tuple to be retrieved from the target tuplecentre
	 * @param timeout the maximum waiting time for completion tolerated by the TuCSoN agent
	 * behind this ACC. Notice that reaching the timeout just unblocks the agent, but
	 * the request IS NOT REMOVED from TuCSoN node pending requests (will still be served
	 * at sometime in the future).
	 * 
	 * @return the interface to access the data about TuCSoN operations outcome.
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	ITucsonOperation uin(Object tid, BioTuple tuple, Long timeout)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;

	
	/**
	 * <code>rd</code> Linda primitive, reads (w/o removing) the specified bio tuple
	 * from the given target tuplecentre. The internal multiplicity value of the bio tuple is a constant or a variable.
	 * In the first case, will be reads from the space an amount of the tuple equal to the value
	 * of the constant; while in the second case, will be reads the entire tuple.
	 * 
	 * If two or more tuples match the specified template, one is returned (w/o removing) with 
	 * probability given by its multiplicity. 
	 * 
	 * Notice that the primitive semantics is SUSPENSIVE: until no bio tuple is found
	 * to match the given template(with multiplicity greater than or equal to the value
	 * of the constant, if it is specified), no success completion answer is forwarded to
	 * the TuCSoN Agent exploiting this ACC, which then is blocked waiting.
	 * 
	 * @param tid the target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param tuple the bio tuple to be read from the target tuplecentre
	 * @param timeout the maximum waiting time for completion tolerated by the TuCSoN agent
	 * behind this ACC. Notice that reaching the timeout just unblocks the agent, but
	 * the request IS NOT REMOVED from TuCSoN node pending requests (will still be served
	 * at sometime in the future).
	 * 
	 * @return the interface to access the data about TuCSoN operations outcome.
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * @throws OperationTimeOutException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	ITucsonOperation urd(Object tid, BioTuple tuple, Long timeout)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException;

//TODO: not yet Bio version
	
	ITucsonOperation uno(Object tid, BioTuple tuple, Long timeout)
			throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	ITucsonOperation uinp(Object tid, BioTuple tuple, Long timeout)
			throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	ITucsonOperation urdp(Object tid, BioTuple tuple, Long timeout)
			throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
	
	ITucsonOperation unop(Object tid, BioTuple tuple, Long timeout)
			throws TucsonOperationNotPossibleException, UnreachableNodeException,
			OperationTimeOutException;
		
	
}
