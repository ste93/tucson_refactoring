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
package it.unibo.sd.jade.examples.bookTrading;

import it.unibo.sd.jade.exceptions.CannotAcquireACCException;
import it.unibo.sd.jade.glue.BridgeToTucson;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;
import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.service.TucsonOpCompletionEvent;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * Adapted from Giovanni Caire's Book Trading example in examples.bookTrading
 * within JADE distribution. This is the seller agent, showing how to exploit
 * TuCSoN4JADE integration services.
 * 
 * @author s.mariani@unibo.it
 */
public class BookSellerAgent extends Agent {
    /*
     * Cyclic behaviour waiting for books availability requests. If the
     * requested book is in the local catalogue, the seller agent replies with a
     * PROPOSE message specifying the price. Otherwise a REFUSE message is sent
     * back.
     */
    private class OfferRequestsServer extends Behaviour {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            BookSellerAgent.this.log("Waiting for CFP messages...");
            LogicTuple cfp = null;
            try {
                cfp = LogicTuple.parse("cfp(to("
                        + BookSellerAgent.this.getAID().getName()
                        + "), from(B), book(T))");
            } catch (final InvalidTupleException e) {
                // should not happen
                e.printStackTrace();
                BookSellerAgent.this.doDelete();
            }
            final In in = new In(BookSellerAgent.this.tcid, cfp);
            TucsonOpCompletionEvent result = null;
            try {
                result = BookSellerAgent.this.bridge.synchronousInvocation(in,
                        null, this);
            } catch (final ServiceException e) {
                BookSellerAgent.this
                        .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                BookSellerAgent.this.doDelete();
            }
            try {
                if (result != null) {
                    final String buyer = result.getTuple().getArg(1).getArg(0)
                            .toString();
                    BookSellerAgent.this.log("Received CFP from " + buyer);
                    /*
                     * We expect the title of the book as the content of the
                     * message.
                     */
                    final String title = result.getTuple().getArg(2).getArg(0)
                            .toString();
                    /*
                     * We check availability of the requested book.
                     */
                    BookSellerAgent.this.log("Checking availability for book "
                            + title);
                    final Float price = BookSellerAgent.this.catalogue
                            .get(title);
                    String p;
                    if (price != null) {
                        BookSellerAgent.this.buyers.add(buyer);
                        /*
                         * The requested book is available, reply with its
                         * price.
                         */
                        BookSellerAgent.this.log("Book " + title
                                + " available, proposing price > " + price);
                        p = String.valueOf(price);
                    } else {
                        /*
                         * The requested book is NOT available, reply
                         * accordingly.
                         */
                        BookSellerAgent.this.log("Book " + title
                                + " NOT available, informing client...");
                        p = "unavailable";
                    }
                    final LogicTuple proposal = LogicTuple.parse("proposal(to("
                            + buyer + "), book(" + title + "), from("
                            + BookSellerAgent.this.getAID().getName()
                            + "), price(" + p + "))");
                    final Out out = new Out(BookSellerAgent.this.tcid, proposal);
                    BookSellerAgent.this.bridge.asynchronousInvocation(out);
                } else {
                    BookSellerAgent.this.log("No CFP yet...");
                    this.block();
                }
            } catch (final InvalidOperationException e) {
                // should not happen
                e.printStackTrace();
                BookSellerAgent.this.doDelete();
            } catch (final InvalidTupleException e) {
                // should not happen
                e.printStackTrace();
                BookSellerAgent.this.doDelete();
            } catch (final ServiceException e) {
                BookSellerAgent.this
                        .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                BookSellerAgent.this.doDelete();
            }
        }

        @Override
        public boolean done() {
            return BookSellerAgent.this.catalogue.isEmpty();
        }

