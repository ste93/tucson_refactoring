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

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.OperationNotAllowedException;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tucson.service.ACCDescription;

import alice.tuplecentre.core.Event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;

public class InspectorContextStub implements InspectorContext{
	
	/** user id */
	private TucsonAgentId id;

	/** id of the tuple centre to be observed */
	protected TucsonTupleCentreId tid;

	/** current observation protocol */
	private InspectorProtocol protocol;

	/** info about connection with remote acceptor */
	private TucsonProtocol info;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;

	/** listeners registrated for virtual machine output events */
	private Vector contextListeners = new Vector();

	private Event evReceived;
	private ACCDescription profile;

	public InspectorContextStub(TucsonAgentId id, TucsonTupleCentreId tid) throws TucsonGenericException, TucsonGenericException{
		profile = new ACCDescription();		
		profile.setProperty("agent-identity", id.toString());
		profile.setProperty("agent-role", "$inspector");
		profile.setProperty("tuple-centre", tid.getName());
		this.id = id;
		this.tid = tid;		
		try {
			info = getTupleCentreInfo(tid);
		} catch (UnreachableNodeException e) {
			e.printStackTrace();
		} catch (OperationNotAllowedException e) {
			e.printStackTrace();
		}				
	}

	/**
	 * waits and processes TuCSoN virtual machine events
	 */
	public void acceptVMEvent() throws Exception{		
		InspectorContextEvent msg = (InspectorContextEvent) inStream.readObject();		
		for (int i = 0; i < contextListeners.size(); i++)
			((InspectorContextListener) contextListeners.elementAt(i)).onContextEvent(msg);
	}

	/** setting a new observation protocol */
	public void setProtocol(InspectorProtocol p) throws Exception{
		InspectorProtocol newp = new InspectorProtocol();
		newp.tsetObservType = p.tsetObservType;
		newp.tsetFilter = p.tsetFilter;
		newp.wsetFilter = p.wsetFilter;
		newp.tracing = p.tracing;
		newp.pendingQueryObservType = p.pendingQueryObservType;
		newp.reactionsObservType = p.reactionsObservType;
		outStream.writeObject(new SetProtocolMsg(id, newp));
		outStream.flush();
		protocol = p;
	}

	/** get a snapshot of tuple set */
	public void getSnapshot(byte snapshotMsg) throws Exception{
		outStream.writeObject(new GetSnapshotMsg(id, snapshotMsg));
		outStream.flush();
	}

	/** reset the tuple centre */
	public void reset() throws Exception{
		outStream.writeObject(new ResetMsg(id));
		outStream.flush();
	}

	/** when doing trace -> ask for a new virtual machine step */
	public void nextStep() throws Exception{
		outStream.writeObject(new NextStepMsg(id));
		outStream.flush();
	}

	/** set a new tuple set */
	public void setTupleSet(ArrayList tset) throws Exception{
		outStream.writeObject(new SetTupleSetMsg(id, tset));
		outStream.flush();
	}
	
	/** set a new query set */
	public void setEventSet(ArrayList wset) throws Exception{
		outStream.writeObject(new SetEventSetMsg(id, wset));
		outStream.flush();
	}

	/** shutdown inspector */
	public void exit() throws Exception{
		outStream.writeObject(new ShutdownMsg(id));
		outStream.flush();
	}

	/**
	 * resolve information about a tuple centre
	 */
	protected void resolveTupleCentreInfo(TucsonTupleCentreId tid) throws TucsonGenericException,
		IOException{		
		try {
			info = getTupleCentreInfo(tid);
		} catch (UnreachableNodeException e) {
			e.printStackTrace();
		} catch (OperationNotAllowedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * if request to a new tuple centre -> create new connection to target
	 * daemon providing the tuple centre otherwise return the already
	 * established connection
	 */
	private TucsonProtocol getTupleCentreInfo(TucsonTupleCentreId tid) throws TucsonGenericException,
		UnreachableNodeException, OperationNotAllowedException{		
		
		try{
			String node = alice.util.Tools.removeApices(tid.getNode());
			int port = tid.getPort();
			TucsonProtocol dialog = new TucsonProtocolTCP(node, port);
			dialog.sendEnterRequest(profile);			
			dialog.receiveEnterRequestAnswer();			
			if (dialog.isEnterRequestAccepted()){								
				outStream = dialog.getOutputStream();
				inStream = dialog.getInputStream();							
				protocol = new InspectorProtocol();				
				NewInspectorMsg msg = new NewInspectorMsg(id, tid.getName(), protocol);
				outStream.writeObject(msg);
				return dialog;
			}
		}catch(IOException e){
			throw new alice.tucson.api.exceptions.UnreachableNodeException();
		} catch (Exception e) {
			e.printStackTrace();
		}

		throw new alice.tucson.api.exceptions.OperationNotAllowedException();
		
	}

	/**
	 * Adds a listener to Inspector Events
	 * 
	 * @param l
	 *            the listener
	 */
	public void addInspectorContextListener(InspectorContextListener l){
		contextListeners.addElement(l);
	}

	/**
	 * Removes a listener to Inspector Events
	 */
	public void removeInspectorContextListener(InspectorContextListener l){
		contextListeners.removeElement(l);
	}

}
