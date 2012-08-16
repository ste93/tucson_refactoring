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

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.tucson.api.*;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.parsing.MyOpManager;
import alice.tucson.parsing.TucsonOpParser;
import alice.tucson.service.TucsonCmd;

import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Parser;

import java.io.*;

/* MODIFIED BY <s.mariani@unibo.it> */

/**
 * Command Line Interpreter TuCSoN agent.
 * Waits for user input, properly parses the issued command, then process it calling the corresponding
 * method on the ACC (gained through constructor).
 * It blocks until TuCSoN reply comes.
 */
@SuppressWarnings("serial")
public class CLIAgent extends alice.util.Automaton{
	
	private EnhancedACC context;
	private String node;
	private int port;
	private BufferedReader stdin;
	
	public CLIAgent(EnhancedACC context, String node, int port){
		this.context = context;
		this.node = node;
		this.port = port;
		stdin = new BufferedReader(new InputStreamReader(System.in));
	}

	public void boot(){
		log("CLI agent listening to user...");
		become("goalRequest");
	}

	/**
	 * Main reasoning cycle
	 */
	public void goalRequest(){
		
		String st = "";
		while(st.equals("")){
			input();
			try{
				st = stdin.readLine();
			}catch(IOException ex){
				System.err.println("[CLI]: " + ex);
				new CLIAgent(context, node, port).boot();
			}
		}
		
		TucsonOpParser parser = new TucsonOpParser(st, node, port);
		try {
			parser.parse();
		} catch (TucsonInvalidTupleCentreIdException e) {
			System.err.println("[CLI]: " + e);
			e.printStackTrace();
		}
		TucsonCmd cmd = parser.getCmd();
		TucsonTupleCentreId tid = parser.getTid();
//		debug("cmd = " + cmd.getPrimitive() + "(" + cmd.getArg() + ")");
//		debug("tid = " + tid.getName() + "@" + tid.getNode() + ":" + tid.getPort());
		
		if(cmd == null || tid == null)
			error(st);
		else if(cmd.getPrimitive().equals("quit") || cmd.getPrimitive().equals("exit")){
			try{
				log("Releasing ACC held (if any)...");
				context.exit();
			}catch(TucsonOperationNotPossibleException ex){
				System.err.println("[CLI]: " + ex);
			}finally{
				log("I'm done, have a nice day :)");
				become("end");
			}
		}else{
				
			/**
			 * Tokenize TuCSoN primitive & involved argument
			 */
			String methodName = cmd.getPrimitive();
			String tuple = cmd.getArg();
				
			try{
			
				/**
				 * Admissible Ordinary primitives
				 */
//				what about timeout? it returns null too...how to discriminate inp/rdp failure?
				if(methodName.equals("out")){
					LogicTuple t = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.out(tid, t, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("in")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.in(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("rd")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.rd(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("inp")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.inp(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("rdp")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.rdp(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("no")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.no(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("nop")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.nop(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("set")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.set(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}else if(methodName.equals("get")){
					busy();
					ITucsonOperation op = context.get(tid, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}
				
				/*MODIFIED BY <s.mariani@unibo.it>*/
				else if(methodName.equals("out_all")){
					LogicTuple t = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.out_all(tid, t, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}else if(methodName.equals("rd_all")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.rd_all(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}else if(methodName.equals("no_all")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.no_all(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}
				else if(methodName.equals("in_all")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.in_all(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}else if(methodName.equals("urd")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.urd(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("uno")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.uno(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}
				else if(methodName.equals("urdp")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.urdp(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("unop")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.unop(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}
				else if(methodName.equals("uin")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.uin(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("uinp")){
					LogicTuple templ = LogicTuple.parse(tuple);
					busy();
					ITucsonOperation op = context.uinp(tid, templ, (Long) null);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}
				
				/**
				 * Admissible Specification primitives
				 */
				
				else if(methodName.equals("out_s")){
					LogicTuple t = new LogicTuple(Parser.parseSingleTerm("reaction("+tuple+")", new MyOpManager()));
//					debug("t = " + t);
					busy();
					ITucsonOperation op = context.out_s(tid, new LogicTuple(t.getArg(0)), new LogicTuple(t.getArg(1)), new LogicTuple(t.getArg(2)), Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("in_s")){
					LogicTuple templ = new LogicTuple(Parser.parseSingleTerm("reaction("+tuple+")", new MyOpManager()));
					busy();
					ITucsonOperation op = context.in_s(tid, new LogicTuple(templ.getArg(0)), new LogicTuple(templ.getArg(1)), new LogicTuple(templ.getArg(2)), Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("rd_s")){
					LogicTuple templ = new LogicTuple(Parser.parseSingleTerm("reaction("+tuple+")", new MyOpManager()));
					busy();
					ITucsonOperation op = context.rd_s(tid, new LogicTuple(templ.getArg(0)), new LogicTuple(templ.getArg(1)), new LogicTuple(templ.getArg(2)), Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("inp_s")){
					LogicTuple templ = new LogicTuple(Parser.parseSingleTerm("reaction("+tuple+")", new MyOpManager()));
					busy();
					ITucsonOperation op = context.inp_s(tid, new LogicTuple(templ.getArg(0)), new LogicTuple(templ.getArg(1)), new LogicTuple(templ.getArg(2)), Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("rdp_s")){
					LogicTuple templ = new LogicTuple(Parser.parseSingleTerm("reaction("+tuple+")", new MyOpManager()));
					busy();
					ITucsonOperation op = context.rdp_s(tid, new LogicTuple(templ.getArg(0)), new LogicTuple(templ.getArg(1)), new LogicTuple(templ.getArg(2)), Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("no_s")){
					LogicTuple templ = new LogicTuple(Parser.parseSingleTerm("reaction("+tuple+")", new MyOpManager()));
					busy();
					ITucsonOperation op = context.no_s(tid, new LogicTuple(templ.getArg(0)), new LogicTuple(templ.getArg(1)), new LogicTuple(templ.getArg(2)), Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("nop_s")){
					LogicTuple templ = new LogicTuple(Parser.parseSingleTerm("reaction("+tuple+")", new MyOpManager()));
					busy();
					ITucsonOperation op = context.nop_s(tid, new LogicTuple(templ.getArg(0)), new LogicTuple(templ.getArg(1)), new LogicTuple(templ.getArg(2)), Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleResult());
					else
						prompt("failure: " + op.getLogicTupleResult());
				}else if(methodName.equals("set_s")){
					LogicTuple templ = new LogicTuple(Parser.parseSingleTerm(tuple, new MyOpManager()));
					busy();
					ITucsonOperation op = context.set_s(tid, templ, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}else if(methodName.equals("get_s")){
					busy();
					ITucsonOperation op = context.get_s(tid, Long.MAX_VALUE);
					if(op.isResultSuccess())
						prompt("success: " + op.getLogicTupleListResult());
					else
						prompt("failure: " + op.getLogicTupleListResult());
				}
				
				else if(methodName.equals("help") || methodName.equals("man")  || methodName.equals("syntax")){
					log("--------------------------------------------------------------------------------");
					log("TuCSoN CLI Syntax:");
					log("");
					log("\t\ttcName@ipAddress:port ? CMD");
					log("");
					log("where CMD can be:");
					log("");
					log("\t\tout(Tuple)");
					log("\t\tin(TupleTemplate)");
					log("\t\trd(TupleTemplate)");
					log("\t\tno(TupleTemplate)");
					log("\t\tinp(TupleTemplate)");
					log("\t\trdp(TupleTemplate)");
					log("\t\tnop(TupleTemplate)");
					log("\t\tget()");
					log("\t\tset([Tuple1, ..., TupleN])");
					log("\t\tin_all(TupleTemplate, TupleList)");
					log("\t\trd_all(TupleTemplate, TupleList)");
					log("\t\tno_all(TupleTemplate, TupleList)");
					log("\t\tuin(TupleTemplate)");
					log("\t\turd(TupleTemplate)");
					log("\t\tuno(TupleTemplate)");
					log("\t\tuinp(TupleTemplate)");
					log("\t\turdp(TupleTemplate)");
					log("\t\tunop(TupleTemplate)");
					log("\t\tout_s(Event,Guard,Reaction)");
					log("\t\tin_s(EventTemplate, GuardTemplate, ReactionTemplate)");
					log("\t\trd_s(EventTemplate, GuardTemplate, ReactionTemplate)");
					log("\t\tinp_s(EventTemplate, GuardTemplate ,ReactionTemplate)");
					log("\t\trdp_s(EventTemplate, GuardTemplate, ReactionTemplate)");
					log("\t\tno_s(EventTemplate, GuardTemplate, ReactionTemplate)");
					log("\t\tnop_s(EventTemplate, GuardTemplate, ReactionTemplate)");
					log("\t\tget_s()");
					log("\t\tset_s([(Event1,Guard1,Reaction1), ..., (EventN,GuardN,ReactionN)])");
					log("--------------------------------------------------------------------------------");
				}
				
				else if(methodName.equals("o/"))
					prompt("\\o");
				else if(methodName.equals("\\o"))
					prompt("o/");
				else if(methodName.equalsIgnoreCase("hi"))
					prompt("Hi there!");
				
				else{
					error(methodName);
				}
			
			}catch(InvalidLogicTupleException ex1){
				System.err.println("[CLI]: " + ex1);
				ex1.printStackTrace();
			} catch (TucsonOperationNotPossibleException ex2) {
				System.err.println("[CLI]: " + ex2);
			} catch (UnreachableNodeException ex3) {
				System.err.println("[CLI]: " + ex3);
			} catch (OperationTimeOutException ex5) {
				System.err.println("[CLI]: " + ex5);
			} catch (InvalidTupleOperationException ex6) {
				System.err.println("[CLI]: " + ex6);
			}
				
//			}else{
//				error(st);
//			}
			
		}
		
		become("goalRequest");
		
	}

	private void log(String s){
		System.out.println("[CLI]: " + s);
	}
	
	private void input(){
		System.out.print("[CLI]: ?> ");
	}
	
	private void busy(){
		System.out.print("[CLI]: ... ");
	}
	
	private void prompt(String s){
		System.out.println(" -> " + s);
	}
	
	private void error(String s){
		System.err.println("[CLI]: Unknown command <" + s + ">");
	}
	
	private void debug(String s){
		System.out.println("--- " + s + " ---");
	}

}
