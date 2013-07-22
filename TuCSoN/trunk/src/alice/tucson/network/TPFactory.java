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

    /**
     * Constant indentify implementated protocol type: one constant for each
     * implemented protocol
     */
    public static final int DIALOG_TYPE_TCP = 0;

    public static AbstractTucsonProtocol getDialogAgentSide(
            final int tucsonProtocolType, final TucsonTupleCentreId tid)
            throws DialogException, UnreachableNodeException {
        final String node = alice.util.Tools.removeApices(tid.getNode());
        final int port = tid.getPort();

        // TODO CICORA: il controllo su porta e address va fatto meglio, vedere come è
        // fatto nel resto del codice
        if ((port < 1) || (port > 64000)) {
            throw new DialogException("Illegal port argument");
        }
        AbstractTucsonProtocol tp = null;
        switch (tucsonProtocolType) {
            case DIALOG_TYPE_TCP:
                tp = new TucsonProtocolTCP(node, port);
                break;

            default:
                throw new DialogException("Unsupported protocol type");
        }

        return tp;
    }

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
     */
    public static AbstractTucsonProtocol getDialogNodeSide()
            throws DialogException {

        final TPConfig config = TPConfig.getInstance();
        return TPFactory.getDialogNodeSide(config.getDefaultProtocolType());
    }

    /**
     * Instantiate a new TucsonProtocol based on type specified by parameter.
     * 
     * @return the TucsonProtocol class
     * @throws DialogException
     */
    public static AbstractTucsonProtocol getDialogNodeSide(
            final int tucsonProtocolType) throws DialogException {

        AbstractTucsonProtocol tp = null;

        switch (tucsonProtocolType) {
            case DIALOG_TYPE_TCP:
                final TPConfig config = TPConfig.getInstance();
                tp = new TucsonProtocolTCP(config.getNodeTcpPort());
                break;

            default:
                throw new DialogException("Unsupported protocol type");
        }

        return tp;
    }

}
