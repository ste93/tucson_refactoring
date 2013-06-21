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

    private boolean request_allowed;

    private int request_type;

    abstract public TucsonProtocol acceptNewDialog() throws IOException,
            SocketTimeoutException;

    abstract public void end() throws Exception;

    public ACCDescription getContextDescription() {
        return this.context;
    }

    abstract public ObjectInputStream getInputStream();

    abstract public ObjectOutputStream getOutputStream();

    public boolean isEnterRequest() {
        return this.request_type == TucsonProtocol.REQ_ENTERCONTEXT;
    }

    public boolean isEnterRequestAccepted() throws Exception {
        return this.request_allowed;
    }

    /* MODIFIED BY <s.mariani@unibo.it> */
    public boolean isTelnet() {
        return this.request_type == TucsonProtocol.REQ_TELNET;
    }

    public void receiveEnterRequest() throws Exception {
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

    public void receiveEnterRequestAnswer() throws Exception {
        this.request_allowed = this.receiveBoolean();
    }

    public void receiveFirstRequest() throws Exception {
        this.request_type = this.receiveInt();
    }

    /**
     * 
     * @param ctx
     * @throws Exception
     */
    public void sendEnterRequest(final ACCDescription ctx) throws Exception {

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

    public void sendEnterRequestAccepted()
            throws Exception {
        this.send(true);
        this.flush();
    }

    public void sendEnterRequestRefused()
            throws Exception {
        this.send(false);
        this.flush();
    }

    abstract protected void flush() throws Exception;

    abstract protected boolean receiveBoolean() throws Exception;

    abstract protected int receiveInt() throws Exception;

    abstract protected Object receiveObject() throws Exception;

    abstract protected String receiveString() throws Exception;

    abstract protected void send(boolean value) throws Exception;

    abstract protected void send(byte[] value) throws Exception;

    abstract protected void send(int value) throws Exception;

    abstract protected void send(Object value) throws Exception;

    abstract protected void send(String value) throws Exception;

}
