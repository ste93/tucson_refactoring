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
package alice.respect.core;

import java.util.LinkedList;
import java.util.List;

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.AgentId;
import alice.respect.api.ISpecificationSynchInterface;
import alice.respect.api.IRespectTC;
import alice.respect.api.IRespectOperation;
import alice.respect.api.RespectSpecification;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;

/**
 *
 * A Blocking Context wraps the access to a tuple centre virtual machine
 * for a specific thread of control, providing a blocking interface.
 * 
 * @author aricci
 */
public class SpecificationSynchInterface extends RootInterface implements ISpecificationSynchInterface {
    
    public SpecificationSynchInterface(IRespectTC core){
    	super(core);
    }
    
    public void out_s(AgentId id, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = getCore().out_s(id,t);
		op.waitForOperationCompletion();
    }
    
    public LogicTuple in_s(AgentId id, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = getCore().in_s(id,t);
		op.waitForOperationCompletion();
        return unify(t,op.getLogicTupleResult());
    }
    
	public LogicTuple rd_s(AgentId id, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		IRespectOperation op = getCore().rd_s(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}

	public LogicTuple no_s(AgentId id, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = getCore().no_s(id,t);
		op.waitForOperationCompletion();
        return unify(t,op.getLogicTupleResult());
    }
	
	public LogicTuple inp_s(AgentId id, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		IRespectOperation op = getCore().inp_s(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}
    
	public LogicTuple rdp_s(AgentId id, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		IRespectOperation op = getCore().rdp_s(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}
	
	public LogicTuple nop_s(AgentId id, LogicTuple t) throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		IRespectOperation op = getCore().nop_s(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}
  	
	public List<LogicTuple> set_s(IId aid, RespectSpecification spec) throws OperationNotPossibleException, InvalidSpecificationException {
		IRespectOperation op = getCore().set_s(aid, spec);
		if(aid.toString().equals("'$TucsonNodeService-Agent'") ||
				aid.toString().startsWith("'$Inspector-'"))
			return new LinkedList<LogicTuple>();
//		op.waitForOperationCompletion();
		return op.getLogicTupleListResult();
	}
	
	public List<LogicTuple> set_s(IId aid, LogicTuple t) throws OperationNotPossibleException {
		IRespectOperation op = getCore().set_s(aid, t);
		op.waitForOperationCompletion();
		return op.getLogicTupleListResult();
	}
	
	public List<LogicTuple> get_s(IId aid) throws OperationNotPossibleException {
		IRespectOperation op = getCore().get_s(aid);
		op.waitForOperationCompletion();
		return op.getLogicTupleListResult();
	}   
    
}

