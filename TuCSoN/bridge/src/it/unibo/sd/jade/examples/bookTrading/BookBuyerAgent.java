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
import it.unibo.sd.jade.operations.bulk.OutAll;
import it.unibo.sd.jade.operations.bulk.RdAll;
import it.unibo.sd.jade.operations.ordinary.In;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.service.TucsonHelper;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.service.TucsonOpCompletionEvent;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * Adapted from Giovanni Caire's Book Trading example in examples.bookTrading
 * within JADE distribution. This is the buyer agent, showing how to query JADE
 * DF in order to look for a desired service.
 * 
 * @author s.mariani@unibo.it
 */
public class BookBuyerAgent extends Agent {
    /*
     * Behaviour sending the Call for Proposals to agents previously found.
     */
    private class CFPSender extends OneShotBehaviour {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            String cfp = "";
            BookBuyerAgent.this.log("Sending CFP for book "
                    + BookBuyerAgent.this.targetBookTitle + " to agents...");
            for (final String p : BookBuyerAgent.this.sellerAgents) {
                cfp += "cfp(to(" + p + "), from("
                        + BookBuyerAgent.this.getAID().getName() + "), book("
                        + BookBuyerAgent.this.targetBookTitle + ")),";
                System.out.println("\t ..." + p);
            }
            cfp = cfp.substring(0, cfp.length() - 1);
            try {
                final OutAll outall = new OutAll(BookBuyerAgent.this.tcid,
                        LogicTuple.parse("[" + cfp + "]"));
                BookBuyerAgent.this.bridge.asynchronousInvocation(outall);
            } catch (final ServiceException e) {
                BookBuyerAgent.this
                        .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                BookBuyerAgent.this.doDelete();
            } catch (final InvalidTupleException e) {
                // should not happen
                e.printStackTrace();
                BookBuyerAgent.this.doDelete();
            }
        }
    }

    /*
     * Behaviour waiting for the purchase confirmation.
     */
    private class ConfirmationReceiver extends Behaviour {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;
        /*
         * For termination.
         */
        private boolean flag = false;

        @Override
        public void action() {
            BookBuyerAgent.this
                    .log("Waiting for purchase confirmation message...");
            LogicTuple confirmation = null;
            try {
                confirmation = LogicTuple.parse("purchase(C, to("
                        + BookBuyerAgent.this.getAID().getName() + "), from("
                        + BookBuyerAgent.this.bestSeller + "), book("
                        + BookBuyerAgent.this.targetBookTitle + "))");
            } catch (final InvalidTupleException e) {
                // should not happen
                e.printStackTrace();
                BookBuyerAgent.this.doDelete();
            }
            final In in = new In(BookBuyerAgent.this.tcid, confirmation);
            TucsonOpCompletionEvent result = null;
            try {
                result = BookBuyerAgent.this.bridge.synchronousInvocation(in,
                        null, this);
            } catch (final ServiceException e) {
                BookBuyerAgent.this
                        .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                BookBuyerAgent.this.doDelete();
            }
            if (result != null) { // operation complete
                this.flag = true;
                BookBuyerAgent.this.log("Received confirmation from "
                        + BookBuyerAgent.this.bestSeller);
                try {
                    if ("confirm"
                            .equals(result.getTuple().getArg(0).toString())) { // book
                                                                               // acquired
                        /*
                         * In case of positive answer, purchase succeeded.
                         */
                        BookBuyerAgent.this.log("Book "
                                + BookBuyerAgent.this.targetBookTitle
                                + " has been successfully purchased"
                                + " from agent "
                                + BookBuyerAgent.this.bestSeller);
                    } else {
                        /*
                         * Otherwise, purchase failed.
                         */
                        BookBuyerAgent.this.log("Book "
                                + BookBuyerAgent.this.targetBookTitle
                                + " has been already sold :(");
                    }
                } catch (final InvalidOperationException e) {
                    // should not happen
                    e.printStackTrace();
                    BookBuyerAgent.this.doDelete();
                }
            } else {
                BookBuyerAgent.this.log("Waiting for confirmation...");
                this.block();
            }
        }

        /*
         * Upon reception of a confirmation/failure message we can terminate.
         */
        @Override
        public boolean done() {
            return this.flag;
        }
    }

    /*
     * Terminating state if no proposals have been received, hence no purchase
     * attempt has to be done.
     */
    private class NoProposals extends OneShotBehaviour {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            BookBuyerAgent.this
                    .log("No proposals received, trying another book in 10 seconds...");
        }
    }

    /*
     * Behaviour collecting the Proposals (possibly) sent by advertising agents.
     */
    private class ProposalsCollector extends Behaviour {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            /*
             * Use previous message template to collect all proposals/refusals
             * from previously found seller agents.
             */
            BookBuyerAgent.this.log("Waiting for proposals...");
            LogicTuple proposal = null;
            try {
                proposal = LogicTuple.parse("proposal(to("
                        + BookBuyerAgent.this.getAID().getName() + "), book("
                        + BookBuyerAgent.this.targetBookTitle
                        + "), from(S), price(P))");
            } catch (final InvalidTupleException e) {
                // should not happen
                e.printStackTrace();
                BookBuyerAgent.this.doDelete();
            }
            final In in = new In(BookBuyerAgent.this.tcid, proposal);
            TucsonOpCompletionEvent res = null;
            try {
                res = BookBuyerAgent.this.bridge.synchronousInvocation(in,
                        null, this);
            } catch (final ServiceException e) {
                BookBuyerAgent.this
                        .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                BookBuyerAgent.this.doDelete();
            }
            if (res != null) {
                String from = null;
                String p = null;
                try {
                    from = res.getTuple().getArg(2).getArg(0).toString();
                    BookBuyerAgent.this.log("Received proposal from '" + from
                            + "' for book "
                            + res.getTuple().getArg(1).getArg(0)
                            + " (target is "
                            + BookBuyerAgent.this.targetBookTitle + ")");
                    p = res.getTuple().getArg(3).getArg(0).toString();
                } catch (final InvalidOperationException e) {
                    // should not happen
                    e.printStackTrace();
                    BookBuyerAgent.this.doDelete();
                }
                if (!"unavailable".equals(p)) {
                    /*
                     * In case of a positive answer, update current best seller
                     * based upon proposed book price.
                     */
                    final float price = Float.parseFloat(p);
                    if (BookBuyerAgent.this.bestSeller == null
                            || price < BookBuyerAgent.this.bestPrice) {
                        BookBuyerAgent.this.bestPrice = price;
                        BookBuyerAgent.this.bestSeller = from;
                    }
                }
                /*
                 * In case of any non-positive answer, do nothing. In any case,
                 * increase replies counter.
                 */
                BookBuyerAgent.this.repliesCnt++;
            } else {
                this.block();
            }
        }

        /*
         * Upon collection of all the responses, this behaviour can be removed.
         */
        @Override
        public boolean done() {
            return BookBuyerAgent.this.repliesCnt >= BookBuyerAgent.this.sellerAgents
                    .size();
        }

        /*
         * If no one had our desired book we should not try to purchase it.
         */
        @Override
        public int onEnd() {
            if (BookBuyerAgent.this.bestSeller != null) {
                BookBuyerAgent.this.log("All proposals received :)");
                BookBuyerAgent.this.sellerAgents
                        .remove(BookBuyerAgent.this.bestSeller);
                return 0;
            }
            return 1;
        }
    }

    /*
     * Behaviour performing the attempt to buy the searched book.
     */
    private class Purchaser extends OneShotBehaviour {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public void action() {
            /*
             * Send the purchase order to the seller who proposed the best
             * offer.
             */
            LogicTuple order;
            try {
                order = LogicTuple.parse("order(accept, from("
                        + BookBuyerAgent.this.getAID().getName() + "), to("
                        + BookBuyerAgent.this.bestSeller + "), book("
                        + BookBuyerAgent.this.targetBookTitle + "))");
                BookBuyerAgent.this.log("Sending purchase order for book "
                        + BookBuyerAgent.this.targetBookTitle + " to agent "
                        + BookBuyerAgent.this.bestSeller);
                final Out out = new Out(BookBuyerAgent.this.tcid, order);
                BookBuyerAgent.this.bridge.asynchronousInvocation(out);
                for (final String r : BookBuyerAgent.this.sellerAgents) {
                    final LogicTuple reject = LogicTuple
                            .parse("order(reject, from("
                                    + BookBuyerAgent.this.getAID().getName()
                                    + "), to(" + r + "), book("
                                    + BookBuyerAgent.this.targetBookTitle
                                    + "))");
                    BookBuyerAgent.this.log("Sending reject for book "
                            + BookBuyerAgent.this.targetBookTitle
                            + " to agent " + r);
                    final Out outi = new Out(BookBuyerAgent.this.tcid, reject);
                    BookBuyerAgent.this.bridge.asynchronousInvocation(outi);
                }
            } catch (final InvalidTupleException e) {
                // should not happen
                e.printStackTrace();
                BookBuyerAgent.this.doDelete();
            } catch (final ServiceException e) {
                BookBuyerAgent.this
                        .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                BookBuyerAgent.this.doDelete();
            }
        }
    }

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /*
     * The best offered price.
     */
    private float bestPrice;
    /*
     * The agent who provides the best offer.
     */
    private String bestSeller;
    /*
     * The bridge class to execute TuCSoN operations
     */
    private BridgeToTucson bridge;
    private TucsonHelper helper;
    /*
     * Overall number of book trading attempts, used for termination.
     */
    private int overallAttempts = 0;
    /*
     * We should keep track of received replies.
     */
    private int repliesCnt = 0;
    /*
     * The list of discovered seller agents.
     */
    private List<String> sellerAgents;
    /*
     * The title of the book to buy.
     */
    private String targetBookTitle;
    /*
     * ID of tuple centre used for objective coordination
     */
    private TucsonTupleCentreId tcid;

    /*
     * Just draw a random book title from an input file.
     */
    private String bootBookTitle() {
        try {
            final BufferedInputStream br = new BufferedInputStream(ClassLoader
                    .getSystemClassLoader().getResourceAsStream(
                            "it/unibo/sd/jade/examples/bookTrading/books.cat"));
            final byte[] res = new byte[br.available()];
            br.read(res);
            br.close();
            final String whole = new String(res);
            String line;
            final StringTokenizer st1 = new StringTokenizer(whole, "\n");
            StringTokenizer st2;
            final LinkedList<String> titles = new LinkedList<String>();
            while (st1.hasMoreTokens()) {
                line = st1.nextToken();
                st2 = new StringTokenizer(line, ";");
                titles.add(st2.nextToken());
            }
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
        this.log("I'm started.");
        try {
            this.helper = (TucsonHelper) this.getHelper(TucsonService.NAME);
            this.helper = (TucsonHelper) this.getHelper(TucsonService.NAME);
            if (!this.helper.isActive("localhost", 20504, 10000)) {
                this.log("Booting local TuCSoN Node on default port...");
                this.helper.startTucsonNode(20504);
            }
            /*
             * Obtain ACC
             */
            this.helper.acquireACC(this);
            /*
             * get tuple centre id
             */
            this.tcid = this.helper.buildTucsonTupleCentreId("default",
                    "localhost", 20504);
            /*
             * get the univocal bridge for the agent
             */
            this.bridge = this.helper.getBridgeToTucson(this);
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
        } catch (final TucsonOperationNotPossibleException e) {
            this.log(">>> TuCSoN Node cannot be installed, check if given port is already in use <<<");
            this.doDelete();
        }
        /*
         * Periodic behaviour performing random book requests.
         */
        this.addBehaviour(new TickerBehaviour(this, 10000) {
            /** serialVersionUID **/
            private static final long serialVersionUID = 1L;

            @Override
            public int onEnd() {
                BookBuyerAgent.this.log("Terminating...");
                this.myAgent.doDelete();
                return super.onEnd();
            }

            private void configureFSM(final FSMBehaviour fsm) {
                fsm.registerFirstState(new CFPSender(), "CFPState");
                fsm.registerState(new ProposalsCollector(), "ProposalsState");
                fsm.registerState(new Purchaser(), "PurchaseState");
                fsm.registerLastState(new ConfirmationReceiver(),
                        "ConfirmationState");
                fsm.registerLastState(new NoProposals(), "NoProposalsState");
                fsm.registerDefaultTransition("CFPState", "ProposalsState");
                fsm.registerTransition("ProposalsState", "PurchaseState", 0);
                fsm.registerTransition("ProposalsState", "NoProposalsState", 1);
                fsm.registerDefaultTransition("PurchaseState",
                        "ConfirmationState");
            }

            @Override
            protected void onTick() {
                /*
                 * Termination condition.
                 */
                if (BookBuyerAgent.this.overallAttempts == 10) {
                    this.stop();
                }
                /*
                 * Randomly draw the book to buy from .catalog file.
                 */
                BookBuyerAgent.this.targetBookTitle = BookBuyerAgent.this
                        .bootBookTitle();
                /*
                 * Resets fields and increase attempts counter.
                 */
                BookBuyerAgent.this.bestSeller = null;
                BookBuyerAgent.this.bestPrice = 0f;
                BookBuyerAgent.this.repliesCnt = 0;
                BookBuyerAgent.this.overallAttempts++;
                BookBuyerAgent.this.sellerAgents = new LinkedList<String>();
                BookBuyerAgent.this.log("Trying to buy "
                        + BookBuyerAgent.this.targetBookTitle);
                /*
                 * 6- Query the DF about the service you look for.
                 */
                BookBuyerAgent.this
                        .log("Searching 'book-trading' services in the 'default' tuple centre...");
                LogicTuple adv;
                TucsonOpCompletionEvent res = null;
                try {
                    adv = LogicTuple
                            .parse("advertise(provider(S), service('book-trading'))");
                    final RdAll rdall = new RdAll(BookBuyerAgent.this.tcid, adv);
                    res = BookBuyerAgent.this.bridge.synchronousInvocation(
                            rdall, null, this);
                } catch (final InvalidTupleException e) {
                    // should not happen
                    e.printStackTrace();
                    BookBuyerAgent.this.doDelete();
                } catch (final ServiceException e) {
                    BookBuyerAgent.this
                            .log(">>> No TuCSoN service active, reboot JADE with -services it.unibo.sd.jade.service.TucsonService option <<<");
                    BookBuyerAgent.this.doDelete();
                }
                /*
                 * res può essere non null ma contenere una lista vuota di
                 * sellers! (è la semantica delle xxx_all)
                 */
                if (res != null) {
                    String agent;
                    try {
                        for (final LogicTuple t : res.getTupleList()) {
                            agent = t.getArg(0).getArg(0).toString();
                            BookBuyerAgent.this.sellerAgents.add(agent);
                            BookBuyerAgent.this.log("Agent '" + agent
                                    + "' found.");
                        }
                    } catch (final InvalidOperationException e) {
                        // should not happen
                        e.printStackTrace();
                        BookBuyerAgent.this.doDelete();
                    }
                    /*
                     * If we found at least one agent offering the desired
                     * service, we try to buy the book using a custom FSM-like
                     * behaviour.
                     */
                    if (!BookBuyerAgent.this.sellerAgents.isEmpty()) {
                        final FSMBehaviour fsm = new FSMBehaviour(this.myAgent);
                        this.configureFSM(fsm);
                        this.myAgent.addBehaviour(fsm);
                    } else {
                        BookBuyerAgent.this
                                .log("No suitable services found, retrying in 10 seconds...");
                    }
                } else {
                    BookBuyerAgent.this
                            .log("No 'book-trading' services available yet...");
                    this.block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        this.log("I'm done.");
    }
}
