package alice.respect.transducer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;


import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.respect.core.InternalEvent;
import alice.respect.core.RespectOperation;
import alice.respect.probe.ProbeId;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.service.OperationHandler;
import alice.tucson.service.TucsonOperation;

/**
 * 
 * This class implements some of common behaviors of transducers and defines some methods
 * to offer the essential interface to the users. To make a specific transducer you'll need to
 * extend this one and defining the behavior needed for the application.
 * 
 * @author Steven Maraldi
 * 
 */
public abstract class Transducer implements TransducerStandardInterface, TucsonOperationCompletionListener {
	
	/** Transducer's identifier **/
	protected TransducerId id;
	/** Identifier of the tuple centre associated **/
	protected TupleCentreId tcId;
	
	/** Class used to perform requested operation to the tuple centre **/
	protected OperationHandler executor;
	
	/** List of probes associated to the transducer **/
	protected HashMap< ProbeId, Object > probes;
	
	/**
	 * Constructs a transducer
	 * 
	 * @param id
	 * 		the transducer's identifier
	 * @param tcId
	 * 		the associated tuple centre's identifier
	 * @param probeId
	 * 		probe's identifier associated to the transducer
	 */
	public Transducer( TransducerId id, TupleCentreId tcId, ProbeId probeId ){
		this.id = id;
		this.tcId = tcId;
		
		executor = new OperationHandler();
		
		probes = new HashMap< ProbeId, Object >();
	}
	
	/**
	 * Adds a new probe. If the probe's name is already recorded, the probe will not be registered.
	 * 
	 * @param id
	 * 		probe's identifier
	 * @param probe
	 * 		the probe itself
	 */
	public void addProbe( ProbeId id, Object probe ){
		if( !probes.containsKey(id) ){
			probes.put( id, probe );
		}
	}
	
	/**
	 * Removes a probe from the probe list associated to the transducer if exist
	 * 
	 * @param probe
	 * 		probe's identifier
	 */
	public void removeProbe( ProbeId id ){
		Object[] keySet = probes.keySet().toArray();
		for( int i=0; i<keySet.length; i++ ){
			if( ((ProbeId)keySet[i]).getLocalName().equals( id.getLocalName() ) ){
				probes.remove( keySet[i] );
				return;
			}
		}
	}
	
	/**
	 * Returns the identifier of the transducer.
	 * 
	 * @return
	 * 		the transducer's identifier
	 */
	public TransducerId getIdentifier(){
		return id;
	}
	
	/**
	 * Returns the tuple centre associated to the transducer
	 * 
	 * @return
	 * 		the tuple centre identifier.
	 */
	public TupleCentreId getTCId(){
		return tcId;
	}
	
	/**
	 * Returns the list of all the probes associated to the transducer
	 * 
	 * @return
	 * 		array of the probes associated to the transducer
	 */
	public ProbeId[] getProbes(){
		Object[] keySet = probes.keySet().toArray();
		ProbeId[] probeList = new ProbeId[ keySet.length ];
		for( int i=0; i<probeList.length; i++ )
			probeList[i] = (ProbeId) keySet[i];
		return probeList;
	}
	
	/**
	 * 
	 * Notifies an event from a probe to the tuple centre.
	 * 
	 * @param key
	 * 		the name of the value
	 * @param value
	 * 		the value to communicate.
	 * @throws UnreachableNodeException 
	 * @throws TucsonOperationNotPossibleException 
	 */
	public void notifyEnvEvent( String key, int value ) throws TucsonOperationNotPossibleException, UnreachableNodeException{
		LogicTuple tupla = new LogicTuple("getEnv", new Value(key), new Value(value));
		executor.doNonBlockingOperation( id, RespectOperation.OPTYPE_GET_ENV, tcId, tupla, this );
	}
	
	/**
	 * Notifies an event from the tuple centre.
	 * 
	 * Events to the transducer should be only getEnv or setEnv ones. The response to each event is specified 
	 * in getEnv and setEnv methods of the transducer.
	 * 
	 * @param ev
	 * 		internal event from the tuple centre
	 * @return
	 * 		true if the operation required is getEnv or setEnv and it's been successfully executed.
	 */
	public boolean notifyOutput( InternalEvent ev ){
		try{
			if( ev.getInternalOperation().isGetEnv() )
				return getEnv( "" + ev.getInternalOperation().getArgument().getArg(0) );
			else if( ev.getInternalOperation().isSetEnv() ){
				String key = "" + ev.getInternalOperation().getArgument().getArg(0);
				int value = Integer.parseInt( "" + ev.getInternalOperation().getArgument().getArg(1) );
				return setEnv( key, value );
			}
		}catch( Exception ex ){
			ex.printStackTrace();
			return false;
		}
		return false;
	}
	
	/**
	 * The behavior of the transducer when a getEnv operation is required
	 * 
	 * @return
	 * 		true if the operation has been successfully executed
	 */
	public abstract boolean getEnv( String key );
	
	/**
	 * The behavior of the transducer when a setEnv operation is required
	 * 
	 * @param key
	 * 		name of the parameter to set
	 * @param value
	 * 		value of the parameter to set
	 * @return
	 * 		true if the operation has been successfully executed
	 */
	public abstract boolean setEnv( String key, int value );
	
	/**
	 * Exit procedure, called to end a session of communication
	 * 
	 * @throws TucsonOperationNotPossibleException
	 */
	public synchronized void exit() throws TucsonOperationNotPossibleException{
		
		Iterator<OperationHandler.ControllerSession> it = executor.getControllerSessions().values().iterator();
		OperationHandler.ControllerSession cs;
		TucsonProtocol info;
		OperationHandler.Controller contr;
		ObjectOutputStream outStream;
		TucsonOperation op;
		TucsonMsgRequest exit;
		
		while(it.hasNext()){
			
			cs = (OperationHandler.ControllerSession) it.next();
			info = cs.getSession();
			contr = cs.getController();
			contr.setStop();
			outStream = info.getOutputStream();
			
			op = new TucsonOperation(TucsonOperation.exitCode(), (TupleTemplate) null, null, executor);
			executor.addOperation(op.getId(), op);
			
			exit = new TucsonMsgRequest((int) op.getId(), op.getType(), null,
					op.getLogicTupleArgument());
			try{
				TucsonMsgRequest.write(outStream, exit);
				outStream.flush();
			}catch(IOException ex){
				speakErr(""+ex);
			}
		}
	}
	
	/*
	 * =========================================================================================
	 * INTERNAL UTILITY METHODS
	 * =========================================================================================
	 */
	
	/**
	 * Utility methods used to communicate an output message to the console.
	 * 
	 * @param msg
	 * 		message to print.
	 */
	protected void speak( String msg ){
		System.out.println("[TRANSDUCER - "+id.toString()+"] "+msg);
	}
	
	protected void speakErr( String msg ){
		System.err.println("[TRANSDUCER - "+id.toString()+"][ERROR] "+msg);
	}
}
