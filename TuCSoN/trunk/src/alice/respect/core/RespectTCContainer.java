package alice.respect.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

import alice.respect.api.ILinkContext;
import alice.respect.api.IManagementContext;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.IOrdinarySynchInterface;
import alice.respect.api.IRemoteLinkProvider;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.ISpecificationSynchInterface;
import alice.respect.api.ITCRegistry;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InstantiationNotPossibleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tucson.service.RemoteLinkProvider;

/**
 * A Container for ReSpecT tuple centres
 * 
 * @author matteo casadei v 2.1.1
 * 
 */
public final class RespectTCContainer {

    public static final int QUEUE_SIZE = 10;
    private static RespectTCContainer container;
    private static int defaultport;

    public static int getDefPort() {
        return RespectTCContainer.defaultport;
    }

    public static RespectTCContainer getRespectTCContainer() {
        if (RespectTCContainer.container == null) {
            RespectTCContainer.container = new RespectTCContainer();
        }
        return RespectTCContainer.container;
    }

    public static void setDefPort(final int port) {
        RespectTCContainer.defaultport = port;
    }

    public String hostname;

    public String loopback;

    private final ITCRegistry registry;

    private IRemoteLinkProvider stub;

    private RespectTCContainer() {
        this.registry = new RespectLocalRegistry();
        this.stub = null;
        try {
            this.loopback =
                    InetAddress.getLocalHost().getHostAddress().toString();
            this.hostname = InetAddress.getLocalHost().getHostName().toString();
        } catch (final UnknownHostException e) {
            // TODO Auto-generated catch block
            this.loopback = null;
            this.hostname = null;
        }
    }

    public void addStub(final IRemoteLinkProvider s) {
        if (s == null) {
            this.stub = s;
        }
    }

    public RespectTC createRespectTC(final TupleCentreId id, final Integer q) {
        final RespectTC rtc = new RespectTC(id, this, q);
        this.registry.addTC(rtc);
        return rtc;
    }

    /**
     * Return a LinkContext for remote/local call
     * 
     * @param id
     *            the identifier of the tuple centre target (local or remote)
     * @throws OperationNotPossibleException
     */
    public ILinkContext getLinkContext(final TupleCentreId id)
            throws OperationNotPossibleException {
        if ((this.hostname.equals(id.getNode()) || this.loopback.equals(id
                .getNode()))
                && (id.getPort() == RespectTCContainer.defaultport)) {
            try {
                return ((RespectTC) this.registry.getTC(id)).getLinkContext();
            } catch (final InstantiationNotPossibleException e) {
                final RespectTC tc =
                        new RespectTC(id, this, RespectTCContainer.QUEUE_SIZE);
                this.registry.addTC(tc);
                return tc.getLinkContext();
            }
        }
        if (this.stub == null) {
            this.stub = new RemoteLinkProvider();
        }
        return this.stub.getRemoteLinkContext(id);
    }

    public IManagementContext getManagementContext(final TupleCentreId id) {
        try {
            return ((RespectTC) this.registry.getTC(id)).getManagementContext();
        } catch (final Exception e) {
            final RespectTC tc =
                    new RespectTC(id, this, RespectTCContainer.QUEUE_SIZE);
            this.registry.addTC(tc);
            return tc.getManagementContext();
        }
    }

    public IOrdinaryAsynchInterface getOrdinaryAsynchInterface(
            final TupleCentreId id) {
        try {
            return ((RespectTC) this.registry.getTC(id))
                    .getOrdinaryAsynchInterface();
        } catch (final Exception e) {
            final RespectTC tc =
                    new RespectTC(id, this, RespectTCContainer.QUEUE_SIZE);
            this.registry.addTC(tc);
            return tc.getOrdinaryAsynchInterface();
        }
    }

    public IOrdinarySynchInterface getOrdinarySynchInterface(
            final TupleCentreId id) {
        try {
            return ((RespectTC) this.registry.getTC(id))
                    .getOrdinarySynchInterface();
        } catch (final Exception e) {
            final RespectTC tc =
                    new RespectTC(id, this, RespectTCContainer.QUEUE_SIZE);
            this.registry.addTC(tc);
            return tc.getOrdinarySynchInterface();
        }
    }

    public ITCRegistry getRegistry() {
        return this.registry;
    }

    public ISpecificationAsynchInterface getSpecificationAsynchInterface(
            final TupleCentreId id) {
        try {
            return ((RespectTC) this.registry.getTC(id))
                    .getSpecificationAsynchInterface();
        } catch (final Exception e) {
            final RespectTC tc =
                    new RespectTC(id, this, RespectTCContainer.QUEUE_SIZE);
            this.registry.addTC(tc);
            return tc.getSpecificationAsynchInterface();
        }
    }

    public ISpecificationSynchInterface getSpecificationSynchInterface(
            final TupleCentreId id) {
        try {
            return ((RespectTC) this.registry.getTC(id))
                    .getSpecificationSynchInterface();
        } catch (final Exception e) {
            final RespectTC tc =
                    new RespectTC(id, this, RespectTCContainer.QUEUE_SIZE);
            this.registry.addTC(tc);
            return tc.getSpecificationSynchInterface();
        }
    }

}
