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

import java.util.LinkedList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.respect.api.IRespectOperation;
import alice.respect.api.RespectSpecification;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import alice.tuplecentre.core.OperationCompletionListener;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * This class represents a ReSpecT operation.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public class RespectOperation extends AbstractTupleCentreOperation implements
        IRespectOperation {

    /**
     * 
     */
    public static final int OPTYPE_ENV = 103;
    /**
     * 
     */
    public static final int OPTYPE_GET_ENV = 101;
    /**
     * 
     */
    public static final int OPTYPE_SET_ENV = 102;
    /**
     * 
     */
    public static final int OPTYPE_TIME = 100;

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeGet(final LogicTuple t,
            final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(AbstractTupleCentreOperation.OPTYPE_GET,
                        (Tuple) t, l);
        return temp;
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeGetEnv(final LogicTuple t,
            final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(RespectOperation.OPTYPE_GET_ENV, t, l);
        temp.setTupleResult(t);
        return temp;
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeGetS(final LogicTuple t,
            final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(AbstractTupleCentreOperation.OPTYPE_GET_S,
                        (Tuple) t, l);
        return temp;
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeIn(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_IN, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeInAll(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_IN_ALL,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeInp(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_INP, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeInpS(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_INP_S,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeInS(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_IN_S,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeNo(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_NO, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeNoAll(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_NO_ALL,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeNop(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_NOP, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeNopS(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_NOP_S,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeNoS(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_NO_S,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeOut(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_OUT,
                (Tuple) t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeOutAll(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(
                AbstractTupleCentreOperation.OPTYPE_OUT_ALL, (Tuple) t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeOutS(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_OUT_S,
                (Tuple) t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeRd(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_RD, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeRdAll(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_RD_ALL,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeRdp(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_RDP, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeRdpS(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_RDP_S,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeRdS(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_RD_S,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeSet(final LogicTuple t,
            final OperationCompletionListener l) {
        if ("[]".equals(t.toString())) {
            return new RespectOperation(
                    AbstractTupleCentreOperation.OPTYPE_SET,
                    new LinkedList<Tuple>(), l);
        }
        final List<Tuple> list = new LinkedList<Tuple>();
        LogicTuple cpy = null;
        try {
            cpy = LogicTuple.parse(t.toString());
        } catch (final InvalidTupleException e) {
            e.printStackTrace();
            return null;
        }
        TupleArgument arg;
        try {
            arg = cpy.getArg(0);
            while (arg != null) {
                if (!arg.isList()) {
                    final LogicTuple t1 = new LogicTuple(arg);
                    list.add(t1);
                    arg = cpy.getArg(1);
                } else {
                    final LogicTuple t2 = new LogicTuple(arg);
                    cpy = t2;
                    if (!"[]".equals(cpy.toString())) {
                        arg = cpy.getArg(0);
                    } else {
                        arg = null;
                    }
                }
            }
        } catch (final InvalidOperationException e) {
            e.printStackTrace();
        }
        final RespectOperation temp =
                new RespectOperation(AbstractTupleCentreOperation.OPTYPE_SET,
                        list, l);
        return temp;
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeSetEnv(final LogicTuple t,
            final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(RespectOperation.OPTYPE_SET_ENV, t, l);
        temp.setTupleResult(t);
        return temp;
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeSetS(final LogicTuple t,
            final OperationCompletionListener l) {
        if ("[]".equals(t.toString())) {
            return new RespectOperation(
                    AbstractTupleCentreOperation.OPTYPE_SET_S,
                    new LinkedList<Tuple>(), l);
        }
        final List<Tuple> list = new LinkedList<Tuple>();
        LogicTuple cpy = null;
        try {
            cpy = LogicTuple.parse(t.toString());
        } catch (final InvalidTupleException e) {
            e.printStackTrace();
            return null;
        }
        TupleArgument arg;
        try {
            arg = cpy.getArg(0);
            while (arg != null) {
                if (!arg.isList()) {
                    final LogicTuple t1 = new LogicTuple(arg);
                    list.add(t1);
                    arg = cpy.getArg(1);
                } else {
                    final LogicTuple t2 = new LogicTuple(arg);
                    cpy = t2;
                    if (!"[]".equals(cpy.toString())) {
                        arg = cpy.getArg(0);
                    } else {
                        arg = null;
                    }
                }
            }
        } catch (final InvalidOperationException e) {
            e.printStackTrace();
        }
        final RespectOperation temp =
                new RespectOperation(AbstractTupleCentreOperation.OPTYPE_SET_S,
                        list, l);
        return temp;
    }

    /**
     * 
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation
            makeSetS(final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_SET_S,
                new LogicTuple(), l);
    }

    /**
     * 
     * @param spec
     *            the ReSpecT specification argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeSetS(final RespectSpecification spec,
            final OperationCompletionListener l) {
        RespectOperation temp = null;
        try {
            temp =
                    new RespectOperation(
                            AbstractTupleCentreOperation.OPTYPE_SET_S,
                            (Tuple) LogicTuple.parse(spec.toString()), l);
        } catch (final InvalidTupleException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeSpawn(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_SPAWN,
                (Tuple) t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeTime(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(RespectOperation.OPTYPE_TIME, t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeUin(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_UIN, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeUinp(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_UINP,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeUno(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_UNO, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeUnop(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_UNOP,
                t, l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeUrd(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_URD, t,
                l);
    }

    /**
     * 
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the ReSpecT operation built
     */
    public static RespectOperation makeUrdp(final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(AbstractTupleCentreOperation.OPTYPE_URDP,
                t, l);
    }

    /**
     * @param type
     *            the integer type-code of the oepration
     * @param tupleList
     *            the list of tuples argument of the operation
     * @param l
     *            the listener for operation completion
     */
    protected RespectOperation(final int type, final List<Tuple> tupleList,
            final OperationCompletionListener l) {
        super(type, tupleList, l);
    }

    /**
     * 
     * @param type
     *            the integer type-code of the oepration
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     */
    protected RespectOperation(final int type, final Tuple t,
            final OperationCompletionListener l) {
        super(type, t, l);
    }

    /**
     * 
     * @param type
     *            the integer type-code of the oepration
     * @param t
     *            the tuple template argument of the operation
     * @param l
     *            the listener for operation completion
     */
    protected RespectOperation(final int type, final TupleTemplate t,
            final OperationCompletionListener l) {
        super(type, t, l);
    }

    public LogicTuple getLogicTupleArgument() {
        if (this.isOut() || this.isOutS() || this.isOutAll() || this.isSpawn()) {
            final LogicTuple lt = (LogicTuple) this.getTupleArgument();
            return lt;
        }
        final LogicTuple lt = (LogicTuple) this.getTemplateArgument();
        return lt;
    }

    public List<LogicTuple> getLogicTupleListResult() {
        final List<Tuple> tl = this.getTupleListResult();
        final List<LogicTuple> tll = new LinkedList<LogicTuple>();
        for (final Tuple t : tl) {
            tll.add((LogicTuple) t);
        }
        return tll;
    }

    public LogicTuple getLogicTupleResult() {
        return (LogicTuple) this.getTupleResult();
    }

    public boolean isEnv() {
        return this.getType() == RespectOperation.OPTYPE_ENV;
    }

    public boolean isGetEnv() {
        return this.getType() == RespectOperation.OPTYPE_GET_ENV;
    }

    public boolean isSetEnv() {
        return this.getType() == RespectOperation.OPTYPE_SET_ENV;
    }

    public boolean isTime() {
        return this.getType() == RespectOperation.OPTYPE_TIME;
    }

    @Override
    public String toString() {
        return this.toTuple().toString();
    }

    /**
     * 
     * @return the logic tuple representation of this operation
     */
    public LogicTuple toTuple() {
        LogicTuple t = null;
        Term[] tl = null;
        if (this.isOperationCompleted()) {
            t = this.getLogicTupleResult();
        } else {
            t = this.getLogicTupleArgument();
        }
        String opName;
        if (this.isSpawn()) {
            opName = "spawn";
        } else if (this.isOut()) {
            opName = "out";
        } else if (this.isIn()) {
            opName = "in";
        } else if (this.isRd()) {
            opName = "rd";
        } else if (this.isInp()) {
            opName = "inp";
        } else if (this.isRdp()) {
            opName = "rdp";
        } else if (this.isNo()) {
            opName = "no";
        } else if (this.isNop()) {
            opName = "nop";
        } else if (this.isOutAll()) {
            opName = "out_all";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getLogicTupleListResult().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
            LogicTuple lt = null;
            try {
                lt =
                        new LogicTuple(opName, new TupleArgument(this
                                .getLogicTupleArgument().getArg(0).toTerm()),
                                new TupleArgument(new Struct(tl)));
            } catch (final InvalidOperationException e) {
                e.printStackTrace();
            }
            return lt;
        } else if (this.isInAll()) {
            opName = "in_all";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getLogicTupleListResult().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
            LogicTuple lt = null;
            try {
                lt =
                        new LogicTuple(opName, new TupleArgument(this
                                .getLogicTupleArgument().getArg(0).toTerm()),
                                new TupleArgument(new Struct(tl)));
            } catch (final InvalidOperationException e) {
                e.printStackTrace();
            }
            return lt;
        } else if (this.isRdAll()) {
            opName = "rd_all";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getLogicTupleListResult().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
            LogicTuple lt = null;
            try {
                lt =
                        new LogicTuple(opName, new TupleArgument(this
                                .getLogicTupleArgument().getArg(0).toTerm()),
                                new TupleArgument(new Struct(tl)));
            } catch (final InvalidOperationException e) {
                e.printStackTrace();
            }
            return lt;
        } else if (this.isNoAll()) {
            opName = "no_all";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getLogicTupleListResult().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
            LogicTuple lt = null;
            try {
                lt =
                        new LogicTuple(opName, new TupleArgument(this
                                .getLogicTupleArgument().getArg(0).toTerm()),
                                new TupleArgument(new Struct(tl)));
            } catch (final InvalidOperationException e) {
                e.printStackTrace();
            }
            return lt;
        } else if (this.isUrd()) {
            opName = "urd";
        } else if (this.isUin()) {
            opName = "uin";
        } else if (this.isUno()) {
            opName = "uno";
        } else if (this.isUrdp()) {
            opName = "urdp";
        } else if (this.isUinp()) {
            opName = "uinp";
        } else if (this.isUnop()) {
            opName = "unop";
        } else if (this.isGet()) {
            opName = "get";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getLogicTupleListResult().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
        } else if (this.isSet()) {
            opName = "set";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getTupleListArgument().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
        } else if (this.isOutS()) {
            opName = "out_s";
        } else if (this.isInS()) {
            opName = "in_s";
        } else if (this.isRdS()) {
            opName = "rd_s";
        } else if (this.isInpS()) {
            opName = "inp_s";
        } else if (this.isRdpS()) {
            opName = "rdp_s";
        } else if (this.isNoS()) {
            opName = "no_s";
        } else if (this.isNopS()) {
            opName = "nop_s";
        } else if (this.isGetS()) {
            opName = "get_s";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getLogicTupleListResult().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
        } else if (this.isSetS()) {
            opName = "set_s";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getTupleListArgument().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
        } else if (this.isGetEnv()) {
            return t;
        } else if (this.isEnv()) {
            opName = "env";
        } else if (this.isSetEnv()) {
            return t;
        } else if (this.isTime()) {
            opName = "time";
        } else {
            opName = "unknownOp";
        }
        return new LogicTuple(opName, new TupleArgument(
                tl != null ? new Struct(tl) : t.toTerm()));
    }

}
