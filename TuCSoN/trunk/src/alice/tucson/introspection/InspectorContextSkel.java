/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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
package alice.tucson.introspection;

import alice.logictuple.LogicTuple;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.service.ACCAbstractProxyNodeSide;
import alice.tucson.service.ACCDescription;
import alice.tucson.service.ACCProvider;
import alice.tucson.service.TucsonNodeService;
import alice.tucson.service.TucsonOperation;
import alice.tucson.service.TucsonTCUsers;
import alice.tucson.service.TupleCentreContainer;

import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.core.InspectableEvent;
import alice.tuplecentre.core.ObservableEventExt;
import alice.tuplecentre.core.ObservableEventReactionFail;
import alice.tuplecentre.core.ObservableEventReactionOK;
import alice.tuplecentre.core.TriggeredReaction;
import alice.tuplecentre.core.TupleCentreOperation;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

public class InspectorContextSkel extends ACCAbstractProxyNodeSide implements InspectableEventListener{
	
	/** channel with remote inspector proxy */
	ObjectOutputStream outStream;
	ObjectInputStream inStream;

	TucsonProtocol dialog;

	/** current observation protocol */
	InspectorProtocol protocol;

	boolean nextStep;
	boolean shutdown = false;
	ACCProvider manager;
	int ctxId;
	TucsonAgentId agentId;
	TucsonTupleCentreId tcId;

	public InspectorContextSkel(ACCProvider man, TucsonProtocol dialog, TucsonNodeService node,
			ACCDescription p) throws TucsonGenericException{
		
		this.dialog = dialog;
		manager = man;
		NewInspectorMsg msg = null;
		try{			
			ctxId = Integer.parseInt(p.getProperty("context-id"));
			String name = p.getProperty("agent-identity");
			agentId = new TucsonAgentId(name);		
			inStream = dialog.getInputStream();
			outStream = dialog.getOutputStream();				
			
			msg = (NewInspectorMsg) inStream.readObject();	
			tcId = new TucsonTupleCentreId(msg.tcName);
		}catch(NumberFormatException e){
			e.printStackTrace();
		} catch (TucsonInvalidAgentIdException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TucsonInvalidTupleCentreIdException e) {
			e.printStackTrace();
		}
		
		TucsonTCUsers coreInfo = node.resolveCore(msg.tcName);		
		if (coreInfo == null)
			throw new TucsonGenericException("Internal error: InspectorContextSkel constructor");
		protocol = msg.info;
		
	}

