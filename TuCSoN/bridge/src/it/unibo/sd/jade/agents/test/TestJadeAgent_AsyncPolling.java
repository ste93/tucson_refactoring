package it.unibo.sd.jade.agents.test;

import it.unibo.sd.jade.coordination.AsyncOpResultData;
import it.unibo.sd.jade.glue.BridgeJADETuCSoN;
import it.unibo.sd.jade.operations.ordinary.Rd;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * TestJadeAgent_AsyncPolling: test esecuzione agente JADE con chiamate di
 * coordinazione sospensive in modalità asincrone gestiste tramite polling
 * 
 * @author lucasangiorgi
 * 
 */
public class TestJadeAgent_AsyncPolling extends Agent {

    @SuppressWarnings("serial")
    private class TucsonTestBehaviour extends SimpleBehaviour {

        @Override
        public void action() {
            try {
                System.out.println("Hello, i am " + this.myAgent.getName());
                final TucsonHelper helper =
                        (TucsonHelper) TestJadeAgent_AsyncPolling.this
                                .getHelper(TucsonService.NAME);
                // Ottengo ACC
                helper.authenticate(this.myAgent);
                // Creo operazione
                final TucsonTupleCentreId tcid =
                        helper.getTupleCentreId("default", "localhost", 20504);
                final LogicTuple tuple = LogicTuple.parse("nome(X)");
                final Rd op = new Rd(tcid, tuple);
                final BridgeJADETuCSoN bridge =
                        helper.getBridgeJADETuCSoN(this.myAgent);
                final AsyncOpResultData result = bridge.executeAsynch(op);
                // controllo a polling se l'operazione è stata completata o meno
                while (!TestJadeAgent_AsyncPolling.this.done) {
                    final TucsonOpCompletionEvent ev =
                            result.getEventComplete(result.getOpId());
                    if (ev != null) { // risultato operazione acquisito
                        // gestione risultato
                        TestJadeAgent_AsyncPolling.this
                                .log("\n\n\n\n Result = " + ev.getTuple()
                                        + "\n\n\n\n");
                        TestJadeAgent_AsyncPolling.this.done = true;
                        bridge.cleanCoordinationStructure(this);
                    } else { // risultato non acquisito
                        TestJadeAgent_AsyncPolling.this
                                .log("risultato non pronto");
                        TestJadeAgent_AsyncPolling.this.done = false;
                    }
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            // TODO Auto-generated method stub
            return TestJadeAgent_AsyncPolling.this.done;
        }
    }

    private static final long serialVersionUID = 1L;
    private boolean done = false;

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Agent " + this.getLocalName() + " started!");
        this.addBehaviour(new TucsonTestBehaviour());
    }

    private void log(final String msg) {
        System.out.println("/***[JADEAGENT-" + this.getName() + "]: " + msg);
    }
}
