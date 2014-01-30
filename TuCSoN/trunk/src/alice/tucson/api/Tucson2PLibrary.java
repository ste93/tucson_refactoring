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

    /**
     * When leaving the TuCSoN system, any agent is kindly requested to release
     * its ACC.
     * 
     * @return <code>true</code> if the operation succeed, <code>false</code>
     *         otherwise
     */
    public boolean release_acc_0() {
        try {
            this.context.exit();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean get_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op = this.context.get(tid, (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
        if (op.isResultSuccess()) {
            this.unify(arg0,
                    Tucson2PLibrary.list2tuple(op.getLogicTupleListResult()));
        }
        return op.isResultSuccess();
    }

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
     * 
     * @see alice.tucson.api.EnhancedACC EnhancedACC
     * @see alice.tucson.api.TucsonAgentId TucsonAgentId
     */
    public boolean acquire_acc_1(final Term id) {
        TucsonAgentId agentId;
        if (this.context != null) {
            try {
                this.context.exit();
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            agentId = new TucsonAgentId(id.getTerm().toString());
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
            return false;
        }
        this.context = TucsonMetaACC.getContext(agentId);
        return true;
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
     * 
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean get_s_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op = this.context.getS(tid, (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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

                + "spawn(T) :- spawn(T, default@localhost:20504). \n"
                + "out(T) :- out(T, default@localhost:20504). \n"
                + "in(T) :- in(T, default@localhost:20504). \n"
                + "inp(T) :- inp(T, default@localhost:20504). \n"
                + "rd(T) :- rd(T, default@localhost:20504). \n"
                + "rdp(T) :- rdp(T, default@localhost:20504). \n"
                + "no(T) :- no(T, default@localhost:20504). \n"
                + "nop(T) :- nop(T, default@localhost:20504). \n"
                + "get(T) :- get(T, default@localhost:20504). \n"
                + "set(T) :- set(T, default@localhost:20504). \n"

                + "uin(T) :- uin(T, default@localhost:20504). \n"
                + "uinp(T) :- uinp(T, default@localhost:20504). \n"
                + "urd(T) :- urd(T, default@localhost:20504). \n"
                + "urdp(T) :- urdp(T, default@localhost:20504). \n"
                + "uno(T) :- uno(T, default@localhost:20504). \n"
                + "unop(T) :- unop(T, default@localhost:20504). \n"
                + "out_all(L) :- out_all(L, default@localhost:20504). \n"
                + "in_all(T,L) :- in_all(T, L, default@localhost:20504). \n"
                + "rd_all(T,L) :- rd_all(T, L, default@localhost:20504). \n"
                + "no_all(T,L) :- no_all(T, L, default@localhost:20504). \n"

                + "out_s(E,G,R) :- out_s(E,G,R, default@localhost:20504). \n"
                + "in_s(E,G,R) :- in_s(E,G,R, default@localhost:20504). \n"
                + "inp_s(E,G,R) :- inp_s(E,G,R, default@localhost:20504). \n"
                + "rd_s(E,G,R) :- rd_s(E,G,R, default@localhost:20504). \n"
                + "rdp_s(E,G,R) :- rdp_s(E,G,R, default@localhost:20504). \n"
                + "no_s(E,G,R) :- no_s(E,G,R, default@localhost:20504). \n"
                + "nop_s(E,G,R) :- nop_s(E,G,R, default@localhost:20504). \n"
                + "get_s(L) :- get_s(L, default@localhost:20504). \n"
                + "set_s(L) :- set_s(L, default@localhost:20504). \n"

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
     * 
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean in_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.in(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean in_all_3(final Term arg0, final Term arg1, final Term arg2) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg2.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.inAll(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean in_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.inS(tid, new LogicTuple(event.getTerm()),
                            new LogicTuple(guards.getTerm()), new LogicTuple(
                                    reactionBody.getTerm()), (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean inp_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.inp(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean inp_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.inpS(tid, new LogicTuple(event.getTerm()),
                            new LogicTuple(guards.getTerm()), new LogicTuple(
                                    reactionBody.getTerm()), (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean no_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.no(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean no_all_3(final Term arg0, final Term arg1, final Term arg2) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg2.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.noAll(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean no_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.noS(tid, new LogicTuple(event.getTerm()),
                            new LogicTuple(guards.getTerm()), new LogicTuple(
                                    reactionBody.getTerm()), (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean nop_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.nop(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean nop_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.nopS(tid, new LogicTuple(event.getTerm()),
                            new LogicTuple(guards.getTerm()), new LogicTuple(
                                    reactionBody.getTerm()), (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean out_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.out(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean out_all_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.outAll(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean out_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.outS(tid, new LogicTuple(event.getTerm()),
                            new LogicTuple(guards.getTerm()), new LogicTuple(
                                    reactionBody.getTerm()), (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * 
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean rd_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.rd(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.BulkAsynchACC BulkAsynchACC
     * @see alice.tucson.api.BulkSynchACC BulkSynchACC
     */
    public boolean rd_all_3(final Term arg0, final Term arg1, final Term arg2) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg2.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.rdAll(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean rd_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.rdS(tid, new LogicTuple(event.getTerm()),
                            new LogicTuple(guards.getTerm()), new LogicTuple(
                                    reactionBody.getTerm()), (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean rdp_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.rdp(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean rdp_s_4(final Term event, final Term guards,
            final Term reactionBody, final Term arg3) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg3.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.rdpS(tid, new LogicTuple(event.getTerm()),
                            new LogicTuple(guards.getTerm()), new LogicTuple(
                                    reactionBody.getTerm()), (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
        if (op.isResultSuccess()) {
            this.unify(event, op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
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
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean set_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.set(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.SpecificationAsynchACC SpecificationAsynchACC
     * @see alice.tucson.api.SpecificationSynchACC SpecificationSynchACC
     */
    public boolean set_s_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.setS(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.OrdinaryAsynchACC OrdinaryAsynchACC
     * @see alice.tucson.api.OrdinarySynchACC OrdinarySynchACC
     */
    public boolean spawn_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.spawn(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean uin_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.uin(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean uinp_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.uinp(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean uno_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.uno(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean unop_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.unop(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean urd_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.urd(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
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
     * @see alice.tucson.api.UniformAsynchACC UniformAsynchACC
     * @see alice.tucson.api.UniformSynchACC UniformSynchACC
     */
    public boolean urdp_2(final Term arg0, final Term arg1) {
        if (this.context == null) {
            return false;
        }
        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(arg1.getTerm().toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        ITucsonOperation op;
        try {
            op =
                    this.context.urdp(tid, new LogicTuple(arg0.getTerm()),
                            (Long) null);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            return false;
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
            return false;
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
            return false;
        }
        if (op.isResultSuccess()) {
            this.unify(arg0.getTerm(), op.getLogicTupleResult().toTerm());
        }
        return op.isResultSuccess();
    }

}
