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

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.OperationNotAllowedException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tucson.service.ACCDescription;
import alice.tuplecentre.api.Tuple;

public class InspectorContextStub implements InspectorContext {

    /** id of the tuple centre to be observed */
    protected TucsonTupleCentreId tid;

    /** listeners registrated for virtual machine output events */
    private final List<InspectorContextListener> contextListeners =
            new ArrayList<InspectorContextListener>();

    /** user id */
    private final TucsonAgentId id;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private final ACCDescription profile;

    /** current observation protocol */
    private InspectorProtocol protocol;

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
            // TODO Properly handle Exception
        } catch (final OperationNotAllowedException e) {
            // TODO Properly handle Exception
        }
    }

    /**
     * waits and processes TuCSoN virtual machine events
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void acceptVMEvent() throws ClassNotFoundException, IOException {
        try {
            final InspectorContextEvent msg =
                    (InspectorContextEvent) this.inStream.readObject();
            for (int i = 0; i < this.contextListeners.size(); i++) {
                this.contextListeners.get(i).onContextEvent(msg);
            }
        } catch (final EOFException e) {
            // TODO Properly handle Exception
        }
    }

    /**
     * Adds a listener to Inspector Events
     * 
     * @param l
     *            the listener
     */
    public void addInspectorContextListener(final InspectorContextListener l) {
        this.contextListeners.add(l);
    }

    /**
     * shutdown inspector
     * 
     * @throws IOException
     */
    public void exit() throws IOException {
        this.outStream.writeObject(new ShutdownMsg(this.id));
        this.outStream.flush();
    }

    /**
     * get a snapshot of tuple set
     * 
     * @throws IOException
     */
    public void getSnapshot(final byte snapshotMsg) throws IOException {
        this.outStream.writeObject(new GetSnapshotMsg(this.id, snapshotMsg));
        this.outStream.flush();
    }

    public TucsonTupleCentreId getTid() {
        return this.tid;
    }

    /**
     * when doing trace -> ask for a new virtual machine step
     * 
     * @throws IOException
     */
    public void nextStep() throws IOException {
        this.outStream.writeObject(new NextStepMsg(this.id));
        this.outStream.flush();
    }

    /**
     * Removes a listener to Inspector Events
     */
    public void
            removeInspectorContextListener(final InspectorContextListener l) {
        this.contextListeners.remove(l);
    }

    /**
     * reset the tuple centre
     * 
     * @throws IOException
     */
    public void reset() throws IOException {
        this.outStream.writeObject(new ResetMsg(this.id));
        this.outStream.flush();
    }

    /**
     * set a new query set
     * 
     * @throws IOException
     */
    public void setEventSet(final List<Tuple> wset) throws IOException {
        this.outStream.writeObject(new SetEventSetMsg(this.id, wset));
        this.outStream.flush();
    }

    /**
     * setting a new observation protocol
     * 
     * @throws IOException
     */
    public void setProtocol(final InspectorProtocol p) throws IOException {
        final InspectorProtocol newp = new InspectorProtocol();
        newp.tsetObservType = p.tsetObservType;
        newp.tsetFilter = p.tsetFilter;
        newp.wsetFilter = p.wsetFilter;
        newp.tracing = p.tracing;
        newp.pendingQueryObservType = p.pendingQueryObservType;
        newp.reactionsObservType = p.reactionsObservType;
        this.outStream.writeObject(new SetProtocolMsg(this.id, newp));
        this.outStream.flush();
        this.protocol = p;
    }

    /**
     * set a new tuple set
     * 
     * @throws IOException
     */
    public void setTupleSet(final List<Tuple> tset) throws IOException {
        this.outStream.writeObject(new SetTupleSetMsg(this.id, tset));
        this.outStream.flush();
    }

    /**
     * resolve information about a tuple centre
     */
    protected void resolveTupleCentreInfo(final TucsonTupleCentreId titcd) {
        try {
            this.getTupleCentreInfo(titcd);
        } catch (final UnreachableNodeException e) {
            // TODO Properly handle Exception
        } catch (final OperationNotAllowedException e) {
            // TODO Properly handle Exception
        }
    }

    /**
     * if request to a new tuple centre -> create new connection to target
     * daemon providing the tuple centre otherwise return the already
     * established connection
     */
    private TucsonProtocol getTupleCentreInfo(final TucsonTupleCentreId tc)
            throws UnreachableNodeException, OperationNotAllowedException {

        try {
            final String node = alice.util.Tools.removeApices(tc.getNode());
            final int port = tc.getPort();
            final TucsonProtocol dialog = new TucsonProtocolTCP(node, port);
            dialog.sendEnterRequest(this.profile);
            dialog.receiveEnterRequestAnswer();
            if (dialog.isEnterRequestAccepted()) {
                this.outStream = dialog.getOutputStream();
                this.inStream = dialog.getInputStream();
                this.protocol = new InspectorProtocol();
                final NewInspectorMsg msg =
                        new NewInspectorMsg(this.id, tc.getName(),
                                this.protocol);
                this.outStream.writeObject(msg);
                return dialog;
            }
        } catch (final IOException e) {
            throw new alice.tucson.api.exceptions.UnreachableNodeException();
        } catch (final Exception e) {
            // TODO Properly handle Exception
        }

        throw new alice.tucson.api.exceptions.OperationNotAllowedException();

    }

}
