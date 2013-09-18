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

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonOpId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.AbstractTucsonProtocol;
import alice.tucson.network.TPFactory;
import alice.tucson.network.TucsonMsgReply;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.exceptions.DialogException;
import alice.tucson.parsing.MyOpManager;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.TCCycleResult.Outcome;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.lib.InvalidObjectIdException;

/**
 * Active part of the Default Agent Coordination Context.
 * 
 * It implements the underlying behavior needed by every TuCSoN Agent
 * {@link alice.tucson.api.AbstractTucsonAgent user} to fruitfully interact with
 * the TuCSoN Node Service {@link alice.tucson.service.TucsonNodeService TuCSoN}
 * . Essentially, it implements every method exposed in the Default ACC
 * Interface {@link alice.tucson.api.DefaultACC default} offered to the agent,
 * maps each of them into TuCSoN Request Messages
 * {@link alice.tucson.network.TucsonMsgRequest req}, then waits for TuCSoN Node
 * Services replies {@link alice.tucson.network.TucsonMsgReply reply} forwarding
 * them to the agent.
 * 
 * It also is in charge of establishing the first connection toward the TuCSoN
 * Node Service and the specific tuplecentre inside it as soon as needed (that
 * is, upon the first invocation of any method belonging to the ACC Interface).
 * 
 * It is created from the TuCSoN Agent class. In it, an internal thread is
 * responsible to obtain the choosen ACC (the Default is the only at the moment)
 * by invoking the {@link alice.tucson.api.TucsonMetaACC#getContext getContext}
 * static method from the TuCSoN Meta ACC class
 * {@link alice.tucson.api.TucsonMetaACC metaACC}. The acquisition of such ACC
 * triggers this proxy creation and execution.
 * 
 * @see alice.tucson.api.AbstractTucsonAgent TucsonAgent
 * @see alice.tucson.service.TucsonNodeService TucsonNodeService
 * @see alice.tucson.api.DefaultACC DefaultACC
 * @see alice.tucson.network.TucsonMsgRequest TucsonMsgRequest
 * @see alice.tucson.network.TucsonMsgReply TucsonMsgReply
 * @see alice.tucson.api.TucsonMetaACC TucsonMetaACC
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * 
 */
public class ACCProxyAgentSide implements EnhancedACC {

    /**
     * 
     */
    protected class Controller extends Thread {

        private final AbstractTucsonProtocol dialog;
        private final Prolog p = new Prolog();
        private boolean stop;

        /**
         * 
         * @param input
         */
        Controller(final AbstractTucsonProtocol d) {

            super();

            this.dialog = d;
            this.stop = false;
            this.setDaemon(true);

            final alice.tuprolog.lib.JavaLibrary jlib =
                    (alice.tuprolog.lib.JavaLibrary) this.p
                            .getLibrary("alice.tuprolog.lib.JavaLibrary");
            try {
                jlib.register(new alice.tuprolog.Struct("config"), this);
            } catch (final InvalidObjectIdException e) {
                e.printStackTrace();
            }

        }

