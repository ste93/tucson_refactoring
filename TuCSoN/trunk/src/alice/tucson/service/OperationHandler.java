package alice.tucson.service;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonOpId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.TucsonMsgReply;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.TCCycleResult.Outcome;
import alice.tuprolog.Prolog;
import alice.tuprolog.lib.InvalidObjectIdException;

public class OperationHandler {
	
	/**
	 * Current ACC session description
	 */
	protected ACCDescription profile;
	/**
	 * TuCSoN requests completion events (node replies events)
	 */
	protected LinkedList<TucsonOpCompletionEvent> events;
	/**
	 * Active sessions toward different nodes
	 */
	protected HashMap<String, ControllerSession> controllerSessions;
	/**
	 * Requested TuCSoN operations
	 */
	protected HashMap<Long, TucsonOperation> operations;
	/**
	 * Expired TuCSoN operations
	 */
	protected ArrayList<Long> operationExpired;
	
	
	public OperationHandler(){
		profile = new ACCDescription();
		events = new LinkedList<TucsonOpCompletionEvent>();
		controllerSessions = new HashMap<String, OperationHandler.ControllerSession>();
		operations = new HashMap<Long, TucsonOperation>();
		operationExpired = new ArrayList<Long>();
	}
	
	public void addOperation( Long id, TucsonOperation op ){
		this.operations.put( id, op );
	}
	
	public HashMap<String, ControllerSession> getControllerSessions(){
		return controllerSessions;
	}
	
	/**
	 * Private method that takes in charge execution of all the Asynchronous
	 * primitives listed above. It simply forwards real execution to another private
	 * method {@link alice.tucson.api doOperation doOp} (notice that in truth
	 * there is no real execution at this point: we are just packing primitives
	 * invocation into TuCSoN messages, then send them to the Node side)
	 * 
	 * @param type TuCSoN operation type (internal integer code)
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param t The Logic Tuple involved in the requested operation
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 */
	public ITucsonOperation doNonBlockingOperation(TucsonAgentId aid, int type, Object tid, LogicTuple t,
			TucsonOperationCompletionListener l)
					throws TucsonOperationNotPossibleException, UnreachableNodeException{
		return doNonBlockingOperation(aid, type, tid, t, null, l);
	}
	
	public ITucsonOperation doNonBlockingOperation(TucsonAgentId aid, int type, Object tid, LogicTuple t,
			List<LogicTuple> list, TucsonOperationCompletionListener l)
					throws TucsonOperationNotPossibleException, UnreachableNodeException{
		
		TucsonTupleCentreId tcid = null;
		if(tid.getClass().getName().equals("alice.tucson.api.TucsonTupleCentreId")){
			tcid = (TucsonTupleCentreId) tid;
		}else{
			try{
				tcid = new TucsonTupleCentreId(tid);
			}catch(TucsonInvalidTupleCentreIdException ex){
				System.err.println("[ACCProxyAgentSide]: " + ex);
				return null;
			}
		}
		
		try{
			return doOperation(aid, tcid, type, t, list, l);
		}catch(TucsonOperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}catch(UnreachableNodeException e){
			throw new UnreachableNodeException();
		}
		
	}
	
	public ITucsonOperation doBlockingOperation(TucsonAgentId aid, int type, Object tid, LogicTuple t, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		return doBlockingOperation(aid, type, tid, t, null, ms);
	}
	
	public ITucsonOperation doBlockingOperation(TucsonAgentId aid, int type, Object tid, LogicTuple t, List<LogicTuple> list, Long ms)
			throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{		
		
		TucsonTupleCentreId tcid = null;
		if(tid.getClass().getName().equals("alice.tucson.api.TucsonTupleCentreId")){
			tcid = (TucsonTupleCentreId) tid;
		}else{
			try{
				tcid = new TucsonTupleCentreId(tid);
			}catch(TucsonInvalidTupleCentreIdException ex){
				System.err.println("[ACCProxyAgentSide]: " + ex);
				return null;
			}
		}
		
		ITucsonOperation op = null;
		try{			
			op = doOperation(aid, tcid, type, t, list, null);
		}catch(TucsonOperationNotPossibleException e){
			throw new TucsonOperationNotPossibleException();
		}catch(UnreachableNodeException e){
			throw new UnreachableNodeException();
		}
		try{
			if(ms == null)
				op.waitForOperationCompletion();
			else
				op.waitForOperationCompletion(ms);
		}catch(OperationTimeOutException e){
			throw new OperationTimeOutException();
		}
		return op;

	}
	
