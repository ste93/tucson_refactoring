/*
 * User.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */
package alice.tucson.service;

import alice.logictuple.*;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tuprolog.InvalidTermException;

/**
 * 
 */
public class NodeManagementAgent extends Thread{
	
	private boolean isLogging = true;
	private TucsonNodeService node;
	private TucsonAgentId nodeManAid;
	private TucsonTupleCentreId config;

	public NodeManagementAgent(TucsonTupleCentreId config, TucsonNodeService node){
		try {
			nodeManAid = new TucsonAgentId("node_management_agent");
		} catch (TucsonInvalidAgentIdException e) {
			System.err.println("[NodeManagementAgent]: " + e);
			e.printStackTrace();
		}
		this.node = node;
		this.config = config;
		start();
	}

	/**
	 * 
	 */
	public void run(){
//		log("Starting...");
		try{
			while(true){
				LogicTuple cmd;
				cmd = TupleCentreContainer.in(nodeManAid, config, new LogicTuple("cmd", new Var("X")));
				execCmd(cmd.getArg(0));
			}
		}catch(InvalidTermException e){
			System.err.println("[NodeManagementAgent]: " + e);
			e.printStackTrace();
		}catch(Exception e){
			System.err.println("[NodeManagementAgent]: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	protected void execCmd(TupleArgument cmd) throws Exception{
		
		String name = cmd.getName();
		log("Executing command " + name);
		
		if(name.equals("destroy")){
			
			String tcName = cmd.getArg(0).getName();
			boolean result = node.destroyCore(tcName);
			if(result)
				TupleCentreContainer.out(nodeManAid, config, new LogicTuple("cmd_result", new Value("destroy"), new Value("ok")));
			else
				TupleCentreContainer.out(nodeManAid, config, new LogicTuple("cmd_result", new Value("destroy"), new Value("failed")));
		
		}else if(name.equals("enable_persistency")){
			
			node.enablePersistence(cmd.getArg(0));
			TupleCentreContainer.out(nodeManAid, config, new LogicTuple("cmd_result", cmd, new Value("ok")));
			
		}else if(name.equals("disable_persistency")){
			
			node.disablePersistence(cmd.getArg(0));
			TupleCentreContainer.out(nodeManAid, config, new LogicTuple("cmd_result", new Value("disable_persistency"), new Value("ok")));
			
		}else if(name.equals("enable_observability")){
			
			node.activateObservability();
			
		}else if(name.equals("disable_observability")){
			
			node.deactivateObservability();
			
		}
		
	}

	protected void log(String s){
		if (isLogging)
			System.out.println("[NodeManagementAgent]: " + s);
	}
	
}