	public void run(){
		
		log("I'm on.");
		try{
			TupleCentreContainer.doManagementOperation(TucsonOperation.addInspCode(), tcId, this);
			while (!shutdown){
				NodeMsg msg = (NodeMsg) inStream.readObject();
				Class<?> cl = msg.getClass();
				Method m = this.getClass().getMethod(msg.action, new Class[] { cl });
				m.invoke(this, new Object[] { msg });
			}
			dialog.end();
			TupleCentreContainer.doManagementOperation(TucsonOperation.rmvInspCode(), tcId, this);
		}catch(EOFException e){
			
		}catch(IOException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			e.printStackTrace();
		} catch (TucsonInvalidLogicTupleException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		manager.shutdownContext(ctxId, agentId);
		log("I'm off, bye :)");
		
	}

	synchronized public void onInspectableEvent(InspectableEvent ev){
		
		try{
			
			while (protocol.tracing && !nextStep)
				wait();
			nextStep = false;
		
			InspectorContextEvent msg = new InspectorContextEvent();
			msg.localTime = System.currentTimeMillis();
			msg.vmTime = ev.getTime();

			if (ev.getType() == InspectableEvent.TYPE_NEWSTATE){
				
				if (protocol.tsetObservType == InspectorProtocol.PROACTIVE_OBSERVATION){
					LogicTuple[] ltSet = (LogicTuple[]) TupleCentreContainer.doManagementOperation(TucsonOperation.getTSetCode(), tcId, protocol.tsetFilter);					
					msg.tuples = new LinkedList<LogicTuple>();
					if(ltSet != null){
						for(LogicTuple lt: ltSet)
							msg.tuples.add(lt);
					}
				}
				
				if (protocol.pendingQueryObservType == InspectorProtocol.PROACTIVE_OBSERVATION){			
					LogicTuple[] ltSet = (LogicTuple[]) TupleCentreContainer.doManagementOperation(TucsonOperation.getWSetCode(), tcId, protocol.wsetFilter);
					msg.wnEvents = new LinkedList<LogicTuple>();					
					if(ltSet != null){
						for(LogicTuple lt: ltSet)
							msg.wnEvents.add(lt);
					}
				}
				
				outStream.writeObject(msg);
				outStream.flush();
				
			}else if (ev.getType() == ObservableEventExt.TYPE_REACTIONOK){
				
				if (protocol.reactionsObservType != InspectorProtocol.NO_OBSERVATION){
					TriggeredReaction zCopy = new TriggeredReaction(null, ((ObservableEventReactionOK) ev).z.getReaction());
					msg.reactionOk = zCopy;
					outStream.writeObject(msg);
					outStream.flush();
				}
				
			}else if (ev.getType() == ObservableEventExt.TYPE_REACTIONFAIL){
				
				if (protocol.reactionsObservType != InspectorProtocol.NO_OBSERVATION){
					TriggeredReaction zCopy = new TriggeredReaction(null, ((ObservableEventReactionFail) ev).z.getReaction());
					msg.reactionFailed = zCopy;
					outStream.writeObject(msg);
					outStream.flush();
				}
				
			}
			
		}catch(InterruptedException e){
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			e.printStackTrace();
		} catch (TucsonInvalidLogicTupleException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/** setting new observation protocol */
	synchronized public void setProtocol(SetProtocolMsg msg){
		boolean wasTracing = protocol.tracing;
		protocol = msg.info;
		if (wasTracing)
			notifyAll();
		if (!protocol.tracing)
			onInspectableEvent(new InspectableEvent(this, InspectableEvent.TYPE_NEWSTATE));
	}

	/** get a tuple centre set (T set, W set,...) snapshot */
	public void getSnapshot(GetSnapshotMsg m){
		
		InspectorContextEvent msg = new InspectorContextEvent();
		msg.vmTime = System.currentTimeMillis();
		msg.localTime = System.currentTimeMillis();						
		
		if (m.what == GetSnapshotMsg.TSET){
			
			msg.tuples = new LinkedList<LogicTuple>();
			LogicTuple[] tSet = null;
			try{
				tSet = (LogicTuple[]) TupleCentreContainer.doManagementOperation(TucsonOperation.getTSetCode(), tcId, protocol.tsetFilter);
			}catch (TucsonOperationNotPossibleException e){
				e.printStackTrace();
			}catch (TucsonInvalidLogicTupleException e){
				e.printStackTrace();
			}
			for(LogicTuple lt: tSet)
				msg.tuples.add(lt);
			
		}else if (m.what == GetSnapshotMsg.WSET){
			
			LogicTuple[] ltSet = null;
			try{
				ltSet = (LogicTuple[]) TupleCentreContainer.doManagementOperation(TucsonOperation.getWSetCode(), tcId, protocol.wsetFilter);
			}catch (TucsonOperationNotPossibleException e){
				e.printStackTrace();
			}catch (TucsonInvalidLogicTupleException e){
				e.printStackTrace();
			}
			msg.wnEvents = new LinkedList<LogicTuple>();
			for(LogicTuple lt: ltSet)
				msg.wnEvents.add(lt);

		}
		
		try{
			outStream.writeObject(msg);
			outStream.flush();
		}catch(IOException e){
			e.printStackTrace();
		}

	}

	/** reset the tuple centre VM */
	synchronized public void reset(ResetMsg msg){
		try{
			TupleCentreContainer.doManagementOperation(TucsonOperation.reset(), tcId, null);
		}catch (TucsonOperationNotPossibleException e){
			e.printStackTrace();
		}catch (TucsonInvalidLogicTupleException e){
			e.printStackTrace();
		}
	}

	/** ask a new step for a tuple centre vm during tracing */
	synchronized public void nextStep(NextStepMsg msg){
		if (protocol.tracing){
			nextStep = true;
			notifyAll();
		}
	}

	/** set a new tuple set */
	synchronized public void setTupleSet(SetTupleSetMsg m){
		try{
			TupleCentreContainer.doBlockingOperation(TucsonOperation.set_Code(), agentId, tcId, m.tupleSet);
		}catch(TucsonInvalidLogicTupleException e){
			e.printStackTrace();
		}catch(TucsonOperationNotPossibleException e){
			e.printStackTrace();
		}
	}
	
	/** set a new tuple set */
	synchronized public void setEventSet(SetEventSetMsg m){
		try{
				TupleCentreContainer.doManagementOperation(TucsonOperation.setWSetCode(), tcId, m.eventWnSet);
		}catch(TucsonInvalidLogicTupleException e){
			e.printStackTrace();
		}catch(TucsonOperationNotPossibleException e){
			e.printStackTrace();
		}
	}

	/** shutdown the inspector */
	public void exit(ShutdownMsg msg){
		exit();
	}

	public void exit(){
		shutdown = true;
	}

	public void operationCompleted(TupleCentreOperation op){
		// TODO Auto-generated method stub
	}
	
	protected void log(String st){
		System.out.println("[InspectorContextSkel]: " + st);
	}
	
}
