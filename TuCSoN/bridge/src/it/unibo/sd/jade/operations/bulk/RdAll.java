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
package it.unibo.sd.jade.operations.bulk;

import it.unibo.sd.jade.operations.AbstractTucsonOrdinaryAction;
import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * <code>rd_all</code> TuCSoN primitive.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class RdAll extends AbstractTucsonOrdinaryAction {
    /**
     * 
     * @param tc
     *            the TuCSoN tuple centre id target of the coordination
     *            operation
     * @param t
     *            the logic tuple argument of the coordination operation
     */
    public RdAll(final TucsonTupleCentreId tc, final LogicTuple t) {
        super(tc, t);
    }

    @Override
    public ITucsonOperation executeAsynch(final EnhancedAsynchACC acc,
            final TucsonOperationCompletionListener listener)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        return acc.rdAll(this.tcid, this.tuple, listener);
    }

    @Override
    public ITucsonOperation executeSynch(final EnhancedSynchACC acc,
            final Long timeout) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        return acc.rdAll(this.tcid, this.tuple, timeout);
    }

    /*
     * (non-Javadoc)
     * @see it.unibo.sd.jade.operations.AbstractTucsonOrdinaryAction#toString()
     */
    @Override
    public String toString() {
        return "rd_all" + super.toString();
    }
}
