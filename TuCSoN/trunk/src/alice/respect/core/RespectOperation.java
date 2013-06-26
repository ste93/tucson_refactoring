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
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.api.IRespectOperation;
import alice.respect.api.RespectSpecification;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.core.OperationCompletionListener;
import alice.tuplecentre.core.TupleCentreOperation;
import alice.tuprolog.Prolog;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * This class represents a ReSpecT operation.
 * 
 * @author aricci
 */
public class RespectOperation extends TupleCentreOperation implements
        IRespectOperation {

    public static final int OPTYPE_ENV = 103;
    public static final int OPTYPE_GET_ENV = 101;
    public static final int OPTYPE_SET_ENV = 102;
    public static final int OPTYPE_TIME = 100;

    public static RespectOperation makeGet(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(p, TupleCentreOperation.OPTYPE_GET,
                        (Tuple) t, l);
        return temp;
    }

    public static RespectOperation makeGet_s(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(p, TupleCentreOperation.OPTYPE_GET_S,
                        (Tuple) t, l);
        return temp;
    }

    public static RespectOperation makeGetEnv(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(p, RespectOperation.OPTYPE_GET_ENV, t, l);
        temp.setTupleResult(t);
        return temp;
    }

    public static RespectOperation makeIn(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_IN, t, l);
    }

    public static RespectOperation makeIn_s(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_IN_S, t, l);
    }

    public static RespectOperation makeInAll(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_IN_ALL, t, l);
    }

    public static RespectOperation makeInp(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_INP, t, l);
    }

    public static RespectOperation makeInp_s(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_INP_S, t, l);
    }

    public static RespectOperation makeNo(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_NO, t, l);
    }

    public static RespectOperation makeNo_s(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_NO_S, t, l);
    }

    public static RespectOperation makeNoAll(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_NO_ALL, t, l);
    }

    public static RespectOperation makeNop(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_NOP, t, l);
    }

    public static RespectOperation makeNop_s(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_NOP_S, t, l);
    }

    public static RespectOperation makeOut(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_OUT,
                (Tuple) t, l);
    }

    public static RespectOperation makeOut_s(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_OUT_S,
                (Tuple) t, l);
    }

    public static RespectOperation makeOutAll(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_OUT_ALL,
                (Tuple) t, l);
    }

    public static RespectOperation makeRd(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_RD, t, l);
    }

    public static RespectOperation makeRd_s(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_RD_S, t, l);
    }

    public static RespectOperation makeRdAll(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_RD_ALL, t, l);
    }

    public static RespectOperation makeRdp(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_RDP, t, l);
    }

    public static RespectOperation makeRdp_s(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_RDP_S, t, l);
    }

    public static RespectOperation makeSet(final Prolog p,
            final LogicTuple logicTuple, final OperationCompletionListener l) {
        if (logicTuple.toString().equals("[]")) {
            return new RespectOperation(p, TupleCentreOperation.OPTYPE_SET,
                    new LinkedList<Tuple>(), l);
        }
        final List<Tuple> list = new LinkedList<Tuple>();
        LogicTuple cpy = null;
        try {
            cpy = LogicTuple.parse(logicTuple.toString());
        } catch (final InvalidLogicTupleException e) {
            // TODO Properly handle Exception
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
                    if (!cpy.toString().equals("[]")) {
                        arg = cpy.getArg(0);
                    } else {
                        arg = null;
                    }
                }
            }
        } catch (final InvalidTupleOperationException e) {
            // TODO Properly handle Exception
        }
        final RespectOperation temp =
                new RespectOperation(p, TupleCentreOperation.OPTYPE_SET, list,
                        l);
        return temp;
    }

    public static RespectOperation makeSet_s(final Prolog p,
            final LogicTuple logicTuple, final OperationCompletionListener l) {
        if (logicTuple.toString().equals("[]")) {
            return new RespectOperation(p, TupleCentreOperation.OPTYPE_SET_S,
                    new LinkedList<Tuple>(), l);
        }
        final List<Tuple> list = new LinkedList<Tuple>();
        LogicTuple cpy = null;
        try {
            cpy = LogicTuple.parse(logicTuple.toString());
        } catch (final InvalidLogicTupleException e) {
            // TODO Properly handle Exception
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
                    if (!cpy.toString().equals("[]")) {
                        arg = cpy.getArg(0);
                    } else {
                        arg = null;
                    }
                }
            }
        } catch (final InvalidTupleOperationException e) {
            // TODO Properly handle Exception
        }
        final RespectOperation temp =
                new RespectOperation(p, TupleCentreOperation.OPTYPE_SET_S,
                        list, l);
        return temp;
    }

    public static RespectOperation makeSet_s(final Prolog p,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_SET_S,
                new LogicTuple(), l);
    }

    public static RespectOperation
            makeSet_s(final Prolog p, final RespectSpecification spec,
                    final OperationCompletionListener l) {
        RespectOperation temp = null;
        try {
            temp =
                    new RespectOperation(p, TupleCentreOperation.OPTYPE_SET_S,
                            (Tuple) LogicTuple.parse(spec.toString()), l);
        } catch (final InvalidLogicTupleException e) {
            // TODO Properly handle Exception
        }
        return temp;
    }

    public static RespectOperation makeSetEnv(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        final RespectOperation temp =
                new RespectOperation(p, RespectOperation.OPTYPE_SET_ENV, t, l);
        temp.setTupleResult(t);
        return temp;
    }

    public static RespectOperation makeSpawn(final Prolog p,
            final LogicTuple t, final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_SPAWN,
                (Tuple) t, l);
    }

    public static RespectOperation makeTime(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, RespectOperation.OPTYPE_TIME, t, l);
    }

    public static RespectOperation makeUin(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_UIN, t, l);
    }

    public static RespectOperation makeUinp(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_UINP, t, l);
    }

    public static RespectOperation makeUno(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_UNO, t, l);
    }

    public static RespectOperation makeUnop(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_UNOP, t, l);
    }

    public static RespectOperation makeUrd(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_URD, t, l);
    }

    public static RespectOperation makeUrdp(final Prolog p, final LogicTuple t,
            final OperationCompletionListener l) {
        return new RespectOperation(p, TupleCentreOperation.OPTYPE_URDP, t, l);
    }

    protected RespectOperation(final Prolog p, final int type,
            final List<Tuple> tupleList, final OperationCompletionListener l) {
        super(p, type, tupleList, l);
    }

    protected RespectOperation(final Prolog p, final int type, final Tuple t,
            final OperationCompletionListener l) {
        super(p, type, t, l);
    }

    protected RespectOperation(final Prolog p, final int type,
            final TupleTemplate t, final OperationCompletionListener l) {
        super(p, type, t, l);
    }

    /**
     * Gets the argument of the operation
     * 
     * @return
     */
    public LogicTuple getLogicTupleArgument() {
        if (this.isOut() || this.isOut_s() || this.isOutAll() || this.isSpawn()) {
            return (LogicTuple) this.getTupleArgument();
        }
        return (LogicTuple) this.getTemplateArgument();
    }

    /**
     * Gets the results of a get operation
     */
    public List<LogicTuple> getLogicTupleListResult() {
        final List<Tuple> tl = this.getTupleListResult();
        final List<LogicTuple> tll = new LinkedList<LogicTuple>();
        for (final Tuple t : tl) {
            tll.add((LogicTuple) t);
        }
        return tll;
    }

    /**
     * Gets the result of the operation,
     * 
     * @return
     */
    public LogicTuple getLogicTupleResult() {
        return (LogicTuple) this.getTupleResult();
    }

    public Term getTermTupleListResult() {
        final List<LogicTuple> listIn = this.getLogicTupleListResult();
        if (listIn == null) {
            return null;
        }
        final Term[] test = new Term[listIn.size()];
        final int numTuples = listIn.size();
        for (int i = 0; i < numTuples; i++) {
            test[i] = listIn.get(i).toTerm();
        }
        return new Struct(test);
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
            } catch (final InvalidTupleOperationException e) {
                // TODO Properly handle Exception
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
            } catch (final InvalidTupleOperationException e) {
                // TODO Properly handle Exception
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
            } catch (final InvalidTupleOperationException e) {
                // TODO Properly handle Exception
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
            } catch (final InvalidTupleOperationException e) {
                // TODO Properly handle Exception
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
        } else if (this.isOut_s()) {
            opName = "out_s";
        } else if (this.isIn_s()) {
            opName = "in_s";
        } else if (this.isRd_s()) {
            opName = "rd_s";
        } else if (this.isInp_s()) {
            opName = "inp_s";
        } else if (this.isRdp_s()) {
            opName = "rdp_s";
        } else if (this.isNo_s()) {
            opName = "no_s";
        } else if (this.isNop_s()) {
            opName = "nop_s";
        } else if (this.isGet_s()) {
            opName = "get_s";
            LogicTuple[] tupleL = new LogicTuple[] {};
            tupleL = this.getLogicTupleListResult().toArray(tupleL);
            tl = new Term[tupleL.length];
            for (int i = 0; i < tupleL.length; i++) {
                tl[i] = tupleL[i].toTerm();
            }
        } else if (this.isSet_s()) {
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
