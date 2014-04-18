/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.respect.api;

import alice.logictuple.LogicTuple;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.respect.core.RespectVM;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * Basic usage interface of a RespecT Tuple Centre
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public interface IRespectTC {
    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @return the operation requested the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation get(IId id) throws OperationNotPossibleException;

    /**
     * Gets the whole tuple set
     * 
     * @param id
     *            agent identifier
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation get(IId id, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * Gets the tuple centre id
     * 
     * @return the tuple centre id
     */
    TupleCentreId getId();

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation getS(IId aid) throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation getS(IId aid, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @return this ReSpecT VM
     */
    RespectVM getVM();

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation the tuple argument of the
     *            operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation in(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * Retrieves a tuple in the tuple centre
     * 
     * @param id
     *            agent identifier
     * @param t
     *            the tuple argument of the operation the tuple
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation in(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * Retrieves all tuples in the tuple centre matching the template
     * 
     * @param id
     *            agent identifier
     * @param t
     *            the tuple argument of the operation the tuple
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inAll(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation the tuple argument of the
     *            operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inAll(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation the tuple argument of the
     *            operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inp(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * Tries to retrieve a tuple in the tuple centre
     * 
     * @param id
     *            agent identifier
     * @param t
     *            the tuple argument of the operation the tuple
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inp(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation the identifier
     *            of who is invoking the operation
     * @param t
     *            the tuple argument of the operation the tuple argument of the
     *            operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inpS(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation the identifier
     *            of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inpS(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inS(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation inS(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation no(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation no(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation noAll(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation noAll(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation nop(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation nop(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation nopS(IId aid, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation nopS(IId aid, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation noS(IId aid, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation noS(IId aid, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation out(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation out(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation outAll(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation outAll(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation outS(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * Inserts a tuple in the tuple centre
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation outS(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rd(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * Reads a tuple in the tuple centre
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rd(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * Retrieves all tuples in the tuple centre matching the template without
     * remove them
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdAll(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdAll(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdp(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * Tries to read a tuple in the tuple centre
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdp(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdpS(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdpS(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdS(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation rdS(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operationuple
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws InvalidTupleException
     *             if the given argument is not a valid Prolog tuple
     */
    IRespectOperation set(IId id, LogicTuple t)
            throws OperationNotPossibleException, InvalidTupleException;

    /**
     * Gets the whole tuple set
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws InvalidTupleException
     *             if the given argument is not a valid Prolog tuple
     */
    IRespectOperation set(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException, InvalidTupleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation setS(IId aid, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation setS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param spec
     *            the ReSpecT specification argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws InvalidSpecificationException
     *             if the given ReSpecT specification has syntactical errors
     */
    IRespectOperation setS(IId aid, RespectSpecification spec)
            throws OperationNotPossibleException, InvalidSpecificationException;

    /**
     * 
     * @param aid
     *            the identifier of who is invoking the operation
     * @param spec
     *            the ReSpecT specification argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation setS(IId aid, RespectSpecification spec,
            OperationCompletionListener l) throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation spawn(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation spawn(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * Retrieves all tuples in the tuple centre matching the template
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation uin(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation uin(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * Retrieves all tuples in the tuple centre matching the template
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation uinp(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation uinp(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation uno(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation uno(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation unop(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation unop(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * Retrieves all tuples in the tuple centre matching the template
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation urd(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation urd(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;

    /**
     * Retrieves all tuples in the tuple centre matching the template
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation urdp(IId id, LogicTuple t)
            throws OperationNotPossibleException;

    /**
     * 
     * @param id
     *            the identifier of who is invoking the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the operation requested
     * @throws OperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    IRespectOperation urdp(IId id, LogicTuple t, OperationCompletionListener l)
            throws OperationNotPossibleException;
}
