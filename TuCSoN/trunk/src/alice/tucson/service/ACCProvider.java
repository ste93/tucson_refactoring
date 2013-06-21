/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.introspection.InspectorContextSkel;
import alice.tucson.network.TucsonProtocol;

/**
 * 
 */
public class ACCProvider {

    private static void log(final String st) {
        System.out.println("[ACCProvider]: " + st);
    }

    protected TucsonAgentId aid;
    protected TucsonTupleCentreId config;
    protected ExecutorService exec;

    protected TucsonNodeService node;

    /**
     * 
     * @param n
     * @param tid
     */
    public ACCProvider(final TucsonNodeService n, final TucsonTupleCentreId tid) {
        try {
            this.aid = new TucsonAgentId("context_manager");
        } catch (final TucsonInvalidAgentIdException e) {
            System.err.println("[ACCProvider]: " + e);
            e.printStackTrace();
        }
        this.node = n;
        this.config = tid;
        this.exec = Executors.newCachedThreadPool();
        ACCProvider.log("Listening to incoming ACC requests...");
    }

    /**
     * 
     * @param profile
     * @param dialog
     * @return
     */
    // exception handling is a mess, need to review it...
    public synchronized boolean processContextRequest(
            final ACCDescription profile, final TucsonProtocol dialog) {

        ACCProvider.log("Processing ACC request...");

        try {

            String agentName = profile.getProperty("agent-identity");
            if (agentName == null) {
                agentName = profile.getProperty("tc-identity");
            }
            final LogicTuple req =
                    new LogicTuple("context_request", new Value(agentName),
                            new Var("CtxId"));
            final LogicTuple result =
                    (LogicTuple) TupleCentreContainer.doBlockingOperation(
                            TucsonOperation.inpCode(), this.aid, this.config,
                            req);

            if (result == null) {
                profile.setProperty("failure", "context not available");
                dialog.sendEnterRequestRefused();
                return false;
            }

            final TupleArgument res = result.getArg(1);

            if (res.getName().equals("failed")) {
                profile.setProperty("failure", res.getArg(0).getName());
                dialog.sendEnterRequestRefused();
                return false;
            }

            final TupleArgument ctxId = res.getArg(0);
            profile.setProperty("context-id", ctxId.toString());
            ACCProvider.log("ACC request accepted, ACC id is < "
                    + ctxId.toString() + " >");
            dialog.sendEnterRequestAccepted();
            final String agentRole = profile.getProperty("agent-role");

            if (agentRole.equals("$inspector")) {
                final ACCAbstractProxyNodeSide skel =
                        new InspectorContextSkel(this, dialog, this.node,
                                profile);
                this.node.addNodeAgent(skel);
                skel.start();
            } else {
                // should I pass here the TuCSoN node port?
                final ACCAbstractProxyNodeSide skel =
                        new ACCProxyNodeSide(this, dialog, this.node, profile);
                this.node.addNodeAgent(skel);
                this.exec.execute(skel);
            }

            return true;

        } catch (final Exception e) {
            profile.setProperty("failure", "generic");
            System.err.println("[ACCProvider]: " + e);
            e.printStackTrace();
            return false;
        }

    }

    public void shutdown() throws InterruptedException {
        ACCProvider.log("Shutdown interrupt received, shutting down...");
        this.exec.shutdownNow();
        if (this.exec.awaitTermination(5, TimeUnit.SECONDS)) {
            ACCProvider.log("Executors correctly stopped");
        } else {
            ACCProvider.log("Executors may be still running");
        }
    }

    /**
     * 
     * @param ctxId
     * @param id
     * @return
     */
    // exception handling is a mess, need to review it...
    public synchronized boolean shutdownContext(final int ctxId,
            final TucsonAgentId id) {

        final LogicTuple req =
                new LogicTuple("context_shutdown", new Value(ctxId), new Value(
                        id.toString()), new Var("CtxId"));
        LogicTuple result;
        try {
            result =
                    (LogicTuple) TupleCentreContainer.doBlockingOperation(
                            TucsonOperation.inpCode(), this.aid, this.config,
                            req);
        } catch (final Exception e) {
            System.err.println("[ACCProvider]: " + e);
            e.printStackTrace();
            return false;
        }

        try {
            if (result.getArg(2).getName().equals("ok")) {
                return true;
            }
            return false;
        } catch (final InvalidTupleOperationException e) {
            System.err.println("[ACCProvider]: " + e);
            e.printStackTrace();
            return false;
        }

    }

}
