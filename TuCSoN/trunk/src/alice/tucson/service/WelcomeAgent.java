/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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
package alice.tucson.service;

import alice.tucson.network.TPConfig;
import alice.tucson.network.TPFactory;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.exceptions.DialogException;

/**
 * 
 */
public class WelcomeAgent extends Thread {

	private final boolean ENABLE_STACK_TRACE = false;

	ACCProvider contextManager;
	TucsonNodeService node;
	TucsonProtocol mainDialog;

	boolean shutdown;

	public WelcomeAgent(TucsonNodeService node, ACCProvider cm) {
		contextManager = cm;
		this.node = node;
		shutdown = false;
		start();
	}

	public void run() {

		try {
			mainDialog = TPFactory.getDialogNodeSide(TPFactory.DIALOG_TYPE_TCP);
		} catch (DialogException e) {
			// TODO BEHAVIOR: what is the correct behavior when a port is
			// already
			// used?
			logErr("");
			logErr("");
			logErr("An error occurred on creation of MainDialog", e);
			logErr("... WelcomAgent shutdown ... ");
			// On shutdown removing this Agent from NodeAgentList
			node.removeNodeAgent(this);
			return;
		}

		TucsonProtocol dialog = null;

		while (!isShutdown()) {

			log("Listening on port " + TPConfig.getInstance().getNodeTcpPort() + " for incoming ACC requests...");

			try {
				dialog = mainDialog.acceptNewDialog();
			} catch (DialogException e) {
				if (isShutdown()) {
					log("Shutdown request received, shutting down...");
					break;
				}

				// TODO BEHAVIOR: what is the correct behavior?
				logErr("");
				logErr("");
				logErr("An error occurred on new dialog aceptiong", e);
				logErr("... WelcomAgent shutdown ... ");
				break;
			}

			try {
				dialog.receiveFirstRequest();

				if (dialog.isEnterRequest()) {
					dialog.receiveEnterRequest();
					ACCDescription desc = dialog.getContextDescription();
					log("Delegating ACCProvider received enter request...");
					contextManager.processContextRequest(desc, dialog);
				} else if (dialog.isTelnet()) {
					// TODO  ????
					log("Welcome to the Tucson Service Node " + TucsonNodeService.getVersion());
				}
			} catch (DialogException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// On shutdown removing this Agent from NodeAgentList
		node.removeNodeAgent(this);

	}

	private synchronized boolean isShutdown() {
		return shutdown;
	}

	/** Method for request the shutdown of WelcomAgent */
	public synchronized void shutdown() {
		// is not the WelcomAgent thread to do this, but it is an acceptable
		// compromise to avoid adding an additional thread
		shutdown = true;
		try {
			if (mainDialog != null)
				mainDialog.end();
		} catch (DialogException e) {
			logErr("Error on closing mainDialog");
		}
	}

	private void log(String st) {
		System.out.println("[WelcomeAgent]: " + st);
	}

	private void logErr(String error) {
		System.err.println("[WelcomeAgent]: " + error);
	}

	private void logErr(String error, Exception e) {
		System.err.println("[WelcomeAgent]:\t" + error);
		System.err.println("\t\t\tException message is: " + e.getMessage() + "\n");
		if (ENABLE_STACK_TRACE)
			e.printStackTrace();
	}

}