        @Override
        public int onEnd() {
            BookSellerAgent.this.log("Terminating...");
            this.myAgent.doDelete();
            return super.onEnd();
        }
    }

    /*
     * Cyclic behaviour waiting for books purchase requests. The seller agent
     * removes the purchased book from its catalogue and replies with an INFORM
     * message to notify the buyer that the purchase has been successfully
     * completed.
     */
    private class PurchaseOrdersServer extends Behaviour {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            BookSellerAgent.this.log("Waiting for purchase orders...");
            LogicTuple order = null;
            try {
                order = LogicTuple.parse("order(X, from(B), to("
                        + BookSellerAgent.this.getAID().getName()
                        + "), book(T))");
            } catch (final InvalidTupleException e) {
                // should not happen
                e.printStackTrace();
                BookSellerAgent.this.doDelete();
            } // x -> accept or reject
            final In in = new In(BookSellerAgent.this.tcid, order);
            TucsonOpCompletionEvent result = null;
            try {
                result = BookSellerAgent.this.bridge.synchronousInvocation(in,
                        null, this);
            } catch (final ServiceException e) {
                BookSellerAgent.this
                        .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                BookSellerAgent.this.doDelete();
            }
            if (result != null) {
                try {
                    final String reply = result.getTuple().getArg(0).toString();
                    final String buyer = result.getTuple().getArg(1).getArg(0)
                            .toString();
                    final String title = result.getTuple().getArg(3).getArg(0)
                            .toString();
                    if (BookSellerAgent.this.buyers.contains(buyer)) {
                        BookSellerAgent.this.buyers.remove(buyer);
                        if ("accept".equals(reply)) { // accept
                            BookSellerAgent.this
                                    .log("Received purchase order from "
                                            + buyer);
                            final Float priceBook = BookSellerAgent.this.catalogue
                                    .remove(title);
                            /*
                             * The requested book may be sold to another buyer
                             * in the meanwhile...
                             */
                            String c;
                            if (priceBook != null) {
                                BookSellerAgent.this.log("Selling book "
                                        + title + " to agent " + buyer);
                                c = "confirm";
                            } else {
                                BookSellerAgent.this.log("Sorry, book " + title
                                        + " is not available anymore :(");
                                c = "failure";
                            }
                            final LogicTuple confirmation = LogicTuple
                                    .parse("purchase("
                                            + c
                                            + ", to("
                                            + buyer
                                            + "), from("
                                            + BookSellerAgent.this.getAID()
                                                    .getName() + "), book("
                                            + title + "))");
                            /*
                             * sending performative
                             */
                            final Out out = new Out(BookSellerAgent.this.tcid,
                                    confirmation);
                            BookSellerAgent.this.bridge
                                    .asynchronousInvocation(out);
                        } else { // reject
                            BookSellerAgent.this.log("Client " + buyer
                                    + " rejected " + title);
                        }
                    } else {
                        BookSellerAgent.this.log("Client " + buyer
                                + " is cheating :O");
                    }
                } catch (final InvalidOperationException e) {
                    // should not happen
                    e.printStackTrace();
                    BookSellerAgent.this.doDelete();
                } catch (final InvalidTupleException e) {
                    // should not happen
                    e.printStackTrace();
                    BookSellerAgent.this.doDelete();
                } catch (final ServiceException e) {
                    BookSellerAgent.this
                            .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                    BookSellerAgent.this.doDelete();
                }
            } else {
                BookSellerAgent.this.log("No purchase orders yet...");
                this.block();
            }
        }

        @Override
        public boolean done() {
            return BookSellerAgent.this.catalogue.isEmpty();
        }

        @Override
        public int onEnd() {
            BookSellerAgent.this.log("Terminating...");
            this.myAgent.doDelete();
            return super.onEnd();
        }
    }

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /*
     * Advertisement tuple
     */
    private LogicTuple adv;
    /*
     * The bridge class to execute TuCSoN operations
     */
    private BridgeToTucson bridge;
    /*
     * list of potential buyers
     */
    private Queue<String> buyers;
    /*
     * The catalogue of books for sale (maps the title of a book to its price).
     */
    private Hashtable<String, Float> catalogue;
    /*
     * ID of tuple centre used for objective coordination
     */
    private TucsonTupleCentreId tcid;

    /*
     * Just reads books and random prices from an input file.
     */
    private void bootCatalogue() {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(
                    "bin/it/unibo/sd/jade/examples/bookTrading/books.cat"));
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
            System.out.println("\t title: " + key + "\t\t price: "
                    + this.catalogue.get(key));
        }
    }

    @Override
    protected void setup() {
        this.log("I'm started.");
        this.catalogue = new Hashtable<String, Float>();
        this.buyers = new ConcurrentLinkedQueue<String>();
        /*
         * Boot catalogue from .catalog file (drawing random prices) and print
         * out the outcome.
         */
        this.bootCatalogue();
        this.printCatalogue();
        TucsonHelper helper;
        try {
            helper = (TucsonHelper) this.getHelper(TucsonService.NAME);
            /*
             * Obtain ACC
             */
            helper.acquireACC(this);
            /*
             * get tuple centre id
             */
            this.tcid = helper.buildTucsonTupleCentreId("default", "localhost",
                    20504);
            /*
             * get the univocal bridge for the agent
             */
            this.bridge = helper.getBridgeToTucson(this);
            /*
             * advertisment of the service provided
             */
            this.log("Advertising 'book-trading' service to the 'default' tuple centre...");
            this.adv = LogicTuple.parse("advertise(provider("
                    + this.getAID().getName() + "), service('book-trading'))");
            final Out out = new Out(this.tcid, this.adv);
            this.bridge.asynchronousInvocation(out);
        } catch (final ServiceException e) {
            this.log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
            this.doDelete();
        } catch (final TucsonInvalidAgentIdException e) {
            this.log(">>> TuCSoN Agent ids should be compliant with Prolog sytnax (start with lowercase letter, no special symbols), choose another agent id <<<");
            this.doDelete();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // should not happen
            e.printStackTrace();
            this.doDelete();
        } catch (final CannotAcquireACCException e) {
            // should not happen
            e.printStackTrace();
            this.doDelete();
        } catch (final InvalidTupleException e) {
            // should not happen
            e.printStackTrace();
            this.doDelete();
        }
        /*
         * Add the behaviour serving CFPs from buyer agents.
         */
        this.addBehaviour(new OfferRequestsServer());
        /*
         * Add the behaviour serving purchase orders from buyer agents.
         */
        this.addBehaviour(new PurchaseOrdersServer());
    }

    /*
     * Remember to deregister the services offered by the agent upon shutdown,
     * because the JADE platform does not do it by itself!
     */
    @Override
    protected void takeDown() {
        /*
         * deregistration of the service
         */
        this.log("De-advertising myself from the 'default' tuple centre...");
        final In in = new In(this.tcid, this.adv);
        try {
            this.bridge.asynchronousInvocation(in);
            this.log("I'm done.");
        } catch (final ServiceException e) {
            this.log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
            this.doDelete();
        }
    }
}
