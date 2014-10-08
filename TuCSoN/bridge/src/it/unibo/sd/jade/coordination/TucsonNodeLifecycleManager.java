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
package it.unibo.sd.jade.coordination;

import alice.tucson.service.TucsonNodeService;

/**
 * TucsonNodeLifecycleManager. Responsible for handling requests for starting
 * and stopping TuCSoN Nodes.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public final class TucsonNodeLifecycleManager {
    /*
     * The node service is in charge of listening to incoming operation
     * invocations on the associated port of the device dispatching them to the
     * target tuple centre returning the operations completion to the source
     * agent
     */
    private static TucsonNodeService tns;

    /**
     * 
     * @param port
     *            the IP port on which the TuCSoN Node should be listening for
     *            incoming requests
     */
    public static synchronized void startTucsonNode(final int port) {
        TucsonNodeLifecycleManager.tns = new TucsonNodeService(port);
        TucsonNodeLifecycleManager.tns.install();
    }

    /**
     * 
     * @param port
     *            the IP port which the TuCSoN service to be stopped is bound to
     */
    public static synchronized void stopTucsonNode(final int port) {
        TucsonNodeLifecycleManager.tns.shutdown();
    }

    private TucsonNodeLifecycleManager() {
        /*
         * Enforce non-instantiability
         */
    }
}
