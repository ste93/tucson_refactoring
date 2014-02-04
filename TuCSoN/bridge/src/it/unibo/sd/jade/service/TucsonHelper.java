package it.unibo.sd.jade.service;

import it.unibo.sd.jade.exceptions.NoTucsonAuthenticationException;
import it.unibo.sd.jade.glue.BridgeJADETuCSoN;
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
     * Equivale a {@link #authenticate(Agent, String, int)} con i parametri
     * relativi al nodo locale.
     * 
     * @param agent
     *            L'agente che richiede l'autenticazione.
     * @throws TucsonInvalidAgentIdException
     *             Se il nome dell'agente non soddisfa i requisiti di TuCSoN.
     */
    void authenticate(Agent agent) throws TucsonInvalidAgentIdException;

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
    void authenticate(Agent agent, String netid, int portno)
            throws TucsonInvalidAgentIdException;

    /**
     * Permette di effettuare la deautenticazione per il nodo TuCSoN sul quale
     * si era precedentemente autenticati.
     * 
     * @param agent
     *            L'agente che richiede la deautenticazione.
     */
    void deauthenticate(Agent agent);

    /**
     * Permette di ottenere il {@link BridgeJADETuCSoN} tramite il quale
     * &egrave; possibile interagire con TuCSoN.
     * 
     * @param agent
     *            L'agente che richiede il TucsonOperationHandler.
     * @return Il BridgeJadeTuCSoN che permette l'interazione con TuCSoN.
     * @throws NoTucsonAuthenticationException
     *             Se l'agente <code>agent</code> non ha ottenuto un ACC.
     */
    BridgeJADETuCSoN getBridgeJADETuCSoN(Agent agent)
            throws NoTucsonAuthenticationException;

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
    TucsonTupleCentreId getTupleCentreId(String tupleCentreName, String netid,
            int portno) throws TucsonInvalidTupleCentreIdException;

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
