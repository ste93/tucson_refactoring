package it.unibo.sd.jade.agents.test;

import it.unibo.sd.jade.coordination.AsynchTucsonOpResult;
import it.unibo.sd.jade.glue.BridgeToTucson;
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
public class TestAsyncPolling extends Agent {
    private class TucsonTestBehaviour extends SimpleBehaviour {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            try {
                System.out.println("Hello, i am " + this.myAgent.getName());
                final TucsonHelper helper = (TucsonHelper) TestAsyncPolling.this
                        .getHelper(TucsonService.NAME);
                // Ottengo ACC
                helper.acquireACC(this.myAgent);
                // Creo operazione
                final TucsonTupleCentreId tcid = helper.getTucsonTupleCentreId(
                        "default", "localhost", 20504);
                final LogicTuple tuple = LogicTuple.parse("nome(X)");
                final Rd op = new Rd(tcid, tuple);
                final BridgeToTucson bridge = helper
                        .getBridgeToTucson(this.myAgent);
                final AsynchTucsonOpResult result = bridge
                        .asynchronousInvocation(op);
                // controllo a polling se l'operazione è stata completata o meno
                while (!TestAsyncPolling.this.done) {
                    final TucsonOpCompletionEvent ev = result
                            .getTucsonCompletionEvent(result.getOpId());
                    if (ev != null) { // risultato operazione acquisito
                        // gestione risultato
                        TestAsyncPolling.this.log("\n\n\n\n Result = "
                                + ev.getTuple() + "\n\n\n\n");
                        TestAsyncPolling.this.done = true;
                        bridge.clearTucsonOpResult(this);
                    } else { // risultato non acquisito
                        TestAsyncPolling.this.log("risultato non pronto");
                        TestAsyncPolling.this.done = false;
                    }
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            return TestAsyncPolling.this.done;
        }
    }

    private static final long serialVersionUID = 1L;
    private boolean done = false;

    private void log(final String msg) {
        System.out.println("/***[JADEAGENT-" + this.getName() + "]: " + msg);
    }

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Agent " + this.getLocalName() + " started!");
        this.addBehaviour(new TucsonTestBehaviour());
    }
}
