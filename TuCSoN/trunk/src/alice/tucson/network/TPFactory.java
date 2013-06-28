package alice.tucson.network;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.exceptions.DialogException;

/**
 * <p>
 * Title: TPFactory (Tucson Protocol Factory)
 * </p>
 * <p>
 * Description: the factory class to build a specific implementation of
 * TucsonProtocol
 * </p>
 * 
 * @author Cicora Saverio
 * @version 1.0
 */

public class TPFactory {

	/** Constant of TucsonProtocol type: one for a possible implementation */
	public static final int DIALOG_TYPE_TCP = 0;

	/**
	 * Instantiate a new TucsonProtocol based of a default implementation type.
	 * 
	 * @return the TucsonProtocol class
	 * @throws DialogException
	 */
	public static TucsonProtocol getDialogNodeSide() throws DialogException {

		TPConfig config = TPConfig.getInstance();
		return getDialogNodeSide(config.getDefaultProtocolType());
	}

	/**
	 * Instantiate a new TucsonProtocol based on type specified by parameter.
	 * 
	 * @return the TucsonProtocol class
	 * @throws DialogException
	 */
	public static TucsonProtocol getDialogNodeSide(int tucsonProtocolType) throws DialogException {

		TucsonProtocol tp = null;

		switch (tucsonProtocolType) {
		case DIALOG_TYPE_TCP:
			TPConfig config = TPConfig.getInstance();
			tp = new TucsonProtocolTCP(config.getNodeTcpPort());
			break;

		default:
			throw new DialogException("Unsupported protocol type");
		}

		return tp;
	}

	public static TucsonProtocol getDialogAgentSide(TucsonTupleCentreId tid) throws DialogException, UnreachableNodeException {

		TPConfig config = TPConfig.getInstance();
		return getDialogAgentSide(config.getDefaultProtocolType(), tid);
	}

	public static TucsonProtocol getDialogAgentSide(int tucsonProtocolType, TucsonTupleCentreId tid) throws DialogException, UnreachableNodeException {
		String node = alice.util.Tools.removeApices(tid.getNode());
		int port = tid.getPort();

		// TODO il controllo su porta e address va fatto meglio, vedere come è
		// fatto nel resto del codice
		if (!TucsonProtocolTCP.checkPortValue(port))
			throw new DialogException("Illegal port argument");
		TucsonProtocol tp = null;
		switch (tucsonProtocolType) {
		case DIALOG_TYPE_TCP:
			tp = new TucsonProtocolTCP(node, port);
			break;

		default:
			throw new DialogException("Unsupported protocol type");
		}

		return tp;
	}

}
