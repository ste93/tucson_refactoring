package alice.respect.situated;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.core.InternalEvent;
import alice.respect.core.RespectOperation;
import alice.tucson.api.TucsonOperationCompletionListener;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.network.AbstractTucsonProtocol;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.exceptions.DialogException;
import alice.tucson.service.OperationHandler;
import alice.tucson.service.TucsonOperation;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.TupleTemplate;

/**
 * 
 * This class implements some of common behaviors of transducers and defines
 * some methods to offer the essential interface to the users. To make a
 * specific transducer you'll need to extend this one and defining the behavior
 * needed for the application.
 * 
 * @author Steven Maraldi
 * 
 */
public abstract class AbstractTransducer implements
        TransducerStandardInterface, TucsonOperationCompletionListener {

    /** Class used to perform requested operation to the tuple centre **/
    protected OperationHandler executor;
    /** Transducer's identifier **/
    protected TransducerId id;

    /** List of probes associated to the transducer **/
    protected Map<AbstractProbeId, Object> probes;

    /** Identifier of the tuple centre associated **/
    protected TupleCentreId tcId;

    /**
     * Constructs a transducer
     * 
     * @param i
     *            the transducer's identifier
     * @param tc
     *            the associated tuple centre's identifier
     */
    public AbstractTransducer(final TransducerId i, final TupleCentreId tc) {
        this.id = i;
        this.tcId = tc;

        this.executor = new OperationHandler();

        this.probes = new HashMap<AbstractProbeId, Object>();
    }

    /**
     * Adds a new probe. If the probe's name is already recorded, the probe will
     * not be registered.
     * 
     * @param i
     *            probe's identifier
     * @param probe
     *            the probe itself
     */
    public void addProbe(final AbstractProbeId i, final Object probe) {
        if (!this.probes.containsKey(i)) {
            this.probes.put(i, probe);
        }
    }

    /**
     * Exit procedure, called to end a session of communication
     * 
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be found
     */
    public synchronized void exit() throws TucsonOperationNotPossibleException {

        final Iterator<OperationHandler.ControllerSession> it =
                this.executor.getControllerSessions().values().iterator();
        OperationHandler.ControllerSession cs;
        AbstractTucsonProtocol info;
        OperationHandler.Controller contr;
        TucsonOperation op;
        TucsonMsgRequest exit;

        while (it.hasNext()) {

            cs = it.next();
            info = cs.getSession();
            contr = cs.getController();
            contr.setStop();

            op =
                    new TucsonOperation(TucsonOperation.exitCode(),
                            (TupleTemplate) null, null, this.executor);
            this.executor.addOperation(op.getId(), op);

            exit =
                    new TucsonMsgRequest((int) op.getId(), op.getType(), null,
                            op.getLogicTupleArgument());
            try {
                info.sendMsgRequest(exit);
            } catch (final DialogException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The behavior of the transducer when a getEnv operation is required
     * 
     * @param key
     *            the environmental property key whose associated value should
     *            be percevied
     * @return true if the operation has been successfully executed
     */
    public abstract boolean getEnv(String key);

    /**
     * Returns the identifier of the transducer.
     * 
     * @return the transducer's identifier
     */
    public TransducerId getIdentifier() {
        return this.id;
    }

    /**
     * Returns the list of all the probes associated to the transducer
     * 
     * @return array of the probes associated to the transducer
     */
    public AbstractProbeId[] getProbes() {
        final Object[] keySet = this.probes.keySet().toArray();
        final AbstractProbeId[] probeList = new AbstractProbeId[keySet.length];
        for (int i = 0; i < probeList.length; i++) {
            probeList[i] = (AbstractProbeId) keySet[i];
        }
        return probeList;
    }

    /**
     * Returns the tuple centre associated to the transducer
     * 
     * @return the tuple centre identifier.
     */
    public TupleCentreId getTCId() {
        return this.tcId;
    }

    /**
     * 
     * Notifies an event from a probe to the tuple centre.
     * 
     * @param key
     *            the name of the value
     * @param value
     *            the value to communicate.
     * @throws UnreachableNodeException
     *             if the target TuCSoN node cannot be reached over the network
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be successfully carried out
     */
    public void notifyEnvEvent(final String key, final int value)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException {
        final LogicTuple tupla =
                new LogicTuple("getEnv", new Value(key), new Value(value));
        this.executor.doNonBlockingOperation(this.id,
                RespectOperation.OPTYPE_GET_ENV, this.tcId, tupla, this);
    }

    /**
     * Notifies an event from the tuple centre.
     * 
     * Events to the transducer should be only getEnv or setEnv ones. The
     * response to each event is specified in getEnv and setEnv methods of the
     * transducer.
     * 
     * @param ev
     *            internal event from the tuple centre
     * @return true if the operation required is getEnv or setEnv and it's been
     *         successfully executed.
     */
    public boolean notifyOutput(final InternalEvent ev) {
        try {
            if (ev.getInternalOperation().isGetEnv()) {
                return this.getEnv(ev.getInternalOperation().getArgument()
                        .getArg(0).toString());
            } else if (ev.getInternalOperation().isSetEnv()) {
                final String key =
                        ev.getInternalOperation().getArgument().getArg(0)
                                .toString();
                final int value =
                        Integer.parseInt(ev.getInternalOperation()
                                .getArgument().getArg(1).toString());
                return this.setEnv(key, value);
            }
        } catch (final InvalidTupleOperationException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Removes a probe from the probe list associated to the transducer if exist
     * 
     * @param i
     *            probe's identifier
     */
    public void removeProbe(final AbstractProbeId i) {
        final Object[] keySet = this.probes.keySet().toArray();
        for (final Object element : keySet) {
            if (((AbstractProbeId) element).getLocalName().equals(
                    i.getLocalName())) {
                this.probes.remove(element);
                return;
            }
        }
    }

    /**
     * The behavior of the transducer when a setEnv operation is required
     * 
     * @param key
     *            name of the parameter to set
     * @param value
     *            value of the parameter to set
     * @return true if the operation has been successfully executed
     */
    public abstract boolean setEnv(String key, int value);

    /*
     * ==========================================================================
     * =============== INTERNAL UTILITY METHODS
     * ==================================
     * =======================================================
     */

    /**
     * Utility methods used to communicate an output message to the console.
     * 
     * @param msg
     *            message to print.
     */
    protected void speak(final String msg) {
        System.out.println("[TRANSDUCER - " + this.id.toString() + "] " + msg);
    }

    /**
     * 
     * @param msg
     *            the message to show on standard error
     */
    protected void speakErr(final String msg) {
        System.err.println("[TRANSDUCER - " + this.id.toString() + "][ERROR] "
                + msg);
    }
}
