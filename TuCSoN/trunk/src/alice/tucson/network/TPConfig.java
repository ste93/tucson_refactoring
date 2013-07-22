package alice.tucson.network;

/**
 * <p>
 * TPConfig
 * </p>
 * <p>
 * A singleton class to manage configuration of TucsonProtocol
 * </p>
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 22/lug/2013
 * 
 */
public final class TPConfig {

    // TCP configuration ----------------------------------
    private static final int DEFAULT_TCP_PORT = 20504;

    private static TPConfig singletonTPConfig = null;

    /**
     * Return the default TCP port number
     * 
     * @return a valid TCP port number
     */
    public static int getDefaultTcpPort() {
        return TPConfig.DEFAULT_TCP_PORT;
    }

    /**
     * Return singleton instance of TPConfig
     * 
     * @return the singleton instance of this TuCSoN Protocol configurator
     */
    public static synchronized TPConfig getInstance() {
        if (TPConfig.singletonTPConfig == null) {
            TPConfig.singletonTPConfig = new TPConfig();
        }
        return TPConfig.singletonTPConfig;
    }

    // Generic configuration ------------------------------
    private final int defProtType = TPFactory.DIALOG_TYPE_TCP;

    private int tcpPort = -1;

    private TPConfig() {
    }

    /**
     * Return the default Protocol type
     * 
     * @return the protocol type codified as an integer
     */
    public int getDefaultProtocolType() {
        return this.defProtType;
    }

    /**
     * Return the TCP port number
     * 
     * @return a valid TCP port number
     */
    public int getNodeTcpPort() {
        if (this.tcpPort < 0) {
            return TPConfig.DEFAULT_TCP_PORT;
        }
        return this.tcpPort;
    }

    /**
     * TODO CICORA: Set the TCP port: only one set is permitted, the second one
     * will be ignored
     * 
     * @param portNumber
     *            the TCP listening port
     * 
     * @throws IllegalArgumentException
     *             if the port value in already set OR parameter is outside the
     *             specified range, which is between 0 and 64000, inclusive.
     */
    public synchronized void setTcpPort(final int portNumber)
            throws IllegalArgumentException {
        if ((portNumber < 1) || (portNumber > 64000) || (this.tcpPort > 0)) {
            throw new IllegalArgumentException();
        }
        this.tcpPort = portNumber;
    }

}