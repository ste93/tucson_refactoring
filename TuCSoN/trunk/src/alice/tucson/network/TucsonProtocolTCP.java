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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 03/lug/2013
 * 
 */
public class TucsonProtocolTCP extends AbstractTucsonProtocol {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    private static final int SOCKET_TIMEOUT = 5000;
    private ObjectInputStream inStream;
    private ServerSocket mainSocket;
    private ObjectOutputStream outStream;
    private Socket socket;

    /**
     * 
     * @param s
     *            the socket whose this connection is bound
     */
    public TucsonProtocolTCP(final ServerSocket s) {
        super();
        this.mainSocket = s;
    }

    /**
     * 
     * @param host
     *            the host where to bound
     * @param port
     *            the listening port where to bound
     * @throws UnknownHostException
     *             if the given host is unknown
     * @throws IOException
     *             if some network problems arise
     */
    public TucsonProtocolTCP(final String host, final int port)
            throws UnknownHostException, IOException {
        super();
        this.socket = new Socket(host, port);
        this.outStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.inStream = new ObjectInputStream(this.socket.getInputStream());
    }

    private TucsonProtocolTCP(final Socket s) throws IOException {
        super();
        this.socket = s;
        this.outStream = new ObjectOutputStream(s.getOutputStream());
        this.inStream = new ObjectInputStream(s.getInputStream());
    }

    @Override
    public AbstractTucsonProtocol acceptNewDialog() throws IOException,
            SocketTimeoutException {
        this.mainSocket.setSoTimeout(TucsonProtocolTCP.SOCKET_TIMEOUT);
        return new TucsonProtocolTCP(this.mainSocket.accept());
    }

    @Override
    public void end() throws IOException {
        this.socket.close();
    }

    @Override
    public ObjectInputStream getInputStream() {
        return this.inStream;
    }

    @Override
    public ObjectOutputStream getOutputStream() {
        return this.outStream;
    }

    @Override
    protected void flush() throws IOException {
        this.outStream.flush();
    }

    @Override
    protected boolean receiveBoolean() throws IOException {
        return this.inStream.readBoolean();
    }

    @Override
    protected int receiveInt() throws IOException {
        return this.inStream.readInt();
    }

    @Override
    protected Object receiveObject() throws ClassNotFoundException, IOException {
        return this.inStream.readObject();
    }

    @Override
    protected String receiveString() throws ClassNotFoundException, IOException {
        return (String) this.inStream.readObject();
    }

    @Override
    protected void send(final boolean value) throws IOException {
        this.outStream.writeBoolean(value);
    }

    @Override
    protected void send(final byte[] value) throws IOException {
        this.outStream.write(value);
    }

    @Override
    protected void send(final int value) throws IOException {
        this.outStream.writeInt(value);
    }

    @Override
    protected void send(final Object value) throws IOException {
        this.outStream.writeObject(value);
    }

    @Override
    protected void send(final String value) throws IOException {
        this.outStream.writeObject(value);
    }

}
