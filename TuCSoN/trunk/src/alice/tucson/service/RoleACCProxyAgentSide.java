package alice.tucson.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import alice.logictuple.LogicTuple;
import alice.logictuple.LogicTupleOpManager;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Role;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Parser;

public class RoleACCProxyAgentSide extends ACCProxyAgentSide {

    private List<String> permissions;
    private Role role;

    public RoleACCProxyAgentSide(final Object aid, final Role role,
            final UUID agentUUID) throws TucsonInvalidAgentIdException {
        this(aid, "localhost", 20504, role, agentUUID);
    }

    public RoleACCProxyAgentSide(final Object aid, final String n, final int p,
            final Role role, final UUID agentUUID)
                    throws TucsonInvalidAgentIdException {
        super(aid, n, p, agentUUID);
        // permissions = new ArrayList<String>();
        this.setRole(role);
    }

    @Override
    public ITucsonOperation get(final TupleCentreId tid, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("get");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.getCode(), tid, null, timeout);
    }

    @Override
    public ITucsonOperation get(final TupleCentreId tid,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("get");
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
        this.checkPermission("getS");
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
        this.checkPermission("getS");
        final LogicTuple spec = new LogicTuple("spec");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.getSCode(), tid, spec, l);
    }

    @Override
    public UUID getUUID() {
        return this.executor.agentUUID;
    }

    @Override
    public ITucsonOperation in(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {

        this.checkPermission("in");
        return super.in(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation in(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {

        this.checkPermission("in");
        return super.in(tid, tuple, l);
    }

    @Override
    public ITucsonOperation inAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("inAll");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.inAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation inAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("inAll");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.inAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation inp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {

        this.checkPermission("inp");
        return super.inp(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation inp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {

        this.checkPermission("inp");
        return super.inp(tid, tuple, l);
    }

    @Override
    public ITucsonOperation inpS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("inpS");
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
        this.checkPermission("inpS");
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
        this.checkPermission("inS");
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
        this.checkPermission("inS");
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
        this.checkPermission("no");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.noCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation no(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("no");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.noCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation noAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("noAll");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.noAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation noAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("noAll");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.noAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation nop(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("nop");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.nopCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation nop(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("nop");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.nopCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation nopS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("nopS");
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
        this.checkPermission("nopS");
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
        this.checkPermission("noS");
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
        this.checkPermission("noS");
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

        this.checkPermission("out");
        return super.out(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation out(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("out");
        return super.out(tid, tuple, l);
    }

    @Override
    public ITucsonOperation outAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("outAll");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.outAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation outAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("outAll");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.outAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation outS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("outS");
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
        this.checkPermission("outS");
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

        this.checkPermission("rd");
        return super.rd(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rd(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {

        this.checkPermission("rd");
        return super.rd(tid, tuple, l);
    }

    @Override
    public ITucsonOperation rdAll(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("rdAll");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.rdAllCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rdAll(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("rdAll");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.rdAllCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation rdp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {

        this.checkPermission("rdp");
        return super.rdp(tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation rdp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {

        this.checkPermission("rdp");
        return super.rdp(tid, tuple, l);
    }

    @Override
    public ITucsonOperation rdpS(final TupleCentreId tid,
            final LogicTuple event, final LogicTuple guards,
            final LogicTuple reactionBody, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("rdpS");
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
        this.checkPermission("rdpS");
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
        this.checkPermission("rdS");
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
        this.checkPermission("rdS");
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
        this.checkPermission("set");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.setCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation set(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("set");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.setCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation setS(final TupleCentreId tid,
            final LogicTuple spec, final Long timeout)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("setS");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.setSCode(), tid, spec, timeout);
    }

    @Override
    public ITucsonOperation setS(final TupleCentreId tid,
            final LogicTuple spec, final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("setS");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.setSCode(), tid, spec, l);
    }

    @Override
    public ITucsonOperation setS(final TupleCentreId tid, final String spec,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("setS");
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
        this.checkPermission("setS");
        final LogicTuple specT = new LogicTuple("spec", new Value(spec));
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.setSCode(), tid, specT, l);
    }

    @Override
    public ITucsonOperation spawn(final TupleCentreId tid, final Tuple toSpawn,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("spawn");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.spawnCode(), tid, toSpawn, timeout);
    }

    @Override
    public ITucsonOperation spawn(final TupleCentreId tid, final Tuple toSpawn,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("spawn");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.spawnCode(), tid, toSpawn, l);
    }

    @Override
    public ITucsonOperation uin(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("uin");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.uinCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation uin(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("uin");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.uinCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation uinp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("uinp");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.uinpCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation uinp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("uinp");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.uinpCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation uno(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("uno");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.unoCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation uno(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("uno");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.unoCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation unop(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("unop");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.unopCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation unop(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("unop");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.unopCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation urd(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("urd");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.urdCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation urd(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("urd");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.urdCode(), tid, tuple, l);
    }

    @Override
    public ITucsonOperation urdp(final TupleCentreId tid, final Tuple tuple,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.checkPermission("urdp");
        return this.executor.doBlockingOperation(this.aid,
                TucsonOperation.urdpCode(), tid, tuple, timeout);
    }

    @Override
    public ITucsonOperation urdp(final TupleCentreId tid, final Tuple tuple,
            final TucsonOperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        this.checkPermission("urdp");
        return this.executor.doNonBlockingOperation(this.aid,
                TucsonOperation.urdpCode(), tid, tuple, l);
    }

    private void checkPermission(final String perm)
            throws TucsonOperationNotPossibleException {
        if (this.permissions.isEmpty() || !this.permissions.contains(perm)) {
            throw new TucsonOperationNotPossibleException();
        }
    }

    private void setPermissions() {
        this.permissions = new ArrayList<String>();
        final List<Permission> perms = this.role.getPolicy().getPermissions();
        for (final Permission perm : perms) {
            this.permissions.add(perm.getPermissionName());
        }
    }

    private void setRole(final Role role) {
        this.role = role;
        this.setPermissions();
    }
}
