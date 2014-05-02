package it.unibo.sd.jade.service;

import it.unibo.sd.jade.exceptions.CannotAcquireACCException;
import it.unibo.sd.jade.glue.BridgeToTucson;
import jade.core.Agent;
import jade.core.ServiceHelper;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 * 
 * @author lucasangiorgi
 * 
 */
public interface TucsonHelper extends ServiceHelper {
    /**
     * Permette di effettuare l'autenticazione per il nodo TuCSoN locale.
     * Equivale a {@link #acquireACC(Agent, String, int)} con i parametri
     * relativi al nodo locale.
     * 
     * @param agent
     *            L'agente che richiede l'autenticazione.
     * @throws TucsonInvalidAgentIdException
     *             Se il nome dell'agente non soddisfa i requisiti di TuCSoN.
     */
    void acquireACC(Agent agent) throws TucsonInvalidAgentIdException;

    /**
     * Permette di effettuare l'autenticazione per il nodo TuCSoN specificato.
     * 
     * @param agent
     *            L'agente che richiede l'autenticazione.
     * @param netid
     *            L'indirizzo IP o nome DNS del nodo TuCSoN.
     * @param portno
     *            Il numero di porta del nodo TuCSoN.
     * @throws TucsonInvalidAgentIdException
     *             Se il nome dell'agente non soddisfa i requisiti di TuCSoN.
     */
    void acquireACC(Agent agent, String netid, int portno)
            throws TucsonInvalidAgentIdException;

    /**
     * Permette di ottenere il {@link alice.tucson.api.TucsonTupleCentreId}
     * TucsonTupleCentreId relativo al <code>tupleCentreName</code>,
     * <code>netid</code> e <code>portno</code> specificati
     * 
     * @param tupleCentreName
     *            Il nome del tuple centre
     * @param netid
     *            L'indirizzo IP o nome DNS del nodo che ospita il tuple centre
     * @param portno
     *            Il numero di porta del tuple centre (default 20504)
     * @return Il TucsonTupleCentreId relativo ai parametri specificati
     * @throws TucsonInvalidTupleCentreIdException
     *             Se il tuple centre non &egrave; valido.
     */
    TucsonTupleCentreId buildTucsonTupleCentreId(String tupleCentreName,
            String netid, int portno)
            throws TucsonInvalidTupleCentreIdException;

    /**
     * Permette di ottenere il {@link BridgeToTucson} tramite il quale &egrave;
     * possibile interagire con TuCSoN.
     * 
     * @param agent
     *            L'agente che richiede il TucsonOperationHandler.
     * @return Il BridgeJadeTuCSoN che permette l'interazione con TuCSoN.
     * @throws CannotAcquireACCException
     *             Se l'agente <code>agent</code> non ha ottenuto un ACC.
     */
    BridgeToTucson getBridgeToTucson(Agent agent)
            throws CannotAcquireACCException;

    /**
     * Checks if a TuCSoN Node is active on the given port
     * 
     * @param port
     * @return wether a TuCSoN Node is active on the given port
     */
    boolean isActive(int port);

    /**
     * Permette di effettuare la deautenticazione per il nodo TuCSoN sul quale
     * si era precedentemente autenticati.
     * 
     * @param agent
     *            L'agente che richiede la deautenticazione.
     */
    void releaseACC(Agent agent);

    /**
     * Permette di avviare un {@link alice.tucson.service.TucsonNodeService}
     * TucsonNodeService sull'host alla porta specificata.
     * 
     * @param port
     *            La porta del TucsonNodeService.
     * @exception TucsonOperationNotPossibleException
     *                if the TuCSoN node cannot be started
     */
    void startTucsonNode(int port) throws TucsonOperationNotPossibleException;

    /**
     * Permette di fermare un {@link alice.tucson.service.TucsonNodeService}
     * TucsonNodeService sull'host.
     * 
     * @param port
     *            La porta del TucsonNodeService
     */
    void stopTucsonNode(int port);
}
