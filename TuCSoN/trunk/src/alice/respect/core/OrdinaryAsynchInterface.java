package alice.respect.core;

import alice.logictuple.BioTuple;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.exceptions.OperationNotPossibleException;

import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

public class OrdinaryAsynchInterface extends RootInterface implements IOrdinaryAsynchInterface {
    
    public OrdinaryAsynchInterface(IRespectTC core_){
        super(core_);
    }

    public IRespectOperation out(IId id, LogicTuple t, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().out(id,t,l);
	}
    
	public IRespectOperation in(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().in(id,t,l);
	}

	public IRespectOperation inp(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().inp(id,t,l);
	}

	public IRespectOperation rd(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().rd(id,t,l);
	}

	public IRespectOperation rdp(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().rdp(id,t,l);
	}
	
	public IRespectOperation no(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().no(id,t,l);
	}
	
	public IRespectOperation nop(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().nop(id,t,l);
	}

	public IRespectOperation get(IId aid, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		return getCore().get(aid, l);
	}

	public IRespectOperation set(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().set(aid,t,l);
	}
	
	public IRespectOperation out_all(IId id, LogicTuple t, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		return getCore().out_all(id,t,l);
	}
	
	public IRespectOperation in_all(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		IRespectOperation op = null;
		try {
			if (t==null)
				throw new InvalidLogicTupleException();
			else if (t.getName().equals(",") && t.getArity()==2){
				op = getCore().in_all(aid, new LogicTuple(t.getArg(0)), l);
			}else{
				op = getCore().in_all(aid,t,l);
			}
		} catch (InvalidTupleOperationException e2) {
			e2.printStackTrace();
		}
		return op;
	}

	public IRespectOperation rd_all(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		IRespectOperation op = null;
		try {
			if (t==null)
				throw new InvalidLogicTupleException();
			else if (t.getName().equals(",") && t.getArity()==2){
				op = getCore().rd_all(aid, new LogicTuple(t.getArg(0)), l);
			}else{
				op = getCore().rd_all(aid,t,l);
			}
		} catch (InvalidTupleOperationException e2) {
			e2.printStackTrace();
		}
		return op;
	}
	
	public IRespectOperation no_all(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		IRespectOperation op = null;
		try {
			if (t==null)
				throw new InvalidLogicTupleException();
			else if (t.getName().equals(",") && t.getArity()==2){
				op = getCore().no_all(aid, new LogicTuple(t.getArg(0)), l);
			}else{
				op = getCore().no_all(aid,t,l);
			}
		} catch (InvalidTupleOperationException e2) {
			throw new OperationNotPossibleException();
		}
		return op;
	}
	
	public IRespectOperation urd(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().urd(aid, t, l);
	}
	
	public IRespectOperation uin(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().uin(aid, t, l);
	}
	
	public IRespectOperation uno(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
	    if (t==null)
	        throw new InvalidLogicTupleException();
		return getCore().uno(id,t,l);
	}
	
	public IRespectOperation urdp(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().urdp(aid, t, l);
	}
	
	public IRespectOperation uinp(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().uinp(aid, t, l);
	}
	
	public IRespectOperation unop(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
	    if (t==null)
	        throw new InvalidLogicTupleException();
		return getCore().unop(id,t,l);
	}

	@Override
	public IRespectOperation spawn(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().spawn(aid, t, l);
	}

	
	//BIO primitives
	
	@Override
	public IRespectOperation out(IId aid, BioTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().out(aid, t, l);
	}

	@Override
	public IRespectOperation inv(IId aid, BioTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().inv(aid, t, l);
	}

	@Override
	public IRespectOperation in(IId aid, BioTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().in(aid, t, l);
	}

	@Override
	public IRespectOperation rdv(IId aid, BioTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().rdv(aid, t, l);
	}

	@Override
	public IRespectOperation rd(IId aid, BioTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
			throw new InvalidLogicTupleException();
		return getCore().rd(aid, t, l);
	}
	
}
