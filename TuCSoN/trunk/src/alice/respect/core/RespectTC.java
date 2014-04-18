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

/**
 * 
 * A ReSpecT tuple centre.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public class RespectTC implements IRespectTC {
    private final RespectVM vm;
    private final Thread vmThread;

    /**
     * 
     * @param tid
     *            the identifier of the tuple centre
     * @param container
     *            the ReSpecT wrapper this tuple centre refers to
     * @param qSize
     *            the maximum size of the input queue
     */
    public RespectTC(final TupleCentreId tid,
            final RespectTCContainer container, final int qSize) {
        this.vm = new RespectVM(tid, container, qSize, this);
        this.vmThread = new Thread(this.vm);
        this.vmThread.start();
    }

    @Override
    public IRespectOperation get(final IId id)
            throws OperationNotPossibleException {
        return this.get(id, null);
    }

    @Override
    public IRespectOperation get(final IId id,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeGet(new LogicTuple(
                "get"), l);
        this.vm.doOperation(id, op);
        return op;
    }

    /**
     * 
     * @return the environment context toward this tuple centre
     */
    public IEnvironmentContext getEnvironmentContext() {
        return new EnviromentContext(this.vm.getRespectVMContext());
    }

    @Override
    public TupleCentreId getId() {
        return this.vm.getId();
    }

    /**
     * Gets a interface for linking operations
     * 
     * @return the linking context toward this tuple centre
     */
    public ILinkContext getLinkContext() {
        return new LinkContext(this.vm);
    }

    /**
     * Gets a context for tuple centre management.
     * 
     * @return the management context toward this tuple centre
     */
    public IManagementContext getManagementContext() {
        return new ManagementContext(this.vm);
    }

    /**
     * Gets a context with no blocking functionalities
     * 
     * @return the ordinary, asynchronous context toward this tuple centre
     */
    public IOrdinaryAsynchInterface getOrdinaryAsynchInterface() {
        return new OrdinaryAsynchInterface(this);
    }

    /**
     * Gets a context with blocking functionalities
     * 
     * @return the ordinary, synchronous context toward this tuple centre
     */
    public IOrdinarySynchInterface getOrdinarySynchInterface() {
        return new OrdinarySynchInterface(this);
    }

    @Override
    public IRespectOperation getS(final IId aid)
            throws OperationNotPossibleException {
        return this.getS(aid, null);
    }

    @Override
    public IRespectOperation getS(final IId aid,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeGetS(new LogicTuple(
                "spec", new Var("S")), l);
        this.vm.doOperation(aid, op);
        return op;
    }

    /**
     * 
     * @return the specification, asynchronous context toward this tuple centre
     */
    public ISpecificationAsynchInterface getSpecificationAsynchInterface() {
        return new SpecificationAsynchInterface(this);
    }

    /**
     * Gets a context with blocking specification functionalities
     * 
     * @return the specification, synchronous context toward this tuple centre
     */
    public ISpecificationSynchInterface getSpecificationSynchInterface() {
        return new SpecificationSynchInterface(this);
    }

    /**
     * Gets a context with timing functionalities.
     * 
     * @return the timed context toward this tuple centre
     */
    public ITimedContext getTimedContext() {
        return new TimedContext(this);
    }

    @Override
    public RespectVM getVM() {
        return this.vm;
    }

    /**
     * 
     * @return the Java thread executing the ReSpecT VM managing this tuple
     *         centre
     */
    public Thread getVMThread() {
        return this.vmThread;
    }

    @Override
    public IRespectOperation in(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.in(id, t, null);
    }

    @Override
    public IRespectOperation in(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeIn(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation inAll(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.inAll(id, t, null);
    }

    @Override
    public IRespectOperation inAll(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeInAll(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation inp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.inp(id, t, null);
    }

    @Override
    public IRespectOperation inp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeInp(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation inpS(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.inpS(id, t, null);
    }

    @Override
    public IRespectOperation inpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeInpS(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation inS(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.inS(id, t, null);
    }

    @Override
    public IRespectOperation inS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeInS(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation no(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.no(id, t, null);
    }

    @Override
    public IRespectOperation no(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeNo(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation noAll(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.noAll(id, t, null);
    }

    @Override
    public IRespectOperation noAll(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeNoAll(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation nop(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.nop(id, t, null);
    }

    @Override
    public IRespectOperation nop(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeNop(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation nopS(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.nopS(id, t, null);
    }

    @Override
    public IRespectOperation nopS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeNopS(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation noS(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.noS(id, t, null);
    }

    @Override
    public IRespectOperation noS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeNoS(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    /**
     * ORDINARY primitives SYNCH semantics
     */
    @Override
    public IRespectOperation out(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.out(id, t, null);
    }

    /**
     * ORDINARY primitives ASYNCH semantics
     */
    @Override
    public IRespectOperation out(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeOut(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation outAll(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.outAll(id, t, null);
    }

    @Override
    public IRespectOperation outAll(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeOutAll(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    /**
     * SPECIFICATION primitives SYNCH semantics
     */
    @Override
    public IRespectOperation outS(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.outS(id, t, null);
    }

    /**
     * SPECIFICATION primitives ASYNCH semantics
     */
    @Override
    public IRespectOperation outS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeOutS(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation rd(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rd(id, t, null);
    }

    @Override
    public IRespectOperation rd(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeRd(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation rdAll(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rdAll(id, t, null);
    }

    @Override
    public IRespectOperation rdAll(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeRdAll(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation rdp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rdp(id, t, null);
    }

    @Override
    public IRespectOperation rdp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeRdp(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation rdpS(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rdpS(id, t, null);
    }

    @Override
    public IRespectOperation rdpS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeRdpS(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation rdS(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.rdS(id, t, null);
    }

    @Override
    public IRespectOperation rdS(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeRdS(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation set(final IId id, final LogicTuple tuple)
            throws OperationNotPossibleException {
        return this.set(id, tuple, null);
    }

    @Override
    public IRespectOperation set(final IId id, final LogicTuple tuple,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeSet(tuple, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation setS(final IId aid, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.setS(aid, t, null);
    }

    @Override
    public IRespectOperation setS(final IId aid, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeSetS(t, l);
        this.vm.doOperation(aid, op);
        return op;
    }

    @Override
    public IRespectOperation setS(final IId aid, final RespectSpecification spec)
            throws InvalidSpecificationException {
        final boolean accepted = this.vm.setReactionSpec(spec);
        if (!accepted) {
            throw new InvalidSpecificationException();
        }
        final RespectOperation op = RespectOperation.makeSetS(null);
        final Iterator<LogicTuple> rit = this.vm.getRespectVMContext()
                .getSpecTupleSetIterator();
        final LinkedList<Tuple> reactionList = new LinkedList<Tuple>();
        while (rit.hasNext()) {
            reactionList.add(rit.next());
        }
        op.setOpResult(Outcome.SUCCESS);
        op.setTupleListResult(reactionList);
        return op;
    }

    @Override
    public IRespectOperation setS(final IId aid,
            final RespectSpecification spec, final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeSetS(spec, l);
        this.vm.doOperation(aid, op);
        return op;
    }

    @Override
    public IRespectOperation spawn(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.spawn(id, t, null);
    }

    @Override
    public IRespectOperation spawn(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeSpawn(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation uin(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.uin(id, t, null);
    }

    @Override
    public IRespectOperation uin(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeUin(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation uinp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.uinp(id, t, null);
    }

    @Override
    public IRespectOperation uinp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeUinp(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation uno(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.uno(id, t, null);
    }

    @Override
    public IRespectOperation uno(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeUno(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation unop(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.unop(id, t, null);
    }

    @Override
    public IRespectOperation unop(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeUnop(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation urd(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.urd(id, t, null);
    }

    @Override
    public IRespectOperation urd(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeUrd(t, l);
        this.vm.doOperation(id, op);
        return op;
    }

    @Override
    public IRespectOperation urdp(final IId id, final LogicTuple t)
            throws OperationNotPossibleException {
        return this.urdp(id, t, null);
    }

    @Override
    public IRespectOperation urdp(final IId id, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationNotPossibleException {
        final RespectOperation op = RespectOperation.makeUrdp(t, l);
        this.vm.doOperation(id, op);
        return op;
    }
}
