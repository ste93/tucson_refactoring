package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.RespectSpecification;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

public class SpecificationAsynchInterface implements
        ISpecificationAsynchInterface {

    private final IRespectTC core;

    public SpecificationAsynchInterface(final IRespectTC core_) {
        this.core = core_;
    }

    public IRespectOperation get_s(final IId aid,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        return this.core.get(aid, l);
    }

    public IRespectOperation in_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.in_s(id, t, l);
    }

    public IRespectOperation inp_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.inp_s(id, t, l);
    }

    public IRespectOperation no_s(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.no_s(aid, t, l);
    }

    public IRespectOperation nop_s(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.nop_s(aid, t, l);
    }

    public IRespectOperation out_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.out_s(id, t, l);
    }

    public IRespectOperation rd_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.rd_s(id, t, l);
    }

    public IRespectOperation rdp_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.rdp_s(id, t, l);
    }

    public IRespectOperation
            set_s(final IId aid, final RespectSpecification spec,
                    final OperationCompletionListener l)
                    throws InvalidLogicTupleException,
                    OperationNotPossibleException {
        if (spec == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.set_s(aid, spec, l);
    }

}
