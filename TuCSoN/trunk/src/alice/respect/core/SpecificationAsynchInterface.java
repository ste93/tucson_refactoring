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

    public IRespectOperation getS(final IId aid,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        return this.core.get(aid, l);
    }

    public IRespectOperation inpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.inpS(id, t, l);
    }

    public IRespectOperation inS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.inS(id, t, l);
    }

    public IRespectOperation nopS(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.nopS(aid, t, l);
    }

    public IRespectOperation noS(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.noS(aid, t, l);
    }

    public IRespectOperation outS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.outS(id, t, l);
    }

    public IRespectOperation rdpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.rdpS(id, t, l);
    }

    public IRespectOperation rdS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.rdS(id, t, l);
    }

    public IRespectOperation
            setS(final IId aid, final RespectSpecification spec,
                    final OperationCompletionListener l)
                    throws InvalidLogicTupleException,
                    OperationNotPossibleException {
        if (spec == null) {
            throw new InvalidLogicTupleException();
        }
        return this.core.setS(aid, spec, l);
    }

}
