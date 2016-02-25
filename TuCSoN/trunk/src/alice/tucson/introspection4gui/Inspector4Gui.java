package alice.tucson.introspection4gui;

import java.util.ArrayList;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.respect.core.LogicReaction;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.introspection.Inspector;
import alice.tucson.introspection.InspectorContextEvent;
import alice.tucson.introspection.InspectorProtocol;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.Reaction;
import alice.tuprolog.Struct;

public class Inspector4Gui extends Inspector {

/*
 * **************************************************
 * 
 * 	Fields
 * 
 * **************************************************/

	private final List<Inspector4GuiObserver> observers;
	
/*
 * **************************************************
 * 
 * 	Constructors
 * 
 * **************************************************/
	
	public Inspector4Gui(final TucsonTupleCentreId arg1) throws Exception {
		super(new TucsonAgentId("inspector4gui_" + System.currentTimeMillis()), arg1, true);	
		this.observers = new ArrayList<>();
		InspectorProtocol protocol = new InspectorProtocol();
		protocol.setTsetObservType(InspectorProtocol.PROACTIVE_OBSERVATION);
		protocol.setReactionsObservType(InspectorProtocol.PROACTIVE_OBSERVATION);
		getContext().setProtocol(protocol);
	}
	
/*
 * **************************************************
 * 
 * 	Callback called by Stub
 * 
 * **************************************************/

	@Override
	public synchronized void onContextEvent(final InspectorContextEvent msg) {
		if (msg instanceof Inspector4GuiContextEvent) {
			Inspector4GuiContextEvent msg4Gui = (Inspector4GuiContextEvent) msg;
			if (msg4Gui.getNewTuples() != null) {
				for (LogicTuple t : msg4Gui.getNewTuples()) {
					notifyNewTuple(t);
				}
			}
			if (msg4Gui.getRemovedTuples() != null) {
				for (LogicTuple t : msg4Gui.getRemovedTuples()) {
					notifyRemovedTuple(t);
				}
			}
		}		
		
		if (msg.getReactionOk() != null) {
			Reaction tr = msg.getReactionOk().getReaction();
			if (tr instanceof LogicReaction) {
				LogicReaction logicReaction = (LogicReaction) tr;
				Struct struct = logicReaction.getStructReaction();
				struct.resolveTerm();
				//System.out.println("START");
				//System.out.println(logicReaction.getStructReaction());
				Struct arg = struct.getArg("?");
				if (arg != null) {
					//Struct destStruct = arg.getArg("@");
					//Struct hostStruct = destStruct.getArg(":");
					// TODO seems reverse
					Struct destStruct = arg.getArg("@");
					Struct hostStruct = destStruct.getArg(":");
					// END
					String tcName = destStruct.getTerm(0).toString();
					String tcHost = hostStruct.getTerm(0).toString();
					String tcPort = alice.util.Tools.removeApices(hostStruct.getTerm(1).toString());
					if (arg.getArg(1) instanceof Struct) {
						Struct termAsStruct = (Struct) arg.getArg(1);
						if (termAsStruct.getName().equals("out")) {
							String tuple = termAsStruct.getArg(0).toString();
							notifyTransfer(tcName, tcHost, tcPort, tuple);
						}
					}
					try {
						TucsonTupleCentreId tcId = new TucsonTupleCentreId(tcName, tcHost, tcPort);
						notifyNewTupleCenter(tcId);
					} catch (TucsonInvalidTupleCentreIdException e) {
						e.printStackTrace();
					}
				}
				//System.out.println(struct);
			}
		} 
	}

/*
 * **************************************************
 * 
 * 	Observer-observable pattern
 * 
 * **************************************************/
	
	public void addOberver(final Inspector4GuiObserver tucsonCoreObserver) {
		this.observers.add(tucsonCoreObserver);		
	}
	
	private void notifyNewTuple(final Tuple newTuple) {
		for (Inspector4GuiObserver observer : observers) {
			observer.onNewTuple(newTuple, this.context.getTid());
		}
	}
	
	private void notifyRemovedTuple(Tuple tupleRemoved) {
		for (Inspector4GuiObserver observer : observers) {
			observer.onRemovedTuple(tupleRemoved,  this.context.getTid());
		}
	}

	private void notifyTransfer(final String tcName, final String tcHost, final String tcPort, final String tuple) {
		for (Inspector4GuiObserver observer : observers) {
			observer.onNewTrasfer(this.context.getTid().getName(), tcName, tuple);
		}
	}

	private void notifyNewTupleCenter(final TucsonTupleCentreId newTcId) {
		for (Inspector4GuiObserver observer : observers) {
			observer.onNewTupleCenter(newTcId);
		}
	}
		
	
}
