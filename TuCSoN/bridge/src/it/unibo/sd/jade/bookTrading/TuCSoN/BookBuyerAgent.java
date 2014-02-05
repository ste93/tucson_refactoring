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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Adapted from Giovanni Caire's Book Trading example in examples.bookTrading
 * within JADE distribution. This is the buyer agent, showing how to query JADE
 * DF in order to look for a desired service.
 * 
 * @author s.mariani@unibo.it
 */
public class BookBuyerAgent extends Agent {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /*
     * The best offered price.
     */
    private float bestPrice;
    /*
     * The agent who provides the best offer.
     */
    private AID bestSeller;
    /*
     * Overall number of book trading attempts, used for termination.
     */
    private int overallAttempts = 0;
    /*
     * The list of discovered seller agents.
     */
    private AID[] sellerAgents;
    /*
     * The title of the book to buy.
     */
    private String targetBookTitle;

    @Override
    protected void setup() {
        this.log("I'm started.");
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

            @Override
            protected void onTick() {
                /*
                 * Termination condition.
                 */
                if (BookBuyerAgent.this.overallAttempts == 30) {
                    this.stop();
                }
                /*
                 * Randomly draw the book to buy from .catalog file.
                 */
                BookBuyerAgent.this.targetBookTitle =
                        BookBuyerAgent.this.bootBookTitle();
                /*
                 * Resets fields and increase attempts counter.
                 */
                BookBuyerAgent.this.bestSeller = null;
                BookBuyerAgent.this.bestPrice = 0f;
                BookBuyerAgent.this.overallAttempts++;
                BookBuyerAgent.this.log("Trying to buy '"
                        + BookBuyerAgent.this.targetBookTitle + "'...");
                /*
                 * 1- Create the agent description template.
                 */
                final DFAgentDescription template = new DFAgentDescription();
                /*
                 * 2- Create the service description template.
                 */
                final ServiceDescription sd = new ServiceDescription();
                /*
                 * 3- Fill its fields you look for.
                 */
                sd.setType("book-selling");
                /*
                 * 4- Add the service template to the agent template.
                 */
                template.addServices(sd);
                /*
                 * 5- Setup your preferred search constraints.
                 */
                final SearchConstraints all = new SearchConstraints();
                all.setMaxResults(new Long(-1));
                DFAgentDescription[] result = null;
                try {
                    /*
                     * 6- Query the DF about the service you look for.
                     */
                    BookBuyerAgent.this.log("Searching '" + sd.getType()
                            + "' service in the default DF...");
                    result = DFService.search(this.myAgent, template, all);
                    BookBuyerAgent.this.sellerAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        /*
                         * 7- Collect found service providers' AIDs.
                         */
                        BookBuyerAgent.this.sellerAgents[i] =
                                result[i].getName();
                        BookBuyerAgent.this.log("Agent '"
                                + BookBuyerAgent.this.sellerAgents[i].getName()
                                + "' found.");
                    }
                } catch (final FIPAException fe) {
                    fe.printStackTrace();
                }
                /*
                 * If we found at least one agent offering the desired service,
                 * we try to buy the book using a custom FSM-like behaviour.
                 */
                if ((result != null) && (result.length != 0)) {
                    final ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
                    for (final AID sellerAgent : BookBuyerAgent.this.sellerAgents) {
                        BookBuyerAgent.this.log("Sending CFP for book '"
                                + BookBuyerAgent.this.targetBookTitle
                                + "' to agent '" + sellerAgent.getName()
                                + "'...");
                        cfp.addReceiver(sellerAgent);
                    }
                    cfp.setContent(BookBuyerAgent.this.targetBookTitle);
                    /*
                     * Timeout is 10 seconds.
                     */
                    cfp.setReplyByDate(new Date(
                            System.currentTimeMillis() + 10000));
                    /*
                     * Here's the ContractNetInitiator handling all the
                     * conversation stages.
                     */
                    final ContractNetInitiator cni =
                            new ContractNetInitiator(this.myAgent, cfp) {

                                /** serialVersionUID **/
                                private static final long serialVersionUID = 1L;

                                /*
                                 * NB: NOW we have either (i) collected all the
                                 * responses (positive or negative doesn't
                                 * matter) (ii) reached timeout (someone failed)
                                 * In both cases we can now discover the best
                                 * seller and (1) accept its proposal (2) reject
                                 * other sellers proposals
                                 */
                                @SuppressWarnings({ "rawtypes", "unchecked" })
                                @Override
                                protected void handleAllResponses(
                                        final java.util.Vector responses,
                                        final java.util.Vector acceptances) {
                                    BookBuyerAgent.this
                                            .log("Proposals collection phase ends (all responses or timeout).");
                                    ACLMessage accept = null;
                                    final Enumeration e = responses.elements();
                                    while (e.hasMoreElements()) {
                                        final ACLMessage msg =
                                                (ACLMessage) e.nextElement();
                                        if (msg.getPerformative() == ACLMessage.PROPOSE) {
                                            final ACLMessage reply =
                                                    msg.createReply();
                                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                            reply.setContent(BookBuyerAgent.this.targetBookTitle);
                                            acceptances.addElement(reply);
                                            final float price =
                                                    Float.parseFloat(msg
                                                            .getContent());
                                            if ((BookBuyerAgent.this.bestSeller == null)
                                                    || (price < BookBuyerAgent.this.bestPrice)) {
                                                BookBuyerAgent.this.bestPrice =
                                                        price;
                                                BookBuyerAgent.this.bestSeller =
                                                        msg.getSender();
                                                accept = reply;
                                            }
                                        }
                                    }
                                    if (accept != null) {
                                        BookBuyerAgent.this
                                                .log("Sending purchase order for book '"
                                                        + BookBuyerAgent.this.targetBookTitle
                                                        + "' to agent '"
                                                        + BookBuyerAgent.this.bestSeller
                                                                .getName()
                                                        + "'...");
                                        accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                    }
                                }

                                /*
                                 * An agent failed.
                                 */
                                @Override
                                protected void handleFailure(
                                        final ACLMessage failure) {
                                    if (failure.getSender().equals(
                                            this.myAgent.getAMS())) {
                                        /*
                                         * The receiver of my CFP does not
                                         * exists anymore.
                                         */
                                        BookBuyerAgent.this
                                                .log("Received failure '"
                                                        + failure.getContent()
                                                        + "' from the AMS.");
                                    } else {
                                        BookBuyerAgent.this
                                                .log("Received failure '"
                                                        + failure.getContent()
                                                        + "' from '"
                                                        + failure.getSender()
                                                                .getName()
                                                        + "'.");
                                    }
                                }

                                /*
                                 * Purchase confirmation received.
                                 */
                                @Override
                                protected void handleInform(
                                        final ACLMessage inform) {
                                    BookBuyerAgent.this
                                            .log("Received confirmation '"
                                                    + inform.getContent()
                                                    + "' from '"
                                                    + inform.getSender()
                                                            .getName() + "'.");
                                    BookBuyerAgent.this
                                            .log("Book '"
                                                    + BookBuyerAgent.this.targetBookTitle
                                                    + "' has been successfully purchased"
                                                    + " from agent '"
                                                    + inform.getSender()
                                                            .getName() + "'.");
                                }

                                /*
                                 * A PROPOSAL arrived.
                                 */
                                @SuppressWarnings("rawtypes")
                                @Override
                                protected void handlePropose(
                                        final ACLMessage propose,
                                        final java.util.Vector acceptances) {
                                    BookBuyerAgent.this
                                            .log("Received proposal '"
                                                    + propose.getContent()
                                                    + "' from '"
                                                    + propose.getSender()
                                                            .getName() + "'.");
                                }

                                /*
                                 * A REFUSAL arrived.
                                 */
                                @Override
                                protected void handleRefuse(
                                        final ACLMessage refuse) {
                                    BookBuyerAgent.this
                                            .log("Received refusal '"
                                                    + refuse.getContent()
                                                    + "' from '"
                                                    + refuse.getSender()
                                                            .getName() + "'.");
                                }
                            };
                    this.myAgent.addBehaviour(cni);
                } else {
                    BookBuyerAgent.this
                            .log("No suitable services found, retrying in 10 seconds...");
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        this.log("I'm done.");
    }

    /*
     * Just draw a random book title from an input file.
     */
    private String bootBookTitle() {
        try {
            final BufferedReader br =
                    new BufferedReader(
                            new FileReader(
                                    "bin/ds/lab/jade/bookTrading/contractNet/books.cat"));
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
}
