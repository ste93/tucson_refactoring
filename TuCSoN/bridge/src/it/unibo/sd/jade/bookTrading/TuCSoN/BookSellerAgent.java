/*****************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent
 * systems in compliance with the FIPA specifications. Copyright (C) 2000 CSELT
 * S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *****************************************************************/
package it.unibo.sd.jade.bookTrading.TuCSoN;

import it.unibo.sd.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.sd.jade.glue.BridgeJADETuCSoN;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.SimpleBehaviour;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * Adapted from Giovanni Caire's Book Trading example in examples.bookTrading
 * within JADE distribution. This is the seller agent, showing how to register
 * to JADE DF in order to offer a service.
 * 
 * @author s.mariani@unibo.it
 */
public class BookSellerAgent extends Agent {

    @SuppressWarnings("serial")
    private class BookSellerBehaviour extends SimpleBehaviour {

        @Override
        public void action() {
            // Attesa richiesta del servizio
            LogicTuple tuple;
            try {
                tuple =
                        LogicTuple.parse("cfp(" + BookSellerAgent.this.idSeller
                                + ",sellbook,I,B)");
                final In op = new In(BookSellerAgent.this.tcid, tuple);
                TucsonOpCompletionEvent result =
                        BookSellerAgent.this.bridge
                                .executeSynch(op, null, this);
                if (result != null) { // operazione completata
                    final String bookTitle =
                            result.getTuple().getArg(3).toString(); // value of
                                                                    // B
                    final String idClient =
                            result.getTuple().getArg(2).toString(); // value of
                                                                    // I
                    // check disponibilità libro
                    final Float price =
                            BookSellerAgent.this.catalogue.get(bookTitle);
                    if (price != null) {
                        /*
                         * The requested book is available, reply with its
                         * price.
                         */
                        BookSellerAgent.this.log("Book '" + bookTitle
                                + "' available, proposing price...");
                        tuple =
                                LogicTuple.parse("responsecfp(propose,"
                                        + idClient + "," + bookTitle + ","
                                        + String.valueOf(price) + ","
                                        + BookSellerAgent.this.idSeller + ")");
                        final Out op1 =
                                new Out(BookSellerAgent.this.tcid, tuple);
                        BookSellerAgent.this.bridge.executeSynch(op1, null,
                                this);
                        tuple =
                                LogicTuple.parse("proposal(X," + idClient + ","
                                        + BookSellerAgent.this.idSeller + ","
                                        + bookTitle + ")"); // x -> accept or
                                                            // reject
                        final In op2 = new In(BookSellerAgent.this.tcid, tuple);
                        result =
                                BookSellerAgent.this.bridge.executeSynch(op2,
                                        null, this);
                        if (result != null) {
                            final String proposal =
                                    result.getTuple().getArg(0).toString();
                            if ("accept".equals(proposal)) { // accept
                                final Float priceBook =
                                        BookSellerAgent.this.catalogue
                                                .remove(bookTitle);
                                /*
                                 * The requested book may be sold to another
                                 * buyer in the meanwhile...
                                 */
                                if (priceBook != null) {
                                    BookSellerAgent.this.log("Selling book '"
                                            + bookTitle + "' to agent '"
                                            + idClient + "'...");
                                    tuple =
                                            LogicTuple
                                                    .parse("performative(inform,"
                                                            + idClient
                                                            + ","
                                                            + bookTitle
                                                            + ","
                                                            + BookSellerAgent.this.idSeller
                                                            + ")");
                                } else {
                                    BookSellerAgent.this.log("Sorry, book '"
                                            + bookTitle
                                            + "' is not available anymore.");
                                    tuple =
                                            LogicTuple
                                                    .parse("performative(failure,"
                                                            + idClient
                                                            + ","
                                                            + bookTitle
                                                            + ","
                                                            + BookSellerAgent.this.idSeller
                                                            + ")");
                                }
                                final Out op3 =
                                        new Out(BookSellerAgent.this.tcid,
                                                tuple);
                                BookSellerAgent.this.bridge.executeSynch(op3,
                                        null, this);
                            } else { // reject
                                BookSellerAgent.this.log("Client '" + idClient
                                        + "' reject '" + bookTitle + "'...");
                            }
                        } else {
                            BookSellerAgent.this.log("mi blocco");
                            this.block();
                        }
                    } else {
                        /*
                         * The requested book is NOT available, reply
                         * accordingly.
                         */
                        BookSellerAgent.this.log("Book '" + bookTitle
                                + "' NOT available, informing client...");
                        tuple =
                                LogicTuple.parse("responsecfp(refuse,"
                                        + idClient + "," + bookTitle
                                        + ",not-available,"
                                        + BookSellerAgent.this.idSeller + ")");
                        final Out op1 =
                                new Out(BookSellerAgent.this.tcid, tuple);
                        BookSellerAgent.this.bridge.executeSynch(op1, null,
                                this);
                    }
                } else { // operazione pendente
                    BookSellerAgent.this.log("mi blocco");
                    this.block();
                }
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } // idSeller,sellbook,idUtente,booktitle
        }

