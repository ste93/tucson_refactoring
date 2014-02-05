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
import it.unibo.sd.jade.glue.BridgeToTucson;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.CyclicBehaviour;
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
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * 
 * @author lucasangiorgi
 * 
 */
@SuppressWarnings("serial")
public class BookSellerAgent extends Agent {

    /*
     * behavior that wait for request of service from buyer
     */
    private class BookSellerBehaviour extends CyclicBehaviour {

        @Override
        public void action() {

            LogicTuple tuple;

            try {
                tuple =
                        LogicTuple.parse("cfp(" + BookSellerAgent.this.idSeller
                                + ",bookselling,I,B)");
                final In op = new In(BookSellerAgent.this.tcid, tuple);
                final TucsonOpCompletionEvent result =
                        BookSellerAgent.this.bridge
                                .synchronousInvocation(op, null, this);
                if (result != null) { // operation complete

                    this.myAgent.addBehaviour(new ContractWithBuyerBehaviour(
                            result.getTuple().getArg(2).toString(), result
                                    .getTuple().getArg(3).toString()));

                    BookSellerAgent.this.bridge
                            .clearTucsonOpResult(this); // clean
                                                               // coordination
                                                               // structure to
                                                               // don't evaluate
                                                               // the same
                                                               // operation and
                                                               // so return the
                                                               // same result

                } else { // pending operation

                    BookSellerAgent.this.log("block() in(cfp)");
                    this.block();
                }
            } catch (final InvalidLogicTupleException e) {
                e.printStackTrace();
            } catch (final ServiceException e) {
                e.printStackTrace();
            } catch (final InvalidTupleOperationException e) {
                e.printStackTrace();
            }
        }

    }

    /*
     * behaviour that contract with only one buyer
     */
    private class ContractWithBuyerBehaviour extends SimpleBehaviour {

        private boolean ok;
        private final String idBuyer;
        private final String targetBookTitle;

        public ContractWithBuyerBehaviour(final String id, final String tit) {
            super();
            this.idBuyer = id;
            this.targetBookTitle = tit;
        }

        @Override
        public void action() {

            LogicTuple tuple;

            try {

                // check availability of the book
                final Float price =
                        BookSellerAgent.this.catalogue
                                .get(this.targetBookTitle);
                if (price != null) {
                    /*
                     * The requested book is available, reply with its price.
                     */
                    BookSellerAgent.this.log("Book '" + this.targetBookTitle
                            + "' available, proposing price..." + price);
                    tuple =
                            LogicTuple.parse("responsecfp(propose,"
                                    + this.idBuyer + "," + this.targetBookTitle
                                    + "," + BookSellerAgent.this.idSeller + ","
                                    + price + ")");
                    final Out op1 = new Out(BookSellerAgent.this.tcid, tuple);
                    BookSellerAgent.this.bridge.synchronousInvocation(op1, null, this);

                    /*
                     * wait for the buyer's proposal
                     */
                    tuple =
                            LogicTuple.parse("proposal(X," + this.idBuyer
                                    + ",bookselling,"
                                    + BookSellerAgent.this.idSeller + ","
                                    + this.targetBookTitle + ")"); // x ->
                                                                   // accept or
                                                                   // reject
                    final In op2 = new In(BookSellerAgent.this.tcid, tuple);
                    final TucsonOpCompletionEvent result =
                            BookSellerAgent.this.bridge.synchronousInvocation(op2, null,
                                    this);
                    if (result != null) {

                        final String proposal =
                                result.getTuple().getArg(0).toString();
                        if ("accept".equals(proposal)) { // accept

                            final Float priceBook =
                                    BookSellerAgent.this.catalogue
                                            .remove(this.targetBookTitle);
                            /*
                             * The requested book may be sold to another buyer
                             * in the meanwhile...
                             */
                            if (priceBook != null) {
                                BookSellerAgent.this.log("Selling book '"
                                        + this.targetBookTitle + "' to agent '"
                                        + this.idBuyer + "'...");
                                tuple =
                                        LogicTuple.parse("performative(inform,"
                                                + this.idBuyer
                                                + ",bookselling,"
                                                + BookSellerAgent.this.idSeller
                                                + "," + this.targetBookTitle
                                                + ")");

                            } else {
                                BookSellerAgent.this.log("Sorry, book '"
                                        + this.targetBookTitle
                                        + "' is not available anymore.");
                                tuple =
                                        LogicTuple
                                                .parse("performative(failure,"
                                                        + this.idBuyer
                                                        + ",bookselling,"
                                                        + BookSellerAgent.this.idSeller
                                                        + ","
                                                        + this.targetBookTitle
                                                        + ")");

                            }
                            /*
                             * sending performative
                             */
                            final Out op3 =
                                    new Out(BookSellerAgent.this.tcid, tuple);
                            BookSellerAgent.this.bridge.synchronousInvocation(op3, null,
                                    this);
                            this.ok = true;
                            BookSellerAgent.this.bridge
                                    .clearTucsonOpResult(this);

                        } else { // reject

                            BookSellerAgent.this.log("Client '" + this.idBuyer
                                    + "' reject '" + this.targetBookTitle
                                    + "'...");
                            this.ok = true;
                            BookSellerAgent.this.bridge
                                    .clearTucsonOpResult(this);

                        }

                    } else {

                        BookSellerAgent.this.log("block() in(proposal)");
                        this.block();
                        this.ok = false;

                    }

                } else {
                    /*
                     * The requested book is NOT available, reply accordingly.
                     */
                    BookSellerAgent.this.log("Book '" + this.targetBookTitle
                            + "' NOT available, informing client...");
                    tuple =
                            LogicTuple.parse("responsecfp(refuse,"
                                    + this.idBuyer + "," + this.targetBookTitle
                                    + "," + BookSellerAgent.this.idSeller
                                    + ",not-available)");

                    final Out op1 = new Out(BookSellerAgent.this.tcid, tuple);
                    BookSellerAgent.this.bridge.synchronousInvocation(op1, null, this);
                    this.ok = true;
                    BookSellerAgent.this.bridge
                            .clearTucsonOpResult(this);

                }

            } catch (final InvalidLogicTupleException e) {
                e.printStackTrace();
            } catch (final InvalidTupleOperationException e) {
                e.printStackTrace();
            } catch (final ServiceException e) {
                e.printStackTrace();
            }

        }

