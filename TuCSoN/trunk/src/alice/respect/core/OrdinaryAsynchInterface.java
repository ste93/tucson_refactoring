package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it)
 * 
 */
public class OrdinaryAsynchInterface extends RootInterface implements
        IOrdinaryAsynchInterface {

    /**
     * 
     * @param core
     *            the ReSpecT tuple centre this context refers to
     */
    public OrdinaryAsynchInterface(final IRespectTC core) {
        super(core);
    }

    public IRespectOperation get(final IId aid,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        return this.getCore().get(aid, l);
    }

    public IRespectOperation in(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().in(id, t, l);
    }

    public IRespectOperation inAll(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        IRespectOperation op = null;
        try {
            if (t == null) {
                throw new InvalidTupleException();
            } else if (",".equals(t.getName()) && (t.getArity() == 2)) {
                op = this.getCore().inAll(aid, new LogicTuple(t.getArg(0)), l);
            } else {
                op = this.getCore().inAll(aid, t, l);
            }
        } catch (final InvalidOperationException e) {
            e.printStackTrace();
        }
        return op;
    }

    public IRespectOperation inp(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().inp(id, t, l);
    }

    public IRespectOperation no(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().no(id, t, l);
    }

    public IRespectOperation noAll(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        IRespectOperation op = null;
        try {
            if (t == null) {
                throw new InvalidTupleException();
            } else if (",".equals(t.getName()) && (t.getArity() == 2)) {
                op = this.getCore().noAll(aid, new LogicTuple(t.getArg(0)), l);
            } else {
                op = this.getCore().noAll(aid, t, l);
            }
        } catch (final InvalidOperationException e2) {
            throw new OperationNotPossibleException();
        }
        return op;
    }

    public IRespectOperation nop(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().nop(id, t, l);
    }

    public IRespectOperation out(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().out(id, t, l);
    }

    public IRespectOperation outAll(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().outAll(id, t, l);
    }

    public IRespectOperation rd(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().rd(id, t, l);
    }

    public IRespectOperation rdAll(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        IRespectOperation op = null;
        try {
            if (t == null) {
                throw new InvalidTupleException();
            } else if (",".equals(t.getName()) && (t.getArity() == 2)) {
                op = this.getCore().rdAll(aid, new LogicTuple(t.getArg(0)), l);
            } else {
                op = this.getCore().rdAll(aid, t, l);
            }
        } catch (final InvalidOperationException e) {
            e.printStackTrace();
        }
        return op;
    }

    public IRespectOperation rdp(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().rdp(id, t, l);
    }

    public IRespectOperation set(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().set(aid, t, l);
    }

    public IRespectOperation spawn(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().spawn(aid, t, l);
    }

    public IRespectOperation uin(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().uin(aid, t, l);
    }

    public IRespectOperation uinp(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().uinp(aid, t, l);
    }

    public IRespectOperation uno(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().uno(id, t, l);
    }

    public IRespectOperation unop(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().unop(id, t, l);
    }

    public IRespectOperation urd(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().urd(aid, t, l);
    }

    public IRespectOperation urdp(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        return this.getCore().urdp(aid, t, l);
    }

}
