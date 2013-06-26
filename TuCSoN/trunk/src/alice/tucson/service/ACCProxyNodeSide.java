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
package alice.tucson.service;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.network.TucsonMsgReply;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.TucsonProtocol;
import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.TupleCentreOperation;

/**
 * 
 */
public class ACCProxyNodeSide extends ACCAbstractProxyNodeSide {

    private TucsonAgentId agentId;
    private final int ctxId;
    private final TucsonProtocol dialog;
    private boolean ex = false;
    private final ObjectInputStream inStream;
    private final ACCProvider manager;
    private final TucsonNodeService node;
    private final Map<Long, Long> opVsReq;
    private final ObjectOutputStream outStream;
    private final Map<Long, TucsonMsgRequest> requests;
    private TucsonTupleCentreId tcId;

    /**
     * 
     * @param man
     * @param d
     * @param n
     * @param p
     */
    public ACCProxyNodeSide(final ACCProvider man, final TucsonProtocol d,
            final TucsonNodeService n, final ACCDescription p) {

        super();
        this.ctxId = Integer.parseInt(p.getProperty("context-id"));

        String name = p.getProperty("agent-identity");
        if (name == null) {
            name = p.getProperty("tc-identity");
            try {
                this.tcId = new TucsonTupleCentreId(name);
                this.agentId = new TucsonAgentId("tcAgent", this.tcId);
            } catch (final TucsonInvalidTupleCentreIdException e) {
                System.err.println("[ACCProxyNodeSide]: " + e);
                // TODO Properly handle Exception
            }
        } else {
            try {
                this.agentId = new TucsonAgentId(name);
            } catch (final TucsonInvalidAgentIdException e) {
                System.err.println("[ACCProxyNodeSide]: " + e);
                // TODO Properly handle Exception
            }
        }

        this.dialog = d;
        this.inStream = d.getInputStream();
        this.outStream = d.getOutputStream();

        this.requests = new HashMap<Long, TucsonMsgRequest>();
        this.opVsReq = new HashMap<Long, Long>();

        this.node = n;
        this.manager = man;

    }

    @Override
    synchronized public void exit() {
        this.ex = true;
        this.notify();
    }

    /**
	 * 
	 */
    public void operationCompleted(final TupleCentreOperation op) {

        Long reqId;
        TucsonMsgRequest msg;
        synchronized (this.requests) {
            reqId = this.opVsReq.remove(Long.valueOf(op.getId()));
            msg = this.requests.remove(reqId);
        }

        TucsonMsgReply reply = null;

        if (op.isInAll() || op.isRdAll() || op.isNoAll() || op.isOutAll()) {
            if (op.getTupleListResult() == null) {
                op.setTupleListResult(new LinkedList<Tuple>());
            }
            if (op.isResultSuccess()) {
                reply =
                        new TucsonMsgReply(msg.getId(), op.getType(), true,
                                true, true, msg.getTuple(),
                                op.getTupleListResult());
            } else {
                reply =
                        new TucsonMsgReply(msg.getId(), op.getType(), true,
                                true, false, msg.getTuple(),
                                op.getTupleListResult());
            }
        } else {
            if (op.isResultSuccess()) {
                reply =
                        new TucsonMsgReply(msg.getId(), op.getType(), true,
                                true, true, msg.getTuple(), op.getTupleResult());
            } else {
                reply =
                        new TucsonMsgReply(msg.getId(), op.getType(), true,
                                true, false, msg.getTuple(),
                                op.getTupleResult());
            }
        }

        try {
            TucsonMsgReply.write(this.outStream, reply);
            this.outStream.flush();
        } catch (final IOException e) {
            // TODO Properly handle Exception
            System.err.println("[ACCProxyNodeSide]: " + e);
        }

    }

