/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.introspection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.OperationNotAllowedException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.AbstractTucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tucson.network.exceptions.DialogException;
import alice.tucson.network.exceptions.DialogExceptionTcp;
import alice.tucson.service.ACCDescription;
import alice.tuplecentre.api.Tuple;

/**
 * 
 * @author Unknown...
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * 
 */
public class InspectorContextStub implements InspectorContext {

    /**
     * @param msg
     */
    private static void log(final String msg) {
        System.out.println("[InspectorContextStub]: " + msg);
    }

    /** id of the tuple centre to be observed */
    protected TucsonTupleCentreId tid;

    /** listeners registrated for virtual machine output events */
    private final List<InspectorContextListener> contextListeners =
            new ArrayList<InspectorContextListener>();

    private AbstractTucsonProtocol dialog;

    private boolean exitFlag;

    /** user id */
    private final TucsonAgentId id;

    private final ACCDescription profile;

    /** current observation protocol */
    private InspectorProtocol protocol;

    /**
     * 
     * @param i
     *            the agent identifier to be used by this inspector
     * @param tc
     *            the identifier of the tuple centre to inspect
     */
    public InspectorContextStub(final TucsonAgentId i,
            final TucsonTupleCentreId tc) {
        this.profile = new ACCDescription();
        this.profile.setProperty("agent-identity", i.toString());
        this.profile.setProperty("agent-role", "$inspector");
        this.profile.setProperty("tuple-centre", tc.getName());
        this.id = i;
        this.tid = tc;
        try {
            this.getTupleCentreInfo(tc);
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationNotAllowedException e) {
            e.printStackTrace();
        }
        this.exitFlag = false;
    }

    public void acceptVMEvent() throws ClassNotFoundException, IOException {
        try {
            final InspectorContextEvent msg =
                    this.dialog.receiveInspectorEvent();
            for (int i = 0; i < this.contextListeners.size(); i++) {
                this.contextListeners.get(i).onContextEvent(msg);
            }
        } catch (final DialogException e) {
            if (!this.exitFlag) {
                InspectorContextStub.log("Unreachable TuCSoN node :(");
            }
        }
    }

    public void addInspectorContextListener(final InspectorContextListener l) {
        this.contextListeners.add(l);
    }

    public void exit() throws IOException {
        this.exitFlag = true;
        try {
            this.dialog.sendNodeMsg(new ShutdownMsg(this.id));
        } catch (final DialogException e) {
            throw new IOException();
        }
    }

    public void getSnapshot(final byte snapshotMsg) throws IOException {
        try {
            this.dialog.sendNodeMsg(new GetSnapshotMsg(this.id, snapshotMsg));
        } catch (final DialogException e) {
            throw new IOException();
        }
    }

    public TucsonTupleCentreId getTid() {
        return this.tid;
    }

    public void nextStep() throws IOException {
        try {
            this.dialog.sendNodeMsg(new NextStepMsg(this.id));
        } catch (final DialogException e) {
            throw new IOException();
        }
    }

    public void
            removeInspectorContextListener(final InspectorContextListener l) {
        this.contextListeners.remove(l);
    }

    public void reset() throws IOException {
        try {
            this.dialog.sendNodeMsg(new ResetMsg(this.id));
        } catch (final DialogException e) {
            throw new IOException();
        }
    }

    public void setEventSet(final List<Tuple> wset) throws IOException {
        try {
            this.dialog.sendNodeMsg(new SetEventSetMsg(this.id, wset));
        } catch (final DialogException e) {
            throw new IOException();
        }
    }

    public void setProtocol(final InspectorProtocol p) throws IOException {
        final InspectorProtocol newp = new InspectorProtocol();
        newp.setTsetObservType(p.getTsetObservType());
        newp.setTsetFilter(p.getTsetFilter());
        newp.setWsetFilter(p.getWsetFilter());
        newp.setTracing(p.isTracing());
        newp.setPendingQueryObservType(p.getPendingQueryObservType());
        newp.setReactionsObservType(p.getReactionsObservType());
        try {
            this.dialog.sendNodeMsg(new SetProtocolMsg(this.id, newp));
        } catch (final DialogException e) {
            throw new IOException();
        }
        this.protocol = p;
    }

    public void setTupleSet(final List<Tuple> tset) throws IOException {
        try {
            this.dialog.sendNodeMsg(new SetTupleSetMsg(this.id, tset));
        } catch (final DialogException e) {
            throw new IOException();
        }
    }

    /**
     * resolve information about a tuple centre
     * 
     * @param titcd
     *            the identifier of the tuple centre to be resolved
     */
    protected void resolveTupleCentreInfo(final TucsonTupleCentreId titcd) {
        try {
            this.getTupleCentreInfo(titcd);
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationNotAllowedException e) {
            e.printStackTrace();
        }
    }

    /**
     * if request to a new tuple centre -> create new connection to target
     * daemon providing the tuple centre otherwise return the already
     * established connection
     */
    private AbstractTucsonProtocol getTupleCentreInfo(
            final TucsonTupleCentreId tc) throws UnreachableNodeException,
            OperationNotAllowedException {

        try {
            final String node = alice.util.Tools.removeApices(tc.getNode());
            final int port = tc.getPort();
            this.dialog = new TucsonProtocolTCP(node, port);
            this.dialog.sendEnterRequest(this.profile);
            this.dialog.receiveEnterRequestAnswer();
            if (this.dialog.isEnterRequestAccepted()) {
                this.protocol = new InspectorProtocol();
                final NewInspectorMsg msg =
                        new NewInspectorMsg(this.id, tc.getName(),
                                this.protocol);
                this.dialog.sendInspectorMsg(msg);
                return this.dialog;
            }
        } catch (final IOException e) {
            throw new alice.tucson.api.exceptions.UnreachableNodeException();
        } catch (final DialogExceptionTcp e) {
            e.printStackTrace();
        } catch (final DialogException e) {
            e.printStackTrace();
        }

        throw new alice.tucson.api.exceptions.OperationNotAllowedException();

    }

}