        @Override
        public boolean done() {
            return this.ok;
        }

    }

    /*
     * The class to execute TuCSoN operations
     */
    private BridgeToTucson bridge;
    /*
     * The catalogue of books for sale (maps the title of a book to its price).
     */
    private Hashtable<String, Float> catalogue;

    /*
     * AID of seller
     */
    private String idSeller;

    /*
     * ID of tuple centre
     */
    private TucsonTupleCentreId tcid;

    @Override
    protected void setup() {

        this.log("I'm started.");

        this.idSeller = this.getAID().getName();
        this.catalogue = new Hashtable<String, Float>();
        /*
         * Boot catalogue from .catalog file (drawing random prices) and print
         * out the outcome.
         */
        this.bootCatalogue();
        this.printCatalogue();

        try {
            final TucsonHelper helper =
                    (TucsonHelper) this.getHelper(TucsonService.NAME);
            /*
             * Obtain ACC
             */
            helper.acquireACC(this);
            /*
             * get tuple centre id
             */
            this.tcid = helper.getTucsonTupleCentreId("default", "localhost", 20504);

            /*
             * get the univocal bridge for the agent
             */
            this.bridge = helper.getBridgeToTucson(this);

            /*
             * registration of the service
             */
            final LogicTuple tuple =
                    LogicTuple.parse("service(" + this.idSeller
                            + ",bookselling)");
            final Out op = new Out(this.tcid, tuple);
            this.bridge.asynchronousInvocation(op);

            this.addBehaviour(new BookSellerBehaviour());

        } catch (final ServiceException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (final NoTucsonAuthenticationException e) {
            e.printStackTrace();
        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * Remember to deregister the services offered by the agent upon shutdown,
     * because the JADE platform does not do it by itself!
     */
    @Override
    protected void takeDown() {

        LogicTuple tuple;
        try {
            /*
             * deregistration of the service
             */
            tuple =
                    LogicTuple.parse("service(" + this.idSeller
                            + ",bookselling)");
            final In op = new In(this.tcid, tuple);
            this.bridge.asynchronousInvocation(op);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * Just reads books and random prices from an input file.
     */
    private void bootCatalogue() {
        try {
            final BufferedReader br =
                    new BufferedReader(
                            new FileReader(
                                    "bin/it/unibo/sd/jade/bookTrading/TuCSoN/books.cat"));
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
