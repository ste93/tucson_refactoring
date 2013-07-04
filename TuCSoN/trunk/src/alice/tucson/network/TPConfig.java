package alice.tucson.network;

/**
 * <p>
 * TPConfig
 * </p>
 * <p>
 * A singleton class to manage configuration of TucsonProtocol
 * </p>
 */
public class TPConfig {

	// Generic configuration ------------------------------
	private final int DEFAULT_PROTOCOL_TYPE = TPFactory.DIALOG_TYPE_TCP;

	// TCP configuration ----------------------------------
	private static final int DEFAULT_TCP_PORT = 20504;
	private int TCP_PORT = -1;

	private static TPConfig singletonTPConfig = null;

	private TPConfig() {
	}

	/** Return singleton instance of TPConfig */
	public static synchronized TPConfig getInstance() {
		if (singletonTPConfig == null) {
			singletonTPConfig = new TPConfig();
		}
		return singletonTPConfig;
	}

	/**
	 * TODO ...
	 * Set the TCP port: only one set is permitted, the second one will be
	 * ignored
	 * 
	 * @throws IllegalArgumentException
	 *             if the port value in already set OR parameter is outside the
	 *             specified range, which is between 0 and 64000, inclusive. See
	 *             {@link InetSocketAddress} for detail.
	 */
	public synchronized void setTcpPort(int portNumber) {
		if (!TucsonProtocolTCP.checkPortValue(portNumber) || TCP_PORT > 0) {
			throw new IllegalArgumentException();
		}
		TCP_PORT = portNumber;
	}

	/**
	 * Return the TCP port number
	 * 
	 * @return a valid TCP port number
	 */
	public int getNodeTcpPort() {
		if (TCP_PORT < 0)
			return DEFAULT_TCP_PORT;
		else
			return TCP_PORT;
	}

	/**
	 * Return the default TCP port number
	 * 
	 * @return a valid TCP port number
	 */
	public int getDefaultTcpPort() {
		return DEFAULT_TCP_PORT;
	}

	/**
	 * Return the default Protocol type
	 * 
	 * @return the protocol type codified as an integer
	 */
	public int getDefaultProtocolType() {
		return DEFAULT_PROTOCOL_TYPE;
	}

}
