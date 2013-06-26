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

import java.io.IOException;

import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tucson.network.exceptions.DialogExceptionTcp;
import alice.tucson.network.exceptions.DialogExceptionTimeout;

/**
 * 
 */
public class WelcomeAgent extends Thread {

	ACCProvider contextManager;
	TucsonNodeService node;
	int port;
	boolean shutdown;

	public WelcomeAgent(int port, TucsonNodeService node, ACCProvider cm) {
		contextManager = cm;
		this.port = port;
		this.node = node;
		shutdown = false;
		start();
	}

	private void log(String st) {
		System.out.println("[WelcomeAgent]: " + st);
	}

	private void logErr(String error) {
		System.err.println("[WelcomeAgent]: " + error);
	}

	/**
	 * 
	 */
	public void run() {

		TucsonProtocol mainDialog = null;
		try {
			mainDialog = new TucsonProtocolTCP(port);
		} catch (DialogExceptionTcp e) {
			// TODO BEHAVIOR: what is the correct behavior when a port is alredy
			// used?
			logErr("");
			logErr("");
			logErr("An error occurred on creation of MainDialog");
			logErr("... WelcomAgent shutdown ... ");
			return;
		}

		TucsonProtocol dialog = null;
		boolean exception = false;
		boolean timeout = false;
		try {
			while (true) {

				if (!timeout)
					log("Listening on port " + port + " for incoming ACC requests...");
				else
					timeout = false;

				try {
					dialog = mainDialog.acceptNewDialog();
				} catch (DialogExceptionTimeout e) {
					timeout = true;
					if (shutdown) {
						exception = true;
						log("Shutdown interrupt received, shutting down...");
						break;
					} else
						continue;
				}
				dialog.receiveFirstRequest();

				if (dialog.isEnterRequest()) {
					dialog.receiveEnterRequest();
					ACCDescription desc = dialog.getContextDescription();
					log("Delegating ACCProvider received enter request...");
					contextManager.processContextRequest(desc, dialog);
				} else if (dialog.isTelnet()) {
					// TO DO
					log("Welcome to the Tucson Service Node " + TucsonNodeService.getVersion());
				}

			}
		} catch (InterruptedException e) {
			exception = true;
			log("Shutdown interrupt received, shutting down...");
		} catch (IOException e) {
			exception = true;
			System.err.println("[WelcomeAgent]: " + e);
			e.printStackTrace();
		} catch (Exception e) {
			exception = true;
			System.err.println("[WelcomeAgent]: " + e);
			e.printStackTrace();
		}

		if (exception && !shutdown) {
			try {
				dialog.end();
			} catch (Exception e) {
				System.err.println("[WelcomeAgent]: " + e);
				e.printStackTrace();
			}
			node.removeNodeAgent(this);
		}

		// log("Actually shutting down...");

	}

	public void shutdown() {
		shutdown = true;
	}

}
