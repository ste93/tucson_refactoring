/**
 * Created by Stefano Mariani on 05/mag/2015 (mailto: s.mariani@unibo.it)
 */

/*
 * Copyright 1999-2014 Alma Mater Studiorum - Universita' di Bologna
 *
 * This file is part of TuCSoN4JADE <http://tucson4jade.apice.unibo.it>.
 *
 *    TuCSoN4JADE is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published
 *    by the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    TuCSoN4JADE is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with TuCSoN4JADE.  If not, see
 *    <https://www.gnu.org/licenses/lgpl.html>.
 *
 */

package alice.tucson.examples.rbac;

import java.util.logging.Logger;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.network.exceptions.DialogInitializationException;
import alice.tucson.service.TucsonNodeService;

/**
 * @author Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public final class RBACLauncher {

    private RBACLauncher() {
        /*
         * To prevent instantiation
         */
    }

    /**
     * @param args
     *            program arguments: args[0] is TuCSoN Node TCP port number.
     */
    public static void main(final String[] args) {
        int portno = 20504;
        if (args.length == 1) {
            portno = Integer.parseInt(args[0]);
        }
        TucsonNodeService tns = new TucsonNodeService(portno);
        tns.setLoginRequired(false);
        tns.setAdminUsername("admin");
        tns.setAdminPassword("psw");
        tns.setBasicAgentClass("basic");
        tns.setListAllRolesAllowed(true);
        tns.setInspectorsAuthorised(true);
        tns.install();
        try {
            while (!TucsonNodeService.isInstalled(portno, 1000)) {
                Thread.sleep(500);
            }
        } catch (DialogInitializationException | InterruptedException e) {
            e.printStackTrace();
        }
        Logger.getLogger("RBACLauncher").info(
                "TuCSoN Node installed on TCP port " + tns.getTCPPort());
        Logger.getLogger("RBACLauncher").info(
                "Launching administrator agent...");
        try {
            new AdminAgent("admin", "localhost", portno).go();
        } catch (TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
        Logger.getLogger("RBACLauncher").info("Administrator agent launched");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.getLogger("RBACLauncher").info(
                "Launching an authorised agent...");
        try {
            new AuthorisedAgent("authorised", "localhost", portno).go();
        } catch (TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
        Logger.getLogger("RBACLauncher").info("Authorised agent launched");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.getLogger("RBACLauncher").info(
                "Launching an unauthorised agent...");
        try {
            new UnauthorisedAgent("unauthorised", "localhost", portno).go();
        } catch (TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
        Logger.getLogger("RBACLauncher").info("Unauthorised agent launched");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
