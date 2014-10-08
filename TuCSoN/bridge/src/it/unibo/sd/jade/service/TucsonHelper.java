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
 * TucsonHelper. Interface class responsible to provide API to JADE agent to
 * fruitfully interact with TuCSoN coordination services.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public interface TucsonHelper extends ServiceHelper {
    /**
     * Enables ACC acquisition on the default local TuCSoN Node. Equivalent to
     * {@link #acquireACC(Agent, String, int)} givin the default local TuCSoN
     * Node addresses.
     * 
     * @param agent
     *            the agent requesting authentication
     * @throws TucsonInvalidAgentIdException
     *             if the given agent id is not a valid TuSCoN agent id
     */
    void acquireACC(Agent agent) throws TucsonInvalidAgentIdException;

    /**
     * Enables ACC acquisition on the given TuCSoN Node.
     * 
     * @param agent
     *            the agent requesting authentication
     * @param netid
     *            IP address of the TuCSoN Node to interact with
     * @param portno
     *            TCP port number of the TuCSoN Node to interact with
     * @throws TucsonInvalidAgentIdException
     *             if the given agent id is not a valid TuSCoN agent id
     */
    void acquireACC(Agent agent, String netid, int portno)
            throws TucsonInvalidAgentIdException;

    /**
     * Builds the {@link alice.tucson.api.TucsonTupleCentreId} identifying the
     * tuple centre named <code>tupleCentreName</code> and reachable at IP
     * address <code>netid</code>:<code>portno</code>.
     * 
     * @param tupleCentreName
     *            the name of the tuple centre
     * @param netid
     *            IP address of the tuple centre
     * @param portno
     *            TCP port number of the tuple centre
     * @return the TucsonTupleCentreId built
     * @throws TucsonInvalidTupleCentreIdException
     *             if given name is not a valid TuCSoN tuple centre id
     */
    TucsonTupleCentreId buildTucsonTupleCentreId(String tupleCentreName,
            String netid, int portno)
            throws TucsonInvalidTupleCentreIdException;

    /**
     * Gets the {@link BridgeToTucson} providing JADE agents the API to benefit
     * of TuCSoN coordination services.
     * 
     * @param agent
     *            the agent requesting access to the TuCSoN4JADE bridge
     *            component
     * @return the TuCSoN4JADE bridge component providing JADE agents the API to
     *         benefit of TuCSoN coordination services
     * @throws CannotAcquireACCException
     *             if the <code>agent</code> cannot get an ACC (e.g. another
     *             agent with same id already exists)
     */
    BridgeToTucson getBridgeToTucson(Agent agent)
            throws CannotAcquireACCException;

    /**
     * Checks if a TuCSoN Node is active on the given TCP/IP address.
     * 
     * @param netid
     *            the IP address to test
     * @param port
     *            the TCP port number to test
     * @param timeout
     *            the maximum waiting time for the test
     * @return wether a TuCSoN Node is active on the given port
     */
    boolean isActive(String netid, int port, int timeout);

    /**
     * Releases the ACC held by the caller agent.
     * 
     * @param agent
     *            the agent releasing its ACC
     */
    void releaseACC(Agent agent);

    /**
     * Enables to start a {@link alice.tucson.service.TucsonNodeService} on the
     * given TCP port on the local host.
     * 
     * @param port
     *            the TCP port number to start the TuCSoN Node on
     * @exception TucsonOperationNotPossibleException
     *                if the TuCSoN node cannot be started (e.g. TCP port
     *                already in use)
     */
    void startTucsonNode(int port) throws TucsonOperationNotPossibleException;

    /**
     * Enables to stop a {@link alice.tucson.service.TucsonNodeService} on the
     * given TCP port on the local host.
     * 
     * @param port
     *            the TCP port number to stop the TuCSoN Node on
     */
    void stopTucsonNode(int port);
}
