/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.introspection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.core.LogicReaction;
import alice.respect.core.RespectOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.network.AbstractTucsonProtocol;
import alice.tucson.network.exceptions.DialogException;
import alice.tucson.network.exceptions.DialogReceiveException;
import alice.tucson.network.exceptions.DialogSendException;
import alice.tucson.service.ACCDescription;
import alice.tucson.service.ACCProvider;
import alice.tucson.service.AbstractACCProxyNodeSide;
import alice.tucson.service.TucsonNodeService;
import alice.tucson.service.TucsonOperation;
import alice.tucson.service.TucsonTCUsers;
import alice.tucson.service.TupleCentreContainer;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import alice.tuplecentre.core.InputEvent;
import alice.tuplecentre.core.InspectableEvent;
import alice.tuplecentre.core.ObservableEventExt;
import alice.tuplecentre.core.ObservableEventReactionFail;
import alice.tuplecentre.core.ObservableEventReactionOK;
import alice.tuplecentre.core.TriggeredReaction;

/**
 *
 * @author Unknown...
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * @author (contributor) Michele Bombardi (mailto:
 *         michele.bombardi@studio.unibo.it)
 *
 */
public class InspectorContextSkel extends AbstractACCProxyNodeSide implements
        InspectableEventListener {

    private final TucsonAgentId agentId;
    private final int ctxId;
    protected final AbstractTucsonProtocol dialog;
    private final ACCProvider manager;
    private boolean nStep;
    /** current observation protocol */
    protected InspectorProtocol protocol;
    private boolean shutdown = false;
    protected final TucsonTupleCentreId tcId;

    /**
     *
     * @param man
     *            the ACC provider distributing ACCs
     * @param d
     *            the TuCSoN protocol to be used
     * @param node
     *            the TuCSoN node service to communicate with
     * @param p
     *            the ACC properties descriptor
     * @throws TucsonGenericException
     *             if the tuple centre to inspect cannot be resolved
     * @throws TucsonInvalidAgentIdException
     *             if the ACCDescription's "agent-identity" property does not
     *             represent a valid TuCSoN identifier
     * @throws TucsonInvalidTupleCentreIdException
     *             if the TupleCentreId, contained into AbstractTucsonProtocol's
     *             message, does not represent a valid TuCSoN identifier
     * @throws DialogReceiveException
     *             if something goes wrong in the underlying network
     */
    public InspectorContextSkel(final ACCProvider man,
            final AbstractTucsonProtocol d, final TucsonNodeService node,
            final ACCDescription p) throws TucsonGenericException,
            TucsonInvalidAgentIdException, DialogReceiveException,
            TucsonInvalidTupleCentreIdException {
        super();
        this.dialog = d;
        this.manager = man;
        NewInspectorMsg msg = null;
        this.ctxId = Integer.parseInt(p.getProperty("context-id"));
        final String name = p.getProperty("agent-identity");
        this.agentId = new TucsonAgentId(name);
        msg = this.dialog.receiveInspectorMsg();
        this.tcId = new TucsonTupleCentreId(msg.getTcName());
        final TucsonTCUsers coreInfo = node.resolveCore(msg.getTcName());
        if (coreInfo == null) {
            throw new TucsonGenericException(
                    "Internal error: InspectorContextSkel constructor");
        }
        this.protocol = msg.getInfo();
    }

    @Override
    public void exit(final ShutdownMsg msg) {
        this.log("Shutdown request received from <" + msg.getAid() + ">...");
        this.shutdown = true;
    }

    /**
     * get a tuple centre set (T set, W set,...) snapshot
     *
     * @param m
     *            the snapshot message
     */
    public void getSnapshot(final GetSnapshotMsg m) {
        final InspectorContextEvent msg = new InspectorContextEvent();
        msg.setVmTime(System.currentTimeMillis());
        msg.setLocalTime(System.currentTimeMillis());
        if (m.getWhat() == GetSnapshotMsg.TSET) {
            msg.setTuples(new LinkedList<LogicTuple>());
            LogicTuple[] tSet = null;
            tSet = (LogicTuple[]) TupleCentreContainer.doManagementOperation(
                    TucsonOperation.getTSetCode(), this.tcId,
                    this.protocol.getTsetFilter());
            if (tSet != null) {
                for (final LogicTuple lt : tSet) {
                    msg.getTuples().add(lt);
                }
            }
        } else if (m.getWhat() == GetSnapshotMsg.WSET) {
            WSetEvent[] ltSet = null;
            ltSet = (WSetEvent[]) TupleCentreContainer.doManagementOperation(
                    TucsonOperation.getWSetCode(), this.tcId,
                    this.protocol.getWsetFilter());
            msg.setWnEvents(new LinkedList<WSetEvent>());
            if (ltSet != null) {
                for (final WSetEvent lt : ltSet) {
                    msg.getWnEvents().add(lt);
                }
            }
        }
        try {
            this.dialog.sendInspectorEvent(msg);
        } catch (final DialogSendException e) {
            e.printStackTrace();
        }
    }

    /**
     * verify if VM step mode is already active
     *
     * @param m
     *            the IsActiveStepModeMsg
     */
    public void isStepMode(final IsActiveStepModeMsg m) {
        final boolean isActive = (Boolean) TupleCentreContainer
                .doManagementOperation(TucsonOperation.isStepModeCode(),
                        this.tcId, null);
        final InspectorContextEvent msg = new InspectorContextEvent();
        msg.setStepMode(isActive);
        try {
            this.dialog.sendInspectorEvent(msg);
        } catch (final DialogException e) {
            e.printStackTrace();
        }
    }

    /**
     * ask a new step for a tuple centre vm during step mode
     *
     * @param m
     *            the NxtStepMsg
     */
    public void nextStep(final NextStepMsg m) {
        TupleCentreContainer.doManagementOperation(
                TucsonOperation.nextStepCode(), this.tcId, null);
    }

    @Override
    public synchronized void onInspectableEvent(final InspectableEvent ev) {
        try {
            while (this.protocol.isTracing() && !this.nStep) {
                this.wait();
            }
            this.nStep = false;
            final InspectorContextEvent msg = new InspectorContextEvent();
            msg.setLocalTime(System.currentTimeMillis());
            msg.setVmTime(ev.getTime());
            if (ev.getType() == InspectableEvent.TYPE_IDLESTATE
                    && this.protocol.getStepModeObservType() == InspectorProtocol.STEPMODE_AGENT_OBSERVATION) {
                if (this.protocol.getTsetObservType() == InspectorProtocol.PROACTIVE_OBSERVATION) {
                    final LogicTuple[] ltSet = (LogicTuple[]) TupleCentreContainer
                            .doManagementOperation(
                                    TucsonOperation.getTSetCode(), this.tcId,
                                    this.protocol.getTsetFilter());
                    msg.setTuples(new LinkedList<LogicTuple>());
                    if (ltSet != null) {
                        for (final LogicTuple lt : ltSet) {
                            msg.getTuples().add(lt);
                        }
                    }
                }
                if (this.protocol.getPendingQueryObservType() == InspectorProtocol.PROACTIVE_OBSERVATION) {
                    final WSetEvent[] ltSet = (WSetEvent[]) TupleCentreContainer
                            .doManagementOperation(
                                    TucsonOperation.getWSetCode(), this.tcId,
                                    this.protocol.getWsetFilter());
                    msg.setWnEvents(new LinkedList<WSetEvent>());
                    if (ltSet != null) {
                        for (final WSetEvent lt : ltSet) {
                            msg.getWnEvents().add(lt);
                        }
                    }
                }
                this.dialog.sendInspectorEvent(msg);
            }
            if (ev.getType() == InspectableEvent.TYPE_NEWSTATE
                    && this.protocol.getStepModeObservType() == InspectorProtocol.STEPMODE_TUPLESPACE_OBSERVATION) {
                if (this.protocol.getTsetObservType() == InspectorProtocol.PROACTIVE_OBSERVATION) {
                    final LogicTuple[] ltSet = (LogicTuple[]) TupleCentreContainer
                            .doManagementOperation(
                                    TucsonOperation.getTSetCode(), this.tcId,
                                    this.protocol.getTsetFilter());
                    msg.setTuples(new LinkedList<LogicTuple>());
                    if (ltSet != null) {
                        for (final LogicTuple lt : ltSet) {
                            msg.getTuples().add(lt);
                        }
                    }
                }
                if (this.protocol.getPendingQueryObservType() == InspectorProtocol.PROACTIVE_OBSERVATION) {
                    final WSetEvent[] ltSet = (WSetEvent[]) TupleCentreContainer
                            .doManagementOperation(
                                    TucsonOperation.getWSetCode(), this.tcId,
                                    this.protocol.getWsetFilter());
                    msg.setWnEvents(new LinkedList<WSetEvent>());
                    if (ltSet != null) {
                        for (final WSetEvent lt : ltSet) {
                            msg.getWnEvents().add(lt);
                        }
                    }
                }
                this.dialog.sendInspectorEvent(msg);
            } else if (ev.getType() == ObservableEventExt.TYPE_REACTIONOK) {
                if (this.protocol.getReactionsObservType() != InspectorProtocol.NO_OBSERVATION) {
                    final TriggeredReaction zCopy = new TriggeredReaction(null,
                            ((ObservableEventReactionOK) ev).getZ()
                                    .getReaction());
                    /* Dradi */
                    if (zCopy.getReaction() instanceof LogicReaction) {
                        ((LogicReaction) zCopy.getReaction())
                                .getStructReaction().resolveTerm();
                    }
                    /* Dradi */
                    msg.setReactionOk(zCopy);
                    this.dialog.sendInspectorEvent(msg);
                }
            } else if (ev.getType() == ObservableEventExt.TYPE_REACTIONFAIL
                    && this.protocol.getReactionsObservType() != InspectorProtocol.NO_OBSERVATION) {
                final TriggeredReaction zCopy = new TriggeredReaction(null,
                        ((ObservableEventReactionFail) ev).getZ().getReaction());
                msg.setReactionFailed(zCopy);
                this.dialog.sendInspectorEvent(msg);
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final DialogSendException e) {
            this.log("Inspector quit");
            e.printStackTrace();
        }
    }

    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        // FIXME What to do here?
    }

    /**
     * reset the tuple centre VM
     */
    public synchronized void reset() {
        TupleCentreContainer.doManagementOperation(TucsonOperation.reset(),
                this.tcId, null);
    }

    @Override
    public void run() {
        try {
            TupleCentreContainer.doManagementOperation(
                    TucsonOperation.addInspCode(), this.tcId, this);
            while (!this.shutdown) {
                final NodeMsg msg = this.dialog.receiveNodeMsg();
                final Class<?> cl = msg.getClass();
                final Method m = this.getClass().getMethod(msg.getAction(),
                        new Class[] { cl });
                m.invoke(this, new Object[] { msg });
            }
            this.dialog.end();
            TupleCentreContainer.doManagementOperation(
                    TucsonOperation.rmvInspCode(), this.tcId, this);
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final SecurityException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        } catch (final DialogException e) {
            e.printStackTrace();
        }
        this.manager.shutdownContext(this.ctxId, this.agentId);
    }

    /**
     * set a new tuple set
     *
     * @param m
     *            the set InQ message
     */
    public synchronized void setEventSet(final SetEventSetMsg m) {
        TupleCentreContainer.doManagementOperation(
                TucsonOperation.setWSetCode(), this.tcId, m.getEventWnSet());
    }

    /**
     * setting new observation protocol
     *
     * @param msg
     *            the set protocol message
     */
    public synchronized void setProtocol(final SetProtocolMsg msg) {
        final boolean wasTracing = this.protocol.isTracing();
        this.protocol = msg.getInfo();
        if (wasTracing) {
            this.notifyAll();
        }
        if (!this.protocol.isTracing()) {
            this.onInspectableEvent(new InspectableEvent(this,
                    InspectableEvent.TYPE_NEWSTATE));
        }
    }

    /**
     * set a new tuple set
     *
     * @param m
     *            the set tuples message
     */
    public synchronized void setTupleSet(final SetTupleSetMsg m) {
        try {
            // Operation Make
            final RespectOperation opRequested = RespectOperation.make(
                    TucsonOperation.setSCode(), (LogicTuple) m.getTupleSet(),
                    null);
            // InputEvent Creation
            final InputEvent ev = new InputEvent(this.agentId, opRequested,
                    this.tcId, System.currentTimeMillis(), null);
            TupleCentreContainer.doBlockingOperation(ev);
            // TupleCentreContainer.doBlockingOperation(TucsonOperation.setCode(),
            // this.agentId, this.tcId, m.getTupleSet());
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
        }
    }

    /**
     * enable/disable VM step Mode
     *
     * @param m
     *            the step mode message
     */
    public void stepMode(final StepModeMsg m) {
        TupleCentreContainer.doManagementOperation(
                TucsonOperation.stepModeCode(), this.tcId, null);
        final ArrayList<InspectableEventListener> inspectors = (ArrayList<InspectableEventListener>) TupleCentreContainer
                .doManagementOperation(TucsonOperation.getInspectorsCode(),
                        this.tcId, null);
        for (final InspectableEventListener insp : inspectors) {
            final InspectorContextSkel skel = (InspectorContextSkel) insp;
            if (skel.getId() == this.getId()) {
                continue;
            }
            final InspectorContextEvent msg = new InspectorContextEvent();
            msg.setModeChanged(true);
            try {
                skel.getDialog().sendInspectorEvent(msg);
            } catch (final DialogException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @return InspectorContextSke dialog
     */
    private AbstractTucsonProtocol getDialog() {
        return this.dialog;
    }

    /**
     *
     * @param st
     *            the String to log
     */
    protected void log(final String st) {
        System.out.println("[InspectorContextSkel]: " + st);
    }
}
