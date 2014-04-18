package it.unibo.sd.jade.agents.test;

import it.unibo.sd.jade.glue.BridgeToTucson;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.operations.ordinary.Rd;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * TestJadeAgent_Sync: test esecuzione agente JADE con chiamate di coordinazione
 * sospensive in modalità sincrona
 * 
 * @author lucasangiorgi
 * 
 */
public class TestSync extends Agent {
    private class TucsonTestBehaviour extends SimpleBehaviour {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            try {
                System.out.println("Hello, i am " + this.myAgent.getName());
                final TucsonHelper helper = (TucsonHelper) TestSync.this
                        .getHelper(TucsonService.NAME);
                // Acquisizione ACC
                helper.acquireACC(this.myAgent);
                // Id tuple centre
                final TucsonTupleCentreId tcid = helper.getTucsonTupleCentreId(
                        "default", "localhost", 20504);
                // Creazione operazione di read
                LogicTuple tuple = LogicTuple.parse("nome(X)");
                final Rd op = new Rd(tcid, tuple);
                // acquisizione del bridge univoco per l'agente
                final BridgeToTucson bridge = helper
                        .getBridgeToTucson(this.myAgent);
                // esecuzione operazione di coordinazione TuCSoN dal "mondo"
                // JADE
                TucsonOpCompletionEvent result = bridge.synchronousInvocation(
                        op, null, this);
                if (result != null) { // operazione completata
                    // gestione risultato
                    final String name = result.getTuple().getArg(0).toString(); // value
                                                                                // of
                                                                                // X
                    tuple = LogicTuple.parse("ciao(" + name + ")");
                    final Out op1 = new Out(tcid, tuple);
                    // esecuzione seconda operazione di coordinazione
                    result = bridge.synchronousInvocation(op1, null, this);
                    if (result != null) { // operazione completata
                        // gestione risultato
                        TestSync.this.done = true; // terminazione
                                                   // behaviour
                        bridge.clearTucsonOpResult(this); // pulizia
                                                          // strutture
                                                          // condivise
                    } else { // operazione non completata, blocco behaviour
                             // corrente
                        TestSync.this.log("mi blocco");
                        TestSync.this.done = false;
                        this.block();
                    }
                } else { // risultato non acquisito
                    TestSync.this.log("mi blocco");
                    TestSync.this.done = false;
                    this.block();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            // TODO Auto-generated method stub
            return TestSync.this.done;
        }
    }

    private static final long serialVersionUID = 1L;
    private boolean done = false;

    private void log(final String msg) {
        System.out.println("[JADEAGENT-" + this.getName() + "]: " + msg);
    }

    @Override
    protected void setup() {
        super.setup();
        System.out.println("Agent " + this.getLocalName() + " started!");
        this.addBehaviour(new TucsonTestBehaviour());
    }
}
