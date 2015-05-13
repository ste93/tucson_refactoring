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
import alice.respect.api.exceptions.OperationNotAllowedException;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.AdminACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.AuthorisedAgent;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonAuthorisedAgent;
import alice.tucson.rbac.TucsonPermission;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRBACStructure;
import alice.tucson.rbac.TucsonRole;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * @author Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public final class AdminAgent extends AbstractTucsonAgent {

    /**
     * @param id
     *            the ID of this TuCSoN agent
     * @param netid
     *            the IP address of the TuCSoN node it is willing to interact
     *            with
     * @param p
     *            the TCP port number of the TuCSoN node it is willing to
     *            interact with
     * @throws TucsonInvalidAgentIdException
     *             if the given String does not represent a valid TuCSoN agent
     *             ID
     */
    public AdminAgent(final String id, final String netid, final int p)
            throws TucsonInvalidAgentIdException {
        super(id, netid, p);
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tuplecentre
     * .core.AbstractTupleCentreOperation)
     */
    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        /*
         * Not used atm
         */
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tucson.
     * api.ITucsonOperation)
     */
    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * Not used atm
         */
    }

    /*
     * (non-Javadoc)
     * @see alice.tucson.api.AbstractTucsonAgent#main()
     */
    @Override
    protected void main() {
        Permission prd = new TucsonPermission("rd");
        Permission prdp = new TucsonPermission("rdp");
        Permission pin = new TucsonPermission("in");
        Permission pinp = new TucsonPermission("inp");
        Permission pout = new TucsonPermission("out");
        Policy policyrd = new TucsonPolicy("policyrd");
        Policy policyrdrdp = new TucsonPolicy("policyrdrdp");
        Policy policyin = new TucsonPolicy("policyin");
        Policy policyout = new TucsonPolicy("policyout");
        policyrd.addPermission(prd);
        policyrdrdp.addPermission(prd);
        policyrdrdp.addPermission(prdp);
        policyin.addPermission(pin);
        policyin.addPermission(pinp);
        policyout.addPermission(pout);
        Role roleRead = new TucsonRole("roleRead");
        roleRead.setPolicy(policyrd);
        Role roleReadP = new TucsonRole("roleReadP");
        roleReadP.setPolicy(policyrdrdp);
        Role roleReadIn = new TucsonRole("roleReadIn");
        roleReadIn.setAgentClass("readClass");
        roleReadIn.setPolicy(policyin);
        Role roleWrite = new TucsonRole("roleWrite");
        roleWrite.setAgentClass("writeClass");
        roleWrite.setPolicy(policyout);
        AuthorisedAgent agent1 = new TucsonAuthorisedAgent("readClass",
                "user12", "psw1");
        AuthorisedAgent agent2 = new TucsonAuthorisedAgent("readClass",
                "user12", "psw2");
        AuthorisedAgent agent3 = new TucsonAuthorisedAgent("writeClass",
                "user3", "psw3");
        TucsonRBACStructure rbac = new TucsonRBACStructure("acme-org");
        rbac.addRole(roleRead);
        rbac.addRole(roleReadP);
        rbac.addRole(roleReadIn);
        rbac.addRole(roleWrite);
        rbac.addPolicy(policyrd);
        rbac.addPolicy(policyrdrdp);
        rbac.addPolicy(policyin);
        rbac.addPolicy(policyout);
        rbac.addAuthorisedAgent(agent1);
        rbac.addAuthorisedAgent(agent2);
        rbac.addAuthorisedAgent(agent3);
        rbac.allowInspection(true);
        rbac.setBasicAgentClass("newBasicClass");
        rbac.requireLogin(false);
        Logger.getLogger("AdminAgent").info(
                "Acquiring AdminACC from TuCSoN Node installed on TCP port "
                        + this.myport());
        AdminACC adminACC = TucsonMetaACC.getAdminContext(
                this.getTucsonAgentId(), "localhost", 20504, "admin", "psw");
        Logger.getLogger("AdminAgent").info("AdminACC acquired");
        try {
            Logger.getLogger("AdminAgent")
                    .info("Installing RBAC configuration");
            adminACC.install(rbac);
            Logger.getLogger("AdminAgent").info("RBAC configuration installed");
            Logger.getLogger("AdminAgent").info("Removing policy 'policyrd'");
            adminACC.removePolicy("policyrd");
            Logger.getLogger("AdminAgent").info("Policy 'policyrd' removed");
            Logger.getLogger("AdminAgent").info("Removing role 'roleRd'");
            adminACC.removeRole("roleRead");
            Logger.getLogger("AdminAgent").info("Role 'roleRd' removed");
            Logger.getLogger("AdminAgent").info(
                    "Changing basic agent class to 'yetAnotherBasicClass'");
            adminACC.setBasicAgentClass("yetAnotherBasicClass");
            Logger.getLogger("AdminAgent").info(
                    "Basic agent class changed to 'yetAnotherBasicClass'");
            Logger.getLogger("AdminAgent").info("Releasing AdminACC");
            adminACC.exit();
            Logger.getLogger("AdminAgent").info("AdminACC released, bye!");
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException | OperationNotAllowedException e) {
            e.printStackTrace();
        }

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
        try {
            new AdminAgent("admin", "localhost", portno).go();
        } catch (TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
    }

}
