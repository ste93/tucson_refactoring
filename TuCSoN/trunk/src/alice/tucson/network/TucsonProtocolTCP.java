/*
 * Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tucson.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.exceptions.DialogExceptionTcp;

/* TODO è necessario separare la classe usata server 
 * side e la classe usata client side anche in vista 
 * di una separazione delle librerie agent-node
 * */

/**
 * This is an implementation of Tucson Protocol based on TCP/IP and socket. For
 * grant the performance it use a buffered stream.
 */
public class TucsonProtocolTCP extends TucsonProtocol {

	private final boolean ENABLE_STACK_TRACE = false;

	private boolean serverSocketClosed = false;

	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private Socket _socket;
	private ServerSocket mainSocket;

	@Deprecated
	public TucsonProtocolTCP(ServerSocket socket) {
		this.mainSocket = socket;
	}

	@Deprecated
	public ObjectInputStream getInputStream() {
		return inStream;
	}

	@Deprecated
	public ObjectOutputStream getOutputStream() {
		return outStream;
	}

	/**
	 * This constructor create a new dialog whit a specific host that identified
	 * by host/port pair. This constructor is typically used from external agent
	 * who want start a new dialogue with the node.
	 * 
	 * It make a new socket and init I/O streams. The streams are bufferized.
	 * 
	 * @param node
	 *            the host name, or <code>null</code> for the loopback address.
	 * @param port
	 *            the port number.
	 * @throws UnreachableNodeException
	 *             - if the IP or port address of a host could not be determined
	 * @throws DialogExceptionTcp
	 *             - if you have a generic TCP error
	 */
	public TucsonProtocolTCP(String node, int port) throws DialogExceptionTcp, UnreachableNodeException {
		try {
			this._socket = new Socket(node, port);
		} catch (UnknownHostException e) {
			logError("The IP or port address of a host could not be determined.", e);
			throw new UnreachableNodeException();
		} catch (ConnectException e) {
			logError("Connection refused " + node + ":" + port, e);
			throw new DialogExceptionTcp();
		} catch (IOException e) {
			logError("generic IO Error", e);
			throw new DialogExceptionTcp();
		}

		/*
		 * To avoid deadlock: with Object I/O streams and Buffered I/O it's
		 * necessary to construct the output stream first and flush it before
		 * creating the input stream. This depend to how the streams are
		 * implemented.
		 */
		try {
			outStream = new ObjectOutputStream(new BufferedOutputStream(_socket.getOutputStream()));
			outStream.flush();
			inStream = new ObjectInputStream(new BufferedInputStream(_socket.getInputStream()));
		} catch (IOException e) {
			logError("An error occurs on I/O stream creation", e);
			throw new DialogExceptionTcp();
		}
	}

	/**
	 * This constructor is typically used node side: it builds a new access
	 * point to which an external agent can engage a new dialog. After the
	 * creation of this object usually is invoked the method acceptNewDialog()
	 * 
	 * It make a new ServerSocket binded at port specified by port parameter.
	 */
	public TucsonProtocolTCP(int port) throws DialogExceptionTcp {
		try {
			mainSocket = new ServerSocket();
			mainSocket.setReuseAddress(true);
			mainSocket.bind(new InetSocketAddress(port));
		} catch (IllegalArgumentException e) {
			logError("The port address specified is not valid: " + port, e);
			throw new DialogExceptionTcp();
		} catch (BindException e) {
			logError("The port address specified is already in use", e);
			throw new DialogExceptionTcp();
		} catch (Exception e) {
			logError("An error occurs on socket creation", e);
			throw new DialogExceptionTcp();
		}

	}

	/**
	 * This constructor initialize the I/O streams associated to socket
	 * specified by parameter.
	 * 
	 * @param socket
	 *            a preconfigurated socket
	 */
	private TucsonProtocolTCP(Socket socket) throws DialogExceptionTcp {
		this._socket = socket;

		/*
		 * To avoid deadlock: with Object I/O streams and Buffered I/O it's
		 * necessary to construct the output stream first and flush it before
		 * creating the input stream. This depend to how the streams are
		 * implemented.
		 */
		try {
			outStream = new ObjectOutputStream(new BufferedOutputStream(_socket.getOutputStream()));
			outStream.flush();
			inStream = new ObjectInputStream(new BufferedInputStream(_socket.getInputStream()));
		} catch (IOException e) {
			logError("An error occurs on I/O stream creation", e);
			throw new DialogExceptionTcp();
		}
	}

