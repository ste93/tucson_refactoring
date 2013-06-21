package alice.tucson.service;

import java.util.LinkedList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.ITucsonOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.OperationCompletionListener;
import alice.tuplecentre.core.TupleCentreOperation;

/**
 * 
 */
public class TucsonOperation extends TupleCentreOperation implements
        ITucsonOperation {

    private static final int OPTYPE_EXIT = 310;

    public static int abortOpCode() {
        return TupleCentreOperation.OPTYPE_ABORT_OP;
    }

    public static int addInspCode() {
        return TupleCentreOperation.OPTYPE_ADD_INSP;
    }

    public static int addObsCode() {
        return TupleCentreOperation.OPTYPE_ADD_OBS;
    }

    public static int exitCode() {
        return TucsonOperation.OPTYPE_EXIT;
    }

    public static int get_Code() {
        return TupleCentreOperation.OPTYPE_GET;
    }

    public static int get_sCode() {
        return TupleCentreOperation.OPTYPE_GET_S;
    }

    public static int getTRSetCode() {
        return TupleCentreOperation.OPTYPE_GET_TRSET;
    }

    public static int getTSetCode() {
        return TupleCentreOperation.OPTYPE_GET_TSET;
    }

    public static int getWSetCode() {
        return TupleCentreOperation.OPTYPE_GET_WSET;
    }

    public static int goCmdCode() {
        return TupleCentreOperation.OPTYPE_GO_CMD;
    }

    public static int hasInspCode() {
        return TupleCentreOperation.OPTYPE_HAS_INSP;
    }

    public static int hasObsCode() {
        return TupleCentreOperation.OPTYPE_HAS_OBS;
    }

    public static int in_allCode() {
        return TupleCentreOperation.OPTYPE_IN_ALL;
    }

    public static int in_sCode() {
        return TupleCentreOperation.OPTYPE_IN_S;
    }

    public static int inCode() {
        return TupleCentreOperation.OPTYPE_IN;
    }

    public static int inp_sCode() {
        return TupleCentreOperation.OPTYPE_INP_S;
    }

    public static int inpCode() {
        return TupleCentreOperation.OPTYPE_INP;
    }

    public static int nextStepCode() {
        return TupleCentreOperation.OPTYPE_NEXT_STEP;
    }

    public static int no_allCode() {
        return TupleCentreOperation.OPTYPE_NO_ALL;
    }

    public static int no_sCode() {
        return TupleCentreOperation.OPTYPE_NO_S;
    }

    public static int noCode() {
        return TupleCentreOperation.OPTYPE_NO;
    }

    public static int nop_sCode() {
        return TupleCentreOperation.OPTYPE_NOP_S;
    }

    public static int nopCode() {
        return TupleCentreOperation.OPTYPE_NOP;
    }

    public static int out_allCode() {
        return TupleCentreOperation.OPTYPE_OUT_ALL;
    }

    public static int out_sCode() {
        return TupleCentreOperation.OPTYPE_OUT_S;
    }

    public static int outCode() {
        return TupleCentreOperation.OPTYPE_OUT;
    }

    public static int rd_allCode() {
        return TupleCentreOperation.OPTYPE_RD_ALL;
    }

    public static int rd_sCode() {
        return TupleCentreOperation.OPTYPE_RD_S;
    }

    public static int rdCode() {
        return TupleCentreOperation.OPTYPE_RD;
    }

    public static int rdp_sCode() {
        return TupleCentreOperation.OPTYPE_RDP_S;
    }

    public static int rdpCode() {
        return TupleCentreOperation.OPTYPE_RDP;
    }

    public static int reset() {
        return TupleCentreOperation.RESET;
    }

    public static int rmvInspCode() {
        return TupleCentreOperation.OPTYPE_RMV_INSP;
    }

    public static int rmvObsCode() {
        return TupleCentreOperation.OPTYPE_RMV_OBS;
    }

    public static int set_Code() {
        return TupleCentreOperation.OPTYPE_SET;
    }

    public static int set_sCode() {
        return TupleCentreOperation.OPTYPE_SET_S;
    }

    public static int setMngModeCode() {
        return TupleCentreOperation.OPTYPE_SET_MNG_MODE;
    }

    public static int setSpyCode() {
        return TupleCentreOperation.OPTYPE_SET_SPY;
    }

    public static int setWSetCode() {
        return TupleCentreOperation.OPTYPE_SET_WSET;
    }

    /**
     * NEW PRIMITIVES CODES
     */

    public static int spawnCode() {
        return TupleCentreOperation.OPTYPE_SPAWN;
    }

    public static int stopCmdCode() {
        return TupleCentreOperation.OPTYPE_STOP_CMD;
    }

    public static int uinCode() {
        return TupleCentreOperation.OPTYPE_UIN;
    }

    public static int uinpCode() {
        return TupleCentreOperation.OPTYPE_UINP;
    }

    public static int unoCode() {
        return TupleCentreOperation.OPTYPE_UNO;
    }

    public static int unopCode() {
        return TupleCentreOperation.OPTYPE_UNOP;
    }

    public static int urdCode() {
        return TupleCentreOperation.OPTYPE_URD;
    }

    public static int urdpCode() {
        return TupleCentreOperation.OPTYPE_URDP;
    }

    private boolean allowed;

    private ACCProxyAgentSide context = null;

    private boolean successed;

    public TucsonOperation(final int type, final Tuple t,
            final OperationCompletionListener l, final ACCProxyAgentSide ctx) {
        super(null, type, t, l);
        this.context = ctx;
        this.successed = false;
    }

    public TucsonOperation(final int type, final TupleTemplate t,
            final OperationCompletionListener l, final ACCProxyAgentSide ctx) {
        super(null, type, t, l);
        this.context = ctx;
        this.successed = false;
    }

    public OperationCompletionListener getListener() {
        return this.listener;
    }

    public LogicTuple getLogicTupleArgument() {
        if (this.isOut() || this.isOut_s() || this.isSet_s() || this.isSet()
                || this.isOutAll() || this.isSpawn()) {
            return (LogicTuple) this.getTupleArgument();
        }
        return (LogicTuple) this.getTemplateArgument();
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

    public String getSpecResult() throws InvalidTupleOperationException {
        return this.getLogicTupleResult().getArg(0).getName();
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    @Override
    public boolean isResultSuccess() {
        return super.isResultSuccess();
    }

    public boolean isSuccessed() {
        return this.successed;
    }

    /**
     * 
     * @param s
     * @param a
     */
    public void notifyCompletion(final boolean s, final boolean a) {

        this.successed = s;
        this.allowed = a;

        if (this.listener != null) {
            System.out.println("......[TucsonOperation]: listener is "
                    + this.listener.getClass().getSimpleName());
            this.operationCompleted = true;
            this.listener.operationCompleted(this);
        } else {
            synchronized (this.token) {
                this.operationCompleted = true;
                this.token.notifyAll();
            }
        }

    }

    public void setLogicTupleListResult(final List<LogicTuple> tl) {
        final List<Tuple> t = new LinkedList<Tuple>();
        for (final LogicTuple logicTuple : tl) {
            t.add(logicTuple);
        }
        this.setTupleListResult(t);
    }

    /**
	 * 
	 */
    @Override
    public void waitForOperationCompletion(final long ms)
            throws OperationTimeOutException {
        synchronized (this.token) {
            if (!this.operationCompleted) {
                try {
                    this.token.wait(ms);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!this.operationCompleted) {
                this.context.addOperationExpired(this.getId());
                throw new OperationTimeOutException();
            }
        }
    }

}
