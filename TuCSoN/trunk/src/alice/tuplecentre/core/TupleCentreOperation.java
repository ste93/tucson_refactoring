/*
 * Tuple Centre media - Copyright (C) 2001-2002 aliCE team at deis.unibo.it This
 * library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package alice.tuplecentre.core;

import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.TCCycleResult.Outcome;
import alice.tuprolog.Prolog;

/**
 * This class represents an Operation on a tuple centre.
 * 
 * @author aricci
 * 
 */
public abstract class TupleCentreOperation implements ITupleCentreOperation {

    protected static final int OPTYPE_ABORT_OP = 58;
    protected static final int OPTYPE_ADD_INSP = 70;
    protected static final int OPTYPE_ADD_OBS = 67;
    protected static final int OPTYPE_GET = 8;
    protected static final int OPTYPE_GET_S = 28;
    protected static final int OPTYPE_GET_TRSET = 66;
    protected static final int OPTYPE_GET_TSET = 64;
    protected static final int OPTYPE_GET_WSET = 65;
    protected static final int OPTYPE_GO_CMD = 61;

    protected static final int OPTYPE_HAS_INSP = 72;

    protected static final int OPTYPE_HAS_OBS = 69;
    protected static final int OPTYPE_IN = 2;
    protected static final int OPTYPE_IN_ALL = 11;
    protected static final int OPTYPE_IN_S = 21;
    protected static final int OPTYPE_INP = 4;
    protected static final int OPTYPE_INP_S = 23;
    protected static final int OPTYPE_NEXT_STEP = 62;
    protected static final int OPTYPE_NO = 6;
    protected static final int OPTYPE_NO_ALL = 13;
    protected static final int OPTYPE_NO_S = 25;

    protected static final int OPTYPE_NOP = 7;
    protected static final int OPTYPE_NOP_S = 26;
    protected static final int OPTYPE_OUT = 1;
    protected static final int OPTYPE_OUT_ALL = 10;
    protected static final int OPTYPE_OUT_S = 20;
    protected static final int OPTYPE_RD = 3;
    protected static final int OPTYPE_RD_ALL = 12;
    protected static final int OPTYPE_RD_S = 22;
    protected static final int OPTYPE_RDP = 5;

    protected static final int OPTYPE_RDP_S = 24;
    protected static final int OPTYPE_RMV_INSP = 71;
    protected static final int OPTYPE_RMV_OBS = 68;
    protected static final int OPTYPE_SET = 9;
    protected static final int OPTYPE_SET_MNG_MODE = 59;
    protected static final int OPTYPE_SET_S = 27;
    protected static final int OPTYPE_SET_SPY = 63;
    protected static final int OPTYPE_SET_WSET = 73;
    protected static final int OPTYPE_SPAWN = 666;
    protected static final int OPTYPE_STOP_CMD = 60;
    protected static final int OPTYPE_UIN = 15;
    protected static final int OPTYPE_UINP = 18;
    protected static final int OPTYPE_UNO = 16;
    protected static final int OPTYPE_UNOP = 19;
    protected static final int OPTYPE_URD = 14;
    protected static final int OPTYPE_URDP = 17;
    protected static final int RESET = 74;

    /**
     * shared id counter
     */
    private static long idCounter = 0;
    protected OperationCompletionListener listener;
    protected boolean operationCompleted;
    /**
     * used for possible synchronisation
     */
    protected Object token;
    /**
     * internal identifier of the operation
     */
    private final long id;
    private final Prolog p;
    private final TCCycleResult result;
    private TupleTemplate templateArgument;
    private Tuple tupleArgument;
    private List<Tuple> tupleListArgument;
    private final int type;

    protected TupleCentreOperation(final Prolog prol, final int t,
            final List<Tuple> tupleList) {
        this(prol, t);
        this.listener = null;
        this.tupleListArgument = tupleList;
    }

    protected TupleCentreOperation(final Prolog prol, final int t,
            final List<Tuple> tupleList, final OperationCompletionListener l) {
        this(prol, t, tupleList);
        this.listener = l;
    }

    protected TupleCentreOperation(final Prolog prol, final int ty,
            final Tuple t) {
        this(prol, ty);
        this.listener = null;
        this.tupleArgument = t;
    }

    protected TupleCentreOperation(final Prolog prol, final int ty,
            final Tuple t, final OperationCompletionListener l) {
        this(prol, ty, t);
        this.listener = l;
    }

    protected TupleCentreOperation(final Prolog prol, final int ty,
            final TupleTemplate t) {
        this(prol, ty);
        this.listener = null;
        this.templateArgument = t;
    }

