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
import java.util.HashMap;
import java.util.Map;
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
import alice.tucson.service.TucsonNodeService;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * @author lucasangiorgi
 * 
 */
public class TucsonService extends BaseService {
    /*
     * Classe interna che ha il compito di eseguire i comandi verticali
     * "interni", ovvero quelli gestiti direttamente dal servizio (quelli
     * inclusi nell'elenco OWNED_COMMANDS) sono l'ultimo step dei comandi
     * verticali che implementa effettivamente il comando verticale
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
                    cmd.setReturnValue(result); // viene ritornato nel corpo
                    // della classe di
                    // TucsonOperationHandler dove
                    // si esegue il comando
                    // verticale
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

    /*
     * Classe interna che ha il compito di eseguire i comandi verticali generati
     * dal ServiceComponent (al momento nessuno)
     */
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

    /*
     * L'implementazione del TuCSoNHelper
     */
    private class TucsonHelperImpl implements TucsonHelper {
        private Agent myAgent;

        @Override
        public void acquireACC(final Agent agent)
                throws TucsonInvalidAgentIdException {
            if (TucsonService.this.mAccManager.hasAcc(agent)) {
                this.log("Agent '" + agent.getName() + "' already has an ACC.");
                return;
            }
            final EnhancedACC acc = this.obtainAcc(agent);
            TucsonService.this.mAccManager.addAcc(agent, acc);
            this.log("Giving ACC '"
                    + acc.getClass().getInterfaces()[0].getSimpleName()
                    + "' to agent " + agent.getLocalName());
        }

        @Override
        public void acquireACC(final Agent agent, final String netid,
                final int portno) throws TucsonInvalidAgentIdException {
            if (TucsonService.this.mAccManager.hasAcc(agent)) {
                this.log("Agent '" + agent.getName() + "' already has an ACC.");
                return;
            }
            final EnhancedACC acc = this.obtainAcc(agent, netid, portno);
            TucsonService.this.mAccManager.addAcc(agent, acc);
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
            if (!TucsonService.this.mAccManager.hasAcc(agent)) {
                throw new CannotAcquireACCException(
                        "The agent does not hold an ACC");
            }
            // Controllo se esiste gia' un OperationHandler per l'agente,
            // altrimenti lo creo
            BridgeToTucson bridge = TucsonService.this.mOperationHandlers
                    .get(agent.getAID());
            if (bridge == null) {
                bridge = new BridgeToTucson(
                        TucsonService.this.mAccManager.getAcc(agent),
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
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void releaseACC(final Agent agent) {
            // Esco dall'acc
            final EnhancedACC acc = TucsonService.this.mAccManager
                    .getAcc(agent);
            if (acc != null) {
                try {
                    acc.exit();
                } catch (final TucsonOperationNotPossibleException e) {
                    e.printStackTrace();
                }
            }
            // Rimuovo l'ACC
            TucsonService.this.mAccManager.removeAcc(agent);
            // Rimuovo eventuali TucsonOperationHandler
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

    /**
     * Service name
     */
    public static final String NAME = "it.unibo.sd.jade.service.TucsonService";
    /*
     * L'insieme dei comandi verticali che il servizio e' in grado di soddisfare
     * autonomamente
     */
    private static final String[] OWNED_COMMANDS = { TucsonSlice.EXECUTE_SYNCH,
            TucsonSlice.EXECUTE_ASYNCH };
    private static final int TUCSON_DEF_PORT = 20504;

    /**
     * @return
     */
    private static String getVersion() {
        return "TuCSoN4JADE-1.0";
    }

    /**
     * @param string
     */
    private static void log(final String msg) {
        System.out.println("[TuCSoN Service]: " + msg);
    }

    /*
     * L'helper del servizio
     */
    private final TucsonHelper helperService = new TucsonHelperImpl();
    // The local slice for this service
    @SuppressWarnings("unused")
    private final ServiceComponent localSlice = new ServiceComponent();
    /*
     * Gestisce gli acc posseduti dagli agenti
     */
    private final TucsonACCsManager mAccManager = TucsonACCsManager
            .getInstance();
    /*
     * Gestisce il mapping Agente-OperationHandler
     */
    private final Map<AID, BridgeToTucson> mOperationHandlers = new HashMap<AID, BridgeToTucson>();
    /*
     * Gestisce il mapping nodeName-InetAddress
     */
    // private final Map<String, InetSocketAddress> mTucsonNodes =
    // new HashMap<String, InetSocketAddress>();
    // @Override
    // public Slice getLocalSlice() {
    // return localSlice;
    // }
    // The source and target sinks
    private final Sink sourceSink = new CommandSourceSink();
    private final Sink targetSink = new CommandTargetSink(); // Al momento non
                                                             // e'

    public TucsonService() {
        super();
        TucsonService
                .log("--------------------------------------------------------------------------------");
        TucsonService.log("Welcome to the TuCSoN4JADE (T4J) bridge :)");
        TucsonService.log("  T4J version " + TucsonService.getVersion());
        TucsonService.log("  TuCSoN version " + TucsonNodeService.getVersion());
        TucsonService
                .log("--------------------------------------------------------------------------------");
    }

    // utilizzato
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

    @SuppressWarnings("rawtypes")
    @Override
    public Class getHorizontalInterface() {
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
