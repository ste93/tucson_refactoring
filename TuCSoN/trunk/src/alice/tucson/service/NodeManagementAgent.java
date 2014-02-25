/*
 * User.java Copyright 2000-2001-2002 aliCE team at deis.unibo.it This software
 * is the proprietary information of deis.unibo.it Use is subject to license
 * terms.
 */
package alice.tucson.service;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuprolog.InvalidTermException;

/**
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * 
 */
public class NodeManagementAgent extends Thread {

    private static void log(final String s) {
        System.out.println("[NodeManagementAgent]: " + s);
    }

    private final TucsonTupleCentreId config;
    private final TucsonNodeService node;

    private TucsonAgentId nodeManAid;

    /**
     * 
     * @param conf
     *            the identifier of the tuple centre to be used for
     *            configuration
     * @param n
     *            the TuCSoN node this management agent belongs to
     */
    public NodeManagementAgent(final TucsonTupleCentreId conf,
            final TucsonNodeService n) {
        super();
        try {
            this.nodeManAid = new TucsonAgentId("node_management_agent");
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
        this.node = n;
        this.config = conf;
        this.start();
    }

    /**
     * 
     */
    @Override
    public void run() {
        try {
            while (true) {
                LogicTuple cmd;
                cmd =
                        (LogicTuple) TupleCentreContainer.doBlockingOperation(
                                TucsonOperation.inCode(), this.nodeManAid,
                                this.config,
                                new LogicTuple("cmd", new Var("X")));
                if (cmd != null) {
                    this.execCmd(cmd.getArg(0));
                } else {
                    throw new InterruptedException();
                }
            }
        } catch (final InvalidTermException e) {
            e.printStackTrace();
            this.node.removeNodeAgent(this);
        } catch (final InterruptedException e) {
            NodeManagementAgent
                    .log("Shutdown interrupt received, shutting down...");
            this.node.removeNodeAgent(this);
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
            this.node.removeNodeAgent(this);
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
            this.node.removeNodeAgent(this);
        } catch (final InvalidOperationException e) {
            e.printStackTrace();
            this.node.removeNodeAgent(this);
        }
    }

    private void execCmd(final TupleArgument cmd)
            throws InvalidOperationException, TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {

        final String name = cmd.getName();
        NodeManagementAgent.log("Executing command " + name);

        if ("destroy".equals(name)) {

            final String tcName = cmd.getArg(0).getName();
            final boolean result = this.node.destroyCore(tcName);
            if (result) {
                TupleCentreContainer.doBlockingOperation(TucsonOperation
                        .outCode(), this.nodeManAid, this.config,
                        new LogicTuple("cmd_result", new Value("destroy"),
                                new Value("ok")));
            } else {
                TupleCentreContainer.doBlockingOperation(TucsonOperation
                        .outCode(), this.nodeManAid, this.config,
                        new LogicTuple("cmd_result", new Value("destroy"),
                                new Value("failed")));
            }

        } else if ("enable_persistency".equals(name)) {

            this.node.enablePersistence(new LogicTuple(cmd.getArg(0)));
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.nodeManAid, this.config, new LogicTuple("cmd_result",
                            cmd, new Value("ok")));

        } else if ("disable_persistency".equals(name)) {

            this.node.disablePersistence(new LogicTuple(cmd.getArg(0)));
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.nodeManAid, this.config, new LogicTuple("cmd_result",
                            new Value("disable_persistency"), new Value("ok")));

        } else if ("enable_observability".equals(name)) {

            this.node.activateObservability();

        } else if ("disable_observability".equals(name)) {

            this.node.deactivateObservability();

        }

    }

}
