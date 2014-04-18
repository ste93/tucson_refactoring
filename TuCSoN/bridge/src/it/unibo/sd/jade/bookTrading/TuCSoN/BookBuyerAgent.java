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

import it.unibo.sd.jade.exceptions.CannotAcquireACCException;
import it.unibo.sd.jade.glue.BridgeToTucson;
import it.unibo.sd.jade.operations.bulk.OutAll;
import it.unibo.sd.jade.operations.bulk.RdAll;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.SimpleBehaviour;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.StringTokenizer;
import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * 
 * @author lucasangiorgi
 * 
 */
public class BookBuyerAgent extends Agent {
    /*
     * Behaviour that search the services and try to buy a book
     */
    private class BookBuyerBehaviour extends SimpleBehaviour {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /*
         * condition to terminate behaviour
         */
        private boolean ok = false;

        @Override
        public void action() {
            LogicTuple tuple;
            try {
                /*
                 * get available seller for service "bookselling"
                 */
                tuple = LogicTuple.parse("service(S,bookselling)");
                final RdAll op1 = new RdAll(BookBuyerAgent.this.tcid, tuple);
                /*
                 * execute sync operation - rd_all don't block but return list
                 * empty if there aren't services available
                 */
                TucsonOpCompletionEvent result = BookBuyerAgent.this.bridge
                        .synchronousInvocation(op1, null, this);
                if (result != null) { // operation complete
                    /*
                     * get AID of the sellers
                     */
                    final ArrayList<String> sellers = new ArrayList<String>();
                    for (final LogicTuple res : result.getTupleList()) {
                        sellers.add(res.getArg(0).toString()); // idSeller
                    }
                    if (!sellers.isEmpty()) { // there are some sellers
                        // send cfp
                        String t = "";
                        for (int i = 0; i < sellers.size(); i++) {
                            if (i < sellers.size() - 1) { // this isn't last
                                                          // tuple
                                t += "cfp(" + sellers.get(i) + ",bookselling,"
                                        + BookBuyerAgent.this.idBuyer + ","
                                        + BookBuyerAgent.this.targetBookTitle
                                        + "),";
                            } else {
                                t += "cfp(" + sellers.get(i) + ",bookselling,"
                                        + BookBuyerAgent.this.idBuyer + ","
                                        + BookBuyerAgent.this.targetBookTitle
                                        + ")";
                            }
                        }
                        tuple = LogicTuple.parse("[" + t + "]");
                        final OutAll op2 = new OutAll(BookBuyerAgent.this.tcid,
                                tuple);
                        BookBuyerAgent.this.bridge.synchronousInvocation(op2,
                                null, this);
                        Float maxPrice = new Float(0);
                        int idxBestSeller = -1;
                        int receiveAll = 0;
                        // receive responce for cfp
                        for (int i = 0; i < sellers.size(); i++) {
                            // X-> propose/refuse P->price/not-available
                            tuple = LogicTuple.parse("responsecfp(X,"
                                    + BookBuyerAgent.this.idBuyer + ","
                                    + BookBuyerAgent.this.targetBookTitle + ","
                                    + sellers.get(i) + ",P)");
                            final In op3 = new In(BookBuyerAgent.this.tcid,
                                    tuple);
                            result = BookBuyerAgent.this.bridge
                                    .synchronousInvocation(op3, null, this);
                            if (result != null) { // operation complete
                                /*
                                 * get the value of X from the tuple
                                 */
                                final String propose = result.getTuple()
                                        .getArg(0).toString();
                                if ("propose".equals(propose)) { // book
                                                                 // available
                                    receiveAll++;
                                    if (result.getTuple().getArg(4)
                                            .floatValue() >= maxPrice) { // bestseller?
                                        maxPrice = result.getTuple().getArg(4)
                                                .floatValue();
                                        idxBestSeller = i;
                                    }
                                    BookBuyerAgent.this
                                            .log("book available from seller "
                                                    + sellers.get(i));
                                } else { // book not available
                                    BookBuyerAgent.this
                                            .log("book not available from seller '"
                                                    + sellers.get(i) + "'");
                                    sellers.remove(i);
                                    i--;
                                }
                            } else {
                                BookBuyerAgent.this
                                        .log("block() for op: in(responsecfp())");
                                this.ok = false;
                                this.block();
                                return; // don't do anything else to wait the
                                        // completition of operation
                            }
                        }
                        if (receiveAll == sellers.size() && receiveAll > 0) { // i
                            // have
                            // checked
                            // all
                            // sellers
                            // and
                            // someone
                            // have
                            // the
                            // book
                            // available
                            /*
                             * tuple to send
                             */
                            String s = "";
                            String propose = "";
                            /*
                             * send proposal
                             */
                            for (int i = 0; i < sellers.size(); i++) {
                                if (idxBestSeller == i) { // bestseller
                                    propose = "accept";
                                } else {
                                    propose = "reject";
                                }
                                if (i < sellers.size() - 1) { // not last
                                                              // tuple
                                    s += "proposal("
                                            + propose
                                            + ","
                                            + BookBuyerAgent.this.idBuyer
                                            + ",bookselling,"
                                            + sellers.get(i)
                                            + ","
                                            + BookBuyerAgent.this.targetBookTitle
                                            + "),";
                                } else {
                                    s += "proposal("
                                            + propose
                                            + ","
                                            + BookBuyerAgent.this.idBuyer
                                            + ",bookselling,"
                                            + sellers.get(i)
                                            + ","
                                            + BookBuyerAgent.this.targetBookTitle
                                            + ")";
                                }
                            }
                            tuple = LogicTuple.parse("[" + s + "]");
                            final OutAll op3 = new OutAll(
                                    BookBuyerAgent.this.tcid, tuple);
                            BookBuyerAgent.this.bridge.synchronousInvocation(
                                    op3, null, this);
                            /*
                             * confirm from bestseller X->inform/failuer
                             */
                            tuple = LogicTuple
                                    .parse("performative(X,"
                                            + BookBuyerAgent.this.idBuyer
                                            + ",bookselling,"
                                            + sellers.get(idxBestSeller)
                                            + ","
                                            + BookBuyerAgent.this.targetBookTitle
                                            + ")");
                            final In op4 = new In(BookBuyerAgent.this.tcid,
                                    tuple);
                            result = BookBuyerAgent.this.bridge
                                    .synchronousInvocation(op4, null, this);
                            if (result != null) { // operation complete
                                if ("inform".equals(result.getTuple().getArg(0)
                                        .toString())) { // book acquired
                                    BookBuyerAgent.this.log("book acquired!!!");
                                    this.ok = true;
                                } else {
                                    BookBuyerAgent.this
                                            .log("book not more available!!(failuer");
                                    BookBuyerAgent.this.bridge
                                            .clearTucsonOpResult(this);
                                    this.ok = true;
                                }
                                BookBuyerAgent.this.bridge
                                        .clearTucsonOpResult(this);
                            } else {
                                BookBuyerAgent.this
                                        .log("block for on(performative)");
                                this.ok = false;
                                this.block();
                            }
                        } else {
                            BookBuyerAgent.this
                                    .log("book not available from no one seller!!");
                            BookBuyerAgent.this.bridge
                                    .clearTucsonOpResult(this);
                            this.ok = true;
                        }
                    } else {
                        BookBuyerAgent.this
                                .log("there aren't sellers available!!");
                        BookBuyerAgent.this.bridge.clearTucsonOpResult(this);
                        this.ok = true;
                    }
                } else {
                    BookBuyerAgent.this.log("block() for rd_all(service)");
                    this.ok = false;
                    this.block();
                }
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public boolean done() {
            // TODO Auto-generated method stub
            if (this.ok) {
                BookBuyerAgent.this.log("THE END");
            }
            return this.ok;
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /*
     * The class to execute TuCSoN operations
     */
    private BridgeToTucson bridge;
    /*
     * AID of buyer
     */
    private String idBuyer;
    /*
     * The title of the book to buy.
     */
    private String targetBookTitle;
    /*
     * ID of tuple centre
     */
    private TucsonTupleCentreId tcid;

    /*
     * Just draw a random book title from an input file.
     */
    private String bootBookTitle() {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(
                    "bin/it/unibo/sd/jade/bookTrading/TuCSoN/books.cat"));
            String line;
            StringTokenizer st;
            final LinkedList<String> titles = new LinkedList<String>();
            line = br.readLine();
            while (line != null) {
                st = new StringTokenizer(line, ";");
                titles.add(st.nextToken());
                line = br.readLine();
            }
            br.close();
            return titles.get((int) Math.round(Math.random()
                    * (titles.size() - 1)));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            this.doDelete();
        } catch (final IOException e) {
            e.printStackTrace();
            this.doDelete();
        }
        return "";
    }

    private void log(final String msg) {
        System.out.println("[" + this.getName() + "]: " + msg);
    }

    @Override
    protected void setup() {
        this.idBuyer = this.getAID().getName();
        try {
            final TucsonHelper helper = (TucsonHelper) this
                    .getHelper(TucsonService.NAME);
            /*
             * Obtain ACC
             */
            helper.acquireACC(this);
            /*
             * get tuple centre id
             */
            this.tcid = helper.getTucsonTupleCentreId("default", "localhost",
                    20504);
            /*
             * get the univocal bridge for the agent
             */
            this.bridge = helper.getBridgeToTucson(this);
            /*
             * Randomly draw the book to buy from .catalog file.
             */
            this.targetBookTitle = this.bootBookTitle();
            System.out.println("targetBook that i want: "
                    + this.targetBookTitle);
            this.addBehaviour(new BookBuyerBehaviour());
        } catch (final ServiceException e1) {
            e1.printStackTrace();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (final CannotAcquireACCException e) {
            e.printStackTrace();
        }
    }
}
