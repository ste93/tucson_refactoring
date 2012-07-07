package alice.respect.core;

import alice.logictuple.InvalidLogicTupleException;
import alice.logictuple.InvalidTupleOperationException;
import alice.logictuple.LogicTuple;
import alice.respect.api.INonBlockingContext;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

public class NonBlockingContext implements INonBlockingContext {

	private IRespectTC core;
    
    public NonBlockingContext(IRespectTC core_){
        core=core_;
    }

    public IRespectOperation out(IId id, LogicTuple t, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.out(id,t,l);
		return op;
	}
    
	public IRespectOperation in(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.in(id,t,l);
		return op;
	}

	public IRespectOperation inp(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.inp(id,t,l);
		return op;
	}

	public IRespectOperation rd(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.rd(id,t,l);
		return op;
	}

	public IRespectOperation rdp(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.rdp(id,t,l);
		return op;
	}
	
	public IRespectOperation no(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.no(id,t,l);
		return op;
	}
	
	public IRespectOperation nop(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.nop(id,t,l);
		return op;
	}


	@Override
	public IRespectOperation get(IId aid, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		IRespectOperation op = core.get(aid, l);
		return op;
	}


	@Override
	public IRespectOperation set(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.set(aid,t,l);
		return op;
	}
	
//	my personal updates
	
	public IRespectOperation in_all(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		try {
			if (t==null || t.getArity()!=2){
				throw new InvalidLogicTupleException();
			}
		} catch (InvalidTupleOperationException e2) {
			e2.printStackTrace();
		}
		
		IRespectOperation op = null;
		try {
			op = core.in_all(aid, new LogicTuple(t.getArg(0)), l);
		} catch (InvalidTupleOperationException e) {
			e.printStackTrace();
		}
		return op;
	}

	public IRespectOperation rd_all(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		try {
			if (t==null || t.getArity()!=2){
				throw new InvalidLogicTupleException();
			}
		} catch (InvalidTupleOperationException e2) {
			e2.printStackTrace();
		}
		
		IRespectOperation op = null;
		try {
			op = core.rd_all(aid, new LogicTuple(t.getArg(0)), l);
		} catch (InvalidTupleOperationException e) {
			e.printStackTrace();
		}
		return op;
	}
	
	public IRespectOperation urd(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		
		IRespectOperation op = core.urd(aid, t, l);
		return op;
	}
	
	public IRespectOperation uin(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		
		IRespectOperation op = core.uin(aid, t, l);
		return op;
	}
	
	public IRespectOperation urdp(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		
		IRespectOperation op = core.urdp(aid, t, l);
		return op;
	}
	
	public IRespectOperation uinp(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
			throw new InvalidLogicTupleException();
		}
		
		IRespectOperation op = core.uinp(aid, t, l);
		return op;
	}
	
//	*******************

}
