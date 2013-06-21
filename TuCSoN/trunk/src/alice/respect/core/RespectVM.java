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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tucson.introspection.WSetEvent;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.api.ObservableEventListener;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.BehaviourSpecification;
import alice.tuplecentre.core.Event;
import alice.tuplecentre.core.InputEvent;
import alice.tuplecentre.core.InspectableEvent;

/**
 * RespecT Tuple Centre Virtual Machine.
 * 
 * Defines the core behaviour of a tuple centre virtual machine.
 * 
 * The behaviour reflects the operational semantic expressed in related tuple
 * centre articles.
 * 
 * @author aricci
 * @version 1.0
 */
public class RespectVM implements Runnable {

    /** listener to VM inspectable events */
    protected List<InspectableEventListener> inspectors;
    protected List<ObservableEventListener> observers;
    private final RespectTCContainer container;
    private final RespectVMContext context;
    private final Object idle;
    private final Object news;

    public RespectVM(final TupleCentreId tid,
            final RespectTCContainer c, final int qSize,
            final RespectTC respectTC) {
        this.container = c;
        this.context = new RespectVMContext(this, tid, qSize, respectTC);
        this.news = new Object();
        this.idle = new Object();
        this.inspectors = new ArrayList<InspectableEventListener>();
        this.observers = new ArrayList<ObservableEventListener>();
    }

    public boolean abortOperation(final long opId) {
        try {
            boolean res;
            synchronized (this.idle) {
                res = this.context.removePendingQueryEvent(opId);
            }
            return res;
        } catch (final Exception ex) {
            return false;
        }
    }

    public void addInspector(final InspectableEventListener l) {
        this.inspectors.add(l);
    }

    public void addObserver(final ObservableEventListener l) {
        this.observers.add(l);
    }

    public void doOperation(final IId id, final RespectOperation op)
            throws OperationNotPossibleException {
        try {
            this.context.doOperation(id, op);
            synchronized (this.news) {
                this.news.notifyAll();
            }
        } catch (final Exception ex) {
            throw new OperationNotPossibleException();
        }
    }

    public RespectTCContainer getContainer() {
        return this.container;
    }

    public TupleCentreId getId() {
        return (TupleCentreId) this.context.getId();
    }

    public List<ObservableEventListener> getObservers() {
        return this.observers;
    }

    public BehaviourSpecification getReactionSpec() {
        synchronized (this.idle) {
            return this.context.getReactionSpec();
        }
    }

    public RespectVMContext getRespectVMContext() {
        return this.context;
    }

    public LogicTuple[] getTRSet() {
        return this.context.getTRSet();
    }

    public LogicTuple[] getTSet(final LogicTuple filter) {
        return this.context.getTSet(filter);
    }

    public WSetEvent[] getWSet(final LogicTuple filter) {
        return this.context.getWSet(filter);
    }

    public void goCommand() throws OperationNotPossibleException {
        try {
            this.context.goCommand();
            synchronized (this.news) {
                this.news.notifyAll();
            }
        } catch (final Exception ex) {
            throw new OperationNotPossibleException();
        }
    }

    public boolean hasInspectors() {
        return this.inspectors.size() > 0;
    }

    public boolean hasObservers() {
        return this.observers.size() > 0;
    }

    public void nextStepCommand() throws OperationNotPossibleException {
        try {
            this.context.nextStepCommand();
            synchronized (this.news) {
                this.news.notifyAll();
            }
        } catch (final Exception ex) {
            throw new OperationNotPossibleException();
        }
    }

    public void notifyInspectableEvent(final InspectableEvent e) {
        final Iterator<? extends InspectableEventListener> it =
                this.inspectors.iterator();
        while (it.hasNext()) {
            try {
                ((InspectableEventListener) it.next()).onInspectableEvent(e);
            } catch (final Exception ex) {
                ex.printStackTrace();
                it.remove();
            }
        }
    }

    public void notifyNewInputEvent() {
        synchronized (this.news) {
            this.news.notifyAll();
        }
    }

    public void notifyObservableEvent(final Event ev) {
        final int size = this.observers.size();
        final InputEvent e = (InputEvent) ev;
        if (ev.isInput()) {
            if (e.getSimpleTCEvent().isIn()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).in_requested(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isInp()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).inp_requested(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isRd()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).rd_requested(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isRdp()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).rdp_requested(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isOut()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).out_requested(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isSet_s()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).setSpec_requested(
                            this.getId(),
                            ev.getSource(),
                            ((Tuple) ((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()).toString());
                }
            } else if (e.getSimpleTCEvent().isGet_s()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).getSpec_requested(this.getId(),
                            ev.getSource());
                }
            }
        } else {
            if (e.getSimpleTCEvent().isIn()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).in_completed(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isInp()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).inp_completed(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isRd()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).rd_completed(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isRdp()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).rdp_completed(
                            this.getId(),
                            ev.getSource(),
                            (((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()));
                }
            } else if (e.getSimpleTCEvent().isSet_s()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).setSpec_completed(this.getId(),
                            ev.getSource());
                }
            } else if (e.getSimpleTCEvent().isGet_s()) {
                for (int i = 0; i < size; i++) {
                    this.observers.get(i).getSpec_completed(
                            this.getId(),
                            ev.getSource(),
                            ((Tuple) ((RespectOperation) ev.getSimpleTCEvent())
                                    .getLogicTupleArgument()).toString());
                }
            }
        }

    }

    public void removeInspector(final InspectableEventListener l) {
        this.inspectors.remove(l);
    }

    public void removeObserver(final ObservableEventListener l) {
        this.observers.remove(l);
    }

    public void reset() {
        this.context.reset();
    }

    public void run() {
        while (true) {
            try {
                synchronized (this.idle) {
                    this.context.execute();
                }
            } catch (final Exception ex) {
                this.context.notifyException(ex);
            }
            try {
                if (this.hasInspectors()) {
                    this.notifyInspectableEvent(new InspectableEvent(this,
                            InspectableEvent.TYPE_NEWSTATE));
                }
                if (!(this.context.pendingEvents() || this.context
                        .pendingEnvEvents())) {
                    synchronized (this.news) {
                        this.news.wait();
                    }
                }
            } catch (final InterruptedException e) {
                System.out
                        .println("[RespectVM]: Shutdown interrupt received, shutting down...");
                break;
            } catch (final Exception ex) {
                this.context.notifyException(ex);
            }
        }
        System.out.println("[RespectVM]: Actually shutting down...");
    }

    public void setManagementMode(final boolean activate) {
        this.context.setManagementMode(activate);
    }

    /**
     * This operation can be executed only when the VM is an idle state, waiting
     * for I/O
     */
    public boolean setReactionSpec(final BehaviourSpecification spec) {
        synchronized (this.idle) {
            this.context.removeReactionSpec();
            return this.context.setReactionSpec(spec);
        }
    }

    public void setWSet(final List<LogicTuple> wSet) {
        this.context.setWSet(wSet);
    }

    public void stopCommand() throws OperationNotPossibleException {
        try {
            this.context.stopCommand();
        } catch (final Exception ex) {
            throw new OperationNotPossibleException();
        }
    }

}
