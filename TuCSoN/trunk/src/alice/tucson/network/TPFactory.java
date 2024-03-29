package alice.tucson.network;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.exceptions.DialogInitializationException;
import alice.tucson.network.exceptions.IllegalPortNumberException;
import alice.tucson.network.exceptions.InvalidProtocolTypeException;
import alice.tucson.service.TucsonNodeService;

/**
 * <p>
 * Title: TPFactory (Tucson Protocol Factory)
 * </p>
 * <p>
 * Description: the factory class to build a specific implementation of
 * TucsonProtocol
 * </p>
 *
 * @author Saverio Cicora
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public final class TPFactory {

    /**
     * Constant indentify implementated protocol type: one constant for each
     * implemented protocol
     */
    public static final int DIALOG_TYPE_TCP = 0;
    private static final int MAX_UNBOUND_PORT = 64000;

    /**
     *
     * @param tucsonProtocolType
     *            the type code of the TuCSoN protocol to be used
     * @param tid
     *            the identifier of the tuple centre to connect to
     * @return the connection protocol hosting communications
     * @throws DialogInitializationException
     *             if something goes wrong in the underlying network
     * @throws UnreachableNodeException
     *             if the target tuple centre is unreachable
     * @throws InvalidProtocolTypeException
     *             if the protocol type used is not DIALOG_TYPE_TCP
     */
    public static AbstractTucsonProtocol getDialogAgentSide(
            final int tucsonProtocolType, final TucsonTupleCentreId tid)
            throws DialogInitializationException, UnreachableNodeException,
            InvalidProtocolTypeException {
        final String node = alice.util.Tools.removeApices(tid.getNode());
        final int port = tid.getPort();
        // TODO CICORA: il controllo su porta e address va fatto meglio, vedere
        // come e'
        // fatto nel resto del codice
        if (port < 1 || port > TPFactory.MAX_UNBOUND_PORT) {
            throw new IllegalPortNumberException(
                    "Port number out of bounds. Port number: " + port);
        }
        AbstractTucsonProtocol tp = null;
        if (tucsonProtocolType == TPFactory.DIALOG_TYPE_TCP) {
            tp = new TucsonProtocolTCP(node, port);
        } else {
            throw new InvalidProtocolTypeException("Unsupported protocol type");
        }
        return tp;
    }

    /**
     *
     * @param tid
     *            the identifier of the tuple centre to connect to
     * @return the connection protocol hosting communications
     * @throws DialogInitializationException
     *             if something goes wrong in the underlying network
     * @throws UnreachableNodeException
     *             if the target tuple centre is unreachable
     */
    public static AbstractTucsonProtocol getDialogAgentSide(
            final TucsonTupleCentreId tid) throws UnreachableNodeException,
            DialogInitializationException {
        final TucsonNodeService tns = TucsonNodeService.getNode(tid.getPort());
        final TPConfig config;
        if (tns != null) {
            config = tns.getTPConfig();
        } else {
            config = new TPConfig();
            config.setTcpPort(tid.getPort());
        }
        try {
            return TPFactory.getDialogAgentSide(
                    config.getDefaultProtocolType(), tid);
        } catch (final InvalidProtocolTypeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Instantiate a new TucsonProtocol based on type specified by parameter.
     *
     * @param tucsonProtocolType
     *            the type code of the TuCSoN protocol to be used
     * @param portno
     *            the port where the TuCSoN node to contact is listening to
     * @return the TucsonProtocol class
     * @throws DialogInitializationException
     *             if something goes wrong in the underlying network
     * @throws InvalidProtocolTypeException
     *             if the protocol type used is not DIALOG_TYPE_TCP
     */
    public static AbstractTucsonProtocol getDialogNodeSide(
            final int tucsonProtocolType, final int portno)
            throws InvalidProtocolTypeException, DialogInitializationException {
        AbstractTucsonProtocol tp = null;
        if (tucsonProtocolType == TPFactory.DIALOG_TYPE_TCP) {
            final TPConfig config = TucsonNodeService.getNode(portno)
                    .getTPConfig();
            final int port = config.getNodeTcpPort();
            if (port < 1 || port > TPFactory.MAX_UNBOUND_PORT) {
                throw new IllegalPortNumberException(
                        "Port number out of bounds. Port number: " + port);
            }
            tp = new TucsonProtocolTCP(port);
        } else {
            throw new InvalidProtocolTypeException("Unsupported protocol type");
        }
        return tp;
    }

    private TPFactory() {
        /*
         *
         */
    }
}