        /**
         * 
         */
        @Override
        public void run() {

            TucsonOpCompletionEvent ev = null;
            while (!this.isStopped()) {

                for (final Long operation : ACCProxyAgentSide.this.operationExpired) {
                    ACCProxyAgentSide.this.operations.remove(operation);
                }

                TucsonMsgReply msg = null;
                try {
                    msg = this.dialog.receiveMsgReply();
                } catch (final DialogException e) {
                    ACCProxyAgentSide.this
                            .log("TuCSoN node service unavailable, nothing I can do");
                    this.setStop();
                    break;
                }

                final boolean ok = msg.isAllowed();
                if (ok) {

                    final int type = msg.getType();
                    if ((type == TucsonOperation.uinCode())
                            || (type == TucsonOperation.uinpCode())
                            || (type == TucsonOperation.urdCode())
                            || (type == TucsonOperation.urdpCode())
                            || (type == TucsonOperation.unoCode())
                            || (type == TucsonOperation.unopCode())
                            || (type == TucsonOperation.noCode())
                            || (type == TucsonOperation.noSCode())
                            || (type == TucsonOperation.nopCode())
                            || (type == TucsonOperation.nopSCode())
                            || (type == TucsonOperation.inCode())
                            || (type == TucsonOperation.rdCode())
                            || (type == TucsonOperation.inpCode())
                            || (type == TucsonOperation.rdpCode())
                            || (type == TucsonOperation.inSCode())
                            || (type == TucsonOperation.rdSCode())
                            || (type == TucsonOperation.inpSCode())
                            || (type == TucsonOperation.rdpSCode())) {

                        final boolean succeeded = msg.isSuccess();
                        if (succeeded) {

                            final LogicTuple tupleReq = msg.getTupleRequested();
                            final LogicTuple tupleRes =
                                    (LogicTuple) msg.getTupleResult();
                            // log("tupleReq="+tupleReq+", tupleRes="+tupleRes);
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

                    } else if ((type == TucsonOperation.setCode())
                            || (type == TucsonOperation.setSCode())
                            || (type == TucsonOperation.outCode())
                            || (type == TucsonOperation.outSCode())
                            || (type == TucsonOperation.outAllCode())
                            || (type == TucsonOperation.spawnCode())) {
                        ev =
                                new TucsonOpCompletionEvent(new TucsonOpId(
                                        msg.getId()), ok, msg.isSuccess());
                    } else if ((type == TucsonOperation.inAllCode())
                            || (type == TucsonOperation.rdAllCode())
                            || (type == TucsonOperation.noAllCode())
                            || (type == TucsonOperation.getCode())
                            || (type == TucsonOperation.getSCode())) {
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

                final TucsonOperation op =
                        ACCProxyAgentSide.this.operations.remove(msg.getId());
                if (op.isNoAll() || op.isInAll() || op.isRdAll() || op.isGet()
                        || op.isSet() || op.isGetS() || op.isSetS()
                        || op.isOutAll()) {
                    op.setLogicTupleListResult((List<LogicTuple>) msg
                            .getTupleResult());
                } else {

                    op.setTupleResult((LogicTuple) msg.getTupleResult());
                }
                if (msg.isResultSuccess()) {
                    op.setOpResult(Outcome.SUCCESS);
                } else {
                    op.setOpResult(Outcome.FAILURE);
                }
                op.notifyCompletion(ev.operationSucceeded(), msg.isAllowed());
                ACCProxyAgentSide.this.postEvent(ev);

            }

        }

        /**
         * 
         * @return
         */
        private synchronized boolean isStopped() {
            return this.stop;
        }

        /**
         * 
         */
        private synchronized void setStop() {
            this.stop = true;
        }

        /**
         * 
         * @param template
         * @param tuple
         * @return
         */
        private LogicTuple
                unify(final TupleTemplate template, final Tuple tuple) {
            final boolean res = template.propagate(this.p, tuple);
            if (res) {
                return (LogicTuple) template;
            }
            return null;

        }

    }

    /**
     * 
     */
    protected class ControllerSession {

        private final Controller controller;
        private final AbstractTucsonProtocol session;

        /**
         * 
         * @param c
         * @param s
         */
        ControllerSession(final Controller c, final AbstractTucsonProtocol s) {
            this.controller = c;
            this.session = s;
        }

        /**
         * 
         * @return the listener to incoming messages
         */
        public Controller getController() {
            return this.controller;
        }

        /**
         * 
         * @return the network protocol used by this ACC
         */
        public AbstractTucsonProtocol getSession() {
            return this.session;
        }

    }

    private static final int DEFAULT_PORT = 20504;
    private static final int TRIES = 3;

    /**
     * TuCSoN Agent Identifier
     */
    protected TucsonAgentId aid;
    /**
     * Active sessions toward different nodes
     */
    protected Map<String, ControllerSession> controllerSessions;
    /**
     * TuCSoN requests completion events (node replies events)
     */
    protected List<TucsonOpCompletionEvent> events;
    /**
     * TuCSoN Node Service ip address
     */
    protected String node;
    /**
     * Expired TuCSoN operations
     */
    protected List<Long> operationExpired;
    /**
     * Requested TuCSoN operations
     */
    protected Map<Long, TucsonOperation> operations;

    /**
     * TuCSoN Node Service listening port
     */
    protected int port;

    /**
     * Current ACC session description
     */
    protected ACCDescription profile;

    /**
     * Default constructor: exploits the default port (20504) in the "localhost"
     * TuCSoN Node Service
     * 
     * @param id
     *            TuCSoN Agent identifier
     * 
     * @throws TucsonInvalidAgentIdException
     *             if the String representation given is not valid TuCSoN agent
     *             identifier
     */
    public ACCProxyAgentSide(final String id)
            throws TucsonInvalidAgentIdException {
        this(id, "localhost", ACCProxyAgentSide.DEFAULT_PORT);
    }

    /**
     * Complete constructor: starts the named TuCSoN Agent on the specific
     * TuCSoN node listening on the specified port
     * 
     * @param id
     *            TuCSoN Agent identifier
     * @param n
     *            TuCSoN node ip address
     * @param p
     *            TuCSoN node listening port
     * 
     * @throws TucsonInvalidAgentIdException
     *             if the String representation given is not valid TuCSoN agent
     *             identifier
     */
    public ACCProxyAgentSide(final String id, final String n, final int p)
            throws TucsonInvalidAgentIdException {

        this.aid = new TucsonAgentId(id);
        this.node = n;
        this.port = p;

        this.profile = new ACCDescription();
        this.events = new LinkedList<TucsonOpCompletionEvent>();
        this.controllerSessions = new HashMap<String, ControllerSession>();
        this.operations = new HashMap<Long, TucsonOperation>();
        this.operationExpired = new ArrayList<Long>();

    }

    /**
     * Method to track expired operations, that is operations whose completion
     * has not been received before specified timeout expiration
     * 
     * @param id
     *            Unique Identifier of the expired operation
     */
    public void addOperationExpired(final long id) {
        this.operationExpired.add(id);
    }

    public synchronized void exit() throws TucsonOperationNotPossibleException {

        final Iterator<ControllerSession> it =
                this.controllerSessions.values().iterator();
        ControllerSession cs;
        AbstractTucsonProtocol info;
        Controller contr;
        TucsonOperation op;
        TucsonMsgRequest exit;

        while (it.hasNext()) {

            cs = it.next();
            info = cs.getSession();
            contr = cs.getController();
            contr.setStop();

            op =
                    new TucsonOperation(TucsonOperation.exitCode(),
                            (TupleTemplate) null, null, this);
            this.operations.put(op.getId(), op);

            exit =
                    new TucsonMsgRequest((int) op.getId(), op.getType(), null,
                            op.getLogicTupleArgument());
            try {
                info.sendMsgRequest(exit);
            } catch (final DialogException e) {
                e.printStackTrace();
            }

        }

    }

    public ITucsonOperation get(final Object tid, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.getCode(), tid, null,
                timeout);
    }

    public ITucsonOperation get(final Object tid,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.getCode(), tid,
                null, l);
    }

