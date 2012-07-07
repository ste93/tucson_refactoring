package alice.respect.core;

import alice.logictuple.InvalidLogicTupleException;
import alice.logictuple.LogicTuple;
import alice.respect.api.INonBlockingSpecContext;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.OperationNotPossibleException;
import alice.respect.api.RespectSpecification;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

public class NonBlockingSpecContext implements INonBlockingSpecContext {

	private IRespectTC core;
    
    public NonBlockingSpecContext(IRespectTC core_){
        core=core_;
    }

    public IRespectOperation out_s(IId id, LogicTuple t, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.out_s(id,t,l);
		return op;
	}
    
	public IRespectOperation in_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.in_s(id,t,l);
		return op;
	}

	public IRespectOperation inp_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.inp_s(id,t,l);
		return op;
	}

	public IRespectOperation rd_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.rd_s(id,t,l);
		return op;
	}

	public IRespectOperation rdp_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.rdp_s(id,t,l);
		return op;
	}


	@Override
	public IRespectOperation no_s(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.no_s(aid,t,l);
		return op;
	}
	
	@Override
	public IRespectOperation nop_s(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.nop_s(aid,t,l);
		return op;
	}


	@Override
	public IRespectOperation get_s(IId aid, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		IRespectOperation op = core.get(aid, l);
		return op;
	}


	@Override
	public IRespectOperation set_s(IId aid, RespectSpecification spec,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (spec==null){
            throw new InvalidLogicTupleException();
        }
		IRespectOperation op = core.set_s(aid,spec,l);
		return op;
	}

}
