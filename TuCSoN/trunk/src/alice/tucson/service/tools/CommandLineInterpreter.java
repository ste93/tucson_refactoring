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
package alice.tucson.service.tools;

import java.util.Date;

import alice.tucson.api.EnhancedACC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

/* MODIFIED BY <s.mariani@unibo.it> */

/**
 * Command Line Interpreter.
 * Can be booted with a TuCSoN agent ID or using a default assigned one (both passed to the CLIAgent).
 * Gets a TuCSoN ACC from TucsonMetaACC then spawns the CLIAgent who manages user input.
 */
public class CommandLineInterpreter{
	
	public static void main(String[] args){
		
		log("--------------------------------------------------------------------------------");
		log("Booting TuCSoN Command Line Intepreter...");
		
		if(alice.util.Tools.isOpt(args, "-?") || alice.util.Tools.isOpt(args, "-help")){
			log("Arguments: {-aid <agent_identifier>} {-netid <node_address>} {-port <port>} {-? | -help}");
		}else{
			
			String aid = null;
			String node = null;
			int port;
			
			if(alice.util.Tools.isOpt(args, "-aid"))
				aid = alice.util.Tools.getOpt(args, "-aid");
			else
				aid = "cli" + System.currentTimeMillis();
			
			if(alice.util.Tools.isOpt(args, "-netid"))
				node = alice.util.Tools.getOpt(args, "-netid");
			else
				node = "localhost";
			
			if(alice.util.Tools.isOpt(args, "-port"))
				port = Integer.parseInt(alice.util.Tools.getOpt(args, "-port"));
			else
				port = 20504;
			
			log("Version " + alice.tucson.api.TucsonMetaACC.getVersion());
			log("--------------------------------------------------------------------------------");
			log("" + new Date());
			log("Demanding for TuCSoN default ACC on port < " + port + " >...");
			
//			DefaultACC context = null;
			EnhancedACC context = null;
			try{
				context = TucsonMetaACC.getContext(new TucsonAgentId(aid), node, port);
			}catch(TucsonInvalidAgentIdException ex1){
				System.err.println("[CommandLineInterpreter]: " + ex1);
				ex1.printStackTrace();
				System.exit(-1);
			}
			
			if(context != null){
				log("Spawning CLI TuCSoN agent...");
				log("--------------------------------------------------------------------------------");
				new Thread(new CLIAgent(context, node, port)).start();
			}else
				System.err.println("[CommandLineInterpreter]: Context not available now, please re-try.");
			
		}
		
	}

	public static void log(String s){
		System.out.println("[CommandLineInterpreter]: " + s);
	}
	
}
