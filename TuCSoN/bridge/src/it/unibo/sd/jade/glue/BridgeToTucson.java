package it.unibo.sd.jade.glue;

import it.unibo.sd.jade.agents.AsynchCompletionBehaviourHandler;
import it.unibo.sd.jade.agents.SynchCompletionBehaviourHandler;
import it.unibo.sd.jade.coordination.AsynchTucsonOpResult;
import it.unibo.sd.jade.coordination.TucsonOpResult;
import it.unibo.sd.jade.operations.AbstractTucsonAction;
import it.unibo.sd.jade.operations.bulk.OutAll;
import it.unibo.sd.jade.operations.ordinary.Out;
import it.unibo.sd.jade.operations.ordinary.Set;
import it.unibo.sd.jade.operations.ordinary.Spawn;
import it.unibo.sd.jade.operations.specification.OutS;
import it.unibo.sd.jade.operations.specification.SetS;
import it.unibo.sd.jade.service.TucsonService;
import it.unibo.sd.jade.service.TucsonSlice;
import jade.core.Agent;
import jade.core.GenericCommand;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonOpId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * BridgeToTucson. Client API for JADE agents willing to benefit of TuCSoN
 * coordination services.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class BridgeToTucson {
    private static void log(final String msg) {
        System.out.println("..[T4J Bridge]: " + msg);
    }

    /**
     * Translate TuCSoN completions into JADE completions.
     * 
     * @param op
     *            the TuCSoN operation to translate
     * @return JADE wrapper to TuCSoN completions
     */
    private static TucsonOpCompletionEvent toTucsonCompletionEvent(
            final ITucsonOperation op) {
        TucsonOpCompletionEvent ev;
        if (op.isInAll() || op.isRdAll() || op.isGet() || op.isGetS()
                || op.isNoAll()) {
            ev = new TucsonOpCompletionEvent(new TucsonOpId(op.getId()), true,
                    op.isOperationCompleted(), op.isResultSuccess(),
                    op.getLogicTupleListResult());
        } else {
            ev = new TucsonOpCompletionEvent(new TucsonOpId(op.getId()), true,
                    op.isOperationCompleted(), op.isResultSuccess(),
                    op.getLogicTupleResult());
        }
        return ev;
    }

    private final EnhancedACC acc;
    private final TucsonService service;
    private final Map<Behaviour, TucsonOpResult> tucsonOpResultsMap;

    /**
     * 
     * @param a
     *            the ACC the JADE bridge needs to interact with TuCSoN service
     * @param s
     *            the TuCSoN service this bridge is interacting with
     */
    public BridgeToTucson(final EnhancedACC a, final TucsonService s) {
        this.acc = a;
        this.service = s;
        this.tucsonOpResultsMap = new HashMap<Behaviour, TucsonOpResult>();
    }

    /**
     * "Polling mode", asynchronous invocation method. Callers themselves should
     * poll the shared data structure returned by this method to test wether a
     * result is available or not.
     * 
     * @param action
     *            the TuCSoN coordination operation to be carried out
     * @return the shared "Future-like" object to query for operation result
     * @throws ServiceException
     *             if the coordination service is not available
     */
    public AsynchTucsonOpResult asynchronousInvocation(
            final AbstractTucsonAction action) throws ServiceException {
        // no listeneer given since we are in "polling mode"
        final GenericCommand cmd = new GenericCommand(
                TucsonSlice.EXECUTE_ASYNCH, TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        cmd.addParam(null);
        BridgeToTucson
                .log("Asynchronous (polling mode) invocation of operation "
                        + action);
        final Object result = this.service.submit(cmd);
        if (result instanceof ITucsonOperation) {
            final ITucsonOperation op = (ITucsonOperation) result;
            // the shared Future-like object
            final AsynchTucsonOpResult asyncData = new AsynchTucsonOpResult(
                    op.getId(), this.acc.getCompletionEventsList(),
                    this.acc.getPendingOperationsMap());
            return asyncData;
        }
        return null;
    }

    /**
     * "Interrupt mode", asynchronous invocation method. Callers should provide
     * a ready-to-use JADE behaviour responsible for handling the asynchronous
     * operation result.
     * 
     * @param action
     *            the TuCSoN coordination operation to be carried out
     * @param behav
     *            the JADE behaviour handling the result
     * @param myAgent
     *            the JADE agent responsible for execution of the behaviour
     */
    public void asynchronousInvocation(final AbstractTucsonAction action,
            final Behaviour behav, final Agent myAgent) {
        // the listener is a behaviour, not known atm, thus it is not set now
        final GenericCommand cmd = new GenericCommand(
                TucsonSlice.EXECUTE_ASYNCH, TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        BridgeToTucson
                .log("Asynchronous (interrupt mode) invocation of operation "
                        + action);
        try {
            new AsynchCompletionBehaviourHandler("tucsonAgentAsync", cmd,
                    this.service, myAgent, behav).go();
        } catch (final TucsonInvalidAgentIdException e) {
            /*
             * cannot really happen
             */
        }
    }

    /**
     * To be called in case of synchronous invocations after result handling so
     * as to clean shared data structures.
     * 
     * @param b
     *            the behaviour calling the coordination operations, whose
     *            result map should be cleaned
     */
    public void clearTucsonOpResult(final Behaviour b) {
        this.tucsonOpResultsMap.remove(b);
    }

    /**
     * Synchronous invocation method. Caller behavioiur is suspended then
     * resumed as soon as the operation result becomes available.
     * 
     * @param action
     *            the TuCSoN coordination operation to be carried out
     * @param timeout
     *            the maximum waiting time for completion reception
     * @param behav
     *            caller behaviour
     * @return the TuCSoN operation completion event
     * @throws ServiceException
     *             if the coordination service is not available
     */
    public TucsonOpCompletionEvent synchronousInvocation(
            final AbstractTucsonAction action, final Long timeout,
            final Behaviour behav) throws ServiceException {
        TucsonOpResult ros;
        if (this.tucsonOpResultsMap.get(behav) == null) {
            ros = new TucsonOpResult();
            this.tucsonOpResultsMap.put(behav, ros);
        } else {
            ros = this.tucsonOpResultsMap.get(behav);
            if (!ros.isReady()) {
                // in this case, the JADE behaviour was resumed not by us, but
                // by JADE runtime due to message reception, thus nothing should
                // be done
                return null;
            }
        }
        final List<TucsonOpCompletionEvent> list = ros
                .getTucsonCompletionEvents();
        int nextRes = ros.getNextRes();
        if (list.size() > nextRes) {
            /*
             * operation already executed, return completion and increment
             * executions counter to next operation in queue
             */
            ros.setNextRes(++nextRes);
            return ros.getTucsonCompletionEvents().get(nextRes - 1);
        }
        /*
         * first time the operation is called, its result cannot be available
         * yet
         */
        if (action instanceof Out || action instanceof OutS
                || action instanceof OutAll || action instanceof Spawn
                || action instanceof Set || action instanceof SetS) {
            // no suspension needed
            final GenericCommand cmd = new GenericCommand(
                    TucsonSlice.EXECUTE_SYNCH, TucsonService.NAME, null);
            cmd.addParam(action);
            cmd.addParam(this.acc);
            cmd.addParam(timeout);
            Object result;
            BridgeToTucson.log("Synchronous invocation of operation " + action);
            result = this.service.submit(cmd);
            final ITucsonOperation op = (ITucsonOperation) result;
            final TucsonOpCompletionEvent res = BridgeToTucson
                    .toTucsonCompletionEvent(op);
            ros.getTucsonCompletionEvents().add(res);
            ros.setNextRes(ros.getNextRes() + 1);
            ros.setReady(true);
            return res;
        }
        // suspension needed
        final GenericCommand cmd = new GenericCommand(
                TucsonSlice.EXECUTE_ASYNCH, TucsonService.NAME, null);
        cmd.addParam(action);
        cmd.addParam(this.acc);
        BridgeToTucson.log("Synchronous invocation of operation " + action);
        SynchCompletionBehaviourHandler ta = null;
        try {
            ta = new SynchCompletionBehaviourHandler("tucsonAgentAsync", ros,
                    cmd, this.service, behav);
        } catch (final TucsonInvalidAgentIdException e) {
            /*
             * cannot really happen
             */
        }
        /*
         * the behaviour will be suspended, thus at resume its whole
         * @code{action} method executed from the beginning
         */
        ros.setNextRes(0);
        ros.setReady(false);
        ta.go();
        // by returning @code{null} the behaviour will be suspended
        return null;
    }
}