	/**
	 * Method to track expired operations, that is operations whose completion
	 * has not been received before specified timeout expiration 
	 * 
	 * @param id Unique Identifier of the expired operation
	 */
	public void addOperationExpired(long id){
		this.operationExpired.add(id);
	}
	
	/**
	 * This method is the real responsible of TuCSoN operations execution.
	 * 
	 * First, it takes the target tuplecentre and checks wether this proxy has
	 * ever established a connection toward it: if it did, the already opened
	 * connection is retrieved and used, otherwise a new connection is opened
	 * and stored for later use {@link alice.tucson.service.ACCProxyAgentSide#getSession getSession}.
	 * 
	 * Then, a Tucson Operation {@link alice.tucson.service.TucsonOperation op}
	 * storing any useful information about the TuCSoN primitive invocation
	 * is created and packed into a Tucson Message Request {@link alice.tucson.network.TucsonMsgRequest}
	 * to be possibly sent over the wire toward the target tuplecentre.
	 * 
	 * Notice that a listener is needed, who is the proxy itself, wichever was the
	 * requested operation (inp, in, etc.) and despite its (a-)synchronous behavior.
	 * This is because of the distributed very nature of TuCSoN: we couldn't expect
	 * to block on a socket waiting for a reply. Instead, requested operations should be
	 * dispatched toward the TuCSoN Node Service, which in turn will take them in
	 * charge and notify the requestor upon completion.
	 * 
	 * @param tcid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * @param type TuCSoN operation type (internal integer code)
	 * @param t The Logic Tuple involved in the requested operation
	 * @param l The listener who should be notified upon operation completion
	 * 
	 * @return An object representing the primitive invocation on the TuCSoN infrastructure
	 * which will store its result
	 * 
	 * @throws TucsonOperationNotPossibleException
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.api.TucsonTupleCentreId TucsonTupleCentreId
	 * @see alice.tucson.api.TucsonOperationCompletionListener TucsonOperationCompletionListener
	 * @see alice.tucson.api.ITucsonOperation ITucsonOperation
	 * @see alice.tucson.service.TucsonOperation TucsonOperation
	 */
	public synchronized ITucsonOperation doOperation(TucsonAgentId aid, TucsonTupleCentreId tcid,
			int type, LogicTuple t, List<LogicTuple> list, TucsonOperationCompletionListener l)
					throws TucsonOperationNotPossibleException, UnreachableNodeException{				

		int nTry = 0;
		boolean exception;
		
		do{
			
			nTry++;
			exception = false;
			
			TucsonProtocol session = null;
			try{
				session = getSession(tcid, aid);
			}catch(UnreachableNodeException ex2){
				exception = true;
				throw new UnreachableNodeException();
			}
			ObjectOutputStream outStream = session.getOutputStream();

			TucsonOperation op = null;
			if((type == TucsonOperation.outCode()) || (type == TucsonOperation.out_sCode())
					|| (type == TucsonOperation.set_sCode()) || (type == TucsonOperation.set_Code())
					|| type == TucsonOperation.out_allCode() || type == TucsonOperation.spawnCode()
					|| type == TucsonOperation.setEnvCode())
				op = new TucsonOperation(type, (Tuple) t, l, this);
			else 
				op = new TucsonOperation(type, (TupleTemplate) t, l, this);
			
			operations.put(op.getId(), op);
			TucsonMsgRequest msg = new TucsonMsgRequest(op.getId(), op.getType(), tcid.toString(),
					op.getLogicTupleArgument());
			log("requesting op " + msg.getType() + ", " + msg.getTuple() + ", " + msg.getTid());
			
			try{
				TucsonMsgRequest.write(outStream, msg);
				outStream.flush();
			}catch(IOException ex){
				exception = true;
				System.err.println("[ACCProxyAgentSide]: " + ex);
			}
			
			if(!exception)
				return op;
			
		}while(nTry < 3);
		
		throw new UnreachableNodeException();
		
	}

