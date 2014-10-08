package it.unibo.sd.jade.service;

import it.unibo.sd.jade.coordination.TucsonACCsManager;
import it.unibo.sd.jade.coordination.TucsonNodeLifecycleManager;
import it.unibo.sd.jade.exceptions.CannotAcquireACCException;
import it.unibo.sd.jade.glue.BridgeToTucson;
import it.unibo.sd.jade.operations.AbstractTucsonAction;
import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.Service;
import jade.core.ServiceHelper;
import jade.core.Sink;
import jade.core.VerticalCommand;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.examples.utilities.Utils;
import alice.tucson.network.exceptions.DialogCloseException;
import alice.tucson.network.exceptions.DialogInitializationException;
import alice.tucson.service.TucsonNodeService;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * TucsonService. The class representing TuCSoN coordination services within the
 * JADE middleware.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class TucsonService extends BaseService {
    /**
     * Inner class repsonsible for executing JADE kernel services
     * "vertical commands". In our case, TuCSoN coordination operations
     * invocations.
     */
    private class CommandSourceSink implements Sink {
        @Override
        public void consume(final VerticalCommand cmd) {
            final String cmdName = cmd.getName();
            if (cmdName.equals(TucsonSlice.EXECUTE_SYNCH)) {
                final AbstractTucsonAction action = (AbstractTucsonAction) cmd
                        .getParam(0);
                final EnhancedSynchACC acc = (EnhancedSynchACC) cmd.getParam(1);
                final Long timeout = (Long) cmd.getParam(2);
                ITucsonOperation result;
                try {
                    result = action.executeSynch(acc, timeout);
                    cmd.setReturnValue(result);
                } catch (final TucsonOperationNotPossibleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    cmd.setReturnValue(e);
                } catch (final UnreachableNodeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    cmd.setReturnValue(e);
                } catch (final OperationTimeOutException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    cmd.setReturnValue(e);
                }
            } else if (cmdName.equals(TucsonSlice.EXECUTE_ASYNCH)) {
                final AbstractTucsonAction action = (AbstractTucsonAction) cmd
                        .getParam(0);
                final EnhancedAsynchACC acc = (EnhancedAsynchACC) cmd
                        .getParam(1);
                final TucsonOperationCompletionListener listener = (TucsonOperationCompletionListener) cmd
                        .getParam(2);
                ITucsonOperation result;
                try {
                    result = action.executeAsynch(acc, listener);
                    cmd.setReturnValue(result);
                } catch (final TucsonOperationNotPossibleException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    cmd.setReturnValue(e);
                } catch (final UnreachableNodeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    cmd.setReturnValue(e);
                }
            }
        }
    }

    private class CommandTargetSink implements Sink {
        @Override
        public void consume(final VerticalCommand cmd) {
            /*
             * not used atm
             */
        }
    }

    private class ServiceComponent implements Service.Slice {
        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        @Override
        public Node getNode() {
            try {
                return TucsonService.this.getLocalNode();
            } catch (final IMTPException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Service getService() {
            return TucsonService.this;
        }

        @Override
        public VerticalCommand serve(final HorizontalCommand arg0) {
            return null;
        }
    }

    /**
     * Inner class implementing the {@link TucsonHelper} interface.
     */
    private class TucsonHelperImpl implements TucsonHelper {
        private Agent myAgent;

        @Override
        public void acquireACC(final Agent agent)
                throws TucsonInvalidAgentIdException {
            if (TucsonACCsManager.INSTANCE.hasAcc(agent)) {
                this.log("Agent '" + agent.getName() + "' already has an ACC.");
                return;
            }
            final EnhancedACC acc = this.obtainAcc(agent);
            TucsonACCsManager.INSTANCE.addAcc(agent, acc);
            this.log("Giving ACC '"
                    + acc.getClass().getInterfaces()[0].getSimpleName()
                    + "' to agent " + agent.getLocalName());
        }

        @Override
        public void acquireACC(final Agent agent, final String netid,
                final int portno) throws TucsonInvalidAgentIdException {
            if (TucsonACCsManager.INSTANCE.hasAcc(agent)) {
                this.log("Agent '" + agent.getName() + "' already has an ACC.");
                return;
            }
            final EnhancedACC acc = this.obtainAcc(agent, netid, portno);
            TucsonACCsManager.INSTANCE.addAcc(agent, acc);
            this.log("Giving ACC '"
                    + acc.getClass().getInterfaces()[0].getSimpleName()
                    + "' to agent " + agent.getLocalName()
                    + "' toward TuCSoN Node @" + netid + ":" + portno);
        }

        @Override
        public TucsonTupleCentreId buildTucsonTupleCentreId(
                final String tupleCentreName, final String netid,
                final int portno) throws TucsonInvalidTupleCentreIdException {
            final TucsonTupleCentreId tcid = new TucsonTupleCentreId(
                    tupleCentreName, netid, String.valueOf(portno));
            return tcid;
        }

        @Override
        public BridgeToTucson getBridgeToTucson(final Agent agent)
                throws CannotAcquireACCException {
            if (!TucsonACCsManager.INSTANCE.hasAcc(agent)) {
                throw new CannotAcquireACCException(
                        "The agent does not hold an ACC");
            }
            BridgeToTucson bridge = TucsonService.this.mOperationHandlers
                    .get(agent.getAID());
            if (bridge == null) {
                bridge = new BridgeToTucson(
                        TucsonACCsManager.INSTANCE.getAcc(agent),
                        TucsonService.this);
                TucsonService.this.mOperationHandlers.put(agent.getAID(),
                        bridge);
            }
            this.log("Booting TuCSoN bridge for agent " + agent.getName());
            return bridge;
        }

        @Override
        public void init(final Agent agent) {
            this.myAgent = agent;
        }

        /*
         * (non-Javadoc)
         * @see it.unibo.sd.jade.service.TucsonHelper#isActive(int)
         */
        @Override
        public boolean isActive(final String netid, final int port,
                final int timeout) {
            try {
                return TucsonNodeService.isInstalled(netid, port, timeout);
            } catch (final DialogInitializationException e) {
                e.printStackTrace();
            } catch (final DialogCloseException e) {
                e.printStackTrace();
            } catch (final UnreachableNodeException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void releaseACC(final Agent agent) {
            final EnhancedACC acc = TucsonACCsManager.INSTANCE.getAcc(agent);
            if (acc != null) {
                try {
                    acc.exit();
                } catch (final TucsonOperationNotPossibleException e) {
                    e.printStackTrace();
                }
            }
            TucsonACCsManager.INSTANCE.removeAcc(agent);
            TucsonService.this.mOperationHandlers.remove(this.myAgent.getAID());
            this.log("Releasing ACC and TuCSoN bridge for agent "
                    + agent.getName());
        }

        @Override
        public void startTucsonNode(final int port) {
            this.log("Booting local TuCSoN Node Service on port " + port);
            TucsonNodeLifecycleManager.startTucsonNode(port);
        }

        @Override
        public void stopTucsonNode(final int port) {
            this.log("Stopping local TuCSoN Node Service on port " + port);
            TucsonNodeLifecycleManager.stopTucsonNode(port);
        }

        private void log(final String msg) {
            System.out.println("[TuCSoN Service Helper]: " + msg);
        }

        private EnhancedACC obtainAcc(final Agent agent)
                throws TucsonInvalidAgentIdException {
            final TucsonAgentId taid = new TucsonAgentId(agent.getLocalName());
            return TucsonMetaACC.getContext(taid);
        }

        private EnhancedACC obtainAcc(final Agent agent, final String netid,
                final int portno) throws TucsonInvalidAgentIdException {
            final TucsonAgentId taid = new TucsonAgentId(agent.getLocalName());
            return TucsonMetaACC.getContext(taid, netid, portno);
        }
    }

    /** Service name */
    public static final String NAME = "it.unibo.sd.jade.service.TucsonService";
    /** The set of JADE "vertical commands" this service is able to serve */
    private static final String[] OWNED_COMMANDS = { TucsonSlice.EXECUTE_SYNCH,
            TucsonSlice.EXECUTE_ASYNCH };
    private static final int TUCSON_DEF_PORT = 20504;
    private static final String VERSION = "TuCSoN4JADE-1.0";

    private static String getVersion() {
        return TucsonService.VERSION;
    }

    private static void log(final String msg) {
        System.out.println("[TuCSoN Service]: " + msg);
    }

    private final TucsonHelper helperService = new TucsonHelperImpl();
    /** Leave this here, seems that JADE requires it anyway */
    private final ServiceComponent localSlice = new ServiceComponent();
    private final Map<AID, BridgeToTucson> mOperationHandlers = new HashMap<AID, BridgeToTucson>();
    private final Sink sourceSink = new CommandSourceSink();
    private final Sink targetSink = new CommandTargetSink();

    public TucsonService() {
        super();
        TucsonService
                .log("--------------------------------------------------------------------------------");
        try {
            final StringTokenizer st = new StringTokenizer(
                    Utils.fileToString("it/unibo/sd/jade/service/t4j-logos.txt"),
                    "\n");
            while (st.hasMoreTokens()) {
                TucsonService.log(st.nextToken());
            }
        } catch (final IOException e) {
            // should not happen
            e.printStackTrace();
        }
        TucsonService
                .log("--------------------------------------------------------------------------------");
        TucsonService.log("Welcome to the TuCSoN4JADE (T4J) bridge :)");
        TucsonService.log("  T4J version " + TucsonService.getVersion());
        // TucsonService.log("  TuCSoN version " +
        // TucsonNodeService.getVersion());
        TucsonService.log(new Date().toString());
        TucsonService
                .log("--------------------------------------------------------------------------------");
    }

    @Override
    public Sink getCommandSink(final boolean direction) {
        if (direction == Sink.COMMAND_SOURCE) {
            return this.sourceSink;
        }
        return this.targetSink;
    }

    @Override
    public ServiceHelper getHelper(final Agent a) {
        return this.helperService;
    }

    @Override
    public Class<TucsonSlice> getHorizontalInterface() {
        return TucsonSlice.class;
    }

    @Override
    public String getName() {
        return TucsonService.NAME;
    }

    @Override
    public String[] getOwnedCommands() {
        return TucsonService.OWNED_COMMANDS;
    }

    @Override
    public void shutdown() {
        TucsonNodeLifecycleManager
                .stopTucsonNode(TucsonService.TUCSON_DEF_PORT);
        super.shutdown();
    }
}