    public ITucsonOperation getS(final Object tid, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple spec = new LogicTuple("spec", new Var("S"));
        return this.doBlockingOperation(TucsonOperation.getSCode(), tid, spec,
                timeout);
    }

    public ITucsonOperation getS(final Object tid,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple spec = new LogicTuple("spec");
        return this.doNonBlockingOperation(TucsonOperation.getSCode(), tid,
                spec, l);
    }

    public ITucsonOperation in(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.inCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation in(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.inCode(), tid,
                tuple, l);
    }

    public ITucsonOperation inAll(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.inAllCode(), tid,
                tuple, timeout);
    }

    public ITucsonOperation inAll(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.inAllCode(), tid,
                tuple, l);
    }

    public ITucsonOperation inp(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.inpCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation inp(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.inpCode(), tid,
                tuple, l);
    }

    public ITucsonOperation inpS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doBlockingOperation(TucsonOperation.inpSCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation inpS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doNonBlockingOperation(TucsonOperation.inpSCode(), tid,
                tuple, l);
    }

    public ITucsonOperation inS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doBlockingOperation(TucsonOperation.inSCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation inS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doNonBlockingOperation(TucsonOperation.inSCode(), tid,
                tuple, l);
    }

    public ITucsonOperation no(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.noCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation no(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.noCode(), tid,
                tuple, l);
    }

    public ITucsonOperation noAll(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.noAllCode(), tid,
                tuple, timeout);
    }

    public ITucsonOperation noAll(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.noAllCode(), tid,
                tuple, l);
    }

    public ITucsonOperation nop(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.nopCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation nop(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.nopCode(), tid,
                tuple, l);
    }

    public ITucsonOperation nopS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doBlockingOperation(TucsonOperation.nopSCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation nopS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doNonBlockingOperation(TucsonOperation.nopSCode(), tid,
                tuple, l);
    }

    public ITucsonOperation noS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doBlockingOperation(TucsonOperation.noSCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation noS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doNonBlockingOperation(TucsonOperation.noSCode(), tid,
                tuple, l);
    }

    public ITucsonOperation out(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.outCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation out(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.outCode(), tid,
                tuple, l);
    }

    public ITucsonOperation outAll(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.outAllCode(), tid,
                tuple, timeout);
    }

    public ITucsonOperation outAll(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.outAllCode(), tid,
                tuple, l);
    }

    public ITucsonOperation outS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doBlockingOperation(TucsonOperation.outSCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation outS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doNonBlockingOperation(TucsonOperation.outSCode(), tid,
                tuple, l);
    }

    public ITucsonOperation rd(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.rdCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation rd(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.rdCode(), tid,
                tuple, l);
    }

    public ITucsonOperation rdAll(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.rdAllCode(), tid,
                tuple, timeout);
    }

    public ITucsonOperation rdAll(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.rdAllCode(), tid,
                tuple, l);
    }

    public ITucsonOperation rdp(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.rdpCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation rdp(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.rdpCode(), tid,
                tuple, l);
    }

    public ITucsonOperation rdpS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doBlockingOperation(TucsonOperation.rdpSCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation rdpS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doNonBlockingOperation(TucsonOperation.rdpSCode(), tid,
                tuple, l);
    }

    public ITucsonOperation rdS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doBlockingOperation(TucsonOperation.rdSCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation rdS(final Object tid, final LogicTuple event,
            final LogicTuple guards, final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple =
                new LogicTuple(Parser.parseSingleTerm("reaction(" + event + ","
                        + guards + "," + reactionBody + ")", new MyOpManager()));
        return this.doNonBlockingOperation(TucsonOperation.rdSCode(), tid,
                tuple, l);
    }

    public ITucsonOperation set(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.setCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation set(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.setCode(), tid,
                tuple, l);
    }

    public ITucsonOperation setS(final Object tid, final LogicTuple spec,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.setSCode(), tid, spec,
                timeout);
    }

    public ITucsonOperation setS(final Object tid, final String spec,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        if ("".equals(spec) || "''".equals(spec) || "'.'".equals(spec)) {
            throw new TucsonOperationNotPossibleException();
        }
        final LogicTuple specT = new LogicTuple("spec", new Value(spec));
        return this.doBlockingOperation(TucsonOperation.setSCode(), tid, specT,
                timeout);
    }

    public ITucsonOperation setS(final Object tid, final String spec,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple specT = new LogicTuple("spec", new Value(spec));
        return this.doNonBlockingOperation(TucsonOperation.setSCode(), tid,
                specT, l);
    }

    public ITucsonOperation spawn(final Object tid, final LogicTuple toSpawn,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.spawnCode(), tid,
                toSpawn, timeout);
    }

    public ITucsonOperation spawn(final Object tid, final LogicTuple toSpawn,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.spawnCode(), tid,
                toSpawn, l);
    }

    public ITucsonOperation uin(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.uinCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation uin(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.uinCode(), tid,
                tuple, l);
    }

    public ITucsonOperation uinp(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.uinpCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation uinp(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.uinpCode(), tid,
                tuple, l);
    }

    public ITucsonOperation uno(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.unoCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation uno(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.unoCode(), tid,
                tuple, l);
    }

    public ITucsonOperation unop(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.unopCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation unop(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.unopCode(), tid,
                tuple, l);
    }

    public ITucsonOperation urd(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.urdCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation urd(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.urdCode(), tid,
                tuple, l);
    }

    public ITucsonOperation urdp(final Object tid, final LogicTuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.doBlockingOperation(TucsonOperation.urdpCode(), tid, tuple,
                timeout);
    }

    public ITucsonOperation urdp(final Object tid, final LogicTuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.doNonBlockingOperation(TucsonOperation.urdpCode(), tid,
                tuple, l);
    }

    /**
     * Private method that takes in charge execution of all the Synchronous
     * primitives listed above. It simply forwards real execution to another
     * private method {@link alice.tucson.api doOperation doOp} (notice that in
     * truth there is no real execution at this point: we are just packing
     * primitives invocation into TuCSoN messages, then send them to the Node
     * side)
     * 
     * The difference w.r.t. the previous method
     * {@link alice.tucson.service.ACCProxyAgentSide#doNonBlockingOperation
     * nonBlocking} is that here we explicitly wait for completion a time
     * specified in the timeout input parameter.
     * 
     * @param type
     *            TuCSoN operation type (internal integer code)
     * @param tid
     *            Target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param t
     *            The Logic Tuple involved in the requested operation
     * @param ms
     *            Maximum waiting time tolerated by the callee TuCSoN Agent
     * 
     * @return An object representing the primitive invocation on the TuCSoN
     *         infrastructure which will store its result
     * 
     * @throws TucsonOperationNotPossibleException
     *             if the operation requested cannot be performed
     * @throws UnreachableNodeException
     *             if the target tuple centre cannot be reached over the network
     * @throws OperationTimeOutException
     *             if the timeout associated to the operation requested expires
     *             prior to operation completion
     * 
     * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
     */
    protected ITucsonOperation doBlockingOperation(final int type,
            final Object tid, final LogicTuple t, final Long ms)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {

        TucsonTupleCentreId tcid = null;
        if ("alice.tucson.api.TucsonTupleCentreId".equals(tid.getClass()
                .getName())) {
            tcid = (TucsonTupleCentreId) tid;
        } else if ("java.lang.String".equals(tid.getClass().getName())) {
            try {
                tcid = new TucsonTupleCentreId((String) tid);
            } catch (final TucsonInvalidTupleCentreIdException ex) {
                System.err.println("[ACCProxyAgentSide]: " + ex);
                return null;
            }
        } else {
            throw new TucsonOperationNotPossibleException();
        }

        ITucsonOperation op = null;
        try {
            op = this.doOperation(tcid, type, t, null);
        } catch (final TucsonOperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final UnreachableNodeException e) {
            throw new UnreachableNodeException();
        }
        try {
            if (ms == null) {
                op.waitForOperationCompletion();
            } else {
                op.waitForOperationCompletion(ms);
            }
        } catch (final OperationTimeOutException e) {
            throw new OperationTimeOutException();
        }
        return op;

    }

    /**
     * Private method that takes in charge execution of all the Asynchronous
     * primitives listed above. It simply forwards real execution to another
     * private method {@link alice.tucson.api doOperation doOp} (notice that in
     * truth there is no real execution at this point: we are just packing
     * primitives invocation into TuCSoN messages, then send them to the Node
     * side)
     * 
     * @param type
     *            TuCSoN operation type (internal integer code)
     * @param tid
     *            Target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param t
     *            The Logic Tuple involved in the requested operation
     * @param l
     *            The listener who should be notified upon operation completion
     * 
     * @return An object representing the primitive invocation on the TuCSoN
     *         infrastructure which will store its result
     * 
     * @throws TucsonOperationNotPossibleException
     *             if the operation requested cannot be performed
     * @throws UnreachableNodeException
     *             if the target tuple centre cannot be reached over the network
     * 
     * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
     * @see alice.tucson.api.TucsonOperationCompletionListener
     *      TucsonOperationCompletionListener
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     */
    protected ITucsonOperation doNonBlockingOperation(final int type,
            final Object tid, final LogicTuple t,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {

        TucsonTupleCentreId tcid = null;
        if ("alice.tucson.api.TucsonTupleCentreId".equals(tid.getClass()
                .getName())) {
            tcid = (TucsonTupleCentreId) tid;
        } else if ("java.lang.String".equals(tid.getClass().getName())) {
            try {
                tcid = new TucsonTupleCentreId((String) tid);
            } catch (final TucsonInvalidTupleCentreIdException ex) {
                System.err.println("[ACCProxyAgentSide]: " + ex);
                return null;
            }
        } else {
            throw new TucsonOperationNotPossibleException();
        }

        try {
            return this.doOperation(tcid, type, t, l);
        } catch (final TucsonOperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final UnreachableNodeException e) {
            throw new UnreachableNodeException();
        }

    }

    /**
     * This method is the real responsible of TuCSoN operations execution.
     * 
     * First, it takes the target tuplecentre and checks wether this proxy has
     * ever established a connection toward it: if it did, the already opened
     * connection is retrieved and used, otherwise a new connection is opened
     * and stored for later use
     * {@link alice.tucson.service.ACCProxyAgentSide#getSession getSession}.
     * 
     * Then, a Tucson Operation {@link alice.tucson.service.TucsonOperation op}
     * storing any useful information about the TuCSoN primitive invocation is
     * created and packed into a Tucson Message Request
     * {@link alice.tucson.network.TucsonMsgRequest} to be possibly sent over
     * the wire toward the target tuplecentre.
     * 
     * Notice that a listener is needed, who is the proxy itself, wichever was
     * the requested operation (inp, in, etc.) and despite its (a-)synchronous
     * behavior. This is because of the distributed very nature of TuCSoN: we
     * couldn't expect to block on a socket waiting for a reply. Instead,
     * requested operations should be dispatched toward the TuCSoN Node Service,
     * which in turn will take them in charge and notify the requestor upon
     * completion.
     * 
     * @param tcid
     *            Target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * @param type
     *            TuCSoN operation type (internal integer code)
     * @param t
     *            The Logic Tuple involved in the requested operation
     * @param l
     *            The listener who should be notified upon operation completion
     * 
     * @return An object representing the primitive invocation on the TuCSoN
     *         infrastructure which will store its result
     * 
     * @throws TucsonOperationNotPossibleException
     *             if the operation requested cannot be performed
     * @throws UnreachableNodeException
     *             if the target tuple centre cannot be reached over the network
     * 
     * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
     * @see alice.tucson.api.TucsonOperationCompletionListener
     *      TucsonOperationCompletionListener
     * @see alice.tucson.api.ITucsonOperation ITucsonOperation
     * @see alice.tucson.service.TucsonOperation TucsonOperation
     */
    protected synchronized ITucsonOperation doOperation(
            final TucsonTupleCentreId tcid, final int type, final LogicTuple t,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {

        int nTry = 0;
        boolean exception;

        do {

            nTry++;
            exception = false;

            AbstractTucsonProtocol session = null;
            try {
                session = this.getSession(tcid);
            } catch (final UnreachableNodeException ex2) {
                exception = true;
                throw new UnreachableNodeException();
            }

            TucsonOperation op = null;
            if ((type == TucsonOperation.outCode())
                    || (type == TucsonOperation.outSCode())
                    || (type == TucsonOperation.setSCode())
                    || (type == TucsonOperation.setCode())
                    || (type == TucsonOperation.outAllCode())
                    || (type == TucsonOperation.spawnCode())) {
                op = new TucsonOperation(type, (Tuple) t, l, this);
            } else {
                op = new TucsonOperation(type, t, l, this);
            }
            this.operations.put(op.getId(), op);
            final TucsonMsgRequest msg =
                    new TucsonMsgRequest(op.getId(), op.getType(),
                            tcid.toString(), op.getLogicTupleArgument());
            this.log("requesting op " + msg.getType() + ", " + msg.getTuple()
                    + ", " + msg.getTid());

            try {
                session.sendMsgRequest(msg);
            } catch (final DialogException ex) {
                exception = true;
                System.err.println("[ACCProxyAgentSide]: " + ex);
            }

            if (!exception) {
                return op;
            }

        } while (nTry < ACCProxyAgentSide.TRIES);

        throw new UnreachableNodeException();

    }

    /**
     * This method is responsible to setup, store and retrieve connections
     * toward all the tuplecentres ever contacted by the TuCSoN Agent behind
     * this proxy.
     * 
     * If a connection toward the given target tuplecentre already exists, it is
     * retrieved and used. If not, the new connection is setup then stored for
     * later use.
     * 
     * It is worth noting a couple of things. Why don't we setup connections
     * once and for all as soon as the TuCSoN Agent is booted? The reason is
     * that new tuplecentres can be created at run-time as TuCSoN Agents please,
     * thus for every TuCSoN Operation request we should check wether a new
     * tuplecentre has to be created and booted. If a new tuplecentre has to be
     * booted the correspondant proxy node side is dinamically triggered and
     * booted {@link alice.tucson.service.ACCProxyNodeSide nodeProxy}
     * 
     * @param tid
     *            Target TuCSoN tuplecentre id
     *            {@link alice.tucson.api.TucsonTupleCentreId tid}
     * 
     * @return The open session toward the given target tuplecentre
     * 
     * @throws UnreachableNodeException
     *             if the target tuple centre cannot be reached over the network
     * 
     * @see alice.tucson.network.AbstractTucsonProtocol TucsonProtocol
     * @see alice.tucson.service.ACCProxyNodeSide ACCProxyNodeSide
     */
    protected AbstractTucsonProtocol getSession(final TucsonTupleCentreId tid)
            throws UnreachableNodeException {

        final String opNode = alice.util.Tools.removeApices(tid.getNode());
        final int p = tid.getPort();
        ControllerSession tc = this.controllerSessions.get(opNode + ":" + p);
        if (tc != null) {
            return tc.getSession();
        }
//        if (InetAddress.getLoopbackAddress().getHostName().equals(opNode)) {
        if ("localhost".equals(opNode)) {
            tc =
//                    this.controllerSessions.get(InetAddress
//                            .getLoopbackAddress().getHostAddress()
//                            .concat(String.valueOf(p)));
                    this.controllerSessions.get("127.0.0.1:".concat(String.valueOf(p)));
        }
//        if (InetAddress.getLoopbackAddress().getHostAddress().equals(opNode)) {
        if ("127.0.0.1".equals(opNode)) {
            tc =
//                    this.controllerSessions.get(InetAddress
//                            .getLoopbackAddress().getHostName()
//                            .concat(String.valueOf(p)));
                    this.controllerSessions.get("localhost:".concat(String.valueOf(p)));
        }
        if (tc != null) {
            return tc.getSession();
        }

        this.profile.setProperty("agent-identity", this.aid.toString());
        this.profile.setProperty("agent-role", "user");

        AbstractTucsonProtocol dialog = null;
        boolean isEnterReqAcpt = false;
        try {
            dialog = TPFactory.getDialogAgentSide(tid);
            dialog.sendEnterRequest(this.profile);
            dialog.receiveEnterRequestAnswer();
            if (dialog.isEnterRequestAccepted()) {
                isEnterReqAcpt = true;
            }
        } catch (final IOException e) {
            throw new UnreachableNodeException();
        } catch (final DialogException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isEnterReqAcpt) {
            final Controller contr = new Controller(dialog);
            final ControllerSession cs = new ControllerSession(contr, dialog);
            this.controllerSessions.put(opNode + ":" + p, cs);
            contr.start();
            return dialog;
        }

        return null;

    }

    /**
     * Method internally used to log proxy activity (could be used for debug)
     * 
     * @param msg
     *            String to display on the standard output
     */
    protected void log(final String msg) {
        System.out.println("[ACCProxyAgentSide]: " + msg);
    }

    /**
     * Method to add a TuCSoN Operation Completion Event
     * {@link alice.tucson.service.TucsonOpCompletionEvent event} to the
     * internal queue of pending completion event to process
     * 
     * @param ev
     *            Completion Event to be added to pending queue
     * 
     * @see alice.tucson.service.TucsonOpCompletionEvent TucsonOpCompletionEvent
     */
    protected void postEvent(final TucsonOpCompletionEvent ev) {
        // FIXME Check correctness
        synchronized (this.events) {
            this.events.add(this.events.size(), ev);
        }
    }

}
