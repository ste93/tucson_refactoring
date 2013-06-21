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
import alice.tuprolog.InvalidTermException;

/**
 * 
 */
public class NodeManagementAgent extends Thread {

    private final TucsonTupleCentreId config;
    private final boolean isLogging = true;
    private final TucsonNodeService node;
    private TucsonAgentId nodeManAid;

    public NodeManagementAgent(final TucsonTupleCentreId conf,
            final TucsonNodeService n) {
        try {
            this.nodeManAid = new TucsonAgentId("node_management_agent");
        } catch (final TucsonInvalidAgentIdException e) {
            System.err.println("[NodeManagementAgent]: " + e);
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
            System.err.println("[NodeManagementAgent]: " + e);
            e.printStackTrace();
            this.node.removeNodeAgent(this);
        } catch (final InterruptedException e) {
            this.log("Shutdown interrupt received, shutting down...");
            this.node.removeNodeAgent(this);
        } catch (final Exception e) {
            System.err.println("[NodeManagementAgent]: " + e);
            e.printStackTrace();
            this.node.removeNodeAgent(this);
        }
    }

    /**
     * 
     * @param cmd
     * @throws Exception
     */
    protected void execCmd(final TupleArgument cmd) throws Exception {

        final String name = cmd.getName();
        this.log("Executing command " + name);

        if (name.equals("destroy")) {

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

        } else if (name.equals("enable_persistency")) {

            this.node.enablePersistence(cmd.getArg(0));
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.nodeManAid, this.config, new LogicTuple("cmd_result",
                            cmd, new Value("ok")));

        } else if (name.equals("disable_persistency")) {

            this.node.disablePersistence(cmd.getArg(0));
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.nodeManAid, this.config, new LogicTuple("cmd_result",
                            new Value("disable_persistency"), new Value("ok")));

        } else if (name.equals("enable_observability")) {

            this.node.activateObservability();

        } else if (name.equals("disable_observability")) {

            this.node.deactivateObservability();

        }

    }

    protected void log(final String s) {
        if (this.isLogging) {
            System.out.println("[NodeManagementAgent]: " + s);
        }
    }

}
