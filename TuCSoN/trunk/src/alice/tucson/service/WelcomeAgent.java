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

import alice.tucson.network.AbstractTucsonProtocol;
import alice.tucson.network.TPFactory;
import alice.tucson.network.exceptions.DialogException;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 17/lug/2013
 * 
 */
public class WelcomeAgent extends Thread {

    private static void log(final String st) {
        System.out.println("[WelcomeAgent]: " + st);
    }

    private final ACCProvider contextManager;
    private final TucsonNodeService node;
    private boolean shut;

    AbstractTucsonProtocol mainDialog;

    /**
     * 
     * @param p
     *            the listening port to be taken as default
     * @param n
     *            the TuCSoN node this internal agent refers to
     * @param cm
     *            the ACC provider this internal agent should delegate ACC
     *            requests to
     */
    public WelcomeAgent(final int p, final TucsonNodeService n,
            final ACCProvider cm) {
        super();
        this.contextManager = cm;
        this.node = n;
        this.shut = false;
        this.start();
    }

    /**
     * 
     */
    @Override
    public void run() {

        try {
            this.mainDialog =
                    TPFactory.getDialogNodeSide(TPFactory.DIALOG_TYPE_TCP);
        } catch (final DialogException e) {
            // TODO CICORA: what is the correct behavior when a port is already
            // used?
            e.printStackTrace();
            this.shut = true;
        }

        AbstractTucsonProtocol dialog = null;
        try {

            while (!this.isShutdown()) {

                try {
                    dialog = this.mainDialog.acceptNewDialog();
                } catch (final DialogException e) {
                    // TODO CICORA: what is the correct behavior?
                    if (this.isShutdown()) {
                        WelcomeAgent
                                .log("Shutdown request received, shutting down...");
                    } else {
                        e.printStackTrace();
                    }
                    this.shut = true;
                    break;
                }

                dialog.receiveFirstRequest();
                if (dialog.isEnterRequest()) {
                    dialog.receiveEnterRequest();
                    final ACCDescription desc = dialog.getContextDescription();
                    WelcomeAgent
                            .log("Delegating ACCProvider received enter request...");
                    this.contextManager.processContextRequest(desc, dialog);
                }

            }

        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.node.removeNodeAgent(this);

    }

    public synchronized void shutdown() {
        /*
         * TODO CICORA: it's not the WelcomAgent's thread to call this, but it's
         * an acceptable compromise to avoid adding an additional thread
         */
        this.shut = true;
        try {
            if (this.mainDialog != null) {
                this.mainDialog.end();
            }
        } catch (final DialogException e) {
            // TODO CICORA: what is the correct behavior?
            e.printStackTrace();
        }
    }

    private synchronized boolean isShutdown() {
        return this.shut;
    }

}
