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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.service.ACCAbstractProxyNodeSide;
import alice.tucson.service.ACCDescription;
import alice.tucson.service.ACCProvider;
import alice.tucson.service.TucsonNodeService;
import alice.tucson.service.TucsonOperation;
import alice.tucson.service.TucsonTCUsers;
import alice.tucson.service.TupleCentreContainer;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.core.InspectableEvent;
import alice.tuplecentre.core.ObservableEventExt;
import alice.tuplecentre.core.ObservableEventReactionFail;
import alice.tuplecentre.core.ObservableEventReactionOK;
import alice.tuplecentre.core.TriggeredReaction;
import alice.tuplecentre.core.TupleCentreOperation;

public class InspectorContextSkel extends ACCAbstractProxyNodeSide implements
        InspectableEventListener {

    TucsonAgentId agentId;
    int ctxId;

    TucsonProtocol dialog;

    ObjectInputStream inStream;

    ACCProvider manager;
    boolean nextStep;
    /** channel with remote inspector proxy */
    ObjectOutputStream outStream;
    /** current observation protocol */
    InspectorProtocol protocol;
    boolean shutdown = false;
    TucsonTupleCentreId tcId;

    public InspectorContextSkel(final ACCProvider man,
            final TucsonProtocol d, final TucsonNodeService node,
            final ACCDescription p) throws TucsonGenericException {

        this.dialog = d;
        this.manager = man;
        NewInspectorMsg msg = null;
        try {
            this.ctxId = Integer.parseInt(p.getProperty("context-id"));
            final String name = p.getProperty("agent-identity");
            this.agentId = new TucsonAgentId(name);
            this.inStream = d.getInputStream();
            this.outStream = d.getOutputStream();

            msg = (NewInspectorMsg) this.inStream.readObject();
            this.tcId = new TucsonTupleCentreId(msg.tcName);
        } catch (final NumberFormatException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        }

        final TucsonTCUsers coreInfo = node.resolveCore(msg.tcName);
        if (coreInfo == null) {
            throw new TucsonGenericException(
                    "Internal error: InspectorContextSkel constructor");
        }
        this.protocol = msg.info;

    }

    @Override
    public void exit() {
        this.shutdown = true;
    }

    /** get a tuple centre set (T set, W set,...) snapshot */
    public void getSnapshot(final GetSnapshotMsg m) {

        final InspectorContextEvent msg = new InspectorContextEvent();
        msg.vmTime = System.currentTimeMillis();
        msg.localTime = System.currentTimeMillis();

        if (m.what == GetSnapshotMsg.TSET) {

            msg.tuples = new LinkedList<LogicTuple>();
            LogicTuple[] tSet = null;
            try {
                tSet =
                        (LogicTuple[]) TupleCentreContainer
                                .doManagementOperation(
                                        TucsonOperation.getTSetCode(),
                                        this.tcId, this.protocol.tsetFilter);
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final TucsonInvalidLogicTupleException e) {
                e.printStackTrace();
            }
            for (final LogicTuple lt : tSet) {
                msg.tuples.add(lt);
            }

        } else if (m.what == GetSnapshotMsg.WSET) {

            WSetEvent[] ltSet = null;
            try {
                ltSet =
                        (WSetEvent[]) TupleCentreContainer
                                .doManagementOperation(
                                        TucsonOperation.getWSetCode(),
                                        this.tcId, this.protocol.wsetFilter);
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final TucsonInvalidLogicTupleException e) {
                e.printStackTrace();
            }
            msg.wnEvents = new LinkedList<WSetEvent>();
            for (final WSetEvent lt : ltSet) {
                msg.wnEvents.add(lt);
            }

        }

        try {
            this.outStream.writeObject(msg);
            this.outStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    /** ask a new step for a tuple centre vm during tracing */
    synchronized public void nextStep() {
        if (this.protocol.tracing) {
            this.nextStep = true;
            this.notifyAll();
        }
    }

    synchronized public void onInspectableEvent(final InspectableEvent ev) {

        try {

            while (this.protocol.tracing && !this.nextStep) {
                this.wait();
            }
            this.nextStep = false;

            final InspectorContextEvent msg = new InspectorContextEvent();
            msg.localTime = System.currentTimeMillis();
            msg.vmTime = ev.getTime();

            if (ev.getType() == InspectableEvent.TYPE_NEWSTATE) {

                if (this.protocol.tsetObservType == InspectorProtocol.PROACTIVE_OBSERVATION) {
                    final LogicTuple[] ltSet =
                            (LogicTuple[]) TupleCentreContainer
                                    .doManagementOperation(
                                            TucsonOperation.getTSetCode(),
                                            this.tcId, this.protocol.tsetFilter);
                    msg.tuples = new LinkedList<LogicTuple>();
                    if (ltSet != null) {
                        for (final LogicTuple lt : ltSet) {
                            msg.tuples.add(lt);
                        }
                    }
                }

                if (this.protocol.pendingQueryObservType == InspectorProtocol.PROACTIVE_OBSERVATION) {
                    final WSetEvent[] ltSet =
                            (WSetEvent[]) TupleCentreContainer
                                    .doManagementOperation(
                                            TucsonOperation.getWSetCode(),
                                            this.tcId, this.protocol.wsetFilter);
                    msg.wnEvents = new LinkedList<WSetEvent>();
                    if (ltSet != null) {
                        for (final WSetEvent lt : ltSet) {
                            msg.wnEvents.add(lt);
                        }
                    }
                }

                this.outStream.writeObject(msg);
                this.outStream.flush();

            } else if (ev.getType() == ObservableEventExt.TYPE_REACTIONOK) {

                if (this.protocol.reactionsObservType != InspectorProtocol.NO_OBSERVATION) {
                    final TriggeredReaction zCopy =
                            new TriggeredReaction(null,
                                    ((ObservableEventReactionOK) ev).z
                                            .getReaction());
                    msg.reactionOk = zCopy;
                    this.outStream.writeObject(msg);
                    this.outStream.flush();
                }

            } else if (ev.getType() == ObservableEventExt.TYPE_REACTIONFAIL) {

                if (this.protocol.reactionsObservType != InspectorProtocol.NO_OBSERVATION) {
                    final TriggeredReaction zCopy =
                            new TriggeredReaction(null,
                                    ((ObservableEventReactionFail) ev).z
                                            .getReaction());
                    msg.reactionFailed = zCopy;
                    this.outStream.writeObject(msg);
                    this.outStream.flush();
                }

            }

        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

    public void operationCompleted(final TupleCentreOperation op) {
        // TODO Auto-generated method stub
    }

    /** reset the tuple centre VM */
    synchronized public void reset() {
        try {
            TupleCentreContainer.doManagementOperation(TucsonOperation.reset(),
                    this.tcId, null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            TupleCentreContainer.doManagementOperation(
                    TucsonOperation.addInspCode(), this.tcId, this);
            while (!this.shutdown) {
                final NodeMsg msg = (NodeMsg) this.inStream.readObject();
                final Class<?> cl = msg.getClass();
                final Method m =
                        this.getClass().getMethod(msg.action,
                                new Class[] { cl });
                m.invoke(this, new Object[] { msg });
            }
            this.dialog.end();
            TupleCentreContainer.doManagementOperation(
                    TucsonOperation.rmvInspCode(), this.tcId, this);
        } catch (final EOFException e) {
            // TODO Properly handle Exception
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
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
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        this.manager.shutdownContext(this.ctxId, this.agentId);

    }

    /** set a new tuple set */
    synchronized public void setEventSet(final SetEventSetMsg m) {
        try {
            TupleCentreContainer.doManagementOperation(
                    TucsonOperation.setWSetCode(), this.tcId, m.eventWnSet);
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        }
    }

    /** setting new observation protocol */
    synchronized public void setProtocol(final SetProtocolMsg msg) {
        final boolean wasTracing = this.protocol.tracing;
        this.protocol = msg.info;
        if (wasTracing) {
            this.notifyAll();
        }
        if (!this.protocol.tracing) {
            this.onInspectableEvent(new InspectableEvent(this,
                    InspectableEvent.TYPE_NEWSTATE));
        }
    }

    /** set a new tuple set */
    synchronized public void setTupleSet(final SetTupleSetMsg m) {
        try {
            TupleCentreContainer.doBlockingOperation(
                    TucsonOperation.set_Code(), this.agentId, this.tcId,
                    m.tupleSet);
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        }
    }

    protected void log(final String st) {
        System.out.println("[InspectorContextSkel]: " + st);
    }

}
