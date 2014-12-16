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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import alice.logictuple.LogicTuple;
import alice.logictuple.LogicTupleOpManager;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.AbstractTucsonProtocol;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.exceptions.DialogSendException;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Parser;

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
    private static final int DEFAULT_PORT = 20504;
    /**
     * TuCSoN Agent Identifier
     */
    protected TucsonAgentId aid;
    /**
     * 
     */
    protected OperationHandler executor;
    /**
     * TuCSoN Node Service ip address
     */
    protected String node;
    /**
     * TuCSoN Node Service listening port
     */
    protected int port;

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
        // Class used to perform tucson operations
        this.executor = new OperationHandler();
    }

    @Override
    public synchronized void exit() {
        final Iterator<OperationHandler.ControllerSession> it = this.executor
                .getControllerSessions().values().iterator();
        OperationHandler.ControllerSession cs;
        OperationHandler.Controller contr;
        AbstractTucsonProtocol info;
        TucsonOperation op;
        TucsonMsgRequest exit;
        while (it.hasNext()) {
            cs = it.next();
            info = cs.getSession();
            contr = cs.getController();
            contr.setStop();
            op = new TucsonOperation(TucsonOperation.exitCode(),
                    (TupleTemplate) null, null, this.executor /* this */);
            this.executor.addOperation(op.getId(), op);
            exit = new TucsonMsgRequest((int) op.getId(), op.getType(), null,
                    op.getLogicTupleArgument());
            try {
                info.sendMsgRequest(exit);
            } catch (final DialogSendException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ITucsonOperation get(final TupleCentreId tid, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.getCode(), tid, null, timeout);
    }

    @Override
    public ITucsonOperation get(final TupleCentreId tid,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.getCode(), tid, null, l);
    }

    // edited by sangio
    // restituzione della lista delle operazioni asincrone completate e non
    // ancora gestite("consumate) dall'agente
    @Override
    public List<TucsonOpCompletionEvent> getCompletionEventsList() {
        // TODO Auto-generated method stub
        return this.executor.events;
    }

    @Override
    public Map<Long, TucsonOperation> getPendingOperationsMap() {
        // TODO Auto-generated method stub
        return this.executor.operations;
    }

    @Override
    public ITucsonOperation getS(final TupleCentreId tid, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        LogicTuple spec = null;
        try {
            spec = new LogicTuple("spec", new Var("S"));
        } catch (final InvalidVarNameException e) {
            // Cannot happen, the var name it's specified here
            e.printStackTrace();
        }
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.getSCode(), tid, spec, timeout);
    }

    @Override
    public ITucsonOperation getS(final TupleCentreId tid,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple spec = new LogicTuple("spec");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.getSCode(), tid, spec, l);
    }

    @Override
    public ITucsonOperation in(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.inCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation in(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.inCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation inAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.inAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation inAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.inAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation inp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.inpCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation inp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.inpCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation inpS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.inpSCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation inpS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.inpSCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation inS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.inSCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation inS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.inSCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation no(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.noCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation no(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.noCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation noAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.noAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation noAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.noAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation nop(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.nopCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation nop(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.nopCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation nopS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.nopSCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation nopS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.nopSCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation noS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.noSCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation noS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.noSCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation out(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.outCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation out(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.outCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation outAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.outAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation outAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.outAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation outS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.outSCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation outS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.outSCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation rd(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.rdCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rd(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.rdCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation rdAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.rdAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rdAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.rdAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation rdp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.rdpCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rdp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.rdpCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation rdpS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.rdpSCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rdpS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.rdpSCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation rdS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.rdSCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rdS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tuple = new LogicTuple(Parser.parseSingleTerm(
                "reaction(" + event + "," + guards + "," + reactionBody + ")",
                new LogicTupleOpManager()));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.rdSCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation set(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.setCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation set(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.setCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation setS(final TupleCentreId tid,
            final LogicTuple spec, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.setSCode(), tid, spec, timeout);
    }

    @Override
    public ITucsonOperation setS(final TupleCentreId tid,
            final LogicTuple spec, final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.setSCode(), tid, spec, l);
    }

    @Override
    public ITucsonOperation setS(final TupleCentreId tid, final String spec,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        if ("".equals(spec) || "''".equals(spec) || "'.'".equals(spec)) {
            throw new TucsonOperationNotPossibleException();
        }
        final LogicTuple specT = new LogicTuple("spec", new Value(spec));
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.setSCode(), tid, specT, timeout);
    }

    @Override
    public ITucsonOperation setS(final TupleCentreId tid, final String spec,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple specT = new LogicTuple("spec", new Value(spec));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.setSCode(), tid, specT, l);
    }

    @Override
    public ITucsonOperation spawn(final TupleCentreId tid, final Tuple toSpawn,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.spawnCode(), tid, toSpawn, timeout);
    }

    @Override
    public ITucsonOperation spawn(final TupleCentreId tid, final Tuple toSpawn,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.spawnCode(), tid, toSpawn, l);
    }

    @Override
    public ITucsonOperation uin(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.uinCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation uin(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.uinCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation uinp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.uinpCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation uinp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.uinpCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation uno(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.unoCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation uno(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.unoCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation unop(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.unopCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation unop(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.unopCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation urd(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.urdCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation urd(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.urdCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation urdp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.urdpCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation urdp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.urdpCode(), tid, tuple, l);
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
}
