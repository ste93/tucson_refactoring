package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
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

    @Override
    public IRespectOperation get(final IId aid,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        return this.getCore().get(aid, l);
    }

    @Override
    public IRespectOperation in(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().in(id, t, l);
    }

    @Override
    public IRespectOperation inAll(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        IRespectOperation op = null;
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        } else if (",".equals(t.getName()) && t.getArity() == 2) {
            op = this.getCore().inAll(aid, new LogicTuple(t.getArg(0)), l);
        } else {
            op = this.getCore().inAll(aid, t, l);
        }
        return op;
    }

    @Override
    public IRespectOperation inp(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().inp(id, t, l);
    }

    @Override
    public IRespectOperation no(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().no(id, t, l);
    }

    @Override
    public IRespectOperation noAll(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        IRespectOperation op = null;
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        } else if (",".equals(t.getName()) && t.getArity() == 2) {
            op = this.getCore().noAll(aid, new LogicTuple(t.getArg(0)), l);
        } else {
        	op = this.getCore().noAll(aid, t, l);
        }
        return op;
    }

    @Override
    public IRespectOperation nop(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().nop(id, t, l);
    }

    @Override
    public IRespectOperation out(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().out(id, t, l);
    }

    @Override
    public IRespectOperation outAll(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().outAll(id, t, l);
    }

    @Override
    public IRespectOperation rd(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().rd(id, t, l);
    }

    @Override
    public IRespectOperation rdAll(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        IRespectOperation op = null;
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        } else if (",".equals(t.getName()) && t.getArity() == 2) {
            op = this.getCore().rdAll(aid, new LogicTuple(t.getArg(0)), l);
        } else {
            op = this.getCore().rdAll(aid, t, l);
        }
        return op;
    }

    @Override
    public IRespectOperation rdp(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().rdp(id, t, l);
    }

    @Override
    public IRespectOperation set(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().set(aid, t, l);
    }

    @Override
    public IRespectOperation spawn(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().spawn(aid, t, l);
    }

    @Override
    public IRespectOperation uin(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().uin(aid, t, l);
    }

    @Override
    public IRespectOperation uinp(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().uinp(aid, t, l);
    }

    @Override
    public IRespectOperation uno(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().uno(id, t, l);
    }

    @Override
    public IRespectOperation unop(final IId id, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().unop(id, t, l);
    }

    @Override
    public IRespectOperation urd(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().urd(aid, t, l);
    }

    @Override
    public IRespectOperation urdp(final IId aid, final LogicTuple t,
            final OperationCompletionListener l) throws InvalidLogicTupleException,
            OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        return this.getCore().urdp(aid, t, l);
    }
}
