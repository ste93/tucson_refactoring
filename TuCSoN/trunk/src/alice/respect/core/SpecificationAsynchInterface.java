package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.logictuple.exception.InvalidLogicTupleException;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.RespectSpecification;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

public class SpecificationAsynchInterface implements ISpecificationAsynchInterface {

	private IRespectTC core;
    
    public SpecificationAsynchInterface(IRespectTC core_){
        core=core_;
    }

    public IRespectOperation out_s(IId id, LogicTuple t, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		return core.out_s(id,t,l);
	}
    
	public IRespectOperation in_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		return core.in_s(id,t,l);
	}

	public IRespectOperation inp_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		return core.inp_s(id,t,l);
	}

	public IRespectOperation rd_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		return core.rd_s(id,t,l);
	}

	public IRespectOperation rdp_s(IId id, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		return core.rdp_s(id,t,l);
	}

	public IRespectOperation no_s(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		return core.no_s(aid,t,l);
	}
	
	public IRespectOperation nop_s(IId aid, LogicTuple t,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null){
            throw new InvalidLogicTupleException();
        }
		return core.nop_s(aid,t,l);
	}

	public IRespectOperation get_s(IId aid, OperationCompletionListener l)
			throws InvalidLogicTupleException, OperationNotPossibleException {
		return core.get(aid, l);
	}

	public IRespectOperation set_s(IId aid, RespectSpecification spec,
			OperationCompletionListener l) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (spec==null){
            throw new InvalidLogicTupleException();
        }
		return core.set_s(aid,spec,l);
	}

}
