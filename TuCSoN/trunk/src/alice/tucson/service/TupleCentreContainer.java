package alice.tucson.service;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.respect.api.AgentId;
import alice.respect.api.IOrdinarySynchInterface;
import alice.respect.api.ISpecificationSynchInterface;
import alice.respect.api.IManagementContext;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.RespectSpecification;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InstantiationNotPossibleException;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.api.exceptions.OperationNotPossibleException;

import alice.respect.core.RespectTC;
import alice.respect.core.SpecificationSynchInterface;
import alice.respect.core.RespectTCContainer;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidSpecificationException;
import alice.tucson.api.exceptions.TCInstantiationNotPossibleException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

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
		
	public static RespectTC createTC(TucsonTupleCentreId id, int q, int def_port) throws TCInstantiationNotPossibleException, InvalidTupleCentreIdException{
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
		IOrdinaryAsynchInterface context = null;
		try{
//			log("(TupleCentreId) tid.getInternalTupleCentreId() = " + ((TupleCentreId) tid.getInternalTupleCentreId()));
			context = (RespectTCContainer.getRespectTCContainer()).getOrdinaryAsynchInterface((TupleCentreId) tid.getInternalTupleCentreId());
			if(type == TucsonOperation.spawnCode()){
				return context.spawn((AgentId) aid.getAgentId(), t, l);
			}if(type == TucsonOperation.outCode())
				return context.out((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.inCode())
				return context.in((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.inpCode())
				return context.inp((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.rdCode())
				return context.rd((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.rdpCode())
				return context.rdp((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.noCode())
				return context.no((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.nopCode())
				return context.nop((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.get_Code())
				return context.get((AgentId) aid.getAgentId(), l);
			if(type == TucsonOperation.set_Code())
				return context.set((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.out_allCode())
				return context.out_all((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.in_allCode())
				return context.in_all((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.rd_allCode())
				return context.rd_all((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.no_allCode())
				return context.no_all((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.uinCode())
				return context.uin((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.uinpCode())
				return context.uinp((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.urdCode())
				return context.urd((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.urdpCode())
				return context.urdp((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.unoCode())
				return context.uno((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.unopCode())
				return context.unop((AgentId) aid.getAgentId(), t, l);
		}catch(InvalidLogicTupleException e){
			throw new TucsonInvalidLogicTupleException();
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}
		return res;
	}
	
	public static ITupleCentreOperation doNonBlockingOperation(int type, TucsonTupleCentreId tcid, TucsonTupleCentreId tid, LogicTuple t, OperationCompletionListener l) throws TucsonInvalidLogicTupleException, TucsonOperationNotPossibleException{			
		ITupleCentreOperation res = null;
		IOrdinaryAsynchInterface context = null;
		try{
//			log("(TupleCentreId) tid.getInternalTupleCentreId() = " + ((TupleCentreId) tid.getInternalTupleCentreId()));
			context = (RespectTCContainer.getRespectTCContainer()).getOrdinaryAsynchInterface((TupleCentreId) tid.getInternalTupleCentreId());
			if(type == TucsonOperation.spawnCode()){
				return context.spawn(tcid, t, l);
			}if(type == TucsonOperation.outCode())
				return context.out(tcid, t, l);
			if(type == TucsonOperation.inCode())
				return context.in(tcid, t, l);
			if(type == TucsonOperation.inpCode())
				return context.inp(tcid, t, l);
			if(type == TucsonOperation.rdCode())
				return context.rd(tcid, t, l);
			if(type == TucsonOperation.rdpCode())
				return context.rdp(tcid, t, l);
			if(type == TucsonOperation.noCode())
				return context.no(tcid, t, l);
			if(type == TucsonOperation.nopCode())
				return context.nop(tcid, t, l);
			if(type == TucsonOperation.get_Code())
				return context.get(tcid, l);
			if(type == TucsonOperation.set_Code())
				return context.set(tcid, t, l);
			if(type == TucsonOperation.out_allCode())
				return context.out_all(tcid, t, l);
			if(type == TucsonOperation.in_allCode())
				return context.in_all(tcid, t, l);
			if(type == TucsonOperation.rd_allCode())
				return context.rd_all(tcid, t, l);
			if(type == TucsonOperation.no_allCode())
				return context.no_all(tcid, t, l);
			if(type == TucsonOperation.uinCode())
				return context.uin(tcid, t, l);
			if(type == TucsonOperation.uinpCode())
				return context.uinp(tcid, t, l);
			if(type == TucsonOperation.urdCode())
				return context.urd(tcid, t, l);
			if(type == TucsonOperation.urdpCode())
				return context.urdp(tcid, t, l);
			if(type == TucsonOperation.unoCode())
				return context.uno(tcid, t, l);
			if(type == TucsonOperation.unopCode())
				return context.unop(tcid, t, l);
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
		ISpecificationAsynchInterface context = null;
		try{
			context = (RespectTCContainer.getRespectTCContainer()).getSpecificationAsynchInterface((TupleCentreId) tid.getInternalTupleCentreId());			
			if(type == TucsonOperation.no_sCode())
				return context.no_s((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.nop_sCode())
				return context.nop_s((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.out_sCode())
				return context.out_s((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.in_sCode())
				return context.in_s((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.inp_sCode())
				return context.inp_s((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.rd_sCode())
				return context.rd_s((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.rdp_sCode())
				return context.rdp_s((AgentId) aid.getAgentId(), t, l);
			if(type == TucsonOperation.get_sCode())
				return context.get_s((AgentId) aid.getAgentId(), l);
			if(type == TucsonOperation.set_sCode())
				return context.set_s((AgentId) aid.getAgentId(), new RespectSpecification(t.toString()), l);
		}catch(InvalidLogicTupleException e){
			throw new TucsonInvalidLogicTupleException();
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}
		return res;
	}
	
	public static ITupleCentreOperation doNonBlockingSpecOperation(int type, TucsonTupleCentreId tcid, TucsonTupleCentreId tid, LogicTuple t, OperationCompletionListener l) throws TucsonInvalidLogicTupleException, TucsonOperationNotPossibleException{
		ITupleCentreOperation res = null;
		ISpecificationAsynchInterface context = null;
		try{
			context = (RespectTCContainer.getRespectTCContainer()).getSpecificationAsynchInterface((TupleCentreId) tid.getInternalTupleCentreId());			
			if(type == TucsonOperation.no_sCode())
				return context.no_s(tcid, t, l);
			if(type == TucsonOperation.nop_sCode())
				return context.nop_s(tcid, t, l);
			if(type == TucsonOperation.out_sCode())
				return context.out_s(tcid, t, l);
			if(type == TucsonOperation.in_sCode())
				return context.in_s(tcid, t, l);
			if(type == TucsonOperation.inp_sCode())
				return context.inp_s(tcid, t, l);
			if(type == TucsonOperation.rd_sCode())
				return context.rd_s(tcid, t, l);
			if(type == TucsonOperation.rdp_sCode())
				return context.rdp_s(tcid, t, l);
			if(type == TucsonOperation.get_sCode())
				return context.get_s(tcid, l);
			if(type == TucsonOperation.set_sCode())
				return context.set_s(tcid, new RespectSpecification(t.toString()), l);
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
		IOrdinarySynchInterface context = null;
		try{
			context = (IOrdinarySynchInterface) (RespectTCContainer.getRespectTCContainer()).getOrdinarySynchInterface((TupleCentreId) tid.getInternalTupleCentreId());			
//			if(type == TucsonOperation.spawnCode())
//				context.spawn((AgentId) aid.getLocalAgentId(), (LogicTuple) o);
			if(type == TucsonOperation.get_Code())
				return context.get((AgentId) aid.getAgentId());
			if(type == TucsonOperation.set_Code()){
				return context.set((AgentId) aid.getAgentId(), (LogicTuple) o);
			}
			if(type == TucsonOperation.outCode()){
				context.out((AgentId) aid.getAgentId(), (LogicTuple)o);
				return o;
			}if(type == TucsonOperation.inCode())
				return context.in((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.inpCode())
				return context.inp((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.rdCode())
				return context.rd((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.rdpCode())
				return context.rdp((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.noCode())
				return context.no((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.nopCode())
				return context.nop((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.out_allCode())
				return context.out_all((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.in_allCode())
				return context.in_all((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.rd_allCode())
				return context.rd_all((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.no_allCode())
				return context.no_all((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.uinCode())
				return context.uin((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.urdCode())
				return context.uin((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.unoCode())
				return context.uin((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.uinpCode())
				return context.uin((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.urdpCode())
				return context.uin((AgentId) aid.getAgentId(), (LogicTuple)o);
			if(type == TucsonOperation.unopCode())
				return context.uin((AgentId) aid.getAgentId(), (LogicTuple)o);
		}catch(InvalidLogicTupleException e){
			throw new TucsonInvalidLogicTupleException();
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}
		return null;
	}
	
	/**
	 * SPAWN ADDITION
	 * 
	 * @param type
	 * @param aid
	 * @param tid
	 * @param t
	 * 
	 * @return
	 * 
	 * @throws TucsonInvalidLogicTupleException
	 * @throws TucsonOperationNotPossibleException
	 */
	public static Object doBlockingOperation(int type,
			TucsonTupleCentreId aid, TucsonTupleCentreId tid, LogicTuple t)
					throws TucsonInvalidLogicTupleException,
					TucsonOperationNotPossibleException{			
		IOrdinarySynchInterface context = null;
		try{
			context = RespectTCContainer.getRespectTCContainer().getOrdinarySynchInterface((TupleCentreId) tid.getInternalTupleCentreId());
			if(type == TucsonOperation.outCode()){
				context.out((TupleCentreId) aid.getInternalTupleCentreId(), t);
				return t;
			}if(type == TucsonOperation.inCode())
				return context.in((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.rdCode())
				return context.rd((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.noCode())
				return context.no((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.inpCode())
				return context.inp((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.rdpCode())
				return context.rdp((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.nopCode())
				return context.nop((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.out_allCode())
				return context.out_all((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.in_allCode())
				return context.in_all((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.rd_allCode())
				return context.rd_all((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.no_allCode())
				return context.no_all((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.uinCode())
				return context.uin((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.urdCode())
				return context.uin((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.unoCode())
				return context.uin((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.uinpCode())
				return context.uin((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.urdpCode())
				return context.uin((TupleCentreId) aid.getInternalTupleCentreId(), t);
			if(type == TucsonOperation.unopCode())
				return context.uin((TupleCentreId) aid.getInternalTupleCentreId(), t);
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
		ISpecificationSynchInterface context = null;
		try{
			context = (ISpecificationSynchInterface) (RespectTCContainer.getRespectTCContainer()).getSpecificationSynchInterface((TupleCentreId) tid.getInternalTupleCentreId());
			if(type == TucsonOperation.set_sCode()){
//				if(aid.toString().equals("node_agent") || aid.toString().startsWith("inspector_edit_spec_")){
				if(t.getName().equals("spec")){
//					log("t = " + t);
					return ((SpecificationSynchInterface)context).set_s((AgentId) aid.getAgentId(), new RespectSpecification(((LogicTuple) t).getArg(0).getName()));
				}
				return ((SpecificationSynchInterface)context).set_s((AgentId) aid.getAgentId(), t);
			}
			if(type == TucsonOperation.get_sCode()){
				return ((SpecificationSynchInterface)context).get_s((AgentId) aid.getAgentId());
			}
		}catch(OperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}catch(InvalidSpecificationException e){
			throw new TucsonInvalidSpecificationException();
		}catch(InvalidTupleOperationException e){
			throw new TucsonOperationNotPossibleException();
		}	
		return res;
	}
	
	public static Object doBlockingSpecOperation(int type, TucsonTupleCentreId tcid, TucsonTupleCentreId tid, LogicTuple t) throws TucsonInvalidLogicTupleException, TucsonOperationNotPossibleException, TucsonInvalidSpecificationException{
		LogicTuple res = null;		
		ISpecificationSynchInterface context = null;
		try{
			context = (ISpecificationSynchInterface) (RespectTCContainer.getRespectTCContainer()).getSpecificationSynchInterface((TupleCentreId) tid.getInternalTupleCentreId());
			if(type == TucsonOperation.set_sCode()){
//				if(aid.toString().equals("node_agent") || aid.toString().startsWith("inspector_edit_spec_")){
				if(t.getName().equals("spec")){
//					log("t = " + t);
					return ((SpecificationSynchInterface)context).set_s(tcid, new RespectSpecification(((LogicTuple) t).getArg(0).getName()));
				}
				return ((SpecificationSynchInterface)context).set_s(tcid, t);
			}
			if(type == TucsonOperation.get_sCode()){
				return ((SpecificationSynchInterface)context).get_s(tcid);
			}
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