    /**
	 * 
	 */
    @Override
    @SuppressWarnings({ "unchecked", "finally" })
    public void run() {

        this.node.addAgent(this.agentId);

        TucsonMsgRequest msg;
        TucsonMsgReply reply;
        TucsonTupleCentreId tid;
        final LogicTuple res = null;
        List<LogicTuple> resList;

        while (!this.ex) {

            this.log("Listening to incoming TuCSoN agents/nodes requests...");

            try {
                msg = TucsonMsgRequest.read(this.inStream);
            } catch (final EOFException e) {
                this.log("Agent " + this.agentId + " quitted");
                break;
            } catch (final Exception e) {
                System.err.println("[ACCProxyNodeSide]: " + e);
                // TODO Properly handle Exception
                break;
            }
            final int msg_type = msg.getType();

            if (msg_type == TucsonOperation.exitCode()) {
                reply =
                        new TucsonMsgReply(msg.getId(),
                                TucsonOperation.exitCode(), true, true, true);
                try {
                    TucsonMsgReply.write(this.outStream, reply);
                    this.outStream.flush();
                } catch (final IOException e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                } finally {
                    break;
                }
            }

            try {
                tid = new TucsonTupleCentreId(msg.getTid());
            } catch (final TucsonInvalidTupleCentreIdException e) {
                System.err.println("[ACCProxyNodeSide]: " + e);
                // TODO Properly handle Exception
                break;
            }

            this.log("Serving TucsonOperation request < id=" + msg.getId()
                    + ", type=" + msg.getType() + ", tuple=" + msg.getTuple()
                    + " >...");

            if (msg_type == TucsonOperation.set_sCode()) {
                this.node.resolveCore(tid.getName());
                this.node.addTCAgent(this.agentId, tid);
                try {
                    if (this.tcId == null) {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingSpecOperation(msg_type,
                                                this.agentId, tid,
                                                msg.getTuple());
                    } else {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingSpecOperation(msg_type,
                                                this.tcId, tid, msg.getTuple());
                    }
                } catch (final Exception e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

                reply =
                        new TucsonMsgReply(msg.getId(), msg_type, true, true,
                                true, msg.getTuple(), resList);

                try {
                    TucsonMsgReply.write(this.outStream, reply);
                    this.outStream.flush();
                } catch (final IOException e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

            } else if (msg_type == TucsonOperation.set_Code()) {

                this.node.resolveCore(tid.getName());
                this.node.addTCAgent(this.agentId, tid);
                try {
                    if (this.tcId == null) {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingOperation(msg_type,
                                                this.agentId, tid,
                                                msg.getTuple());
                    } else {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingOperation(msg_type,
                                                this.tcId, tid, msg.getTuple());
                    }
                } catch (final Exception e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

                reply =
                        new TucsonMsgReply(msg.getId(), msg_type, true, true,
                                true, res, resList);

                try {
                    TucsonMsgReply.write(this.outStream, reply);
                    this.outStream.flush();
                } catch (final IOException e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

            } else if (msg_type == TucsonOperation.get_Code()) {

                this.node.resolveCore(tid.getName());
                this.node.addTCAgent(this.agentId, tid);
                try {
                    if (this.tcId == null) {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingOperation(msg_type,
                                                this.agentId, tid, null);
                    } else {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingOperation(msg_type,
                                                this.tcId, tid, null);
                    }
                } catch (final Exception e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

                reply =
                        new TucsonMsgReply(msg.getId(), msg_type, true, true,
                                true, null, resList);

                try {
                    TucsonMsgReply.write(this.outStream, reply);
                    this.outStream.flush();
                } catch (final IOException e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

            } else if (msg_type == TucsonOperation.get_sCode()) {

                this.node.resolveCore(tid.getName());
                this.node.addTCAgent(this.agentId, tid);
                try {
                    if (this.tcId == null) {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingSpecOperation(msg_type,
                                                this.agentId, tid, null);
                    } else {
                        resList =
                                (List<LogicTuple>) TupleCentreContainer
                                        .doBlockingSpecOperation(msg_type,
                                                this.tcId, tid, null);
                    }
                    if (resList == null) {
                        resList = new LinkedList<LogicTuple>();
                    }
                } catch (final Exception e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

                reply =
                        new TucsonMsgReply(msg.getId(), msg_type, true, true,
                                true, null, resList);

                try {
                    TucsonMsgReply.write(this.outStream, reply);
                    this.outStream.flush();
                } catch (final IOException e) {
                    System.err.println("[ACCProxyNodeSide]: " + e);
                    // TODO Properly handle Exception
                    break;
                }

            } else if ((msg_type == TucsonOperation.noCode())
                    || (msg_type == TucsonOperation.nopCode())
                    || (msg_type == TucsonOperation.outCode())
                    || (msg_type == TucsonOperation.out_allCode())
                    || (msg_type == TucsonOperation.inCode())
                    || (msg_type == TucsonOperation.inpCode())
                    || (msg_type == TucsonOperation.rdCode())
                    || (msg_type == TucsonOperation.rdpCode())
                    || (msg_type == TucsonOperation.uinCode())
                    || (msg_type == TucsonOperation.uinpCode())
                    || (msg_type == TucsonOperation.urdCode())
                    || (msg_type == TucsonOperation.urdpCode())
                    || (msg_type == TucsonOperation.unoCode())
                    || (msg_type == TucsonOperation.unopCode())
                    || (msg_type == TucsonOperation.in_allCode())
                    || (msg_type == TucsonOperation.rd_allCode())
                    || (msg_type == TucsonOperation.no_allCode())
                    || (msg_type == TucsonOperation.spawnCode())) {

                this.node.resolveCore(tid.getName());
                this.node.addTCAgent(this.agentId, tid);
                ITupleCentreOperation op;

                synchronized (this.requests) {
                    try {
                        if (this.tcId == null) {
                            op =
                                    TupleCentreContainer
                                            .doNonBlockingOperation(msg_type,
                                                    this.agentId, tid,
                                                    msg.getTuple(), this);
                        } else {
                            op =
                                    TupleCentreContainer
                                            .doNonBlockingOperation(msg_type,
                                                    this.tcId, tid,
                                                    msg.getTuple(), this);
                        }
                    } catch (final Exception e) {
                        System.err.println("[ACCProxyNodeSide]: " + e);
                        // TODO Properly handle Exception
                        break;
                    }
                    this.requests.put(Long.valueOf(msg.getId()), msg);
                    this.opVsReq.put(Long.valueOf(op.getId()),
                            Long.valueOf(msg.getId()));
                }

            } else if ((msg_type == TucsonOperation.no_sCode())
                    || (msg_type == TucsonOperation.nop_sCode())
                    || (msg_type == TucsonOperation.out_sCode())
                    || (msg_type == TucsonOperation.in_sCode())
                    || (msg_type == TucsonOperation.inp_sCode())
                    || (msg_type == TucsonOperation.rd_sCode())
                    || (msg_type == TucsonOperation.rdp_sCode())) {

                this.node.resolveCore(tid.getName());
                this.node.addTCAgent(this.agentId, tid);
                ITupleCentreOperation op;

                synchronized (this.requests) {
                    try {
                        if (this.tcId == null) {
                            op =
                                    TupleCentreContainer
                                            .doNonBlockingSpecOperation(
                                                    msg_type, this.agentId,
                                                    tid, msg.getTuple(), this);
                        } else {
                            op =
                                    TupleCentreContainer
                                            .doNonBlockingSpecOperation(
                                                    msg_type, this.tcId, tid,
                                                    msg.getTuple(), this);
                        }
                    } catch (final Exception e) {
                        System.err.println("[ACCProxyNodeSide]: " + e);
                        // TODO Properly handle Exception
                        break;
                    }
                    this.requests.put(Long.valueOf(msg.getId()), msg);
                    this.opVsReq.put(Long.valueOf(op.getId()),
                            Long.valueOf(msg.getId()));
                }

            }

        }

        try {
            this.dialog.end();
        } catch (final Exception e) {
            // TODO Properly handle Exception
            System.err.println("[ACCProxyNodeSide]: " + e);
        }

        this.log("Releasing ACC < " + this.ctxId + " > held by TuCSoN agent < "
                + this.agentId.toString() + " >");
        this.node.removeAgent(this.agentId);
        this.manager.shutdownContext(this.ctxId, this.agentId);
        this.node.removeNodeAgent(this);

    }

    private void log(final String st) {
        System.out.println("[ACCProxyNodeSide (" + this.node.getTCPPort()
                + ", " + this.ctxId + ")]: " + st);
    }

}