        @Override
        public boolean done() {
            // TODO Auto-generated method stub
            return false;
        }
    }

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    private BridgeJADETuCSoN bridge;
    /*
     * The catalogue of books for sale (maps the title of a book to its price).
     */
    private Hashtable<String, Float> catalogue;
    private TucsonHelper helper;
    private String idSeller;
    private TucsonTupleCentreId tcid;

    @Override
    protected void setup() {
        this.log("I'm started.");
        this.catalogue = new Hashtable<String, Float>();
        this.idSeller = this.getAID().toString();
        /*
         * Boot catalogue from .catalog file (drawing random prices) and print
         * out the outcome.
         */
        this.bootCatalogue();
        this.printCatalogue();
        try {
            this.helper = (TucsonHelper) this.getHelper(TucsonService.NAME);
            // Ottengo ACC
            this.helper.authenticate(this);
            // Creo operazione
            this.tcid =
                    this.helper.getTupleCentreId("default", "localhost", 20504);
            LogicTuple tuple = null;
            // acquisizione del bridge univoco per l'agente
            this.bridge = this.helper.getBridgeJADETuCSoN(this);
            // registrazione al servizio
            tuple =
                    LogicTuple.parse("service(" + this.toString()
                            + ",book-selling)");
            final Out op = new Out(this.tcid, tuple);
            this.bridge.executeSynch(op, null);
            this.addBehaviour(new BookSellerBehaviour());
        } catch (final TucsonInvalidAgentIdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final NoTucsonAuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final ServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvalidLogicTupleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * Remember to deregister the services offered by the agent upon shutdown,
     * because the JADE platform does not do it by itself!
     */
    @Override
    protected void takeDown() {
        this.log("I'm done. Remember to deregire the service!!");
    }

    /*
     * Just reads books and random prices from an input file.
     */
    private void bootCatalogue() {
        try {
            final BufferedReader br =
                    new BufferedReader(
                            new FileReader(
                                    "bin/ds/lab/jade/bookTrading/contractNet/books.cat"));
            String line;
            StringTokenizer st;
            String title;
            LinkedList<Float> prices;
            line = br.readLine();
            while (line != null) {
                st = new StringTokenizer(line, ";");
                title = st.nextToken();
                prices = new LinkedList<Float>();
                while (st.hasMoreTokens()) {
                    prices.add(Float.parseFloat(st.nextToken()));
                }
                this.catalogue.put(
                        title,
                        prices.get((int) Math.round(Math.random()
                                * (prices.size() - 1))));
                line = br.readLine();
            }
            br.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            this.doDelete();
        } catch (final IOException e) {
            e.printStackTrace();
            this.doDelete();
        }
    }

    private void log(final String msg) {
        System.out.println("[" + this.getName() + "]: " + msg);
    }

    private void printCatalogue() {
        final Enumeration<String> keys = this.catalogue.keys();
        String key;
        this.log("My catalogue is:");
        for (int i = 0; i < this.catalogue.size(); i++) {
            key = keys.nextElement();
            System.out.println("\ttitle: " + key + " price: "
                    + this.catalogue.get(key));
        }
    }
}