	/**
	 * This method is responsible to setup, store and retrieve connections toward
	 * all the tuplecentres ever contacted by the TuCSoN Agent behind this proxy.
	 * 
	 * If a connection toward the given target tuplecentre already exists, it is
	 * retrieved and used. If not, the new connection is setup then stored for later use.
	 * 
	 * It is worth noting a couple of things. Why don't we setup connections
	 * once and for all as soon as the TuCSoN Agent is booted? The reason is that
	 * new tuplecentres can be created at run-time as TuCSoN Agents please, thus for every
	 * TuCSoN Operation request we should check wether a new tuplecentre has to be 
	 * created and booted. If a new tuplecentre has to be booted the correspondant
	 * proxy node side is dinamically triggered and booted {@link alice.tucson.service.ACCProxyNodeSide
	 * nodeProxy} 
	 * 
	 * @param tid Target TuCSoN tuplecentre id {@link alice.tucson.api.TucsonTupleCentreId tid}
	 * 
	 * @return The open session toward the given target tuplecentre
	 * 
	 * @throws UnreachableNodeException
	 * 
	 * @see alice.tucson.network.TucsonProtocol TucsonProtocol
	 * @see alice.tucson.service.ACCProxyNodeSide ACCProxyNodeSide
	 */
	public TucsonProtocol getSession(TucsonTupleCentreId tid, TucsonAgentId aid ) throws UnreachableNodeException{				
		
		String opNode = alice.util.Tools.removeApices(tid.getNode());
		int port = tid.getPort();
//		log(opNode+":"+port);
		ControllerSession tc = controllerSessions.get(opNode+":"+port);
		if(tc != null)
			return tc.getSession();
		else{
			if(opNode.equals("localhost"))
				tc = controllerSessions.get("127.0.0.1:"+port);
			if(opNode.equals("127.0.0.1"))
				tc = controllerSessions.get("localhost:"+port);
			if(tc != null)
				return tc.getSession();
		}
			
		profile.setProperty("agent-identity", aid.toString());
		profile.setProperty("agent-role", "user");

		TucsonProtocol dialog = null;
		boolean isEnterReqAcpt = false;
		try{
			dialog = new TucsonProtocolTCP(opNode, port);
			dialog.sendEnterRequest(profile);
			dialog.receiveEnterRequestAnswer();
			if(dialog.isEnterRequestAccepted())
				isEnterReqAcpt = true;
		}catch(Exception ex){
			throw new UnreachableNodeException();
		}								

		if(isEnterReqAcpt){
			ObjectInputStream din = dialog.getInputStream();
			Controller contr = new Controller(din);
			ControllerSession cs = new ControllerSession(contr, dialog);
			controllerSessions.put(opNode+":"+port, cs);
			contr.start();
			return dialog;
		}
		
		return null;
		
	}

	/**
	 * Method to add a TuCSoN Operation Completion Event {@link alice.tucson.service.TucsonOpCompletionEvent
	 * event} to the internal queue of pending completion event to process
	 * 
	 * @param ev Completion Event to be added to pending queue
	 * 
	 * @see alice.tucson.service.TucsonOpCompletionEvent TucsonOpCompletionEvent
	 */
	public void postEvent(TucsonOpCompletionEvent ev){
		synchronized(events){
			events.addLast(ev);
		}
	}

	/**
	 * Method internally used to log proxy activity (could be used for debug)
	 * 
	 * @param msg String to display on the standard output
	 */
	private void log(String msg){
		System.out.println("[ACCProxyAgentSide]: " + msg);
	}
	
	/**
	 * 
	 */
	public class Controller extends Thread{
		
		private boolean stop;
		private ObjectInputStream in;
		private final Prolog p = new Prolog();

		/**
		 * 
		 * @param in
		 */
		Controller(ObjectInputStream in){
			
			this.in = in;
			stop = false;
			this.setDaemon(true);
			
			alice.tuprolog.lib.JavaLibrary jlib = (alice.tuprolog.lib.JavaLibrary) p.getLibrary("alice.tuprolog.lib.JavaLibrary");
			try{
				jlib.register(new alice.tuprolog.Struct("config"), this);
			}catch(InvalidObjectIdException ex){
				System.err.println("[ACCProxyAgentSide] Controller: " + ex);
				ex.printStackTrace();
			}
			
		}

