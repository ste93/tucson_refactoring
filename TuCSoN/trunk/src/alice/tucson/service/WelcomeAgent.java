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
package alice.tucson.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;

/**
 * 
 */
public class WelcomeAgent extends Thread {

    private static void log(final String st) {
        System.out.println("[WelcomeAgent]: " + st);
    }

    ACCProvider contextManager;
    TucsonNodeService node;
    int port;

    boolean shutdown;

    public WelcomeAgent(final int p, final TucsonNodeService n,
            final ACCProvider cm) {
        this.contextManager = cm;
        this.port = p;
        this.node = n;
        this.shutdown = false;
        this.start();
    }

    /**
	 * 
	 */
    @Override
    public void run() {

        TucsonProtocol mainDialog = null;
        try {
            final ServerSocket mainSocket = new ServerSocket();
            mainSocket.setReuseAddress(true);
            mainSocket.bind(new InetSocketAddress(this.port));
            mainDialog = new TucsonProtocolTCP(mainSocket);
        } catch (final SocketException e2) {
            System.err.println("[WelcomeAgent]: " + e2);
            e2.printStackTrace();
        } catch (final IOException e1) {
            System.err.println("[WelcomeAgent]: " + e1);
            e1.printStackTrace();
        }

        TucsonProtocol dialog = null;
        boolean exception = false;
        boolean timeout = false;
        try {
            while (true) {

                if (!timeout) {
                    WelcomeAgent.log("Listening on port " + this.port
                            + " for incoming ACC requests...");
                } else {
                    timeout = false;
                }

                try {
                    dialog = mainDialog.acceptNewDialog();
                } catch (final SocketTimeoutException e) {
                    timeout = true;
                    if (this.shutdown) {
                        exception = true;
                        WelcomeAgent
                                .log("Shutdown interrupt received, shutting down...");
                        break;
                    }
                    continue;
                }
                dialog.receiveFirstRequest();

                if (dialog.isEnterRequest()) {
                    dialog.receiveEnterRequest();
                    final ACCDescription desc = dialog.getContextDescription();
                    WelcomeAgent
                            .log("Delegating ACCProvider received enter request...");
                    this.contextManager.processContextRequest(desc, dialog);
                } else if (dialog.isTelnet()) {
                    // TODO How to implement telnet test?
                    WelcomeAgent.log("Welcome to the Tucson Service Node "
                            + TucsonNodeService.getVersion());
                }

            }
        } catch (final InterruptedException e) {
            exception = true;
            WelcomeAgent.log("Shutdown interrupt received, shutting down...");
        } catch (final IOException e) {
            exception = true;
            System.err.println("[WelcomeAgent]: " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            exception = true;
            System.err.println("[WelcomeAgent]: " + e);
            e.printStackTrace();
        }

        if (exception && !this.shutdown) {
            try {
                dialog.end();
            } catch (final Exception e) {
                System.err.println("[WelcomeAgent]: " + e);
                e.printStackTrace();
            }
            this.node.removeNodeAgent(this);
        }

    }

    public void shutdown() {
        this.shutdown = true;
    }

}
