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
package alice.tucson.service;

import alice.logictuple.*;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

import alice.tucson.network.*;
import alice.tucson.service.TucsonNodeService;

import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.TupleCentreOperation;

import java.io.*;
import java.util.*;

/**
 * 
 */
public class ACCProxyNodeSide extends ACCAbstractProxyNodeSide{
	
	private boolean exit = false;
	private TucsonAgentId agentId;
	private TucsonTupleCentreId tcId;
	TucsonProtocol dialog;
	ObjectInputStream inStream;
	ObjectOutputStream outStream;
	TucsonNodeService node;
	HashMap<Long, TucsonMsgRequest> requests;
	HashMap<Long, Long> opVsReq;
	ACCProvider manager;
	int ctxId;

	/**
	 * 
	 * @param man
	 * @param dialog
	 * @param node
	 * @param p
	 */
	public ACCProxyNodeSide(ACCProvider man, TucsonProtocol dialog, TucsonNodeService node, ACCDescription p){

		ctxId = Integer.parseInt(p.getProperty("context-id"));

		String name = p.getProperty("agent-identity");
		if(name == null){
			name = p.getProperty("tc-identity");
			try {
				tcId = new TucsonTupleCentreId(name);
				agentId = new TucsonAgentId("tcAgent", tcId);
			} catch (TucsonInvalidTupleCentreIdException e) {
				System.err.println("[ACCProxyNodeSide]: " + e);
				e.printStackTrace();
//			} catch (TucsonInvalidAgentIdException e) {
//				System.err.println("[ACCProxyNodeSide]: " + e);
//				e.printStackTrace();
			}
		}else{
			try{
				agentId = new TucsonAgentId(name);
			}catch(TucsonInvalidAgentIdException e){
				System.err.println("[ACCProxyNodeSide]: " + e);
				e.printStackTrace();
			}
		}

		this.dialog = dialog;
		inStream = dialog.getInputStream();
		outStream = dialog.getOutputStream();

		requests = new HashMap<Long, TucsonMsgRequest>();
		opVsReq = new HashMap<Long, Long>();

		this.node = node;
		manager = man;
		
	}

	private void log(String st){
		System.out.println("[ACCProxyNodeSide (" + node.getTCPPort() + ", " + ctxId + ")]: " + st);
	}

