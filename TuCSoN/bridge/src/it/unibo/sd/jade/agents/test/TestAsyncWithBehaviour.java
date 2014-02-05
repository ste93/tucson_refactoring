package it.unibo.sd.jade.agents.test;

import it.unibo.sd.jade.coordination.IAsynchCompletionBehaviour;
import it.unibo.sd.jade.glue.BridgeToTucson;
import it.unibo.sd.jade.operations.ordinary.Rd;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * TestJadeAgent_AsyncWithBehav: test esecuzione agente JADE con chiamate di
 * coordinazione sospensive in modalità asincrone gestiste tramite behaviour
 * aggiuntivo
 * 
 * @author lucasangiorgi
 * 
 */
public class TestAsyncWithBehaviour extends Agent {

    @SuppressWarnings("serial")
    private class ResultCompleteBehaviour extends OneShotBehaviour implements
            IAsynchCompletionBehaviour {

        private TucsonOpCompletionEvent ev = null;

        @Override
        public void action() {
            // TODO Auto-generated method stub
            TestAsyncWithBehaviour.this
                    .log(" behaviour risultato ricevuto");
            if (this.ev != null) { // risultato operazione acquisito
                // gestione risultato
                TestAsyncWithBehaviour.this.log("\n\n\n\n Result = "
                        + this.ev.getTuple() + "\n\n\n\n");
            } else { // risultato non acquisito
                TestAsyncWithBehaviour.this.log("risultato non pronto");
            }
        }

        @Override
        public void setTucsonOpCompletionEvent(final TucsonOpCompletionEvent e) {
            // TODO Auto-generated method stub
            this.ev = e;
        }
    }

    @SuppressWarnings("serial")
    private class TucsonTestBehaviour extends SimpleBehaviour {

        @Override
        public void action() {
            try {
                System.out.println("Hello, i am " + this.myAgent.getName());
                final TucsonHelper helper =
                        (TucsonHelper) TestAsyncWithBehaviour.this
                                .getHelper(TucsonService.NAME);
                // Ottengo ACC
                helper.acquireACC(this.myAgent);
                // Creo operazione
                final TucsonTupleCentreId tcid =
                        helper.getTucsonTupleCentreId("default", "localhost", 20504);
                final LogicTuple tuple = LogicTuple.parse("nome(X)");
                final Rd op = new Rd(tcid, tuple);
                final BridgeToTucson bridge =
                        helper.getBridgeToTucson(this.myAgent);
                // creazione behaviour delegato alla gestione del risultato
                // dell'operazione di coordinazione
                final ResultCompleteBehaviour behav =
                        new ResultCompleteBehaviour();
                bridge.asynchronousInvocation(op, behav, this.myAgent);
                bridge.clearTucsonOpResult(this);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            // TODO Auto-generated method stub
            return true;
        }
    }

    private static final long serialVersionUID = 1L;

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
