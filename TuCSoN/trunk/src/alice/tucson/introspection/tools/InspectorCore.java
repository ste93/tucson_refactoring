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
package alice.tucson.introspection.tools;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Calendar;
import java.util.Iterator;

import alice.logictuple.LogicTuple;
import alice.respect.api.AgentId;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.introspection.WSetEvent;
import alice.tuplecentre.core.TriggeredReaction;

/**
 * 
 * 
 * @author s.mariani@unibo.it
 */
public class InspectorCore extends alice.tucson.introspection.Inspector{
	
	private Inspector form;

	protected boolean loggingTuples = false;
	protected String logTupleFilename;
	protected FileWriter logTupleWriter;
	protected LogicTuple logTupleFilter;

	protected boolean loggingQueries = false;
	protected String logQueryFilename;
	protected FileWriter logQueryWriter;

	protected boolean loggingReactions = false;
	protected String logReactionFilename;
	protected FileWriter logReactionWriter;
	
	/*
	 * Used when logging tuples/operations/reactions.
	 */
	Calendar cal;

	/**
	 * 
	 * 
	 * @param f
	 * @param id_
	 * @param tid_
	 * 
	 * @throws Exception
	 */
	public InspectorCore(Inspector f, TucsonAgentId id_, TucsonTupleCentreId tid_) throws Exception{
		super(id_, tid_);
		form = f;
		logTupleFilename = "inspector-tuples.log";
		logQueryFilename = "inspector-queries.log";
		logReactionFilename = "inspector-reactions.log";		
		try{
			logTupleWriter = new FileWriter(logTupleFilename, true);
			logQueryWriter = new FileWriter(logQueryFilename, true);
			logReactionWriter = new FileWriter(logReactionFilename, true);
		}catch (IOException e){
			e.printStackTrace();
		}
//		cal = Calendar.getInstance();
	}

