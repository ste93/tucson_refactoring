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
package alice.tucson.api;

import java.util.Map;
import java.util.UUID;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.service.TucsonOperation;

/**
 * Root ACC, no Linda nor TuCSoN operations available, only ACC release back to
 * TuCSoN node is possible.
 *
 * @author ste (mailto: s.mariani@unibo.it)
 * @author (contributor) Emanuele Buccelli
 */
public interface RootACC {

    /**
     * Enters an Agent Coordination Context, that is, tries to acquire it,
     * setting up a communication/coordination channel with TuCSoN services.
     *
     * @throws UnreachableNodeException
     *             if the TuCSoN node target of this operation is not
     *             network-reachable
     * @throws TucsonOperationNotPossibleException
     *             if the requested TuCSoN operation cannot be performed
     * @throws TucsonInvalidTupleCentreIdException
     *             if the target tuple centre ID is not a valid TuCSoN tuple
     *             centre ID
     */
    void enterACC()
            throws UnreachableNodeException, // galassi
            TucsonOperationNotPossibleException,
            TucsonInvalidTupleCentreIdException;

    /**
     * Releases the ACC, exiting from the TuCSoN system. Notice: if the same
     * agent releases and then re-acquires "the same" ACC, it is anyway a brand
     * new agent from TuCSoN perspective.
     *
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    void exit() throws TucsonOperationNotPossibleException;

    /**
     * Gets the (encrypted) RBAC password (if existing).
     *
     * @return the (encrypted) password (as a String)
     */
    String getPassword();

    /**
     * Gets the set of pending operations, that is, thos TuCSoN operations
     * invoked asynchronously for which no reply has been received yet.
     *
     * @return the Map associating operation ids with the actual TuCSoN
     *         operation
     */
    Map<Long, TucsonOperation> getPendingOperationsMap();

    /**
     * Gets the RBAC username (if existing).
     *
     * @return the username (as a String)
     */
    String getUsername();

    /**
     * Gets the assigned UUID.
     *
     * @return the assigned UUID
     */
    UUID getUUID();

    /**
     * Checks whether an ACC has been succesfully acquired.
     *
     * @return {@code true} or {@code false} depending on whether an ACC has
     *         been succesfully acquired
     */
    boolean isACCEntered();

}
