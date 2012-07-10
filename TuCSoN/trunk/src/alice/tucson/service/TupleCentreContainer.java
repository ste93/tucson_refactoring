package alice.tucson.service;

import alice.logictuple.InvalidLogicTupleException;
import alice.logictuple.InvalidTupleOperationException;
import alice.logictuple.LogicTuple;

import alice.respect.api.AgentId;
import alice.respect.api.IBlockingContext;
import alice.respect.api.IBlockingSpecContext;
import alice.respect.api.IManagementContext;
import alice.respect.api.INonBlockingContext;
import alice.respect.api.INonBlockingSpecContext;
import alice.respect.api.InstantiationNotPossibleException;
import alice.respect.api.InvalidSpecificationException;
import alice.respect.api.InvalidTupleCentreIdException;
import alice.respect.api.OperationNotPossibleException;
import alice.respect.api.RespectSpecification;
import alice.respect.api.TupleCentreId;

import alice.respect.core.BlockingSpecContext;
import alice.respect.core.RespectTCContainer;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidSpecificationException;
import alice.tucson.api.exceptions.TCInstantiationNotPossibleException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

import alice.tucson.parsing.RespectReactionParser;
import alice.tucson.service.TucsonOperation;

import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.api.ObservableEventListener;

import alice.tuplecentre.core.OperationCompletionListener;

import java.util.List;

/**
 * 
 */
public class TupleCentreContainer{
	
	public static final int RESPECTQUEUE = 10;
	private static int defaultport;
		
