/*
 * Tuple Centre media - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuplecentre.core;

import java.util.List;

import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.core.TCCycleResult.Outcome;
import alice.tuprolog.Prolog;

/**
 * This class represents an Operation on a tuple centre.
 * 
 * @author aricci
 * 
 */
public abstract class TupleCentreOperation implements ITupleCentreOperation
{
	protected static final int OPTYPE_IN = 1;
	protected static final int OPTYPE_RD = 2;
	protected static final int OPTYPE_OUT = 3;
	protected static final int OPTYPE_INP = 4;
	protected static final int OPTYPE_RDP = 5;
	protected static final int OPTYPE_NO = 6;
	protected static final int OPTYPE_NOP = 666;
	protected static final int OPTYPE_GET = 7;
	protected static final int OPTYPE_SET = 8;
	
	protected static final int OPTYPE_NO_S = 66;
	protected static final int OPTYPE_NOP_S = 67;

	protected static final int OPTYPE_IN_S = 11;
	protected static final int OPTYPE_RD_S = 12;
	protected static final int OPTYPE_OUT_S = 13;
	protected static final int OPTYPE_INP_S = 14;
	protected static final int OPTYPE_RDP_S = 15;

	public static final int OPTYPE_SET_SPEC = 16;
	public static final int OPTYPE_GET_SPEC = 17;
	
	// da 18 a 21 in RespectOperation
	
	public static final int OPTYPE_ABORT_OP = 22;
	public static final int OPTYPE_SET_MNG_MODE = 23;
	public static final int OPTYPE_STOP_CMD = 24;
	public static final int OPTYPE_GO_CMD = 25;
	public static final int OPTYPE_NEXT_STEP = 26;
	public static final int OPTYPE_SET_SPY = 27;
	public static final int OPTYPE_GET_TSET = 28;
	public static final int OPTYPE_GET_WSET = 29;	
	public static final int OPTYPE_GET_TRSET = 30;
	// 31 = OPTYPE_EXIT in TucsonOperation
	public static final int OPTYPE_ADD_OBS = 32;
	public static final int OPTYPE_RMV_OBS = 33;
	public static final int OPTYPE_HAS_OBS = 34;
	public static final int OPTYPE_ADD_INSP = 35;
	public static final int OPTYPE_RMV_INSP = 36;
	public static final int OPTYPE_HAS_INSP = 37;
	public static final int OPTYPE_SET_WSET = 38;
	public static final int RESET = 39;
	
//	my personal update
	
	public static final int OPTYPE_IN_ALL = 40;
	public static final int OPTYPE_RD_ALL = 41;
	public static final int OPTYPE_NO_ALL = 410;
	public static final int OPTYPE_URD = 44;
	public static final int OPTYPE_UNO = 440;
	public static final int OPTYPE_UIN = 45;
	public static final int OPTYPE_URDP = 46;
	public static final int OPTYPE_UNOP = 460;
	public static final int OPTYPE_UINP = 47;
	
//	******************
	
	protected boolean operationCompleted;
	protected OperationCompletionListener listener;

	private Tuple tupleArgument;
	private List<Tuple> tupleListArgument;
	private TupleTemplate templateArgument;
	private TCCycleResult result;	
	
	private int type;

	// internal identifier of the operation
	private long id;
	// shared id counter
	private static long idCounter = 0;

	// used for possible synchronisation
	protected Object token;
	
	private Prolog p;

	private TupleCentreOperation(Prolog p, int type)
	{
		operationCompleted = false;
		this.result = new TCCycleResult();
		this.type = type;
		token = new Object();
		id = idCounter;
		idCounter++;
		this.p = p;
	}

	protected TupleCentreOperation(Prolog p, int type, Tuple t)
	{
		this(p, type);
		listener = null;
		this.tupleArgument = t;
	}

	protected TupleCentreOperation(Prolog p, int type, TupleTemplate t)
	{
		this(p, type);
		listener = null;
		this.templateArgument = t;
	}
	
	// Aggiunta per la set
	protected TupleCentreOperation(Prolog p, int type, List<Tuple> tupleList)
	{
		this(p, type);
		listener = null;
		this.tupleListArgument = tupleList;
	}

	protected TupleCentreOperation(Prolog p, int type, Tuple t, OperationCompletionListener l)
	{
		this(p, type, t);
		listener = l;
	}

	protected TupleCentreOperation(Prolog p, int type, TupleTemplate t, OperationCompletionListener l)
	{
		this(p, type, t);
		listener = l;
	}
	
	// Aggiunta per la set
	protected TupleCentreOperation(Prolog p, int type, List<Tuple> tupleList, OperationCompletionListener l)
	{
		this(p, type, tupleList);
		listener = l;
	}

	public void setListener(OperationCompletionListener l)
	{
		this.listener = l;
	}
	
	public TupleTemplate getTemplateArgument()
	{
		return templateArgument;
	}

	public Tuple getTupleArgument()
	{
		return tupleArgument;
	}
	
	// Aggiunta per la set
	public List<Tuple> getTupleListArgument()
	{
		return tupleListArgument;
	}
	
	public List<Tuple> getTupleListResult(){
		return result.getTupleListResult();
	}
	
