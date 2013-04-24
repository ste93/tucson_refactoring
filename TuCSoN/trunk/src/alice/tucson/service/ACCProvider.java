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

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.tucson.api.*;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

import alice.tucson.introspection.*;
import alice.tucson.network.TucsonProtocol;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class ACCProvider{
	
//	protected final static int threadsNumber = Runtime.getRuntime().availableProcessors()+1;
	protected TucsonTupleCentreId config;
	protected TucsonAgentId aid;
	protected TucsonNodeService node;
	protected ExecutorService exec;

	/**
	 * 
	 * @param node
	 * @param tid
	 */
	public ACCProvider(TucsonNodeService node, TucsonTupleCentreId tid){
		try{
			aid = new TucsonAgentId("context_manager");
		}catch(TucsonInvalidAgentIdException e){
			System.err.println("[ACCProvider]: " + e);
			e.printStackTrace();
		}
		this.node = node;
		this.config = tid;
//		exec = Executors.newFixedThreadPool(threadsNumber);
		exec = Executors.newCachedThreadPool();
		log("Listening to incoming ACC requests...");
	}

	private void log(String st){
		System.out.println("[ACCProvider]: " + st);
	}

	/**
	 * 
	 * @param profile
	 * @param dialog
	 * @return
	 */
//	exception handling is a mess, need to review it...
	public synchronized boolean processContextRequest(ACCDescription profile, TucsonProtocol dialog){
		
		log("Processing ACC request...");
		
		try{
			
			String agentName = profile.getProperty("agent-identity");
			if(agentName == null)
				agentName = profile.getProperty("tc-identity");
			LogicTuple req = new LogicTuple("context_request", new Value(agentName), new Var("CtxId"));
			LogicTuple result = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), aid, config, req);
			
			if(result == null){
				profile.setProperty("failure", "context not available");
				dialog.sendEnterRequestRefused(profile);
				return false;
			}
			
			TupleArgument res = result.getArg(1);
			
			if(res.getName().equals("failed")){
				profile.setProperty("failure", res.getArg(0).getName());
				dialog.sendEnterRequestRefused(profile);
				return false;
			}

			log("result = " + result + ", res = " + res);
			
			TupleArgument ctxId = res.getArg(0);
			profile.setProperty("context-id", ctxId.toString());
			log("ACC request accepted, ACC id is < " + ctxId.toString() + " >");
			dialog.sendEnterRequestAccepted(profile);
			String agentRole = profile.getProperty("agent-role");
			
			if(agentRole.equals("$inspector")){
				ACCAbstractProxyNodeSide skel = new InspectorContextSkel(this, dialog, node, profile);
				node.addNodeAgent(skel);
				skel.start();
			}else{
//				should I pass here the TuCSoN node port?
				ACCAbstractProxyNodeSide skel = new ACCProxyNodeSide(this, dialog, node, profile);
				node.addNodeAgent(skel);
				exec.execute(skel);
			}
			
			return true;
			
		}catch(Exception e){
			profile.setProperty("failure", "generic");
			System.err.println("[ACCProvider]: " + e);
			e.printStackTrace();
			return false;
		}
		
	}

	/**
	 * 
	 * @param ctxId
	 * @param aid
	 * @return
	 */
//	exception handling is a mess, need to review it...
	public synchronized boolean shutdownContext(int ctxId, TucsonAgentId aid){
		
		LogicTuple req = new LogicTuple("context_shutdown", new Value(ctxId), new Value(aid.toString()), new Var("CtxId"));
		LogicTuple result;
		try{
			result = (LogicTuple) TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), this.aid, config, req);
		}catch(Exception e){
			System.err.println("[ACCProvider]: " + e);
			e.printStackTrace();
			return false;
		}
		
		try{
			if(result.getArg(2).getName().equals("ok"))
				return true;
			else
				return false;
		}catch(InvalidTupleOperationException e) {
			System.err.println("[ACCProvider]: " + e);
			e.printStackTrace();
			return false;
		}

	}
	
	public void shutdown() throws InterruptedException{
		log("Shutdown interrupt received, shutting down...");
		exec.shutdownNow();
		if(exec.awaitTermination(5, TimeUnit.SECONDS))
			log("Executors correctly stopped");
		else
			log("Executors may be still running");
	}
	
}
