package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.RespectSpecification;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
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

    public IRespectOperation getS(final IId aid,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        return this.core.get(aid, l);
    }

    public IRespectOperation inpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.core.inpS(id, t, l);
    }

    public IRespectOperation inS(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.core.inS(id, t, l);
    }

    public IRespectOperation nopS(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.core.nopS(aid, t, l);
    }

    public IRespectOperation noS(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.core.noS(aid, t, l);
    }

    public IRespectOperation outS(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.core.outS(id, t, l);
    }

    public IRespectOperation rdpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.core.rdpS(id, t, l);
    }

    public IRespectOperation rdS(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.core.rdS(id, t, l);
    }

    public IRespectOperation
            setS(final IId aid, final RespectSpecification spec,
                    final OperationCompletionListener l)
                    throws InvalidTupleException, OperationNotPossibleException {
        if (spec == null) {
            throw new InvalidTupleException();
        }
        return this.core.setS(aid, spec, l);
    }

}
