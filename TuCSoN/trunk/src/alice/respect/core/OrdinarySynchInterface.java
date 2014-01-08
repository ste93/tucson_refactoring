/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.respect.core;

import java.util.Iterator;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.api.IOrdinarySynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * 
 * A Blocking Context wraps the access to a tuple centre virtual machine for a
 * specific thread of control, providing a blocking interface.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public class OrdinarySynchInterface extends RootInterface implements
        IOrdinarySynchInterface {

    private static Term list2tuple(final List<LogicTuple> list) {
        final Term[] termArray = new Term[list.size()];
        final Iterator<LogicTuple> it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            termArray[i] = it.next().toTerm();
            i++;
        }
        return new Struct(termArray);
    }

    /**
     * 
     * @param core
     *            the ReSpecT tuple centre this context refers to
     */
    public OrdinarySynchInterface(final IRespectTC core) {
        super(core);
    }

    public List<LogicTuple> get(final IId aid)
            throws OperationNotPossibleException {
        final IRespectOperation op = this.getCore().get(aid);
        op.waitForOperationCompletion();
        return op.getLogicTupleListResult();
    }

    public LogicTuple in(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().in(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public List<LogicTuple> inAll(final IId aid, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        IRespectOperation op = null;
        TupleArgument arg = null;
        try {
            if (t == null) {
                throw new InvalidTupleException();
            } else if (",".equals(t.getName()) && (t.getArity() == 2)) {
                op = this.getCore().inAll(aid, new LogicTuple(t.getArg(0)));
            } else {
                op = this.getCore().inAll(aid, t);
            }
            op.waitForOperationCompletion();
            if (",".equals(t.getName()) && (t.getArity() == 2)) {
                arg = t.getArg(1);
                this.unify(
                        new LogicTuple(new TupleArgument(arg.toTerm())),
                        new LogicTuple(OrdinarySynchInterface.list2tuple(op
                                .getLogicTupleListResult())));
                return op.getLogicTupleListResult();
            }
        } catch (final InvalidTupleOperationException e2) {
            throw new OperationNotPossibleException();
        }
        return op.getLogicTupleListResult();
    }

    public LogicTuple inp(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().inp(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple no(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().no(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public List<LogicTuple> noAll(final IId aid, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        IRespectOperation op = null;
        TupleArgument arg = null;
        try {
            if (t == null) {
                throw new InvalidTupleException();
            } else if (",".equals(t.getName()) && (t.getArity() == 2)) {
                op = this.getCore().noAll(aid, new LogicTuple(t.getArg(0)));
            } else {
                op = this.getCore().noAll(aid, t);
            }
            op.waitForOperationCompletion();
            if (",".equals(t.getName()) && (t.getArity() == 2)) {
                arg = t.getArg(1);
                this.unify(
                        new LogicTuple(new TupleArgument(arg.toTerm())),
                        new LogicTuple(OrdinarySynchInterface.list2tuple(op
                                .getLogicTupleListResult())));
                return op.getLogicTupleListResult();
            }
        } catch (final InvalidTupleOperationException e2) {
            throw new OperationNotPossibleException();
        }
        return op.getLogicTupleListResult();
    }

    public LogicTuple nop(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().nop(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public void out(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().out(id, t);
        op.waitForOperationCompletion();
    }

    public List<LogicTuple> outAll(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().outAll(id, t);
        op.waitForOperationCompletion();
        return op.getLogicTupleListResult();
    }

    public LogicTuple rd(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().rd(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public List<LogicTuple> rdAll(final IId aid, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        IRespectOperation op = null;
        TupleArgument arg = null;
        try {
            if (t == null) {
                throw new InvalidTupleException();
            } else if (",".equals(t.getName()) && (t.getArity() == 2)) {
                op = this.getCore().rdAll(aid, new LogicTuple(t.getArg(0)));
            } else {
                op = this.getCore().rdAll(aid, t);
            }
            op.waitForOperationCompletion();
            if (",".equals(t.getName()) && (t.getArity() == 2)) {
                arg = t.getArg(1);
                this.unify(
                        new LogicTuple(new TupleArgument(arg.toTerm())),
                        new LogicTuple(OrdinarySynchInterface.list2tuple(op
                                .getLogicTupleListResult())));
                return op.getLogicTupleListResult();
            }
        } catch (final InvalidTupleOperationException e2) {
            throw new OperationNotPossibleException();
        }
        return op.getLogicTupleListResult();
    }

    public LogicTuple rdp(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().rdp(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public List<LogicTuple> set(final IId aid, final LogicTuple tuple)
            throws OperationNotPossibleException, InvalidTupleException {
        final IRespectOperation op = this.getCore().set(aid, tuple);
        op.waitForOperationCompletion();
        return op.getLogicTupleListResult();
    }

    public LogicTuple spawn(final IId aid, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().spawn(aid, t);
        op.waitForOperationCompletion();
        return t;
    }

    public LogicTuple uin(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().uin(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple uinp(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().uinp(id, t);
        op.waitForOperationCompletion();
        final LogicTuple result = op.getLogicTupleResult();
        return this.unify(t, result);
    }

    public LogicTuple uno(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().uno(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple unop(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().unop(id, t);
        op.waitForOperationCompletion();
        final LogicTuple result = op.getLogicTupleResult();
        return this.unify(t, result);
    }

    public LogicTuple urd(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().urd(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple urdp(final IId id, final LogicTuple t)
            throws InvalidTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidTupleException();
        }
        final IRespectOperation op = this.getCore().urdp(id, t);
        op.waitForOperationCompletion();
        final LogicTuple result = op.getLogicTupleResult();
        return this.unify(t, result);
    }

}
