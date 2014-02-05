package it.unibo.sd.jade.service;

import it.unibo.sd.jade.coordination.TucsonAccManager;
import it.unibo.sd.jade.coordination.TucsonNodeUtility;
import it.unibo.sd.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.sd.jade.glue.BridgeJADETuCSoN;
import it.unibo.sd.jade.operations.AbstractTucsonAction;
import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.HorizontalCommand;
import jade.core.IMTPException;
import jade.core.Node;
import jade.core.Service;
import jade.core.ServiceException;
import jade.core.ServiceHelper;
import jade.core.Sink;
import jade.core.VerticalCommand;

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
                final AbstractTucsonAction action =
                        (AbstractTucsonAction) cmd.getParam(0);
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
                final AbstractTucsonAction action =
                        (AbstractTucsonAction) cmd.getParam(0);
                final EnhancedAsynchACC acc =
                        (EnhancedAsynchACC) cmd.getParam(1);
                final TucsonOperationCompletionListener listener =
                        (TucsonOperationCompletionListener) cmd.getParam(2);
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
            // TODO Auto-generated method stub
            return TucsonService.this;
        }

        @Override
        public VerticalCommand serve(final HorizontalCommand arg0) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    /*
     * L'implementazione del TuCSoNHelper
     */
    private class TucsonHelperImpl implements TucsonHelper {

        private Agent myAgent;

        @Override
        public void authenticate(final Agent agent)
                throws TucsonInvalidAgentIdException {
            if (TucsonService.this.mAccManager.hasAcc(agent)) {
                System.out
                        .println("[TuCSoNHelper] L'agente possiede già un ACC");
                return;
            }
            final EnhancedACC acc = this.obtainAcc(agent);
            TucsonService.this.mAccManager.addAcc(agent, acc);
            System.out.println("[TuCSoNHelper] Ottenuto acc " + acc
                    + " per l'agente " + agent.getLocalName());
        }

        @Override
        public void authenticate(final Agent agent, final String netid,
                final int portno) throws TucsonInvalidAgentIdException {
            if (TucsonService.this.mAccManager.hasAcc(agent)) {
                System.out
                        .println("[TuCSoNHelper] L'agente possiede già un ACC");
                return;
            }
            final EnhancedACC acc = this.obtainAcc(agent, netid, portno);
            TucsonService.this.mAccManager.addAcc(agent, acc);
            System.out.println("[TuCSoNHelper] Ottenuto acc " + acc
                    + " per l'agente " + agent.getLocalName());
        }

        @Override
        public void deauthenticate(final Agent agent) {
            // Esco dall'acc
            final EnhancedACC acc =
                    TucsonService.this.mAccManager.getAcc(agent);
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
        }

        @Override
        public BridgeJADETuCSoN getBridgeJADETuCSoN(final Agent agent)
                throws NoTucsonAuthenticationException {
            if (!TucsonService.this.mAccManager.hasAcc(agent)) {
                throw new NoTucsonAuthenticationException(
                        "The agent does not hold an ACC");
            }
            // Controllo se esiste già un OperationHandler per l'agente,
            // altrimenti lo creo
            BridgeJADETuCSoN bridge =
                    TucsonService.this.mOperationHandlers.get(agent.getAID());
            if (bridge == null) {
                bridge =
                        new BridgeJADETuCSoN(
                                TucsonService.this.mAccManager.getAcc(agent),
                                TucsonService.this);
                TucsonService.this.mOperationHandlers.put(agent.getAID(),
                        bridge);
            }
            return bridge;
        }

        @Override
        public TucsonTupleCentreId getTupleCentreId(
                final String tupleCentreName, final String netid,
                final int portno) throws TucsonInvalidTupleCentreIdException {
            final TucsonTupleCentreId tcid =
                    new TucsonTupleCentreId(tupleCentreName, netid,
                            String.valueOf(portno));
            return tcid;
        }

        @Override
        public void init(final Agent agent) {
            this.myAgent = agent;
        }

        @Override
        public void startTucsonNode(final int port) {
            TucsonNodeUtility.startTucsonNode(port);
        }

        @Override
        public void stopTucsonNode(final int port) {
            TucsonNodeUtility.stopTucsonNode(port);
        }

        private EnhancedACC obtainAcc(final Agent agent)
                throws TucsonInvalidAgentIdException {
            final TucsonAgentId taid = new TucsonAgentId(agent.getLocalName());
            System.out.println("****[TucsonService] ID agente: " + taid);
            return TucsonMetaACC.getContext(taid);
        }

        private EnhancedACC obtainAcc(final Agent agent, final String netid,
                final int portno) throws TucsonInvalidAgentIdException {
            final TucsonAgentId taid = new TucsonAgentId(agent.getLocalName());
            final EnhancedACC acc =
                    TucsonMetaACC.getContext(taid, netid, portno);
            return acc;
        }
    }

    /**
     * Service name
     */
    public static final String NAME = "it.unibo.sd.jade.service.TucsonService";

    /*
     * L'insieme dei comandi verticali che il servizio è in grado di soddisfare
     * autonomamente
     */
    private static final String[] OWNED_COMMANDS = { TucsonSlice.EXECUTE_SYNCH,
            TucsonSlice.EXECUTE_ASYNCH };
    private static final int TUCSON_DEF_PORT = 20504;
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
    private final TucsonAccManager mAccManager = TucsonAccManager.getInstance();
    /*
     * Gestisce il mapping Agente-OperationHandler
     */
    private final Map<AID, BridgeJADETuCSoN> mOperationHandlers =
            new HashMap<AID, BridgeJADETuCSoN>();
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
    private final Sink targetSink = new CommandTargetSink(); // Al momento non è

    // utilizzato
    @Override
    public Sink getCommandSink(final boolean direction) {
        if (direction == Sink.COMMAND_SOURCE) {
            return this.sourceSink;
        }
        return this.targetSink;
    }

    @Override
    public ServiceHelper getHelper(final Agent a) throws ServiceException {
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
        System.out.println("[TuCSoNService] Shutting down TuCSoN node");
        TucsonNodeUtility.stopTucsonNode(TucsonService.TUCSON_DEF_PORT);
        super.shutdown();
    }
}