	public void onContextEvent(alice.tucson.introspection.InspectorContextEvent msg){
		
		if (msg.tuples != null){
			
			TupleViewer viewer = form.getTupleForm();
//			viewer.setVMTime(msg.vmTime);
//			msg.localTime = System.currentTimeMillis();
//			viewer.setLocalTime(msg.localTime);
			String st = "";
			Iterator it = msg.tuples.iterator();
			int n = 0;
			
			while (it.hasNext()){
				st = st + it.next().toString() + "\n";
				n++;
			}
			viewer.setNItems(n);
			viewer.setText(st);
			
			if (loggingTuples){
				
				try{
					
					cal = Calendar.getInstance();
					st = "";
//					logTupleWriter.write("snapshot(\n" + "    time_vm(" + msg.vmTime + "),\n" + "    time_local("
//							+ msg.localTime + "),\n" + "    tuple_filter(" + form.protocol.tsetFilter + "),\n"
//							+ "    tuple_log_filter(" + logTupleFilter + "),\n" + "    tuple_list([ \n");
					logTupleWriter.write("snapshot(\n    " +
							"localtime(date("+cal.get(Calendar.DAY_OF_MONTH)+"-"+
									cal.get(Calendar.MONTH)+"-"+
									cal.get(Calendar.YEAR)+"), time("+
									cal.get(Calendar.HOUR_OF_DAY)+":"+
									cal.get(Calendar.MINUTE)+":"+
									cal.get(Calendar.SECOND)+")"+
							"),\n    tuples([ \n");

					it = msg.tuples.iterator();
					if (logTupleFilter == null){
						if (it.hasNext()){
							st = st + "        " + it.next().toString();
							while (it.hasNext())
								st = st + ",\n        " + it.next().toString();
						}
					}else{
						if (it.hasNext()){
							LogicTuple tuple = (LogicTuple) it.next();
							if (logTupleFilter.match(tuple))
								st = st + "        " + tuple.toString();
							while (it.hasNext()){
								tuple = (LogicTuple) it.next();
								if (logTupleFilter.match(tuple))
									st = st + ",\n        " + tuple.toString();
							}
						}
					}
					
					logTupleWriter.write(st + "\n	])\n).\n");
					logTupleWriter.flush();
					
				}catch(IOException e){
					e.printStackTrace();
				}
				
			}
			
		}
		
		if (msg.wnEvents != null){
			
			EventViewer viewer = form.getQueryForm();
//			msg.localTime = System.currentTimeMillis();
//			viewer.setVMTime(msg.vmTime);
//			viewer.setLocalTime(msg.localTime);
			String st = "";
			Iterator it = msg.wnEvents.iterator();
			int n = 0;
			WSetEvent ev;

			while (it.hasNext()){
				ev = (WSetEvent)it.next();
				st = st + ev.getOp() + 
						" from <" + ((AgentId)ev.getSource()).getLocalName() + 
						"> to <" + ev.getTarget() + ">\n";				
				n++;
			}
			viewer.setNItems(n);
			viewer.setText(st);
			
			if (loggingQueries){
				try{
					cal = Calendar.getInstance();
					st = "";
//					logQueryWriter.write("snapshot( \n" + "    time_vm(" + msg.vmTime + "),\n" + "    time_local("
//							+ msg.localTime + "),\n" + "    query_list([ \n");
					logQueryWriter.write("snapshot(\n    " +
							"localtime(date("+cal.get(Calendar.DAY_OF_MONTH)+"-"+
									cal.get(Calendar.MONTH)+"-"+
									cal.get(Calendar.YEAR)+"), time("+
									cal.get(Calendar.HOUR_OF_DAY)+":"+
									cal.get(Calendar.MINUTE)+":"+
									cal.get(Calendar.SECOND)+"))"+
							"),\n    operations([ \n");
					it = msg.wnEvents.iterator();
//					if (logTupleFilter == null){
						if (it.hasNext()){
							ev = (WSetEvent)it.next();
							st = st + "\t\top(what(" + ev.getOp()
									+ "),\n\t\t\twho(" + ((AgentId)ev.getSource()).getLocalName() + 
									"),\n\t\t\twhere(" + ev.getTarget() + ")\n\t\t)";
							while (it.hasNext()){
								ev = (WSetEvent)it.next();
								st = st + ",\n\t\top(what(" + ev.getOp()
										+ "),\n\t\t\twho(" + ((AgentId)ev.getSource()).getLocalName() + 
										"),\n\t\t\twhere(" + ev.getTarget() + ")\n\t\t)";
							}
						}
//					}else{
//						if (it.hasNext()){
//							LogicTuple tuple = (LogicTuple) it.next();
//							if (logTupleFilter.match(tuple))
//								st = st + "        " + tuple.toString();
//							while (it.hasNext()){
//								tuple = (LogicTuple) it.next();
//								if (logTupleFilter.match(tuple))
//									st = st + ",\n        " + tuple.toString();
//							}
//						}
//					}
					logQueryWriter.write(st + "\n\t])\n).\n");
					logQueryWriter.flush();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
			
		}

		if (msg.reactionOk != null){
			
			cal = Calendar.getInstance();
			ReactionViewer viewer = form.getReactionForm();
			TriggeredReaction tr = msg.reactionOk;
//			viewer.appendText("time: " + msg.vmTime + "\n" + tr.getReaction() + " OK\n");
			viewer.appendText("reaction < " + tr.getReaction() + " > SUCCEEDED @ "+
					cal.get(Calendar.HOUR_OF_DAY)+":"+
					cal.get(Calendar.MINUTE)+":"+
					cal.get(Calendar.SECOND)
			+".\n");
			if (loggingReactions){
				try{
//					logReactionWriter.write("succeed-reaction( time(" + msg.vmTime + "), " + tr.getReaction() + ").\n");
					logReactionWriter.write("snapshot(\n    " +
							"localtime(date("+cal.get(Calendar.DAY_OF_MONTH)+"-"+
									cal.get(Calendar.MONTH)+"-"+
									cal.get(Calendar.YEAR)+"), time("+
									cal.get(Calendar.HOUR_OF_DAY)+":"+
									cal.get(Calendar.MINUTE)+":"+
									cal.get(Calendar.SECOND)+")"+
							"),\n    succeeded( " + tr.getReaction() + " ).\n");
					logReactionWriter.flush();
				}catch (IOException e){
					e.printStackTrace();
				}
			}

		}else if (msg.reactionFailed != null){
			
			cal = Calendar.getInstance();
			ReactionViewer viewer = form.getReactionForm();
			TriggeredReaction tr = msg.reactionFailed;
//			viewer.appendText("time: " + msg.vmTime + "\n" + tr.getReaction() + " FAILED\n");
			viewer.appendText("reaction < " + tr.getReaction() + " > FAILED @ "+
					cal.get(Calendar.HOUR_OF_DAY)+":"+
					cal.get(Calendar.MINUTE)+":"+
					cal.get(Calendar.SECOND)
			+".\n");
			if (loggingReactions){
				try{
//					logReactionWriter.write("failed-reaction( time(" + msg.vmTime + "), " + tr.getReaction() + ").\n");
					logReactionWriter.write("snapshot(\n    " +
							"localtime(date("+cal.get(Calendar.DAY_OF_MONTH)+"-"+
									cal.get(Calendar.MONTH)+"-"+
									cal.get(Calendar.YEAR)+"), time("+
									cal.get(Calendar.HOUR_OF_DAY)+":"+
									cal.get(Calendar.MINUTE)+":"+
									cal.get(Calendar.SECOND)+")"+
							"),\n    failed( " + tr.getReaction() + " ).\n");
					logReactionWriter.flush();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
			
		}
		
	}

	void changeLogTupleFile(String name){
		logTupleFilename = name;
		if (logTupleWriter != null){
			try{
				logTupleWriter.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}try{
			logTupleWriter = new FileWriter(logTupleFilename, true);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	void changeLogQueryFile(String name){
		logQueryFilename = name;
		if (logQueryWriter != null){
			try{
				logQueryWriter.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}try{
			logQueryWriter = new FileWriter(logQueryFilename, true);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	void changeLogReactionFile(String name){
		logReactionFilename = name;
		if (logReactionWriter != null){
			try{
				logReactionWriter.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}try{
			logReactionWriter = new FileWriter(logReactionFilename, true);
		}catch (IOException e){
			e.printStackTrace();
		}
	}

}
