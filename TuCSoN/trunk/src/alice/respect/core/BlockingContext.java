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

import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exception.InvalidLogicTupleException;
import alice.logictuple.exception.InvalidTupleOperationException;

import alice.respect.api.AgentId;
import alice.respect.api.IOrdinarySynchInterface;
import alice.respect.api.IRespectTC;
import alice.respect.api.IRespectOperation;
import alice.respect.api.exceptions.OperationNotPossibleException;

/**
 *
 * A Blocking Context wraps the access to a tuple centre virtual machine
 * for a specific thread of control, providing a blocking interface.
 * 
 * @author aricci
 */
public class BlockingContext extends AbstractContext implements IOrdinarySynchInterface {
    
    public BlockingContext(IRespectTC core){
        super(core);
    }
    
    public void out(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
    	OperationNotPossibleException {
        if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().out(id,t);
		op.waitForOperationCompletion();
    }
    
    public LogicTuple in(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
    	OperationNotPossibleException {
        if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().in(id,t);
		op.waitForOperationCompletion();
        return unify(t,op.getLogicTupleResult());
    }
    
    public LogicTuple no(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
    	OperationNotPossibleException {
        if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().no(id,t);
		op.waitForOperationCompletion();
        return unify(t,op.getLogicTupleResult());
    }
    
	public LogicTuple rd(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().rd(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}

	public LogicTuple inp(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().inp(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}
	
	public LogicTuple nop(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().nop(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}
    
	public LogicTuple rdp(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().rdp(id,t);
		op.waitForOperationCompletion();
		return unify(t,op.getLogicTupleResult());
	}
  	
	public List<LogicTuple> set(AgentId aid, LogicTuple tuple) throws OperationNotPossibleException,
		InvalidLogicTupleException {		
		IRespectOperation op = getCore().set(aid, tuple);
		op.waitForOperationCompletion();
		return op.getLogicTupleListResult();
	}
	
	public List<LogicTuple> get(AgentId aid) throws OperationNotPossibleException {
		IRespectOperation op = getCore().get(aid);
		op.waitForOperationCompletion();
		return op.getLogicTupleListResult();
	}
	
	public void out_all(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
	    if (t==null)
	        throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().out_all(id,t);
		op.waitForOperationCompletion();
	}
	
	public LogicTuple in_all(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
		IRespectOperation op = null;
		TupleArgument arg = null;
		try {
			if (t==null || t.getArity()!=2)
				throw new InvalidLogicTupleException();
			op = getCore().in_all(id,new LogicTuple(t.getArg(0)));
			op.waitForOperationCompletion();
			arg = t.getArg(1);
		} catch (InvalidTupleOperationException e2) {
			throw new OperationNotPossibleException();
		}
		return unify(new LogicTuple(new TupleArgument(arg.toTerm())),op.getLogicTupleResult());
	}

	public LogicTuple rd_all(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
		IRespectOperation op = null;
		TupleArgument arg = null;
		try {
			if (t==null || t.getArity()!=2)
				throw new InvalidLogicTupleException();
			op = getCore().rd_all(id,new LogicTuple(t.getArg(0)));
			op.waitForOperationCompletion();
			arg = t.getArg(1);
		} catch (InvalidTupleOperationException e2) {
			throw new OperationNotPossibleException();
		}
		return unify(new LogicTuple(new TupleArgument(arg.toTerm())),op.getLogicTupleResult());
	}
	
	public LogicTuple no_all(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
		IRespectOperation op = null;
		TupleArgument arg = null;
		try {
			if (t==null || t.getArity()!=2)
				throw new InvalidLogicTupleException();
			op = getCore().no_all(id,new LogicTuple(t.getArg(0)));
			op.waitForOperationCompletion();
			arg = t.getArg(1);
		} catch (InvalidTupleOperationException e2) {
			throw new OperationNotPossibleException();
		}
		return unify(new LogicTuple(new TupleArgument(arg.toTerm())),op.getLogicTupleResult());
	}
	
    public LogicTuple urd(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
    	OperationNotPossibleException {
        if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().urd(id,t);
		op.waitForOperationCompletion();
        return unify(t,op.getLogicTupleResult());
    }
    
    public LogicTuple uin(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
    	OperationNotPossibleException {
        if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().uin(id,t);
		op.waitForOperationCompletion();
        return unify(t,op.getLogicTupleResult());
    }
    
    public LogicTuple uno(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
	    if (t==null)
	        throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().uno(id,t);
		op.waitForOperationCompletion();
	    return unify(t,op.getLogicTupleResult());
	}
    
    public LogicTuple urdp(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
    	OperationNotPossibleException {
        if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().urdp(id,t);
		op.waitForOperationCompletion();
		LogicTuple result = op.getLogicTupleResult(); 
		return unify(t,result);
    }
    
    public LogicTuple uinp(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
    	OperationNotPossibleException {
        if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().uinp(id,t);
		op.waitForOperationCompletion();
		LogicTuple result = op.getLogicTupleResult(); 
		return unify(t,result);
    }
    
    public LogicTuple unop(AgentId id, LogicTuple t) throws InvalidLogicTupleException,
		OperationNotPossibleException {
	    if (t==null)
	        throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().unop(id,t);
		op.waitForOperationCompletion();
		LogicTuple result = op.getLogicTupleResult(); 
		return unify(t,result);
	}
	
}
