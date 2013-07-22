package alice.tucson.network;

import java.net.InetSocketAddress;

/**
 * <p>
 * TPConfig
 * </p>
 * <p>
 * A singleton class to manage configuration of TucsonProtocol
 * </p>
 */
public class TPConfig {

    // TCP configuration ----------------------------------
    private static final int DEFAULT_TCP_PORT = 20504;

    private static TPConfig singletonTPConfig = null;

    /** Return singleton instance of TPConfig */
    public static synchronized TPConfig getInstance() {
        if (TPConfig.singletonTPConfig == null) {
            TPConfig.singletonTPConfig = new TPConfig();
        }
        return TPConfig.singletonTPConfig;
    }

    // Generic configuration ------------------------------
    private final int DEFAULT_PROTOCOL_TYPE = TPFactory.DIALOG_TYPE_TCP;

    private int TCP_PORT = -1;

    private TPConfig() {
    }

    /**
     * Return the default Protocol type
     * 
     * @return the protocol type codified as an integer
     */
    public int getDefaultProtocolType() {
        return this.DEFAULT_PROTOCOL_TYPE;
    }

    /**
     * Return the default TCP port number
     * 
     * @return a valid TCP port number
     */
    public int getDefaultTcpPort() {
        return TPConfig.DEFAULT_TCP_PORT;
    }

    /**
     * Return the TCP port number
     * 
     * @return a valid TCP port number
     */
    public int getNodeTcpPort() {
        if (this.TCP_PORT < 0) {
            return TPConfig.DEFAULT_TCP_PORT;
        }
        return this.TCP_PORT;
    }

    /**
     * TODO CICORA: Set the TCP port: only one set is permitted, the second one will
     * be ignored
     * 
     * @throws IllegalArgumentException
     *             if the port value in already set OR parameter is outside the
     *             specified range, which is between 0 and 64000, inclusive. See
     *             {@link InetSocketAddress} for detail.
     */
    public synchronized void setTcpPort(final int portNumber) {
        if ((portNumber < 1) || (portNumber > 64000) || (this.TCP_PORT > 0)) {
            throw new IllegalArgumentException();
        }
        this.TCP_PORT = portNumber;
    }

}