	/**
	 * 
	 */
	@SuppressWarnings({ "unchecked", "finally" })
	public void run(){
		
		node.addAgent(agentId);
		
		TucsonMsgRequest msg;
		TucsonMsgReply reply;
		TucsonTupleCentreId tid;
		LogicTuple res = null;
		List<LogicTuple> resList;

		while(!exit){
			
			log("Listening to incoming TuCSoN agents/nodes requests...");

			try{
				msg = TucsonMsgRequest.read(inStream);
			}catch(EOFException e){
				log("Agent " + agentId + " quitted");
				break;
			}catch(Exception e){
				System.err.println("[ACCProxyNodeSide]: " + e);
				e.printStackTrace();
				break;
			}
			int msg_type = msg.getType();				
			
			if(msg_type == TucsonOperation.exitCode()){
				reply = new TucsonMsgReply(msg.getId(), TucsonOperation.exitCode(), true, true, true);
				try{
					TucsonMsgReply.write(outStream, reply);
					outStream.flush();
				}catch(IOException e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
				}finally{
					break;
				}
			}
			
			try{
				tid = new TucsonTupleCentreId((Object) msg.getTid());  
			}catch(TucsonInvalidTupleCentreIdException e){
				System.err.println("[ACCProxyNodeSide]: " + e);
				e.printStackTrace();
				break;
			}
			
			log("Serving TucsonOperation request < id="+msg.getId()+", type="+msg.getType()+", tuple="+msg.getTuple()+" >...");
			
			if(msg_type == TucsonOperation.set_sCode()){
				node.resolveCore(tid.getName());
				node.addTCAgent(agentId, tid);
				try{
					if(tcId == null)
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingSpecOperation(msg_type, agentId, tid, msg.getTuple());
					else
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingSpecOperation(msg_type, tcId, tid, msg.getTuple());
//					log("resList = " + resList);
				}catch(Exception e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
				
				reply = new TucsonMsgReply(msg.getId(), msg_type, true, true, true, msg.getTuple(), resList);
				
				try{
					TucsonMsgReply.write(outStream, reply);
					outStream.flush();
				}catch(IOException e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
				
			}else if(msg_type == TucsonOperation.set_Code()){
				
				node.resolveCore(tid.getName());
				node.addTCAgent(agentId, tid);
				try{
//					log("msg.getTuple = " + msg.getTuple());
					if(tcId == null)
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(msg_type, agentId, tid, msg.getTuple());
					else
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(msg_type, tcId, tid, msg.getTuple());
				}catch(Exception e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
				
				reply = new TucsonMsgReply(msg.getId(), msg_type, true, true, true, res, resList);
				
				try{
					TucsonMsgReply.write(outStream, reply);
					outStream.flush();
				}catch(IOException e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
				
			}else if(msg_type == TucsonOperation.get_Code()){
				
				node.resolveCore(tid.getName());
				node.addTCAgent(agentId, tid);
				try{
					if(tcId == null)
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(msg_type, agentId, tid, null);
					else
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingOperation(msg_type, tcId, tid, null);
				}catch(Exception e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
				
				reply = new TucsonMsgReply(msg.getId(), msg_type, true, true, true, null, resList);
				
				try{
					TucsonMsgReply.write(outStream, reply);
					outStream.flush();
				}catch(IOException e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
				
			}else if(msg_type == TucsonOperation.get_sCode()){
				
				node.resolveCore(tid.getName());
				node.addTCAgent(agentId, tid);
				try{
					if(tcId == null)
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingSpecOperation(msg_type, agentId, tid, null);
					else
						resList = (List<LogicTuple>) TupleCentreContainer.doBlockingSpecOperation(msg_type, tcId, tid, null);
					if(resList==null)
						resList = new LinkedList<LogicTuple>();
//					log("res = " + res);
				}catch(Exception e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
				
				reply = new TucsonMsgReply(msg.getId(), msg_type, true, true, true, null, resList);
				
				try{
					TucsonMsgReply.write(outStream, reply);
					outStream.flush();
				}catch(IOException e){
					System.err.println("[ACCProxyNodeSide]: " + e);
					e.printStackTrace();
					break;
				}
							
			}else if(msg_type == TucsonOperation.noCode() || msg_type == TucsonOperation.nopCode()
					|| msg_type == TucsonOperation.outCode() || msg_type == TucsonOperation.out_allCode()
					|| msg_type == TucsonOperation.inCode() || msg_type == TucsonOperation.inpCode()
					|| msg_type == TucsonOperation.rdCode() || msg_type == TucsonOperation.rdpCode()
					|| msg_type == TucsonOperation.uinCode() || msg_type == TucsonOperation.uinpCode()
					|| msg_type == TucsonOperation.urdCode() || msg_type == TucsonOperation.urdpCode()
					|| msg_type == TucsonOperation.unoCode() || msg_type == TucsonOperation.unopCode()
					|| msg_type == TucsonOperation.in_allCode() || msg_type == TucsonOperation.rd_allCode()
					|| msg_type == TucsonOperation.no_allCode() || msg_type == TucsonOperation.spawnCode()){

				node.resolveCore(tid.getName());
				node.addTCAgent(agentId, tid);
				ITupleCentreOperation op;
				
				synchronized(requests){
					try{
//						log("doing op " + msg.getType() + ", " + msg.getTuple() + ", " + msg.getTid());
						if(tcId == null)
							op = TupleCentreContainer.doNonBlockingOperation(msg_type, agentId, tid, msg.getTuple(), this);
						else
							op = TupleCentreContainer.doNonBlockingOperation(msg_type, tcId, tid, msg.getTuple(), this);
					}catch(Exception e){
						System.err.println("[ACCProxyNodeSide]: " + e);
						e.printStackTrace();
						break;
					}
					requests.put(new Long(msg.getId()), msg);
					opVsReq.put(new Long(op.getId()), new Long(msg.getId()));
				}
				
			}else if(msg_type == TucsonOperation.no_sCode() || msg_type == TucsonOperation.nop_sCode()
					|| msg_type == TucsonOperation.out_sCode()
					|| msg_type == TucsonOperation.in_sCode() || msg_type == TucsonOperation.inp_sCode()
					|| msg_type == TucsonOperation.rd_sCode() || msg_type == TucsonOperation.rdp_sCode()){
				
				node.resolveCore(tid.getName());
				node.addTCAgent(agentId, tid);
				ITupleCentreOperation op;
				
				synchronized(requests){
					try{
						if(tcId == null)
							op = TupleCentreContainer.doNonBlockingSpecOperation(msg_type, agentId, tid, msg.getTuple(), this);
						else
							op = TupleCentreContainer.doNonBlockingSpecOperation(msg_type, tcId, tid, msg.getTuple(), this);
					}catch(Exception e){
						System.err.println("[ACCProxyNodeSide]: " + e);
						e.printStackTrace();
						break;
					}
					requests.put(new Long(msg.getId()), msg);
					opVsReq.put(new Long(op.getId()), new Long(msg.getId()));
				}
				
			}else if(msg_type == TucsonOperation.getEnvCode() || msg_type == TucsonOperation.setEnvCode()){

				node.resolveCore(tid.getName());
				node.addTCAgent(agentId, tid);
				
				ITupleCentreOperation op = null;
				synchronized(requests){
					try{
						if(tcId == null)
							op = TupleCentreContainer.doEnvironmentalOperation(msg_type, agentId, tid, msg.getTuple(), this);
						else
							op = TupleCentreContainer.doEnvironmentalOperation(msg_type, tcId, tid, msg.getTuple(), this);
					}catch(Exception e){
						System.err.println("[ACCProxyNodeSide]: " + e);
						e.printStackTrace();
						break;
					}
				}
				requests.put(new Long(msg.getId()), msg);
				opVsReq.put(new Long(op.getId()), new Long(msg.getId()));
			}
			
		}

		try{
			dialog.end();
		}catch(Exception ex){
			System.err.println("[ACCProxyNodeSide]: " + ex);
			ex.printStackTrace();
		}

		log("Releasing ACC < " + ctxId + " > held by TuCSoN agent < " + agentId.toString() + " >");
		node.removeAgent(agentId);
		manager.shutdownContext(ctxId, agentId);
		node.removeNodeAgent(this);

	}

	synchronized public void exit(){
		exit = true;
		notify();
	}

	/**
	 * 
	 */
	public void operationCompleted(TupleCentreOperation op){
		
		Long reqId;
		TucsonMsgRequest msg;
		synchronized(requests){
			reqId = opVsReq.remove(new Long(op.getId()));
			msg = requests.remove(reqId);
		}
		
		TucsonMsgReply reply = null;
		
		if(op.isInAll() || op.isRdAll() || op.isNoAll() || op.isOutAll()){
			if(op.getTupleListResult()==null)
				op.setTupleListResult(new LinkedList<Tuple>());
			if(op.isResultSuccess())
				reply = new TucsonMsgReply(msg.getId(), op.getType(), true, true, true, msg.getTuple(), (List<Tuple>) op.getTupleListResult());
			else
				reply = new TucsonMsgReply(msg.getId(), op.getType(), true, true, false, msg.getTuple(), (List<Tuple>) op.getTupleListResult());
		}else{
			if(op.isResultSuccess())
				reply = new TucsonMsgReply(msg.getId(), op.getType(), true, true, true, msg.getTuple(), (LogicTuple) op.getTupleResult());
			else
				reply = new TucsonMsgReply(msg.getId(), op.getType(), true, true, false, msg.getTuple(), (LogicTuple) op.getTupleResult());
		}
		
		try{
			TucsonMsgReply.write(outStream, reply);
			outStream.flush();
		}catch(IOException ex){
			System.err.println("[ACCProxyNodeSide]: " + ex);
			ex.printStackTrace();
		}

	}
	
}
