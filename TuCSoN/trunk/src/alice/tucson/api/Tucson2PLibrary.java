/*
 * tuProlog - Copyright (C) 2001-2002 aliCE team at deis.unibo.it This library
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.api;

import java.util.Iterator;
import java.util.List;
import alice.logictuple.LogicTuple;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Library;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

/**
 * TuCSoN library for tuProlog agents. By loading this library tuProlog agents
 * are enabled to interact with a TuCSoN system. All the TuCSoN primitives
 * available to Java agents and human agents (through the CLI tool) are thus
 * made available to tuProlog agents too.
 *
 * @see alice.tuprolog.Agent Agent
 *
 * @author ste (mailto: s.mariani@unibo.it)
 */
public class Tucson2PLibrary extends Library {

    private static final long serialVersionUID = 6716779172091533171L;

    /**
     * Utility to convert a list of tuple into a tuple list of tuples
     *
     * @param list
     *            the list of tuples to convert
     *
     * @return the tuple list of tuples result of the conversion
     */
    private static Term list2tuple(final List<LogicTuple> list) {
        final Term[] termArray = new Term[list.size()];
        final Iterator<LogicTuple> it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            termArray[i] = it.next().toTerm();
            i++;
        }
        return new Struct(termArray);
    }

    private EnhancedACC context;
    private String aid;
    private String netid;
    private int port;

    /**
     * To be enabled to interact with any TuCSoN system, an ACC must be acquired
     * first.
     *
     * @param id
     *            the TucsonAgentId of the tuProlog agent willing to interact
     *            with TuCSoN
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidAgentIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     *
     * @see alice.tucson.api.EnhancedACC EnhancedACC
     * @see alice.tucson.api.TucsonAgentId TucsonAgentId
     */
    public boolean acquire_acc_3(final Term id, final Term nodeHost, final Term portTerm)
            throws TucsonInvalidAgentIdException {
        TucsonAgentId agentId;
        if (this.context != null) {
            try {
                this.context.exit();
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
                return false;
            }
        }
        agentId = new TucsonAgentId(id.getTerm().toString());
        
        if (!nodeHost.isGround() || !portTerm.isGround()) {
        	return false;
        }
        
        final String netId = nodeHost.getTerm().toString();
        final String portString = portTerm.getTerm().toString();
        final int port;
        
		try {
			port = Integer.parseInt(portString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
        this.context = TucsonMetaACC.getContext(agentId, netId, port);
        try {
			this.context.enterACC();
		} catch (UnreachableNodeException | TucsonOperationNotPossibleException
				| TucsonInvalidTupleCentreIdException e) {
			e.printStackTrace();
			return false;
		}
        
        this.aid = agentId.getAgentName();
        this.netid = netId;
        if (this.netid.charAt(0) == '\'' && this.netid.charAt(this.netid.length() - 1) == '\'')
        	this.netid = this.netid.substring(1,  this.netid.length() - 1);
        this.port = port;
        
        return true;
    }

    /**
     * <code>get</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to store the result
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean get_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.get(tid, (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0,
                    Tucson2PLibrary.list2tuple(op.getLogicTupleListResult()));
        }
        return op.isResultSuccess();
    }

    /**
     * <code>get_s</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to store the specification result
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean get_s_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.getS(tid, (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0,
                    Tucson2PLibrary.list2tuple(op.getLogicTupleListResult()));
        }
        return op.isResultSuccess();
    }

    /**
     * Gets the Prolog theory defining all operators and predicates available.
     * If only a tuple is specified as argument of a TuCSoN primitive, the
     * default tuplecentre is targeted, otherwise the tuProlog agent must
     * specify the full name of the target tuplecentre.
     *
     * @return the String representation of the tuProlog theory usable by
     *         tuProlog agents
     *
     * @see alice.tuprolog.Theory Theory
     * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
     */
    @Override
    public String getTheory() {
        return ":- op(551, xfx, '?'). \n"
                + ":- op(550, xfx, '@'). \n"
                + ":- op(549, xfx, ':'). \n"
                + ":- op(548, xfx, '.'). \n"
                + "acquire_acc(Name) :- acquire_acc(Name, localhost, 20504). \n"
                + "spawn(T) :- spawn(T, default@localhost:20504). \n"
                + "out(T) :- acc(_, N, P), out(T, default@N:P). \n"
                + "in(T) :- acc(_, N, P), in(T, default@N:P). \n"
                + "inp(T) :- acc(_, N, P), inp(T, default@N:P). \n"
                + "rd(T) :- acc(_, N, P), rd(T, default@N:P). \n"
                + "rdp(T) :- acc(_, N, P), rdp(T, default@N:P). \n"
                + "no(T) :- acc(_, N, P), no(T, default@N:P). \n"
                + "nop(T) :- acc(_, N, P), nop(T, default@N:P). \n"
                + "get(T) :- acc(_, N, P), get(T, default@N:P). \n"
                + "set(T) :- acc(_, N, P), set(T, default@N:P). \n"
                + "uin(T) :- acc(_, N, P), uin(T, default@N:P). \n"
                + "uinp(T) :- acc(_, N, P), uinp(T, default@N:P). \n"
                + "urd(T) :- acc(_, N, P), urd(T, default@N:P). \n"
                + "urdp(T) :- acc(_, N, P), urdp(T, default@N:P). \n"
                + "uno(T) :- acc(_, N, P), uno(T, default@N:P). \n"
                + "unop(T) :- acc(_, N, P), unop(T, default@N:P). \n"
                + "out_all(L) :- acc(_, N, P), out_all(L, default@N:P). \n"
                + "in_all(T,L) :- acc(_, N, P), in_all(T, L, default@N:P). \n"
                + "rd_all(T,L) :- acc(_, N, P), rd_all(T, L, default@N:P). \n"
                + "no_all(T,L) :- acc(_, N, P), no_all(T, L, default@N:P). \n"
                + "out_s(E,G,R) :- acc(_, N, P), out_s(E,G,R, default@N:P). \n"
                + "in_s(E,G,R) :- acc(_, N, P), in_s(E,G,R, default@N:P). \n"
                + "inp_s(E,G,R) :- acc(_, N, P), inp_s(E,G,R, default@N:P). \n"
                + "rd_s(E,G,R) :- acc(_, N, P), rd_s(E,G,R, default@N:P). \n"
                + "rdp_s(E,G,R) :- acc(_, N, P), rdp_s(E,G,R, default@N:P). \n"
                + "no_s(E,G,R) :- acc(_, N, P), no_s(E,G,R, default@N:P). \n"
                + "nop_s(E,G,R) :- acc(_, N, P), nop_s(E,G,R, default@N:P). \n"
                + "get_s(L) :- acc(_, N, P), get_s(L, default@N:P). \n"
                + "set_s(L) :- acc(_, N, P), set_s(L, default@N:P). \n"
                + "TC@Netid:Port ? spawn(T) :- !, spawn(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? out(T) :- !, out(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? in(T) :- !, in(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? inp(T) :- !, inp(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? rd(T) :- !, rd(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? rdp(T) :- !, rdp(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? no(T) :- !, no(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? nop(T) :- !, nop(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? get(L) :- !, get(L, TC@Netid:Port). \n"
                + "TC@Netid:Port ? set(L) :- !, set(L, TC@Netid:Port). \n"
                + "TC@Netid:Port ? uin(T) :- uin(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? uinp(T) :- uinp(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? urd(T) :- urd(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? urdp(T) :- urdp(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? uno(T) :- uno(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? unop(T) :- unop(T, TC@Netid:Port). \n"
                + "TC@Netid:Port ? out_all(L) :- out_all(L, TC@Netid:Port). \n"
                + "TC@Netid:Port ? in_all(T,L) :- in_all(T, L, TC@Netid:Port). \n"
                + "TC@Netid:Port ? rd_all(T,L) :- rd_all(T, L, TC@Netid:Port). \n"
                + "TC@Netid:Port ? no_all(T,L) :- no_all(T, L, TC@Netid:Port). \n"
                + "TC@Netid:Port ? out_s(E,G,R) :- !, out_s(E,G,R, TC@Netid:Port). \n"
                + "TC@Netid:Port ? in_s(E,G,R) :- !, in_s(E,G,R, TC@Netid:Port). \n"
                + "TC@Netid:Port ? inp_s(E,G,R) :- !, inp_s(E,G,R, TC@Netid:Port). \n"
                + "TC@Netid:Port ? rd_s(E,G,R) :- !, rd_s(E,G,R, TC@Netid:Port). \n"
                + "TC@Netid:Port ? rdp_s(E,G,R) :- !, rdp_s(E,G,R, TC@Netid:Port). \n"
                + "TC@Netid:Port ? no_s(E,G,R) :- !, no_s(E,G,R, TC@Netid:Port). \n"
                + "TC@Netid:Port ? nop_s(E,G,R) :- !, nop_s(E,G,R, TC@Netid:Port). \n"
                + "TC@Netid:Port ? get_s(L) :- !, get_s(L, TC@Netid:Port). \n"
                + "TC@Netid:Port ? set_s(L) :- !, set_s(L, TC@Netid:Port). \n";
    }

    /**
     * <code>in</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to retrieve
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean in_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.in(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>in_all</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to retrieve
     * @param arg1
     *            the tuple to store the result
     * @param arg2
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean in_all_3(final Term arg0, final Term arg1, final Term arg2)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg2.getTerm().toString());
        ITucsonOperation op;
        op = this.context.inAll(tid, new LogicTuple(arg0.getTerm()),
                (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg1,
                    Tucson2PLibrary.list2tuple(op.getLogicTupleListResult()));
        }
        return op.isResultSuccess();
    }

    /**
     * <code>in_s</code> TuCSoN primitive.
     *
     * @param event
     *            the template for the TuCSoN primitive to react to
     * @param guards
     *            the template for the guard predicates to be checked for
     *            satisfaction so to actually trigger the body of the ReSpecT
     *            reaction
     * @param reactionBody
     *            the template for the computation to be done in response to the
     *            <code>event</code>
     * @param arg3
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean in_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        ITucsonOperation op;
        op = this.context.inS(tid, new LogicTuple(event.getTerm()),
                new LogicTuple(guards.getTerm()),
                new LogicTuple(reactionBody.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(event, op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>inp</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to retrieve
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean inp_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.inp(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>inp_s</code> TuCSoN primitive.
     *
     * @param event
     *            the template for the TuCSoN primitive to react to
     * @param guards
     *            the template for the guard predicates to be checked for
     *            satisfaction so to actually trigger the body of the ReSpecT
     *            reaction
     * @param reactionBody
     *            the template for the computation to be done in response to the
     *            <code>event</code>
     * @param arg3
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean inp_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        ITucsonOperation op;
        op = this.context.inpS(tid, new LogicTuple(event.getTerm()),
                new LogicTuple(guards.getTerm()),
                new LogicTuple(reactionBody.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(event, op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>no</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to check for absence
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean no_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.no(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (!op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>no_all</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to check for absence
     * @param arg1
     *            the tuple to store the result
     * @param arg2
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean no_all_3(final Term arg0, final Term arg1, final Term arg2)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg2.getTerm().toString());
        ITucsonOperation op;
        op = this.context.noAll(tid, new LogicTuple(arg0.getTerm()),
                (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg1,
                    Tucson2PLibrary.list2tuple(op.getLogicTupleListResult()));
        }
        return op.isResultSuccess();
    }

    /**
     * <code>no_s</code> TuCSoN primitive.
     *
     * @param event
     *            the template for the TuCSoN primitive to react to
     * @param guards
     *            the template for the guard predicates to be checked for
     *            satisfaction so to actually trigger the body of the ReSpecT
     *            reaction
     * @param reactionBody
     *            the template for the computation to be done in response to the
     *            <code>event</code>
     * @param arg3
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean no_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        ITucsonOperation op;
        op = this.context.noS(tid, new LogicTuple(event.getTerm()),
                new LogicTuple(guards.getTerm()),
                new LogicTuple(reactionBody.getTerm()), (Long) null);
        if (!op.isResultSuccess()) {
            this.unify(event, op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>nop</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to check for absence
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean nop_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.nop(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (!op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>nop_s</code> TuCSoN primitive.
     *
     * @param event
     *            the template for the TuCSoN primitive to react to
     * @param guards
     *            the template for the guard predicates to be checked for
     *            satisfaction so to actually trigger the body of the ReSpecT
     *            reaction
     * @param reactionBody
     *            the template for the computation to be done in response to the
     *            <code>event</code>
     * @param arg3
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean nop_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        ITucsonOperation op;
        op = this.context.nopS(tid, new LogicTuple(event.getTerm()),
                new LogicTuple(guards.getTerm()),
                new LogicTuple(reactionBody.getTerm()), (Long) null);
        if (!op.isResultSuccess()) {
            this.unify(event, op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>out</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to insert
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean out_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.out(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        return op.isResultSuccess();
    }

    /**
     * <code>out_all</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple list of tuples to insert
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean out_all_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.outAll(tid, new LogicTuple(arg0.getTerm()),
                (Long) null);
        return op.isResultSuccess();
    }

    /**
     * <code>out_s</code> TuCSoN primitive.
     *
     * @param event
     *            the TuCSoN primitive to react to
     * @param guards
     *            the guard predicates to be checked for satisfaction so to
     *            actually trigger the body of the ReSpecT reaction
     * @param reactionBody
     *            the computation to be done in response to the
     *            <code>event</code>
     * @param arg3
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean out_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        ITucsonOperation op;
        op = this.context.outS(tid, new LogicTuple(event.getTerm()),
                new LogicTuple(guards.getTerm()),
                new LogicTuple(reactionBody.getTerm()), (Long) null);
        return op.isResultSuccess();
    }

    /**
     * <code>rd</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to read (w/o removing)
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean rd_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.rd(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>rd_all</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to read (w/o removing)
     * @param arg1
     *            the tuple to store the result
     * @param arg2
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean rd_all_3(final Term arg0, final Term arg1, final Term arg2)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg2.getTerm().toString());
        ITucsonOperation op;
        op = this.context.rdAll(tid, new LogicTuple(arg0.getTerm()),
                (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg1,
                    Tucson2PLibrary.list2tuple(op.getLogicTupleListResult()));
        }
        return op.isResultSuccess();
    }

    /**
     * <code>rd_s</code> TuCSoN primitive.
     *
     * @param event
     *            the template for the TuCSoN primitive to react to
     * @param guards
     *            the template for the guard predicates to be checked for
     *            satisfaction so to actually trigger the body of the ReSpecT
     *            reaction
     * @param reactionBody
     *            the template for the computation to be done in response to the
     *            <code>event</code>
     * @param arg3
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean rd_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        ITucsonOperation op;
        op = this.context.rdS(tid, new LogicTuple(event.getTerm()),
                new LogicTuple(guards.getTerm()),
                new LogicTuple(reactionBody.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(event, op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>rdp</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to read (w/o removing)
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean rdp_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.rdp(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>rdp_s</code> TuCSoN primitive.
     *
     * @param event
     *            the template for the TuCSoN primitive to react to
     * @param guards
     *            the template for the guard predicates to be checked for
     *            satisfaction so to actually trigger the body of the ReSpecT
     *            reaction
     * @param reactionBody
     *            the template for the computation to be done in response to the
     *            <code>event</code>
     * @param arg3
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean rdp_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        ITucsonOperation op;
        op = this.context.rdpS(tid, new LogicTuple(event.getTerm()),
                new LogicTuple(guards.getTerm()),
                new LogicTuple(reactionBody.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(event, op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * When leaving the TuCSoN system, any agent is kindly requested to release
     * its ACC.
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    public boolean release_acc_0() throws TucsonOperationNotPossibleException {
        this.context.exit();
        this.context = null;
        this.aid = null;
        this.netid = null;
        this.port = 0;
        return true;
    }
    
    public boolean acc_3(Term aid, Term netid, Term port) throws TucsonOperationNotPossibleException {

		if (this.aid != null) {
			this.unify(aid, new Struct(this.aid));
		} else {
			return false;
		}
		
		if (this.netid != null) {
			this.unify(netid, new Struct(this.netid));
		} else {
			return false;
		}
		
		if (this.port > 0) {
			this.unify(port, new alice.tuprolog.Int(this.port));
		} else {
			return false;
		}
    	
        return true;
    }


    /**
     * <code>set</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple list of tuples to overwrite the space
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean set_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.set(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        return op.isResultSuccess();
    }

    /**
     * <code>set_s</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple list of ReSpecT specification tuples to overwrite
     *            the specification space
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean set_s_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context
                .setS(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        return op.isResultSuccess();
    }

    /**
     * <code>spawn</code> TuCSoN primitive.
     *
     * @param arg0
     *            the activity to spawn
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean spawn_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.spawn(tid, new LogicTuple(arg0.getTerm()),
                (Long) null);
        return op.isResultSuccess();
    }

    /**
     * <code>uin</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to probabilistically retrieve
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean uin_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.uin(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>uinp</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to probabilistically retrieve
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean uinp_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context
                .uinp(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>uno</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to probabilistically check for absence
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean uno_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.uno(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (!op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>unop</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to probabilistically check for absence
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean unop_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context
                .unop(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (!op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>urd</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to probabilistically read (w/o removing)
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean urd_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context.urd(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

    /**
     * <code>urdp</code> TuCSoN primitive.
     *
     * @param arg0
     *            the tuple to probabilistically read (w/o removing)
     * @param arg1
     *            the tuplecentre target
     *
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     *
     * @throws TucsonInvalidTupleCentreIdException
     *             if the given Term does not represent a valid TuCSoN
     *             identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     * @throws UnreachableNodeException
     *             if the target tuple centre is not reachable over the network
     * @throws OperationTimeOutException
     *             if the operation timeout expired prior to operation
     *             completion
     *
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean urdp_2(final Term arg0, final Term arg1)
            throws TucsonInvalidTupleCentreIdException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        ITucsonOperation op;
        op = this.context
                .urdp(tid, new LogicTuple(arg0.getTerm()), (Long) null);
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }
}
