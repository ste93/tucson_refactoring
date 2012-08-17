package alice.tucson.service;

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.tucson.api.*;

import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.*;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public class TucsonOperation extends TupleCentreOperation implements ITucsonOperation{
	
	private static final int OPTYPE_EXIT = 310;
	private boolean successed;
	private boolean allowed;
	private ACCProxyAgentSide context = null;
	
	public TucsonOperation(int type, Tuple t, OperationCompletionListener l, ACCProxyAgentSide context){
		super(null, type, t, l);
		this.context = context;
		successed = false;
	}

	public TucsonOperation(int type, TupleTemplate t, OperationCompletionListener l, ACCProxyAgentSide context){
		super(null, type, t, l);
		this.context = context;
		successed = false;
	}
	
	/**
	 * NEW PRIMITIVES CODES
	 */
	
	public static int uinCode(){
		return OPTYPE_UIN;
	}
	
	public static int urdCode(){
		return OPTYPE_URD;
	}
	
	public static int unoCode(){
		return OPTYPE_UNO;
	}
	
	public static int uinpCode(){
		return OPTYPE_UINP;
	}
	
	public static int urdpCode(){
		return OPTYPE_URDP;
	}
	
	public static int unopCode(){
		return OPTYPE_UNOP;
	}
	
	public static int out_allCode(){
		return OPTYPE_OUT_ALL;
	}
	
	public static int in_allCode(){
		return OPTYPE_IN_ALL;
	}
	
	public static int rd_allCode(){
		return OPTYPE_RD_ALL;
	}
	
	public static int no_allCode(){
		return OPTYPE_NO_ALL;
	}
	
	public static int outCode(){
		return OPTYPE_OUT;
	}
	
	public static int inCode(){
		return OPTYPE_IN;
	}
	
	public static int rdCode(){
		return OPTYPE_RD;
	}
	
	public static int inpCode(){
		return OPTYPE_INP;
	}
	
	public static int rdpCode(){
		return OPTYPE_RDP;
	}

	public static int set_Code(){
		return OPTYPE_SET;
	}
	
	public static int get_Code(){
		return OPTYPE_GET;
	}
	
	public static int noCode(){
		return OPTYPE_NO;
	}
	
	public static int nopCode(){
		return OPTYPE_NOP;
	}
	
	public static int out_sCode(){
		return OPTYPE_OUT_S;
	}
	
	public static int in_sCode(){
		return OPTYPE_IN_S;
	}
	
	public static int rd_sCode(){
		return OPTYPE_RD_S;
	}
	
	public static int inp_sCode(){
		return OPTYPE_INP_S;
	}
	
	public static int rdp_sCode(){
		return OPTYPE_RDP_S;
	}
	
	public static int set_sCode(){
		return OPTYPE_SET_S;
	}
	
	public static int get_sCode(){
		return OPTYPE_GET_S;
	}	
	
	public static int no_sCode(){
		return OPTYPE_NO_S;
	}
	
	public static int nop_sCode(){
		return OPTYPE_NOP_S;
	}
	
	public static int exitCode(){
		return OPTYPE_EXIT;
	}
	
	public static int abortOpCode(){
		return OPTYPE_ABORT_OP;
	}
	
	public static int setMngModeCode(){
		return OPTYPE_SET_MNG_MODE;
	}
	
	public static int stopCmdCode(){
		return OPTYPE_STOP_CMD;
	}
	
	public static int goCmdCode(){
		return OPTYPE_GO_CMD;
	}
	
	public static int nextStepCode(){
		return OPTYPE_NEXT_STEP;
	}
	
	public static int setSpyCode(){
		return OPTYPE_SET_SPY;
	}
	
	public static int getTSetCode(){
		return OPTYPE_GET_TSET;
	}
	
	public static int getWSetCode(){
		return OPTYPE_GET_WSET;
	}
	
	public static int setWSetCode(){
		return OPTYPE_SET_WSET;
	}
	
	public static int getTRSetCode(){
		return OPTYPE_GET_TRSET;
	}
	
//	public EnhancedACC getTucsonContex(){
//		return context;
//	}
	
	public static int addObsCode(){
		return OPTYPE_ADD_OBS;
	}
	
	public static int rmvObsCode(){
		return OPTYPE_RMV_OBS;
	}
	
	public static int hasObsCode(){
		return OPTYPE_HAS_OBS;
	}
	
	public static int addInspCode(){
		return OPTYPE_ADD_INSP;
	}
	
	public static int rmvInspCode(){
		return OPTYPE_RMV_INSP;
	}
	
	public static int hasInspCode(){
		return OPTYPE_HAS_INSP;
	}
	
	public static int reset(){
		return RESET;
	}
	
	public OperationCompletionListener getListener(){
		return listener;
	}	
	
	/**
	 * 
	 * @param successed
	 * @param allowed
	 */
	public void notifyCompletion(boolean successed, boolean allowed){
		
		this.successed = successed;
		this.allowed = allowed;
		
		if(listener != null){
			operationCompleted = true;
			listener.operationCompleted(this);
		}else{
			synchronized(token){
				operationCompleted = true;
				token.notifyAll();
			}
		}
		
	}
	
	public LogicTuple getLogicTupleResult(){
		return (LogicTuple) getTupleResult();
	}

	public LogicTuple getLogicTupleArgument(){
		if(isOut() || isOut_s() || isSet_s() || isSet() || isOutAll())
			return (LogicTuple) getTupleArgument();
		else
			return (LogicTuple) getTemplateArgument();
	}
	
	public List<LogicTuple> getLogicTupleListResult(){
		List<Tuple> tl = this.getTupleListResult();
		List<LogicTuple> tll = new LinkedList<LogicTuple>();
		for(Tuple t : tl)
			tll.add((LogicTuple) t);
		return tll;
	}
	
	public void setLogicTupleListResult(List<LogicTuple> tl){
		List<Tuple> t = new LinkedList<Tuple>();
		for(LogicTuple logicTuple : tl)
			t.add(logicTuple);
		setTupleListResult(t);
	}
	
	public String getSpecResult() throws InvalidTupleOperationException{
		return getLogicTupleResult().getArg(0).getName();
	}
	
	public boolean isResultSuccess(){
		return super.isResultSuccess();
	}
	
	public boolean isSuccessed(){
		return successed;
	}
	
	public boolean isAllowed(){
		return allowed;
	}
	
	/**
	 * 
	 */
	public void waitForOperationCompletion(long ms) throws OperationTimeOutException{
		synchronized(token){
			if(!operationCompleted)
				try {
					token.wait(ms);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			if(!operationCompleted){
				this.context.addOperationExpired(getId());
				throw new OperationTimeOutException();
			}
		}
	}
	
}
