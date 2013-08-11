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
public abstract class TPFactory {

    private static final int MAX_UNBOUND_PORT = 64000;
    
    /**
     * Constant indentify implementated protocol type: one constant for each
     * implemented protocol
     */
    public static final int DIALOG_TYPE_TCP = 0;

    /**
     * 
     * @param tucsonProtocolType
     *            the type code of the TuCSoN protocol to be used
     * @param tid
     *            the identifier of the tuple centre to connect to
     * @return the connection protocol hosting communications
     * @throws DialogException
     *             if something goes wrong in the underlying network
     * @throws UnreachableNodeException
     *             if the target tuple centre is unreachable
     */
    public static AbstractTucsonProtocol getDialogAgentSide(
            final int tucsonProtocolType, final TucsonTupleCentreId tid)
            throws DialogException, UnreachableNodeException {
        final String node = alice.util.Tools.removeApices(tid.getNode());
        final int port = tid.getPort();

        // TODO CICORA: il controllo su porta e address va fatto meglio, vedere
        // come e'
        // fatto nel resto del codice
        if ((port < 1) || (port > MAX_UNBOUND_PORT)) {
            throw new DialogException("Illegal port argument");
        }
        AbstractTucsonProtocol tp = null;
        if (tucsonProtocolType == TPFactory.DIALOG_TYPE_TCP) {
            tp = new TucsonProtocolTCP(node, port);
        } else {
            throw new DialogException("Unsupported protocol type");
        }

        return tp;
    }

    /**
     * 
     * @param tid
     *            the identifier of the tuple centre to connect to
     * @return the connection protocol hosting communications
     * @throws DialogException
     *             if something goes wrong in the underlying network
     * @throws UnreachableNodeException
     *             if the target tuple centre is unreachable
     */
    public static AbstractTucsonProtocol getDialogAgentSide(
            final TucsonTupleCentreId tid) throws DialogException,
            UnreachableNodeException {

        final TPConfig config = TPConfig.getInstance();
        return TPFactory.getDialogAgentSide(config.getDefaultProtocolType(),
                tid);
    }

    /**
     * Instantiate a new TucsonProtocol based of a default implementation type.
     * 
     * @return the TucsonProtocol class
     * @throws DialogException
     *             if something goes wrong in the underlying network
     */
    public static AbstractTucsonProtocol getDialogNodeSide()
            throws DialogException {

        final TPConfig config = TPConfig.getInstance();
        return TPFactory.getDialogNodeSide(config.getDefaultProtocolType());
    }

    /**
     * Instantiate a new TucsonProtocol based on type specified by parameter.
     * 
     * @param tucsonProtocolType
     *            the type code of the TuCSoN protocol to be used
     * @return the TucsonProtocol class
     * @throws DialogException
     *             if something goes wrong in the underlying network
     */
    public static AbstractTucsonProtocol getDialogNodeSide(
            final int tucsonProtocolType) throws DialogException {

        AbstractTucsonProtocol tp = null;

        if (tucsonProtocolType == TPFactory.DIALOG_TYPE_TCP) {
            final TPConfig config = TPConfig.getInstance();
            tp = new TucsonProtocolTCP(config.getNodeTcpPort());
        } else {
            throw new DialogException("Unsupported protocol type");
        }

        return tp;
    }

    private TPFactory() {
        /*
         * 
         */
    }

}