    protected TupleCentreOperation(final Prolog prol, final int ty,
            final TupleTemplate t, final OperationCompletionListener l) {
        this(prol, ty, t);
        this.listener = l;
    }

    private TupleCentreOperation(final Prolog prol, final int ty) {
        this.operationCompleted = false;
        this.result = new TCCycleResult();
        this.type = ty;
        this.token = new Object();
        this.id = TupleCentreOperation.idCounter;
        TupleCentreOperation.idCounter++;
        this.p = prol;
    }

    public void addListener(final OperationCompletionListener lis) {
        this.listener = lis;
    }

    /**
     * Get operation identifier
     * 
     * @return Operation identifier
     */
    public long getId() {
        return this.id;
    }

    public LogicTuple getPredicate() {
        try {
            if (this.isOut() || this.isOut_s() || this.isOutAll()
                    || this.isSpawn() || this.isSet() || this.isSet_s()) {
                return LogicTuple.parse("" + this.getPrimitive() + "("
                        + this.tupleArgument + ")");
            }
            return LogicTuple.parse("" + this.getPrimitive() + "("
                    + this.templateArgument + ")");
        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LogicTuple getPrimitive() {
        switch (this.type) {
            case OPTYPE_OUT:
                return new LogicTuple("out");
            case OPTYPE_IN:
                return new LogicTuple("in");
            case OPTYPE_RD:
                return new LogicTuple("rd");
            case OPTYPE_INP:
                return new LogicTuple("inp");
            case OPTYPE_RDP:
                return new LogicTuple("rdp");
            case OPTYPE_NO:
                return new LogicTuple("no");
            case OPTYPE_NOP:
                return new LogicTuple("nop");
            case OPTYPE_GET:
                return new LogicTuple("get");
            case OPTYPE_SET:
                return new LogicTuple("set");
            case OPTYPE_SPAWN:
                return new LogicTuple("spawn");
            case OPTYPE_OUT_ALL:
                return new LogicTuple("out_all");
            case OPTYPE_IN_ALL:
                return new LogicTuple("in_all");
            case OPTYPE_RD_ALL:
                return new LogicTuple("rd_all");
            case OPTYPE_NO_ALL:
                return new LogicTuple("no_all");
            case OPTYPE_URD:
                return new LogicTuple("urd");
            case OPTYPE_UIN:
                return new LogicTuple("uin");
            case OPTYPE_UNO:
                return new LogicTuple("uno");
            case OPTYPE_URDP:
                return new LogicTuple("urdp");
            case OPTYPE_UINP:
                return new LogicTuple("uinp");
            case OPTYPE_UNOP:
                return new LogicTuple("unop");
            case OPTYPE_OUT_S:
                return new LogicTuple("out_s");
            case OPTYPE_IN_S:
                return new LogicTuple("in_s");
            case OPTYPE_RD_S:
                return new LogicTuple("rd_s");
            case OPTYPE_INP_S:
                return new LogicTuple("inp_s");
            case OPTYPE_RDP_S:
                return new LogicTuple("rdp_s");
            case OPTYPE_NO_S:
                return new LogicTuple("no_s");
            case OPTYPE_NOP_S:
                return new LogicTuple("nop_s");
            case OPTYPE_SET_S:
                return new LogicTuple("set_s");
            case OPTYPE_GET_S:
                return new LogicTuple("get_s");
            default:
                return null;
        }
    }

    public TupleTemplate getTemplateArgument() {
        return this.templateArgument;
    }

    public Tuple getTupleArgument() {
        return this.tupleArgument;
    }

    public List<Tuple> getTupleListArgument() {
        return this.tupleListArgument;
    }

    public List<Tuple> getTupleListResult() {
        return this.result.getTupleListResult();
    }

    public Tuple getTupleResult() {
        return this.result.getTupleResult();
    }

    public int getType() {
        return this.type;
    }

    public boolean isGet() {
        return this.type == TupleCentreOperation.OPTYPE_GET;
    }

    public boolean isGet_s() {
        return this.type == TupleCentreOperation.OPTYPE_GET_S;
    }

    public boolean isIn() {
        return this.type == TupleCentreOperation.OPTYPE_IN;
    }

    public boolean isIn_s() {
        return this.type == TupleCentreOperation.OPTYPE_IN_S;
    }

    public boolean isInAll() {
        return this.type == TupleCentreOperation.OPTYPE_IN_ALL;
    }

    public boolean isInp() {
        return this.type == TupleCentreOperation.OPTYPE_INP;
    }

    public boolean isInp_s() {
        return this.type == TupleCentreOperation.OPTYPE_INP_S;
    }

    public boolean isNo() {
        return this.type == TupleCentreOperation.OPTYPE_NO;
    }

    public boolean isNo_s() {
        return this.type == TupleCentreOperation.OPTYPE_NO_S;
    }

    public boolean isNoAll() {
        return this.type == TupleCentreOperation.OPTYPE_NO_ALL;
    }

    public boolean isNop() {
        return this.type == TupleCentreOperation.OPTYPE_NOP;
    }

    public boolean isNop_s() {
        return this.type == TupleCentreOperation.OPTYPE_NOP_S;
    }

    /**
     * Tests if the operation is completed
     * 
     * @return true if the operation is completed
     */
    public boolean isOperationCompleted() {
        return this.operationCompleted;
    }

    public boolean isOut() {
        return this.type == TupleCentreOperation.OPTYPE_OUT;
    }

    public boolean isOut_s() {
        return this.type == TupleCentreOperation.OPTYPE_OUT_S;
    }

    public boolean isOutAll() {
        return this.type == TupleCentreOperation.OPTYPE_OUT_ALL;
    }

    public boolean isRd() {
        return this.type == TupleCentreOperation.OPTYPE_RD;
    }

    public boolean isRd_s() {
        return this.type == TupleCentreOperation.OPTYPE_RD_S;
    }

    public boolean isRdAll() {
        return this.type == TupleCentreOperation.OPTYPE_RD_ALL;
    }

    public boolean isRdp() {
        return this.type == TupleCentreOperation.OPTYPE_RDP;
    }

    public boolean isRdp_s() {
        return this.type == TupleCentreOperation.OPTYPE_RDP_S;
    }

    /**
     * Tests if the result is defined
     * 
     * @return true if the result is defined
     */
    public boolean isResultDefined() {
        return this.result.isResultDefined();
    }

    public boolean isResultFailure() {
        return this.result.isResultFailure();
    }

    public boolean isResultSuccess() {
        return this.result.isResultSuccess();
    }

    public boolean isSet() {
        return this.type == TupleCentreOperation.OPTYPE_SET;
    }

    public boolean isSet_s() {
        return this.type == TupleCentreOperation.OPTYPE_SET_S;
    }

    public boolean isSpawn() {
        return this.type == TupleCentreOperation.OPTYPE_SPAWN;
    }

    public boolean isUin() {
        return this.type == TupleCentreOperation.OPTYPE_UIN;
    }

    public boolean isUinp() {
        return this.type == TupleCentreOperation.OPTYPE_UINP;
    }

    public boolean isUno() {
        return this.type == TupleCentreOperation.OPTYPE_UNO;
    }

    public boolean isUnop() {
        return this.type == TupleCentreOperation.OPTYPE_UNOP;
    }

    public boolean isUrd() {
        return this.type == TupleCentreOperation.OPTYPE_URD;
    }

    public boolean isUrdp() {
        return this.type == TupleCentreOperation.OPTYPE_URDP;
    }

    /**
     * Changes the state of the operation to complete.
     * 
     */
    public void notifyCompletion() {
        if (this.listener != null) {
            this.operationCompleted = true;
            this.listener.operationCompleted(this);
        } else {
            try {
                synchronized (this.token) {
                    this.operationCompleted = true;
                    this.token.notifyAll();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void removeListener() {
        this.listener = null;
    }

    public void setListener(final OperationCompletionListener l) {
        this.listener = l;
    }

    public void setOpResult(final Outcome o) {
        this.result.setOpResult(o);
    }

    public void setTupleListResult(final List<Tuple> t) {
        this.result.setTupleListResult(t);
        this.result.setEndTime(System.currentTimeMillis());
    }

    public void setTupleResult(final Tuple t) {
        this.result.setTupleResult(t);
        if (this.templateArgument != null) {
            this.templateArgument.propagate(this.p, t);
        }
        this.tupleArgument = t;
        this.result.setEndTime(System.currentTimeMillis());
    }

    /**
     * Wait for operation completion
     * 
     * Current execution flow is blocked until the operation is completed
     */
    public void waitForOperationCompletion() {
        try {
            synchronized (this.token) {
                while (!this.operationCompleted) {
                    this.token.wait();
                }
            }
        } catch (final InterruptedException e) {
            // TODO Properly handle Excpetion
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Wait for operation completion, with time out
     * 
     * Current execution flow is blocked until the operation is completed or a
     * maximum waiting time is elapsed
     * 
     * @param ms
     *            maximum waiting time
     * @throws OperationTimeOutException
     */
    public void waitForOperationCompletion(final long ms)
            throws OperationTimeOutException {
        try {
            synchronized (this.token) {
                if (!this.operationCompleted) {
                    this.token.wait(ms);
                }
                if (!this.operationCompleted) {
                    throw new OperationTimeOutException();
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

}