	public static boolean createTC(TucsonTupleCentreId id, int q, int def_port) throws TCInstantiationNotPossibleException, InvalidTupleCentreIdException{
		defaultport = def_port;
		try{
			RespectTCContainer rtcc = RespectTCContainer.getRespectTCContainer();
			rtcc.setDefPort(defaultport);
			TupleCentreId tid = new TupleCentreId(id.getName(), id.getNode(), ""+id.getPort());
//			log("tid = " + tid);
			return rtcc.createRespectTC(tid, q);
		}catch(InstantiationNotPossibleException e){
			throw new TCInstantiationNotPossibleException();
		} catch (InvalidTupleCentreIdException e) {
			throw new InvalidTupleCentreIdException();
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
//	if we follow @deprecated instructions we should get a <...>Context insted of a RespectTC,
//	but how to decide which? who decides? based upon what?
	public static Object getTC(TucsonTupleCentreId id){ 	
		try{
			return (RespectTCContainer.getRespectTCContainer()).getTC((TupleCentreId)id.getInternalTupleCentreId());
		}catch (InstantiationNotPossibleException e){
			System.err.println("[TupleCentreContainer]: " + e);
			return null;
		}
	 }	 
	
	/**
	 * 
	 * @param type
	 * @param tid
	 * @param obj
	 * @return
	 * @throws TucsonOperationNotPossibleException
	 * @throws TucsonInvalidLogicTupleException
	 */
	public static Object doManagementOperation(int type, TucsonTupleCentreId tid, Object obj) throws TucsonOperationNotPossibleException, TucsonInvalidLogicTupleException{
		
		IManagementContext context = null;
		try{
			context = (RespectTCContainer.getRespectTCContainer()).getManagementContext((TupleCentreId) tid.getInternalTupleCentreId());
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}
		
		if(type == TucsonOperation.abortOpCode())
			return context.abortOperation((Long)obj);
		
		if(type == TucsonOperation.set_sCode()){
			try{
				context.setSpec(new RespectSpecification(((LogicTuple) obj).getArg(0).getName()));
				return true;
			}catch(InvalidSpecificationException e){
				System.err.println("[TupleCentreContainer]: " + e);
				e.printStackTrace();
				return false;
			}catch(InvalidTupleOperationException e){
				System.err.println("[TupleCentreContainer]: " + e);
				e.printStackTrace();
				return false;
			}
		}
		if(type == TucsonOperation.get_sCode())
			return new LogicTuple(context.getSpec().toString());
		
		if(type == TucsonOperation.getTRSetCode())
			return context.getTRSet((LogicTuple) obj);
		if(type == TucsonOperation.getTSetCode())
			return context.getTSet((LogicTuple) obj);
		if(type == TucsonOperation.getWSetCode())
			return context.getWSet((LogicTuple) obj);
		if(type == TucsonOperation.setWSetCode()){
			context.setWSet((List<LogicTuple>) obj);
			return true;
		}
		
		if(type == TucsonOperation.goCmdCode()){
			try{
				context.goCommand();
				return true;
			}catch(OperationNotPossibleException e){
				System.err.println("[TupleCentreContainer]: " + e);
				e.printStackTrace();
				return false;
			}
		}
		if(type == TucsonOperation.stopCmdCode()){
			try{
				context.stopCommand();
				return true;
			}catch(OperationNotPossibleException e){
				System.err.println("[TupleCentreContainer]: " + e);
				e.printStackTrace();
				return false;
			}
		}
		if(type == TucsonOperation.nextStepCode()){
			try{
				context.nextStepCommand();
				return true;
			}catch(OperationNotPossibleException e){
				System.err.println("[TupleCentreContainer]: " + e);
				e.printStackTrace();
				return false;
			}
		}
		
		if(type == TucsonOperation.setMngModeCode()){
			context.setManagementMode((Boolean)obj);
			return true;
		}
		if(type == TucsonOperation.setSpyCode()){
			context.setSpy((Boolean)obj);
			return true;
		}
		
		if(type == TucsonOperation.addObsCode()){
			context.addObserver((ObservableEventListener)obj);
			return true;
		}
		if(type == TucsonOperation.rmvObsCode()){
			context.removeObserver((ObservableEventListener)obj);
			return true;
		}
		if(type == TucsonOperation.hasObsCode())
			return context.hasObservers();
		if(type == TucsonOperation.addInspCode()){
			context.addInspector((InspectableEventListener)obj);
			return true;
		}
		
		if(type == TucsonOperation.rmvInspCode()){
			context.removeInspector((InspectableEventListener)obj);
			return true;
		}
		if(type == TucsonOperation.hasInspCode())
			return context.hasInspectors();

//		I don't think this is finished...
		if(type == TucsonOperation.reset())
			return context.hasInspectors();
		
		return null;
		
	}
	
	/**
	 * 
	 * @param type
	 * @param aid
	 * @param tid
	 * @param t
	 * @param l
	 * @return
	 * @throws TucsonInvalidLogicTupleException
	 * @throws TucsonOperationNotPossibleException
	 */
	public static ITupleCentreOperation doNonBlockingOperation(int type, TucsonAgentId aid, TucsonTupleCentreId tid, LogicTuple t, OperationCompletionListener l) throws TucsonInvalidLogicTupleException, TucsonOperationNotPossibleException{			
		ITupleCentreOperation res = null;
		INonBlockingContext context = null;
		try{
//			log("(TupleCentreId) tid.getInternalTupleCentreId() = " + ((TupleCentreId) tid.getInternalTupleCentreId()));
			context = (RespectTCContainer.getRespectTCContainer()).getNonBlockingContext((TupleCentreId) tid.getInternalTupleCentreId());
			if(type == TucsonOperation.noCode())
				return context.no((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.nopCode())
				return context.nop((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.outCode())
				return context.out((AgentId) aid.getLocalAgentId(), t, l);
//			MODIFIED BY <s.mariani@unibo.it>
			if(type == TucsonOperation.in_allCode())
				return context.in_all((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.rd_allCode())
				return context.rd_all((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.uinCode())
				return context.uin((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.uinpCode())
				return context.uinp((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.urdCode())
				return context.urd((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.urdpCode())
				return context.urdp((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.inCode())
				return context.in((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.inpCode())
				return context.inp((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.rdCode())
				return context.rd((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.rdpCode())
				return context.rdp((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.get_Code())
				return context.get((AgentId) aid.getLocalAgentId(), l);
			if(type == TucsonOperation.set_Code())
				return context.set((AgentId) aid.getLocalAgentId(), t, l);
		}catch(InvalidLogicTupleException e){
			throw new TucsonInvalidLogicTupleException();
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}
		return res;
	}

	/**
	 * 
	 * @param type
	 * @param aid
	 * @param tid
	 * @param t
	 * @param l
	 * @return
	 * @throws TucsonInvalidLogicTupleException
	 * @throws TucsonOperationNotPossibleException
	 */
	public static ITupleCentreOperation doNonBlockingSpecOperation(int type, TucsonAgentId aid, TucsonTupleCentreId tid, LogicTuple t, OperationCompletionListener l) throws TucsonInvalidLogicTupleException, TucsonOperationNotPossibleException{
		ITupleCentreOperation res = null;
		INonBlockingSpecContext context = null;
		try{
			context = (RespectTCContainer.getRespectTCContainer()).getNonBlockingSpecContext((TupleCentreId) tid.getInternalTupleCentreId());			
			if(type == TucsonOperation.no_sCode())
				return context.no_s((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.nop_sCode())
				return context.nop_s((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.out_sCode())
				return context.out_s((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.in_sCode())
				return context.in_s((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.inp_sCode())
				return context.inp_s((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.rd_sCode())
				return context.rd_s((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.rdp_sCode())
				return context.rdp_s((AgentId) aid.getLocalAgentId(), t, l);
			if(type == TucsonOperation.get_sCode())
				return context.get_s((AgentId) aid.getLocalAgentId(), l);
			if(type == TucsonOperation.set_sCode())
				return context.set_s((AgentId) aid.getLocalAgentId(), new RespectSpecification(t.toString()), l);
		}catch(InvalidLogicTupleException e){
			throw new TucsonInvalidLogicTupleException();
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}
		return res;
	}

	/**
	 * 
	 * @param type
	 * @param aid
	 * @param tid
	 * @param o
	 * @return
	 * @throws TucsonInvalidLogicTupleException
	 * @throws TucsonOperationNotPossibleException
	 */
	public static Object doBlockingOperation(int type, TucsonAgentId aid, TucsonTupleCentreId tid, Object o) throws TucsonInvalidLogicTupleException, TucsonOperationNotPossibleException{
		IBlockingContext context = null;
		try{
			context = (IBlockingContext) (RespectTCContainer.getRespectTCContainer()).getBlockingContext((TupleCentreId) tid.getInternalTupleCentreId());			
			if(type == TucsonOperation.outCode()){
				context.out((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
				return o;
			}
			if(type == TucsonOperation.inCode())
				return context.in((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			if(type == TucsonOperation.inpCode())
				return context.inp((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			if(type == TucsonOperation.rdCode())
				return context.rd((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			if(type == TucsonOperation.rdpCode())
				return context.rdp((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			if(type == TucsonOperation.noCode())
				return context.no((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			if(type == TucsonOperation.nopCode())
				return context.nop((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			if(type == TucsonOperation.get_Code())
				return context.get((AgentId) aid.getLocalAgentId());
			if(type == TucsonOperation.set_Code()){
				return context.set((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			}
		}catch(InvalidLogicTupleException e){
			throw new TucsonInvalidLogicTupleException();
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}
		return null;
	}

	/**
	 * 
	 * @param type
	 * @param aid
	 * @param tid
	 * @param t
	 * @return
	 * @throws TucsonInvalidLogicTupleException
	 * @throws TucsonOperationNotPossibleException
	 * @throws TucsonInvalidSpecificationException
	 */
	public static Object doBlockingSpecOperation(int type, TucsonAgentId aid, TucsonTupleCentreId tid, LogicTuple t) throws TucsonInvalidLogicTupleException, TucsonOperationNotPossibleException, TucsonInvalidSpecificationException{
		LogicTuple res = null;		
		IBlockingSpecContext context = null;
		try{
			context = (IBlockingSpecContext) (RespectTCContainer.getRespectTCContainer()).getBlockingSpecContext((TupleCentreId) tid.getInternalTupleCentreId());
			if(type == TucsonOperation.out_sCode()){
				context.out_s((AgentId) aid.getLocalAgentId(), (LogicTuple) t);
				return res;
			}
			if(type == TucsonOperation.in_sCode())
				return context.in_s((AgentId) aid.getLocalAgentId(), (LogicTuple) t);
			if(type == TucsonOperation.inp_sCode())
				return context.inp_s((AgentId) aid.getLocalAgentId(), (LogicTuple) t);
			if(type == TucsonOperation.rd_sCode())
				return context.rd_s((AgentId) aid.getLocalAgentId(), (LogicTuple) t);
			if(type == TucsonOperation.rdp_sCode())
				return context.rdp_s((AgentId) aid.getLocalAgentId(), (LogicTuple) t);
			if(type == TucsonOperation.no_sCode())
				return context.no_s((AgentId) aid.getLocalAgentId(), (LogicTuple) t);
			if(type == TucsonOperation.nop_sCode())
				return context.nop_s((AgentId) aid.getLocalAgentId(), (LogicTuple) t);
			if(type == TucsonOperation.set_sCode()){
//				if(aid.toString().equals("node_agent") || aid.toString().startsWith("inspector_edit_spec_")){
				if(t.getName().equals("spec")){
					((BlockingSpecContext)context).set_s((AgentId) aid.getLocalAgentId(), new RespectSpecification(((LogicTuple) t).getArg(0).getName()));
					return res;
				}
				return ((BlockingSpecContext)context).set_s((AgentId) aid.getLocalAgentId(), t);
			}
			if(type == TucsonOperation.get_sCode()){
				return ((BlockingSpecContext)context).get_s((AgentId) aid.getLocalAgentId());
			}
		}catch(InvalidLogicTupleException e){
			throw new TucsonInvalidLogicTupleException();
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}catch(InvalidSpecificationException e){
			throw new TucsonInvalidSpecificationException();
		}catch(InvalidTupleOperationException e){
			throw new TucsonOperationNotPossibleException();
		}	
		return res;
	}

//	why are these methods not implemented yet?
	public static synchronized void enablePersistence(TucsonTupleCentreId tid, String path){
		
	}

	public static synchronized void disablePersistence(TucsonTupleCentreId tid){
		
	}

	public static void loadPersistentInformation(TucsonTupleCentreId tid, String fileName){
		
	}

	public static synchronized void destroyTC(TucsonTupleCentreId tid){
		
	}

	private static void log(String m){
		System.out.println("[TupleCentreContainer]: " + m);
	}
	
}
