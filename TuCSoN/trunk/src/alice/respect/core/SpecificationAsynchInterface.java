package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.RespectSpecification;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 *
 * @author ste (mailto: s.mariani@unibo.it)
 *
 */
public class SpecificationAsynchInterface implements
        ISpecificationAsynchInterface {

    private final IRespectTC core;

    /**
     *
     * @param c
     *            the ReSpecT tuple centres manager this interface refers to
     */
    public SpecificationAsynchInterface(final IRespectTC c) {
        this.core = c;
    }

    @Override
    public IRespectOperation getS(final IId aid,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        return this.core.get(aid, l);
    }

    @Override
    public IRespectOperation inpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.core.inpS(id, t, l);
    }

    @Override
    public IRespectOperation inS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.core.inS(id, t, l);
    }

    @Override
    public IRespectOperation nopS(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.core.nopS(aid, t, l);
    }

    @Override
    public IRespectOperation noS(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.core.noS(aid, t, l);
    }

    @Override
    public IRespectOperation outS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.core.outS(id, t, l);
    }

    @Override
    public IRespectOperation rdpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.core.rdpS(id, t, l);
    }

    @Override
    public IRespectOperation rdS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.core.rdS(id, t, l);
    }

    @Override
    public IRespectOperation setS(final IId aid,
            final RespectSpecification spec, final OperationCompletionListener l)
            throws InvalidSpecificationException, OperationNotPossibleException {
        if (spec == null) {
            throw new InvalidSpecificationException("Null value");
        }
        return this.core.setS(aid, spec, l);
    }
}