	/**
	 * Listens for a new dialog request and accepts it. The method blocks until
	 * a new dialog is made.
	 * 
	 * 
	 * @return a new TucsonProtocol
	 * @exception DialogExceptionTcp
	 *                -if a generic error occurs
	 */
	public TucsonProtocol acceptNewDialog() throws DialogExceptionTcp {

		try {
			return new TucsonProtocolTCP(mainSocket.accept());
		} catch (IOException e) {
			if (serverSocketClosed)
				log("SocketClosed...");
			else
				logError("Generic IO error", e);
			throw new DialogExceptionTcp();
		}

	}

	/**
	 * Close the dialog.
	 * 
	 * @exception DialogExceptionTcp
	 *                -if a generic error occurs
	 */
	public void end() throws DialogExceptionTcp {
		try {
			if (_socket != null)
				_socket.close();
			if (mainSocket != null)
				mainSocket.close();
			serverSocketClosed = true;
		} catch (Exception e) {
			logError("Generic error on closing socket", e);
			throw new DialogExceptionTcp();
		}
	}

	@Override
	public void sendMsg(TucsonMsg msg) throws DialogExceptionTcp {
		try {
			outStream.writeObject(msg);
			outStream.flush();
		} catch (IOException e) {
			logError("Generic IO error on sending a TucsonMessage");
			logError("Message content: " + msg, e);
			throw new DialogExceptionTcp();
		}
	}

	@Override
	public TucsonMsg receiveMsg() throws DialogExceptionTcp {
		TucsonMsg msg;
		try {
			msg = (TucsonMsg) inStream.readObject();
		} catch (Exception e) {
			logError("Generic IO error on receiving a TucsonMessage", e);
			throw new DialogExceptionTcp();
		}
		return msg;
	}

	@Override
	public void sendMsgRequest(TucsonMsgRequest request) throws DialogExceptionTcp {

		try {
			outStream.writeObject(request);
			outStream.flush();
		} catch (IOException e) {
			logError("Generic IO error on sending a TucsonMsgRequest");
			logError("Message content: " + request, e);
			throw new DialogExceptionTcp();
		}
	}

	@Override
	public TucsonMsgRequest receiveMsgRequest() throws DialogExceptionTcp {
		TucsonMsgRequest msg = new TucsonMsgRequest();
		try {
			msg = (TucsonMsgRequest) inStream.readObject();
		} catch (Exception e) {
			logError("Generic IO error on receiving a TucsonMsgRequest", e);
			throw new DialogExceptionTcp();
		}
		return msg;
	}

	@Override
	public void sendMsgReply(TucsonMsgReply reply) throws DialogExceptionTcp {
		try {
			outStream.writeObject(reply);
			outStream.flush();
		} catch (IOException e) {
			logError("Generic IO error on sending a TucsonMsgReply");
			logError("Message content: " + reply, e);
			throw new DialogExceptionTcp();
		}

	}

	@Override
	public TucsonMsgReply receiveMsgReply() throws DialogExceptionTcp {

		TucsonMsgReply msg = new TucsonMsgReply();
		try {
			msg = (TucsonMsgReply) inStream.readObject();
		} catch (Exception e) {
			logError("Generic IO error on receiving a TucsonMsgReply", e);
			throw new DialogExceptionTcp();
		}
		return msg;
	}

	protected String receiveString() throws Exception {
		return inStream.readUTF();
	}

	protected int receiveInt() throws Exception {
		return inStream.readInt();
	}

	protected boolean receiveBoolean() throws Exception {
		return inStream.readBoolean();
	}

	protected Object receiveObject() throws Exception {
		return inStream.readObject();
	}

	protected void send(int value) throws Exception {
		outStream.writeInt(value);
	}

	protected void send(boolean value) throws Exception {
		outStream.writeBoolean(value);
	}

	protected void send(String value) throws Exception {
		outStream.writeUTF(value);
	}

	protected void send(Object value) throws Exception {
		outStream.writeObject(value);
	}

	protected void flush() throws Exception {
		outStream.flush();
	}

	protected void send(byte[] value) throws Exception {
		outStream.write(value);
	}

	/**
	 * Check the TCP port value return false if the parameter is outside the
	 * specified range of valid port values, which is between 0 and 64000,
	 * inclusive. See {@link InetSocketAddress} for detail.
	 */
	protected static boolean checkPortValue(int portNumber) {
		// TODO why 64000 and not 65535?
		if (portNumber < 0 || portNumber > 64000)
			return false;
		else
			return true;
	}

	private void log(String error) {
		System.out.println("[TucsonProtocolTCP]: " + error);
	}

	private void logError(String error) {
		System.err.println("[TucsonProtocolTCP]: " + error);
	}

	private void logError(String error, Exception e) {
		System.err.println("[TucsonProtocolTCP]:\t" + error);
		System.err.println("\t\t\tException message is: " + e.getMessage() + "\n");
		if (ENABLE_STACK_TRACE)
			e.printStackTrace();
	}

}
