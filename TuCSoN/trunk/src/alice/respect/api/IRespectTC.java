/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it
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
package alice.respect.api;

import alice.logictuple.*;
import alice.respect.core.RespectVM;

import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * Basic usage interface of a  RespecT Tuple Centre
 * 
 * 
 * @author aricci
 */
public interface IRespectTC  {
	
	/**
	 * 
	 * @param id
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation out(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation out(IId id, LogicTuple t) throws OperationNotPossibleException;
   
	/**
	 * Gets the whole tuple set
	 * 
	 * @param id agent identifier
	 * @param listener listening for operation completion
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation get(IId id,OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation get(IId id) throws OperationNotPossibleException;
	
	/**
	 * Gets the whole tuple set
	 * 
	 * @param id agent identifier
	 * @param listener listening for operation completion
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 * @throws InvalidLogicTupleException 
	 */
	IRespectOperation set(IId id, LogicTuple tuple, OperationCompletionListener l) throws OperationNotPossibleException, InvalidLogicTupleException;
	
	IRespectOperation set(IId id, LogicTuple tuple) throws OperationNotPossibleException, InvalidLogicTupleException;
	
	/**
	 * Retrieves a tuple in the tuple centre
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @param listener listening for operation completion
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation in(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation in(IId id, LogicTuple t) throws OperationNotPossibleException;
	 
	/**
	 * Reads a tuple in the tuple centre
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @param listener listening for operation completion
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation rd(IId id, LogicTuple t, OperationCompletionListener l)  throws OperationNotPossibleException;
	
	IRespectOperation rd(IId id, LogicTuple t)  throws OperationNotPossibleException;
    
	/**
	 * Tries to retrieve a tuple in the tuple centre
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @param listener listening for operation completion
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation inp(IId id, LogicTuple t, OperationCompletionListener l)  throws OperationNotPossibleException;
	
	IRespectOperation inp(IId id, LogicTuple t)  throws OperationNotPossibleException;
	
	/**
	 * Tries to read a tuple in the tuple centre
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @param listener listening for operation completion
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation rdp(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation rdp(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param id
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation no(IId id, LogicTuple t, OperationCompletionListener l)  throws OperationNotPossibleException;
	
	IRespectOperation no(IId id, LogicTuple t)  throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param id
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation nop(IId id, LogicTuple t, OperationCompletionListener l)  throws OperationNotPossibleException;
	
	IRespectOperation nop(IId id, LogicTuple t)  throws OperationNotPossibleException;
    
	
	
	/**
	 * Inserts a tuple in the tuple centre
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @param listener listening for operation completion
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation out_s(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	IRespectOperation out_s(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param id
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation in_s(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	IRespectOperation in_s(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param id
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation rd_s(IId id, LogicTuple t, OperationCompletionListener l)  throws OperationNotPossibleException;	

	IRespectOperation rd_s(IId id, LogicTuple t)  throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param id
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation inp_s(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	IRespectOperation inp_s(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param id
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation rdp_s(IId id, LogicTuple t, OperationCompletionListener l)  throws OperationNotPossibleException;
	
	IRespectOperation rdp_s(IId id, LogicTuple t)  throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param aid
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation no_s(IId aid, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	IRespectOperation no_s(IId aid, LogicTuple t) throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param aid
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation nop_s(IId aid, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	IRespectOperation nop_s(IId aid, LogicTuple t) throws OperationNotPossibleException;
	
	/**
	 * 
	 * @param aid
	 * @param t
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation set_s(IId aid, RespectSpecification spec, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation set_s(IId aid, RespectSpecification spec) throws OperationNotPossibleException, InvalidSpecificationException;
	
	IRespectOperation set_s(IId aid, LogicTuple t) throws OperationNotPossibleException;

	/**
	 * 
	 * @param aid
	 * @param l
	 * @return
	 * @throws OperationNotPossibleException
	 */
	IRespectOperation get_s(IId aid, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation get_s(IId aid) throws OperationNotPossibleException;
	
	
	
//	my personal updates
	
	/**
	 * Retrieves all tuples in the tuple centre matching the template
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation in_all(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	IRespectOperation in_all(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	/**
	 * Retrieves all tuples in the tuple centre matching the template without remove them 
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation rd_all(IId id, LogicTuple t) throws OperationNotPossibleException;

	IRespectOperation rd_all(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation no_all(IId id, LogicTuple t) throws OperationNotPossibleException;

	IRespectOperation no_all(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	/**
	 * Retrieves all tuples in the tuple centre matching the template
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation urd(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	IRespectOperation urd(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	/**
	 * Retrieves all tuples in the tuple centre matching the template
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation uin(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	IRespectOperation uin(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation uno(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	IRespectOperation uno(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	/**
	 * Retrieves all tuples in the tuple centre matching the template
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation urdp(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	IRespectOperation urdp(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	/**
	 * Retrieves all tuples in the tuple centre matching the template
	 * 
	 * @param id agent identifier
	 * @param t the tuple
	 * @return The object representing the operation
	 * @throws OperationNotPossibleException if the operation is not possible given current state of the tuple centre
	 */
	IRespectOperation uinp(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	IRespectOperation uinp(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;
	
	IRespectOperation unop(IId id, LogicTuple t) throws OperationNotPossibleException;
	
	IRespectOperation unop(IId id, LogicTuple t, OperationCompletionListener l) throws OperationNotPossibleException;

	//	*******************
	
	
	
	/**
	 * Gets the tuple centre id
	 * 
	 * @return the id
	 */
	TupleCentreId getId();
	
	RespectVM getVM();

}