		/**
		 * 
		 */
		@SuppressWarnings("unchecked")
		public void run(){
			
			TucsonOpCompletionEvent ev = null;
			while(!isStopped()){
				
				for(Long operation : operationExpired)
					operations.remove(operation);
				
				TucsonMsgReply msg = null;
				try{
					msg = TucsonMsgReply.read(in);
				}catch(EOFException e){
					log("TuCSoN node service unavailable, nothing I can do");
					setStop();
					break;
				}catch(Exception ex){
					setStop();
					System.err.println("[ACCProxyAgentSide] Controller: " + ex);
				}

				boolean ok = msg.isAllowed();
				if(ok){
					
					int type = msg.getType();
					if(type == TucsonOperation.uinCode() || type == TucsonOperation.uinpCode()
							|| type == TucsonOperation.urdCode() || type == TucsonOperation.urdpCode()
							|| type == TucsonOperation.unoCode() || type == TucsonOperation.unopCode()
							|| type == TucsonOperation.noCode() || type == TucsonOperation.no_sCode()
							|| type == TucsonOperation.nopCode() || type == TucsonOperation.nop_sCode()
							|| type == TucsonOperation.inCode() || type == TucsonOperation.rdCode()
							|| type == TucsonOperation.inpCode() || type == TucsonOperation.rdpCode()
							|| type == TucsonOperation.in_sCode() || type == TucsonOperation.rd_sCode()
							|| type == TucsonOperation.inp_sCode() || type == TucsonOperation.rdp_sCode()){

						boolean succeeded = msg.isSuccess();
						if(succeeded){
							
							LogicTuple tupleReq = msg.getTupleRequested();
							LogicTuple tupleRes = (LogicTuple) msg.getTupleResult();
//							log("tupleReq="+tupleReq+", tupleRes="+tupleRes);
							LogicTuple res = unify(tupleReq, tupleRes);
							ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, true, res);
							
						}else{
							ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, false);
						}
						
					}else if(type == TucsonOperation.set_Code() || type == TucsonOperation.set_sCode()
							|| type == TucsonOperation.outCode() || type == TucsonOperation.out_sCode()
							|| type == TucsonOperation.out_allCode() || type == TucsonOperation.spawnCode()
							|| type == TucsonOperation.getEnvCode() || type == TucsonOperation.setEnvCode()){
						ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, msg.isSuccess());
					}else if(type == TucsonOperation.in_allCode() || type == TucsonOperation.rd_allCode()
							|| type == TucsonOperation.no_allCode()
							|| type == TucsonOperation.get_Code() || type == TucsonOperation.get_sCode()){
						List<LogicTuple> tupleSetRes = (List<LogicTuple>) msg.getTupleResult();
						ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), ok, msg.isSuccess(), tupleSetRes);
					}else if(type == TucsonOperation.exitCode()){
						setStop();
						break;
					}
					
				}else{
					ev = new TucsonOpCompletionEvent(new TucsonOpId(msg.getId()), false, false);
				}
				
				TucsonOperation op = operations.remove(msg.getId());
				if(op.isNoAll() || op.isInAll() || op.isRdAll() || op.isGet() || op.isSet() || op.isGet_s()
						|| op.isSet_s() || op.isOutAll()){
					op.setLogicTupleListResult((List<LogicTuple>) msg.getTupleResult());
				}else{
					op.setTupleResult((LogicTuple) msg.getTupleResult());
				}
				if(msg.isResultSuccess()){
					op.setOpResult(Outcome.SUCCESS);
				}else
					op.setOpResult(Outcome.FAILURE);
				op.notifyCompletion(ev.operationSucceeded(), msg.isAllowed());
				postEvent(ev);
				
			}
			
		}

		/**
		 * 
		 * @return
		 */
		public synchronized boolean isStopped(){
			return stop;
		}

		/**
		 * 
		 */
		public synchronized void setStop(){
			stop = true;
		}

		/**
		 * 
		 * @param template
		 * @param tuple
		 * @return
		 */
		LogicTuple unify(TupleTemplate template, Tuple tuple){
			boolean res = template.propagate(p, tuple);
			if(res)
				return (LogicTuple) template;
			else
				return null;

		}
		
	}

	/**
	 * 
	 */
	public class ControllerSession{
		
		private Controller controller;
		private TucsonProtocol session;

		/**
		 * 
		 * @param c
		 * @param s
		 */
		ControllerSession(Controller c, TucsonProtocol s){
			controller = c;
			session = s;
		}

		/**
		 * 
		 * @return
		 */
		public Controller getController(){
			return controller;
		}

		/**
		 * 
		 * @return
		 */
		public TucsonProtocol getSession(){
			return session;
		}
	}
}
