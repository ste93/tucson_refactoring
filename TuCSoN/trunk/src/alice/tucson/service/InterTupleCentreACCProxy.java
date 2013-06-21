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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonOpId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.TucsonMsgReply;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.core.OperationCompletionListener;
import alice.tuplecentre.core.TCCycleResult.Outcome;
import alice.tuplecentre.core.TupleCentreOperation;
import alice.tuprolog.Prolog;
import alice.tuprolog.lib.InvalidObjectIdException;

/**
 * 
 */
public class InterTupleCentreACCProxy implements InterTupleCentreACC,
        OperationCompletionListener {
    /**
	 * 
	 */
    class Controller extends Thread {

        private final ObjectInputStream in;
        private final Prolog p = new Prolog();
        private boolean stop;

        /**
         * 
         * @param input
         */
        Controller(final ObjectInputStream input) {

            this.in = input;
            this.stop = false;
            this.setDaemon(true);

            final alice.tuprolog.lib.JavaLibrary jlib =
                    (alice.tuprolog.lib.JavaLibrary) this.p
                            .getLibrary("alice.tuprolog.lib.JavaLibrary");
            try {
                jlib.register(new alice.tuprolog.Struct("config"), this);
            } catch (final InvalidObjectIdException ex) {
                System.err.println("[InterTupleCentreACCProxy] Controller: "
                        + ex);
                ex.printStackTrace();
            }

        }

        @Override
        @SuppressWarnings("unchecked")
        public void run() {

            TucsonOpCompletionEvent ev = null;
            while (!this.isStopped()) {

                TucsonMsgReply msg = null;
                try {
                    msg = TucsonMsgReply.read(this.in);
                } catch (final EOFException e) {
                    InterTupleCentreACCProxy
                            .log("TuCSoN node service unavailable, nothing I can do");
                    this.setStop();
                    break;
                } catch (final Exception ex) {
                    this.setStop();
                    System.err
                            .println("[InterTupleCentreACCProxy] Controller: "
                                    + ex);
                }

                final boolean ok = msg.isAllowed();
                if (ok) {

                    final int type = msg.getType();
                    if ((type == TucsonOperation.noCode())
                            || (type == TucsonOperation.no_sCode())
                            || (type == TucsonOperation.nopCode())
                            || (type == TucsonOperation.nop_sCode())
                            || (type == TucsonOperation.inCode())
                            || (type == TucsonOperation.rdCode())
                            || (type == TucsonOperation.inpCode())
                            || (type == TucsonOperation.rdpCode())
                            || (type == TucsonOperation.uinCode())
                            || (type == TucsonOperation.urdCode())
                            || (type == TucsonOperation.uinpCode())
                            || (type == TucsonOperation.urdpCode())
                            || (type == TucsonOperation.unoCode())
                            || (type == TucsonOperation.unopCode())
                            || (type == TucsonOperation.in_sCode())
                            || (type == TucsonOperation.rd_sCode())
                            || (type == TucsonOperation.inp_sCode())
                            || (type == TucsonOperation.rdp_sCode())) {

                        final boolean succeeded = msg.isSuccess();
                        if (succeeded) {

                            final LogicTuple tupleReq = msg.getTupleRequested();
                            final LogicTuple tupleRes =
                                    (LogicTuple) msg.getTupleResult();
                            final LogicTuple res =
                                    this.unify(tupleReq, tupleRes);
                            ev =
                                    new TucsonOpCompletionEvent(new TucsonOpId(
                                            msg.getId()), ok, true, res);

                        } else {
                            ev =
                                    new TucsonOpCompletionEvent(new TucsonOpId(
                                            msg.getId()), ok, false);
                        }

                    } else if ((type == TucsonOperation.set_Code())
                            || (type == TucsonOperation.set_sCode())
                            || (type == TucsonOperation.outCode())
                            || (type == TucsonOperation.out_sCode())
                            || (type == TucsonOperation.out_allCode())
                            || (type == TucsonOperation.spawnCode())) {
                        ev =
                                new TucsonOpCompletionEvent(new TucsonOpId(
                                        msg.getId()), ok, msg.isSuccess());
                    } else if ((type == TucsonOperation.in_allCode())
                            || (type == TucsonOperation.rd_allCode())
                            || (type == TucsonOperation.no_allCode())
                            || (type == TucsonOperation.get_Code())
                            || (type == TucsonOperation.get_sCode())) {
                        final List<LogicTuple> tupleSetRes =
                                (List<LogicTuple>) msg.getTupleResult();
                        ev =
                                new TucsonOpCompletionEvent(new TucsonOpId(
                                        msg.getId()), ok, msg.isSuccess(),
                                        tupleSetRes);
                    } else if (type == TucsonOperation.exitCode()) {
                        this.setStop();
                        break;
                    }

                } else {
                    ev =
                            new TucsonOpCompletionEvent(new TucsonOpId(
                                    msg.getId()), false, false);
                }

                final TupleCentreOperation op =
                        InterTupleCentreACCProxy.this.operations.remove(msg
                                .getId());
                if (op.isNoAll() || op.isInAll() || op.isRdAll() || op.isGet()
                        || op.isSet() || op.isGet_s() || op.isSet_s()
                        || op.isOutAll()) {
                    InterTupleCentreACCProxy.log("received completion msg "
                            + msg.getId() + ", op " + op.getType() + ", "
                            + op.getTupleListResult());
                    op.setTupleListResult((List<Tuple>) msg.getTupleResult());
                } else {
                    InterTupleCentreACCProxy.log("received completion msg "
                            + msg.getId() + ", op " + op.getType() + ", "
                            + op.getTupleResult());
                    op.setTupleResult((LogicTuple) msg.getTupleResult());
                }
                if (msg.isResultSuccess()) {
                    op.setOpResult(Outcome.SUCCESS);
                } else {
                    op.setOpResult(Outcome.FAILURE);
                }

                op.notifyCompletion();
                InterTupleCentreACCProxy.this.postEvent(ev);

            }

        }

        synchronized boolean isStopped() {
            return this.stop;
        }

        synchronized void setStop() {
            this.stop = true;
        }

        LogicTuple unify(final TupleTemplate template, final Tuple tuple) {
            final boolean res = template.propagate(this.p, tuple);
            if (res) {
                return (LogicTuple) template;
            }
            return null;

        }

    }

    class ControllerSession {

        private final Controller controller;
        private final TucsonProtocol session;

        ControllerSession(final Controller c, final TucsonProtocol s) {
            this.controller = c;
            this.session = s;
        }

        public Controller getController() {
            return this.controller;
        }

        public TucsonProtocol getSession() {
            return this.session;
        }

    }

    private static void log(final String msg) {
        System.out.println("[InterTupleCentreACCProxy]: " + msg);
    }

    protected HashMap<Long, TupleCentreOperation> operations;
    // aid è il tuplecentre source
    private TucsonTupleCentreId aid;
    private final HashMap<String, ControllerSession> controllerSessions;

    private final LinkedList<TucsonOpCompletionEvent> events;

    private long opId;

    private final ACCDescription profile;

    /**
     * 
     * @param id
     *            tuplecentre source
     */
    public InterTupleCentreACCProxy(final Object id) {

        if (id.getClass().getName()
                .equals("alice.tucson.api.TucsonTupleCentreId")) {
            this.aid = (TucsonTupleCentreId) id;
        } else {
            try {
                this.aid = new TucsonTupleCentreId(id);
            } catch (final TucsonInvalidTupleCentreIdException e) {
                System.err.println("[InterTupleCentreACCProxy]: " + e);
                e.printStackTrace();
            }
        }

        this.profile = new ACCDescription();
        this.events = new LinkedList<TucsonOpCompletionEvent>();
        this.controllerSessions = new HashMap<String, ControllerSession>();
        this.operations = new HashMap<Long, TupleCentreOperation>();
        this.opId = -1;

    }

    /**
     * tid il tuplecentre target
     */
    public synchronized TucsonOpId doOperation(final Object tid,
            final TupleCentreOperation op)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {

        TucsonTupleCentreId tcid = null;
        if (tid.getClass().getName()
                .equals("alice.tucson.api.TucsonTupleCentreId")) {
            tcid = (TucsonTupleCentreId) tid;
        } else {
            try {
                tcid = new TucsonTupleCentreId(tid);
            } catch (final TucsonInvalidTupleCentreIdException e) {
                throw new TucsonOperationNotPossibleException();
            }
        }

        int nTry = 0;
        boolean exception;

        do {

            this.opId++;
            nTry++;
            exception = false;

            TucsonProtocol session = null;
            try {
                session = this.getSession(tcid);
            } catch (final UnreachableNodeException ex2) {
                exception = true;
                throw new UnreachableNodeException();
            }
            final ObjectOutputStream outStream = session.getOutputStream();

            this.operations.put(this.opId, op);
            final int type = op.getType();
            TucsonMsgRequest msg;

            if ((type == TucsonOperation.outCode())
                    || (type == TucsonOperation.out_sCode())
                    || (type == TucsonOperation.set_sCode())
                    || (type == TucsonOperation.set_Code())
                    || (type == TucsonOperation.out_allCode())
                    || (type == TucsonOperation.spawnCode())) {
                msg =
                        new TucsonMsgRequest(this.opId, type, tcid.toString(),
                                (LogicTuple) op.getTupleArgument());
            } else {
                msg =
                        new TucsonMsgRequest(this.opId, type, tcid.toString(),
                                (LogicTuple) op.getTemplateArgument());
            }
            InterTupleCentreACCProxy.log("sending msg " + msg.getId()
                    + ", op = " + msg.getType() + ", " + msg.getTuple() + ", "
                    + msg.getTid());
            try {
                TucsonMsgRequest.write(outStream, msg);
                outStream.flush();
            } catch (final IOException ex) {
                exception = true;
                System.err.println("[InterTupleCentreACCProxy]: " + ex);
                ex.printStackTrace();
            }

            if (!exception) {
                return new TucsonOpId(this.opId);
            }

        } while (nTry < 3);

        throw new UnreachableNodeException();

    }

    public void operationCompleted(final TupleCentreOperation op) {
        // TODO Auto-generated method stub

    }

    /**
	 * 
	 */
    public TucsonOpCompletionEvent waitForCompletion(final TucsonOpId id) {

        try {
            synchronized (this.events) {
                TucsonOpCompletionEvent ev = this.findEvent(id);
                while (ev == null) {
                    this.events.wait();
                    ev = this.findEvent(id);
                }
                return ev;
            }
        } catch (final Exception ex) {
            return null;
        }

    }

    /**
	 * 
	 */
    public TucsonOpCompletionEvent waitForCompletion(final TucsonOpId id,
            final int timeout) {

        try {
            final long startTime = System.currentTimeMillis();
            synchronized (this.events) {
                long dt = System.currentTimeMillis() - startTime;
                TucsonOpCompletionEvent ev = this.findEvent(id);
                while ((ev == null) && (dt < timeout)) {
                    this.events.wait(timeout - dt);
                    ev = this.findEvent(id);
                    dt = System.currentTimeMillis() - startTime;
                }
                return ev;
            }
        } catch (final Exception ex) {
            return null;
        }

    }

    /**
     * 
     * @param id
     * @return
     */
    private TucsonOpCompletionEvent findEvent(final TucsonOpId id) {
        final Iterator<TucsonOpCompletionEvent> it = this.events.iterator();
        while (it.hasNext()) {
            final TucsonOpCompletionEvent ev = it.next();
            if (ev.getOpId().equals(id)) {
                it.remove();
                return ev;
            }
        }
        return null;
    }

    /**
     * 
     * @param tid
     * @return
     * @throws TucsonOperationNotPossibleException
     * @throws UnreachableNodeException
     */
    private TucsonProtocol getSession(final TucsonTupleCentreId tid)
            throws UnreachableNodeException {

        final String opNode = alice.util.Tools.removeApices(tid.getNode());
        final int port = tid.getPort();
        ControllerSession tc = this.controllerSessions.get(opNode + ":" + port);
        if (tc != null) {
            return tc.getSession();
        }
        if (opNode.equals("localhost")) {
            tc = this.controllerSessions.get("127.0.0.1:" + port);
        }
        if (opNode.equals("127.0.0.1")) {
            tc = this.controllerSessions.get("localhost:" + port);
        }
        if (tc != null) {
            return tc.getSession();
        }
        this.profile.setProperty("tc-identity", this.aid.toString());
        this.profile.setProperty("agent-role", "user");

        TucsonProtocol dialog = null;
        boolean isEnterReqAcpt = false;
        try {
            dialog = new TucsonProtocolTCP(opNode, port);
            dialog.sendEnterRequest(this.profile);
            dialog.receiveEnterRequestAnswer();
            if (dialog.isEnterRequestAccepted()) {
                isEnterReqAcpt = true;
            }
        } catch (final Exception ex) {
            throw new UnreachableNodeException();
        }

        if (isEnterReqAcpt) {
            final ObjectInputStream din = dialog.getInputStream();
            final Controller contr = new Controller(din);
            final ControllerSession cs = new ControllerSession(contr, dialog);
            this.controllerSessions.put(opNode + ":" + port, cs);
            contr.start();
            return dialog;
        }

        return null;

    }

    private void postEvent(final TucsonOpCompletionEvent ev) {
        synchronized (this.events) {
            this.events.addLast(ev);
            this.events.notifyAll();
        }
    }

}
