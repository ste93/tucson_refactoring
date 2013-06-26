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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;
import java.util.Properties;

import alice.tucson.service.ACCDescription;

/**
 * 
 */
@SuppressWarnings("serial")
public abstract class TucsonProtocol implements java.io.Serializable {

    private static final int REQ_ENTERCONTEXT = 1;

    // private static final int TUCSON_MARK = 303;
    // is this the value sent when I do a telnet via command line?
    private static final int REQ_TELNET = 0;

    private ACCDescription context;

    private boolean reqAllowed;

    private int reqType;

    abstract public TucsonProtocol acceptNewDialog() throws IOException,
            SocketTimeoutException;

    abstract public void end() throws IOException;

    public ACCDescription getContextDescription() {
        return this.context;
    }

    abstract public ObjectInputStream getInputStream();

    abstract public ObjectOutputStream getOutputStream();

    public boolean isEnterRequest() {
        return this.reqType == TucsonProtocol.REQ_ENTERCONTEXT;
    }

    public boolean isEnterRequestAccepted() {
        return this.reqAllowed;
    }

    /* MODIFIED BY <s.mariani@unibo.it> */
    public boolean isTelnet() {
        return this.reqType == TucsonProtocol.REQ_TELNET;
    }

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

    public void receiveEnterRequestAnswer() throws IOException {
        this.reqAllowed = this.receiveBoolean();
    }

    public void receiveFirstRequest() throws IOException {
        this.reqType = this.receiveInt();
    }

    /**
     * 
     * @param ctx
     * @throws IOException
     */
    public void sendEnterRequest(final ACCDescription ctx) throws IOException {

        this.send(TucsonProtocol.REQ_ENTERCONTEXT);

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

    public void sendEnterRequestAccepted() throws IOException {
        this.send(true);
        this.flush();
    }

    public void sendEnterRequestRefused() throws IOException {
        this.send(false);
        this.flush();
    }

    abstract protected void flush() throws IOException;

    abstract protected boolean receiveBoolean() throws IOException;

    abstract protected int receiveInt() throws IOException;

    abstract protected Object receiveObject() throws ClassNotFoundException,
            IOException;

    abstract protected String receiveString() throws ClassNotFoundException,
            IOException;

    abstract protected void send(boolean value) throws IOException;

    abstract protected void send(byte[] value) throws IOException;

    abstract protected void send(int value) throws IOException;

    abstract protected void send(Object value) throws IOException;

    abstract protected void send(String value) throws IOException;

}
