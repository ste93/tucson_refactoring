package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.core.OperationCompletionListener;

public class OrdinaryAsynchInterface extends RootInterface implements
        IOrdinaryAsynchInterface {

    public OrdinaryAsynchInterface(final IRespectTC core_) {
        super(core_);
    }

    public IRespectOperation get(final IId aid,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        return this.getCore().get(aid, l);
    }

    public IRespectOperation in(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().in(id, t, l);
    }

    public IRespectOperation in_all(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        IRespectOperation op = null;
        try {
            if (t == null) {
                throw new InvalidLogicTupleException();
            } else if (t.getName().equals(",") && (t.getArity() == 2)) {
                op = this.getCore().in_all(aid, new LogicTuple(t.getArg(0)), l);
            } else {
                op = this.getCore().in_all(aid, t, l);
            }
        } catch (final InvalidTupleOperationException e2) {
            e2.printStackTrace();
        }
        return op;
    }

    public IRespectOperation inp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().inp(id, t, l);
    }

    public IRespectOperation no(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().no(id, t, l);
    }

    public IRespectOperation no_all(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        IRespectOperation op = null;
        try {
            if (t == null) {
                throw new InvalidLogicTupleException();
            } else if (t.getName().equals(",") && (t.getArity() == 2)) {
                op = this.getCore().no_all(aid, new LogicTuple(t.getArg(0)), l);
            } else {
                op = this.getCore().no_all(aid, t, l);
            }
        } catch (final InvalidTupleOperationException e2) {
            throw new OperationNotPossibleException();
        }
        return op;
    }

    public IRespectOperation nop(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().nop(id, t, l);
    }

    public IRespectOperation out(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().out(id, t, l);
    }

    public IRespectOperation out_all(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().out_all(id, t, l);
    }

    public IRespectOperation rd(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().rd(id, t, l);
    }

    public IRespectOperation rd_all(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        IRespectOperation op = null;
        try {
            if (t == null) {
                throw new InvalidLogicTupleException();
            } else if (t.getName().equals(",") && (t.getArity() == 2)) {
                op = this.getCore().rd_all(aid, new LogicTuple(t.getArg(0)), l);
            } else {
                op = this.getCore().rd_all(aid, t, l);
            }
        } catch (final InvalidTupleOperationException e2) {
            e2.printStackTrace();
        }
        return op;
    }

    public IRespectOperation rdp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().rdp(id, t, l);
    }

    public IRespectOperation set(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().set(aid, t, l);
    }

    public IRespectOperation spawn(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().spawn(aid, t, l);
    }

    public IRespectOperation uin(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().uin(aid, t, l);
    }

    public IRespectOperation uinp(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().uinp(aid, t, l);
    }

    public IRespectOperation uno(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().uno(id, t, l);
    }

    public IRespectOperation unop(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().unop(id, t, l);
    }

    public IRespectOperation urd(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().urd(aid, t, l);
    }

    public IRespectOperation urdp(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        return this.getCore().urdp(aid, t, l);
    }

}
