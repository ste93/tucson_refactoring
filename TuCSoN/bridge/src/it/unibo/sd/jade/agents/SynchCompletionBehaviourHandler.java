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
package it.unibo.sd.jade.agents;

import it.unibo.sd.jade.coordination.TucsonOpResult;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.GenericCommand;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import java.util.List;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedAsynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.service.TucsonOpCompletionEvent;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * SynchCompletionBehaviourHandler. TuCSoN agent in charge of carrying out
 * coordination operations in synchronous mode. Completion of such operations is
 * handled by resuming the caller behaviour, scheduled for execution as soon as
 * TuCSoN reply is available.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class SynchCompletionBehaviourHandler extends AbstractTucsonAgent {
    /** the caller behaviour to resume */
    private final Behaviour behav;
    /** command wrapping the coordination operation to execute */
    private final GenericCommand cmd;
    /** TuCSoN reply storing the coordination operation result */
    private ITucsonOperation result;
    /** shared (with caller behaviour and ACC) object storing operation result */
    private final TucsonOpResult ros;
    /** The JADE service responsible for command execution */
    private final TucsonService service;

    /**
     * 
     * @param id
     *            the id of this TuCSoN agent
     * @param r
     *            the data structure storing the operations result
     * @param c
     *            the command to dispatch to the JADE middleware
     * @param s
     *            the JADE service to interact with
     * @param b
     *            the caller JADE Behaviour to resume
     * @throws TucsonInvalidAgentIdException
     *             if the given String is not a valid representation of a TuCSoN
     *             agent id
     */
    public SynchCompletionBehaviourHandler(final String id,
            final TucsonOpResult r, final GenericCommand c,
            final TucsonService s, final Behaviour b)
            throws TucsonInvalidAgentIdException {
        super(id);
        this.ros = r;
        this.cmd = c;
        this.service = s;
        this.behav = b;
        this.result = null;
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tuplecentre
     * .core.AbstractTupleCentreOperation)
     */
    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        final EnhancedAsynchACC acc = (EnhancedAsynchACC) this.cmd.getParam(1);
        final List<TucsonOpCompletionEvent> list = acc
                .getCompletionEventsList();
        boolean found = false;
        synchronized (list) {
            for (int i = 0; i < list.size() && !found; i++) {
                if (list.get(i).getOpId().getId() == this.result.getId()) {
                    found = true;
                    // removing completed operation from completion list
                    final TucsonOpCompletionEvent ev = list.remove(i);
                    // storing result in shared object
                    this.ros.getTucsonCompletionEvents().add(ev);
                    // signal to JADE bahaviour that result is available
                    this.ros.setReady(true);
                }
            }
        }
        this.behav.restart();
    }

    @Override
    public void operationCompleted(final ITucsonOperation arg0) {
        /*
         * not used atm
         */
    }

    @Override
    protected void main() {
        // Adding myself as listeneer of TuCSoN operation completion
        this.cmd.addParam(this);
        try {
            // actual execution of coordination operation
            this.result = (ITucsonOperation) this.service.submit(this.cmd);
        } catch (final ServiceException e) {
            System.err.println(e);
        }
    }
}
