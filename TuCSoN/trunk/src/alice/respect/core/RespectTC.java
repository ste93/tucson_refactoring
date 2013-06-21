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
import java.util.LinkedList;

import alice.logictuple.LogicTuple;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.IEnvironmentContext;
import alice.respect.api.ILinkContext;
import alice.respect.api.IManagementContext;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.IOrdinarySynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.ISpecificationSynchInterface;
import alice.respect.api.ITimedContext;
import alice.respect.api.RespectSpecification;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.OperationCompletionListener;
import alice.tuplecentre.core.TCCycleResult.Outcome;
import alice.tuprolog.Prolog;

/**
 * 
 * A ReSpecT tuple centre.
 * 
 * @author aricci
 * 
 */
public class RespectTC implements IRespectTC {

    private final RespectVM vm;
    private final Thread vmThread;

    public RespectTC(final TupleCentreId tid,
            final RespectTCContainer container, final int qSize) {
        this.vm = new RespectVM(tid, container, qSize, this);
        this.vmThread = new Thread(this.vm);
        this.vmThread.start();
    }

    public IRespectOperation get(final IId id)
            throws OperationNotPossibleException {
        return this.get(id, null);
    }

    public IRespectOperation get(final IId id,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeGet(this.getProlog(),
                        new LogicTuple("get"), l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation get_s(final IId aid)
            throws OperationNotPossibleException {
        return this.get_s(aid, null);
    }

    public IRespectOperation get_s(final IId aid,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeGet_s(this.getProlog(), new LogicTuple(
                        "spec", new Var("S")), l);
        this.vm.doOperation(aid, op);
        return op;
    }

    public IEnvironmentContext getEnvironmentContext() {
        return new EnviromentContext(this.vm.getRespectVMContext());
    }

    /**
     * Gets the tuple centre id
     * 
     * @return the id
     */
    public TupleCentreId getId() {
        return this.vm.getId();
    }

    /**
     * Gets a interface for linking operations
     * 
     * @return
     */
    public ILinkContext getLinkContext() {
        return new LinkContext(this.vm);
    }

    /**
     * Gets a context for tuple centre management.
     * 
     * @return
     */
    public IManagementContext getManagementContext() {
        return new ManagementContext(this.vm);
    }

    /**
     * Gets a context with no blocking functionalities
     * 
     * @return
     */
    public IOrdinaryAsynchInterface getOrdinaryAsynchInterface() {
        return new OrdinaryAsynchInterface(this);
    }

    /**
     * Gets a context with blocking functionalities
     * 
     * @return
     */
    public IOrdinarySynchInterface getOrdinarySynchInterface() {
        return new OrdinarySynchInterface(this);
    }

    public ISpecificationAsynchInterface getSpecificationAsynchInterface() {
        return new SpecificationAsynchInterface(this);
    }

    /**
     * Gets a context with blocking specification functionalities
     * 
     * @return
     */
    public ISpecificationSynchInterface getSpecificationSynchInterface() {
        return new SpecificationSynchInterface(this);
    }

    /**
     * Gets a context with timing functionalities.
     * 
     * @return
     */
    public ITimedContext getTimedContext() {
        return new TimedContext(this);
    }

    public RespectVM getVM() {
        return this.vm;
    }

    public Thread getVMThread() {
        return this.vmThread;
    }

    public IRespectOperation in(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.in(id, t, null);
    }

    public IRespectOperation in(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeIn(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation in_all(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.in_all(id, t, null);
    }

    public IRespectOperation in_all(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeInAll(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation in_s(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.in_s(id, t, null);
    }

    public IRespectOperation in_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeIn_s(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation inp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.inp(id, t, null);
    }

    public IRespectOperation inp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeInp(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation inp_s(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.inp_s(id, t, null);
    }

    public IRespectOperation inp_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeInp_s(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation no(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.no(id, t, null);
    }

    public IRespectOperation no(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeNo(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation no_all(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.no_all(id, t, null);
    }

    public IRespectOperation no_all(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeNoAll(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation no_s(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.no_s(id, t, null);
    }

    public IRespectOperation no_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeNo_s(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation nop(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.nop(id, t, null);
    }

    public IRespectOperation nop(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeNop(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation nop_s(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.nop_s(id, t, null);
    }

    public IRespectOperation nop_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeNop_s(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    /**
     * ORDINARY primitives SYNCH semantics
     */

    public IRespectOperation out(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.out(id, t, null);
    }

    /**
     * ORDINARY primitives ASYNCH semantics
     */

    public IRespectOperation out(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeOut(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation out_all(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.out_all(id, t, null);
    }

    public IRespectOperation out_all(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeOutAll(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    /**
     * SPECIFICATION primitives SYNCH semantics
     */

    public IRespectOperation out_s(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.out_s(id, t, null);
    }

    /**
     * SPECIFICATION primitives ASYNCH semantics
     */

    public IRespectOperation out_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeOut_s(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation rd(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rd(id, t, null);
    }

    public IRespectOperation rd(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeRd(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation rd_all(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rd_all(id, t, null);
    }

    public IRespectOperation rd_all(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeRdAll(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation rd_s(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rd_s(id, t, null);
    }

    public IRespectOperation rd_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeRd_s(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation rdp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rdp(id, t, null);
    }

    public IRespectOperation rdp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeRdp(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation rdp_s(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rdp_s(id, t, null);
    }

    public IRespectOperation rdp_s(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeRdp_s(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation set(final IId id, final LogicTuple tuple)
            throws OperationNotPossibleException, InvalidLogicTupleException {
        return this.set(id, tuple, null);
    }

    public IRespectOperation set(final IId id, final LogicTuple tuple,
            final OperationCompletionListener l)
            throws OperationNotPossibleException, InvalidLogicTupleException {
        final RespectOperation op =
                RespectOperation.makeSet(this.getProlog(), tuple, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation set_s(final IId aid, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.set_s(aid, t, null);
    }

    public IRespectOperation set_s(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeSet_s(this.getProlog(), t, l);
        this.vm.doOperation(aid, op);
        return op;
    }

    public IRespectOperation set_s(final IId aid,
            final RespectSpecification spec)
            throws OperationNotPossibleException, InvalidSpecificationException {
        final boolean accepted = this.vm.setReactionSpec(spec);
        if (!accepted) {
            throw new InvalidSpecificationException();
        }
        final RespectOperation op =
                RespectOperation.makeSet_s(this.getProlog(), null);
        final Iterator<LogicTuple> rit =
                this.vm.getRespectVMContext().getSpecTupleSetIterator();
        final LinkedList<Tuple> reactionList = new LinkedList<Tuple>();
        while (rit.hasNext()) {
            reactionList.add(rit.next());
        }
        op.setOpResult(Outcome.SUCCESS);
        op.setTupleListResult(reactionList);
        return op;
    }

    public IRespectOperation
            set_s(final IId aid, final RespectSpecification spec,
                    final OperationCompletionListener l)
                    throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeSet_s(this.getProlog(), spec, l);
        this.vm.doOperation(aid, op);
        return op;
    }

    public IRespectOperation spawn(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.spawn(id, t, null);
    }

    public IRespectOperation spawn(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeSpawn(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation uin(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.uin(id, t, null);
    }

    public IRespectOperation uin(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeUin(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation uinp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.uinp(id, t, null);
    }

    public IRespectOperation uinp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeUinp(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation uno(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.uno(id, t, null);
    }

    public IRespectOperation uno(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeUno(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation unop(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.unop(id, t, null);
    }

    public IRespectOperation unop(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeUnop(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation urd(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.urd(id, t, null);
    }

    public IRespectOperation urd(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeUrd(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    public IRespectOperation urdp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.urdp(id, t, null);
    }

    public IRespectOperation urdp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op =
                RespectOperation.makeUrdp(this.getProlog(), t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    private Prolog getProlog() {
        return this.vm.getRespectVMContext().getPrologCore();
    }

}