	public void setTupleListResult(List<Tuple> t){
		result.setTupleListResult(t);
		result.setEndTime(System.currentTimeMillis());
	}
	
	public Tuple getTupleResult(){
		return result.getTupleResult();
	}

	public void setTupleResult(Tuple t){
		result.setTupleResult(t);
		if (this.templateArgument != null)
			this.templateArgument.propagate(p, t);
		this.tupleArgument = t;
		result.setEndTime(System.currentTimeMillis());
	}
	
	public void setOpResult(Outcome o){
		result.setOpResult(o);
	}
	
	/**
	 * Tests if the result is defined
	 * 
	 * @return true if the result is defined
	 */
	public boolean isResultDefined(){
		return this.result.isResultDefined();
	}
	
	public boolean isResultSuccess(){
		return this.result.isResultSuccess();
	}
	
	public boolean isResultFailure(){
		return this.result.isResultFailure();
	}

	public boolean isIn(){
		return type == OPTYPE_IN;
	}

	public boolean isRd(){
		return type == OPTYPE_RD;
	}

	public boolean isOut(){
		return type == OPTYPE_OUT;
	}

	public boolean isInp(){
		return type == OPTYPE_INP;
	}

	public boolean isRdp(){
		return type == OPTYPE_RDP;
	}
	
	public boolean isGet(){
		return type == TupleCentreOperation.OPTYPE_GET;
	}
	
	public boolean isSet(){
		return type == TupleCentreOperation.OPTYPE_SET;
	}
	
	public boolean isNo(){
		return type == TupleCentreOperation.OPTYPE_NO;
	}
	
	public boolean isNop(){
		return type == TupleCentreOperation.OPTYPE_NOP;
	}
	
//	my personal update
	
	public boolean isInAll(){
		return type == TupleCentreOperation.OPTYPE_IN_ALL;
	}
	
	public boolean isRdAll(){
		return type == TupleCentreOperation.OPTYPE_RD_ALL;
	}
	
	public boolean isNoAll(){
		return type == TupleCentreOperation.OPTYPE_NO_ALL;
	}
	
	public boolean isUrd(){
		return type == TupleCentreOperation.OPTYPE_URD;
	}
	
	public boolean isUno(){
		return type == TupleCentreOperation.OPTYPE_UNO;
	}
	
	public boolean isUin(){
		return type == TupleCentreOperation.OPTYPE_UIN;
	}
	
	public boolean isUrdp(){
		return type == TupleCentreOperation.OPTYPE_URDP;
	}
	
	public boolean isUnop(){
		return type == TupleCentreOperation.OPTYPE_UNOP;
	}
	
	public boolean isUinp(){
		return type == TupleCentreOperation.OPTYPE_UINP;
	}
	
//	*******************

	public boolean isIn_s(){
		return type == TupleCentreOperation.OPTYPE_IN_S;
	}

	public boolean isRd_s(){
		return type == TupleCentreOperation.OPTYPE_RD_S;
	}

	public boolean isOut_s(){
		return type == TupleCentreOperation.OPTYPE_OUT_S;
	}

	public boolean isInp_s(){
		return type == TupleCentreOperation.OPTYPE_INP_S;
	}

	public boolean isRdp_s(){
		return type == TupleCentreOperation.OPTYPE_RDP_S;
	}
	
	public boolean isSet_s(){
		return type == OPTYPE_SET_SPEC;
	}

	public boolean isGet_s(){
		return type == OPTYPE_GET_SPEC;
	}
	
	public boolean isNo_s(){
		return type == TupleCentreOperation.OPTYPE_NO_S;
	}
	
	public boolean isNop_s(){
		return type == TupleCentreOperation.OPTYPE_NOP_S;
	}
	
	/**
	 * Tests if the operation is completed
	 * 
	 * @return true if the operation is completed
	 */
	public boolean isOperationCompleted()
	{
		return operationCompleted;
	}

	public int getType()
	{
		return this.type;
	}

	/**
	 * Changes the state of the operation to complete.
	 * 
	 */
	public void notifyCompletion(){
		if (listener != null){
			operationCompleted = true;
			listener.operationCompleted(this);
		}else{
			try{
				synchronized (token){
					operationCompleted = true;
					token.notifyAll();
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Wait for operation completion
	 * 
	 * Current execution flow is blocked until the operation is completed
	 */
	public void waitForOperationCompletion()
	{
		try
		{
			synchronized (token)
			{
				while (!operationCompleted)
				{
					token.wait();
				}
			}
		}
		catch (Exception ex)
		{
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
	public void waitForOperationCompletion(long ms) throws OperationTimeOutException
	{
		try
		{
			synchronized (token)
			{
				if (!operationCompleted)
				{
					token.wait(ms);
				}
				if (!operationCompleted)
				{
					throw new OperationTimeOutException();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void removeListener()
	{
		this.listener = null;
	}

	public void addListener(OperationCompletionListener listener)
	{
		this.listener = listener;
	}

	/**
	 * Get operation identifier
	 * 
	 * @return Operation identifier
	 */
	public long getId()
	{
		return id;
	}
}
