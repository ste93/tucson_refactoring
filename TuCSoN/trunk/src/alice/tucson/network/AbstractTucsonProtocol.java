/*
 * Copyright (C) 2001-2002 aliCE team at deis.unibo.it This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.network;

import java.io.IOException;
import java.util.Properties;

import alice.tucson.network.exceptions.DialogException;
import alice.tucson.service.ACCDescription;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 03/lug/2013
 * 
 */
public abstract class AbstractTucsonProtocol implements java.io.Serializable {

    private static final int REQ_ENTERCONTEXT = 1;

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    private ACCDescription context;

    private boolean reqAllowed;

    private int reqType;

    /**
     * 
     * @return the protocol to be used for interacting with TuCSoN
     */
    public abstract AbstractTucsonProtocol acceptNewDialog()
            throws DialogException;

    /**
     */
    public abstract void end() throws DialogException;

    /**
     * 
     * @return the ACC profile associated to this protocol
     */
    public ACCDescription getContextDescription() {
        return this.context;
    }

    /*
     * public abstract ObjectInputStream getInputStream(); public abstract
     * ObjectOutputStream getOutputStream();
     */

    /**
     * 
     * @return wether the received request is an ACC acquisition request
     */
    public boolean isEnterRequest() {
        return this.reqType == AbstractTucsonProtocol.REQ_ENTERCONTEXT;
    }

    /**
     * 
     * @return wether the ACC acquisition requested has been accepted or not
     */
    public boolean isEnterRequestAccepted() {
        return this.reqAllowed;
    }

    /**
     * 
     * @throws ClassNotFoundException
     *             if the received object's class cannot be found
     * @throws IOException
     *             if some network problems arise
     */
    public void receiveEnterRequest() throws ClassNotFoundException,
            IOException {
        final String agentName = this.receiveString();
        final String agentRole = this.receiveString();
        final String tcName = this.receiveString();
        final Properties profile = new Properties();
        if (agentName.startsWith("'@'")) {
            profile.setProperty("tc-identity", agentName);
        } else {
            profile.setProperty("agent-identity", agentName);
        }
        profile.setProperty("agent-role", agentRole);
        profile.setProperty("tuple-centre", tcName);
        this.context = new ACCDescription(profile);
    }

    /**
     * 
     * @throws IOException
     *             if some network problems arise
     */
    public void receiveEnterRequestAnswer() throws IOException {
        this.reqAllowed = this.receiveBoolean();
    }

    /**
     * 
     * @throws IOException
     *             if some network problems arise
     */
    public void receiveFirstRequest() throws IOException {
        this.reqType = this.receiveInt();
    }

    abstract public TucsonMsg receiveMsg() throws DialogException;

    abstract public TucsonMsgReply receiveMsgReply() throws DialogException;

    abstract public TucsonMsgRequest receiveMsgRequest() throws DialogException;

    /**
     * 
     * @param ctx
     *            the ACC profile to be associated to this protocol
     * @throws IOException
     *             if some network problems arise
     */
    public void sendEnterRequest(final ACCDescription ctx) throws IOException {

        this.send(AbstractTucsonProtocol.REQ_ENTERCONTEXT);

        String agentName = ctx.getProperty("agent-identity");
        if (agentName == null) {
            agentName = ctx.getProperty("tc-identity");
            if (agentName == null) {
                agentName = "anonymous";
            }
        }
        this.send(agentName);

        String agentProfile = ctx.getProperty("agent-role");
        if (agentProfile == null) {
            agentProfile = "default";
        }
        this.send(agentProfile);

        String tcName = ctx.getProperty("tuple-centre");
        if (tcName == null) {
            tcName = "_";
        }
        this.send(tcName);

        this.flush();

    }

    /**
     * 
     * @throws IOException
     *             if some network problems arise
     */
    public void sendEnterRequestAccepted() throws IOException {
        this.send(true);
        this.flush();
    }

    /**
     * 
     * @throws IOException
     *             if some network problems arise
     */
    public void sendEnterRequestRefused() throws IOException {
        this.send(false);
        this.flush();
    }

    abstract public void sendMsg(TucsonMsg msg) throws DialogException;

    abstract public void sendMsgReply(TucsonMsgReply reply)
            throws DialogException;

    abstract public void sendMsgRequest(TucsonMsgRequest request)
            throws DialogException;

    /**
     * 
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract void flush() throws IOException;

    /**
     * 
     * @return the Java boolean value received
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract boolean receiveBoolean() throws IOException;

    /**
     * 
     * @return the Java int value received
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract int receiveInt() throws IOException;

    /**
     * 
     * @return the Java object received
     * @throws ClassNotFoundException
     *             if the received object's class cannot be found
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract Object receiveObject() throws ClassNotFoundException,
            IOException;

    /**
     * 
     * @return the Java string received
     * @throws ClassNotFoundException
     *             if the received object's class cannot be found
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract String receiveString() throws ClassNotFoundException,
            IOException;

    /**
     * 
     * @param value
     *            the Jaba boolean value to send
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract void send(boolean value) throws IOException;

    /**
     * 
     * @param value
     *            the Java byte array to send
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract void send(byte[] value) throws IOException;

    /**
     * 
     * @param value
     *            the Java int value to send
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract void send(int value) throws IOException;

    /**
     * 
     * @param value
     *            the Java object to send
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract void send(Object value) throws IOException;

    /**
     * 
     * @param value
     *            the Java String to send
     * @throws IOException
     *             if some network problems arise
     */
    protected abstract void send(String value) throws IOException;

}
