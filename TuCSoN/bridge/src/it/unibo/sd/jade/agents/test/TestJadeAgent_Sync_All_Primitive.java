package it.unibo.sd.jade.agents.test;

import it.unibo.sd.jade.glue.BridgeJADETuCSoN;
import it.unibo.sd.jade.operations.ordinary.Out;
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
public class TestJadeAgent_Sync_All_Primitive extends Agent {

    @SuppressWarnings("serial")
    private class TucsonTestBehaviour extends SimpleBehaviour {

        private BridgeJADETuCSoN bridge;

        @Override
        public void action() {
            try {
                System.out.println("Hello, i am " + this.myAgent.getName());
                final TucsonHelper helper =
                        (TucsonHelper) TestJadeAgent_Sync_All_Primitive.this
                                .getHelper(TucsonService.NAME);
                // Ottengo ACC
                helper.authenticate(this.myAgent);
                // Creo operazione
                final TucsonTupleCentreId tcid =
                        helper.getTupleCentreId("default", "localhost", 20504);
                LogicTuple tuple = null;
                // acquisizione del bridge univoco per l'agente
                this.bridge = helper.getBridgeJADETuCSoN(this.myAgent);
                /*
                 * ********************* ORDINARY PRIMITIVE
                 * **************************
                 */
                /*-----------------OUT------------------------*/
                tuple = LogicTuple.parse("nome(luca)");
                final Out op = new Out(tcid, tuple);
                // esecuzione operazione dal "mondo Jade"
                final TucsonOpCompletionEvent result =
                        this.bridge.executeSynch(op, null, this);
                if (result != null) { // risultato operazione acquisito
                    // gestione risultato
                    TestJadeAgent_Sync_All_Primitive.this.log("\n\n--> "
                            + (result.resultOperationSucceeded() ? "success"
                                    : "failure") + ": " + result.getTuple()
                            + "\n\n");
                    TestJadeAgent_Sync_All_Primitive.this.done = true;
                    helper.deauthenticate(this.myAgent);
                } else { // risultato non acquisito
                    TestJadeAgent_Sync_All_Primitive.this.log("mi blocco");
                    TestJadeAgent_Sync_All_Primitive.this.done = false;
                    this.block();
                }
                /*-----------------RD------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); Rd op = new Rd(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------RDP------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); Rd_p op = new Rd_p(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------IN------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); In op = new In(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------INP------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); In_p op = new In_p(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------NO------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); No op = new No(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------NOP------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); No_p op = new No_p(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------GET------------------------*/
                /*
                 * Get op = new Get(tcid); //esecuzione operazione dal
                 * "mondo Jade" TucsonOpCompletionEvent result =
                 * bridge.executeSynch(op, null,this); if (result!=null) {
                 * //risultato operazione acquisito //gestione risultato
                 * log("\n\n--> "
                 * +(result.resultOperationSucceeded()?"success":"failure"
                 * )+": "+result.getTupleList()+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------SET------------------------*/
                /*
                 * tuple = LogicTuple.parse("[ciao,nome(luca),nome(stefano)]");
                 * Set op = new Set(tcid, tuple); //esecuzione operazione dal
                 * "mondo Jade" TucsonOpCompletionEvent result =
                 * bridge.executeSynch(op, null,this); if (result!=null) {
                 * //risultato operazione acquisito //gestione risultato
                 * log("\n\n--> "
                 * +(result.resultOperationSucceeded()?"success":"failure"
                 * )+": "+result.getTupleList()+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
                /*
                 * ********************* BLUCK PRIMITIVE
                 * **************************
                 */
                /*-----------------OUT-ALL------------------------*/
                /*
                 * tuple = LogicTuple.parse("[nome(luca),nome(stefano)]");
                 * Out_all op = new Out_all(tcid, tuple); //esecuzione
                 * operazione dal "mondo Jade" TucsonOpCompletionEvent result =
                 * bridge.executeSynch(op, null,this); if (result!=null) {
                 * //risultato operazione acquisito //gestione risultato
                 * log("\n\n--> "
                 * +(result.resultOperationSucceeded()?"success":"failure"
                 * )+": "+result.getTupleList()+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------RD-ALL------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); Rd_all op = new
                 * Rd_all(tcid, tuple); //esecuzione operazione dal "mondo Jade"
                 * result = bridge.executeSynch(op, null,this); if
                 * (result!=null) { //risultato operazione acquisito //gestione
                 * risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded()?"success"
                 * :"failure")+": "+result.getTupleList()+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------IN-ALL------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); In_all op = new
                 * In_all(tcid, tuple); //esecuzione operazione dal "mondo Jade"
                 * result = bridge.executeSynch(op, null,this); if
                 * (result!=null) { //risultato operazione acquisito //gestione
                 * risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded()?"success"
                 * :"failure")+": "+result.getTupleList()+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------NO-ALL------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); No_all op = new
                 * No_all(tcid, tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTupleList()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*
                 * ********************* UNIFORM PRIMITIVE
                 * **************************
                 */
                /*-----------------URD------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); U_Rd op = new U_Rd(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------URDP------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); U_Rd_p op = new
                 * U_Rd_p(tcid, tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------UIN------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); U_In op = new U_In(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------UINP------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); U_In_p op = new
                 * U_In_p(tcid, tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------UNO------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); U_No op = new U_No(tcid,
                 * tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------UNOP------------------------*/
                /*
                 * tuple = LogicTuple.parse("nome(X)"); U_No_p op = new
                 * U_No_p(tcid, tuple); //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*
                 * ********************* SPECIFICATION PRIMITIVE
                 * **************************
                 */
                /*-----------------OUTS------------------------*/
                /*
                 * Out_s op = new Out_s(tcid, LogicTuple.parse("out(nome(R))"),
                 * //event LogicTuple.parse("(completion,success)"), //guard
                 * LogicTuple.parse("(in(nome(R)), out(ciao(R)))")); //reaction
                 * //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------RDS------------------------*/
                /*
                 * Rd_s op = new Rd_s(tcid, LogicTuple.parse("out(nome(R))"),
                 * //event LogicTuple.parse("(completion,success)"), //guard
                 * LogicTuple.parse("(in(nome(R)), out(ciao(R)))")); //reaction
                 * //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------RDPS------------------------*/
                /*
                 * Rdp_s op = new Rdp_s(tcid, LogicTuple.parse("out(nome(R))"),
                 * //event LogicTuple.parse("(completion,success)"), //guard
                 * LogicTuple.parse("(in(nome(R)), out(ciao(R)))")); //reaction
                 * //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 * /*-----------------INS------------------------
                 */
                /*
                 * In_s op = new In_s(tcid, LogicTuple.parse("out(nome(R))"),
                 * //event LogicTuple.parse("(completion,success)"), //guard
                 * LogicTuple.parse("(in(nome(R)), out(ciao(R)))")); //reaction
                 * //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------INPS------------------------*/
                /*
                 * Inp_s op = new Inp_s(tcid, LogicTuple.parse("out(nome(R))"),
                 * //event LogicTuple.parse("(completion,success)"), //guard
                 * LogicTuple.parse("(in(nome(R)), out(ciao(R)))")); //reaction
                 * //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------NOS------------------------*/
                /*
                 * No_s op = new No_s(tcid, LogicTuple.parse("out(nome(R))"),
                 * //event LogicTuple.parse("(completion,success)"), //guard
                 * LogicTuple.parse("(in(nome(R)), out(ciao(R)))")); //reaction
                 * //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------NOPS------------------------*/
                /*
                 * Nop_s op = new Nop_s(tcid, LogicTuple.parse("out(nome(R))"),
                 * //event LogicTuple.parse("(completion,success)"), //guard
                 * LogicTuple.parse("(in(nome(R)), out(ciao(R)))")); //reaction
                 * //esecuzione operazione dal "mondo Jade"
                 * TucsonOpCompletionEvent result = bridge.executeSynch(op,
                 * null,this); if (result!=null) { //risultato operazione
                 * acquisito //gestione risultato
                 * log("\n\n--> "+(result.resultOperationSucceeded
                 * ()?"success":"failure")+": "+result.getTuple()+"\n\n");
                 * done=true; helper.deauthenticate(myAgent); }else{ //risultato
                 * non acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------GETS------------------------*/
                /*
                 * Get_s op = new Get_s(tcid); //esecuzione operazione dal
                 * "mondo Jade" TucsonOpCompletionEvent result =
                 * bridge.executeSynch(op, null,this); if (result!=null) {
                 * //risultato operazione acquisito //gestione risultato
                 * log("\n\n--> "
                 * +(result.resultOperationSucceeded()?"success":"failure"
                 * )+": "+result.getTupleList()+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
                /*-----------------SETS------------------------*/
                /*
                 * tuple = LogicTuple.parse(
                 * "[((out(ciao(X))),(completion,success),(in(ciao(X)),out(ciao(X))))]"
                 * ); Set_s op = new Set_s(tcid, tuple); //esecuzione operazione
                 * dal "mondo Jade" TucsonOpCompletionEvent result =
                 * bridge.executeSynch(op, null,this); if (result!=null) {
                 * //risultato operazione acquisito //gestione risultato
                 * log("\n\n--> "
                 * +(result.resultOperationSucceeded()?"success":"failure"
                 * )+": "+result.getTupleList()+"\n\n"); done=true;
                 * helper.deauthenticate(myAgent); }else{ //risultato non
                 * acquisito log("mi blocco"); done=false; block(); }
                 */
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            // TODO Auto-generated method stub
            return TestJadeAgent_Sync_All_Primitive.this.done;
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
