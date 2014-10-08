package it.unibo.sd.jade.agents;

import it.unibo.sd.jade.coordination.IAsynchCompletionBehaviour;
import it.unibo.sd.jade.service.TucsonService;
import jade.core.Agent;
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
 * AsynchCompletionBehaviourHandler. TuCSoN agent in charge of carrying out
 * coordination operations in asynchronous mode. Completion of such operations
 * is handled by a user-supplied behaviour, scheduled for execution as soon as
 * TuCSoN reply is available.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class AsynchCompletionBehaviourHandler extends AbstractTucsonAgent {
    private static void err(final String msg) {
        System.err.println("..[AsynchHandler]: " + msg);
    }

    /** the completion handling behaviour to schedule */
    private final Behaviour behav;
    /** command wrapping the coordination operation to execute */
    private final GenericCommand cmd;
    /** JADE agent to whom the behaviour should be added */
    private final Agent myAgent;
    /** TuCSoN reply storing the coordination operation result */
    private ITucsonOperation result;
    /** The JADE service responsible for command execution */
    private final TucsonService service;

    /**
     * 
     * @param id
     *            the id of this TuCSoN agent
     * @param c
     *            the command to dispatch to the JADE middleware
     * @param s
     *            the JADE service to interact with
     * @param a
     *            the JADE agent this TuCSoN agent acts on behalf of
     * @param b
     *            the JADE Behaviour to handle result
     * @throws TucsonInvalidAgentIdException
     *             if the given String is not a valid representation of a TuCSoN
     *             agent id
     */
    public AsynchCompletionBehaviourHandler(final String id,
            final GenericCommand c, final TucsonService s, final Agent a,
            final Behaviour b) throws TucsonInvalidAgentIdException {
        super(id);
        this.cmd = c;
        this.service = s;
        this.myAgent = a;
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
        TucsonOpCompletionEvent ev = null;
        boolean found = false;
        synchronized (list) {
            for (int i = 0; i < list.size() && !found; i++) {
                if (list.get(i).getOpId().getId() == this.result.getId()) {
                    found = true;
                    // removing completed operation from completion list
                    ev = list.remove(i);
                }
            }
        }
        // cheking if result handling behaviour complies with its contract
        if (this.behav instanceof IAsynchCompletionBehaviour) {
            final IAsynchCompletionBehaviour b = (IAsynchCompletionBehaviour) this.behav;
            b.setTucsonOpCompletionEvent(ev);
            this.myAgent.addBehaviour(this.behav);
        } else {
            AsynchCompletionBehaviourHandler
                    .err("The given result-handling behaviour does not implement interface 'IAsynchCompletionBehaviour' :/");
        }
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
            e.printStackTrace();
        }
    }
}
