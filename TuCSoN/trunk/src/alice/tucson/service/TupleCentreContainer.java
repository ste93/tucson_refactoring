package alice.tucson.service;

import java.util.HashMap;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.IEnvironmentContext;
import alice.respect.api.IManagementContext;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.IOrdinarySynchInterface;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.ISpecificationSynchInterface;
import alice.respect.api.RespectSpecification;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.respect.core.InternalEvent;
import alice.respect.core.InternalOperation;
import alice.respect.core.RespectOperation;
import alice.respect.core.RespectTC;
import alice.respect.core.RespectTCContainer;
import alice.respect.core.SpecificationSynchInterface;
import alice.respect.core.TransducersManager;
import alice.respect.situatedness.TransducerId;
import alice.respect.situatedness.TransducerStandardInterface;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidSpecificationException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.api.ObservableEventListener;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.InputEvent;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * 
 */
public final class TupleCentreContainer {

    private static int defaultport;

    /**
     * 
     * @param id
     *            the identifier of the tuple centre this wrapper refers to
     * @param q
     *            the size of the input queue
     * @param defPort
     *            the default listening port
     * @return the Object representing the ReSpecT tuple centre
     * @throws InvalidTupleCentreIdException
     *             if the given tuple centre identifier is not a valid TuCSoN
     *             tuple centre identifier
     */
    public static RespectTC createTC(final TucsonTupleCentreId id, final int q,
            final int defPort) throws InvalidTupleCentreIdException {
        TupleCentreContainer.defaultport = defPort;
        try {
            final RespectTCContainer rtcc = RespectTCContainer
                    .getRespectTCContainer();
            RespectTCContainer.setDefPort(TupleCentreContainer.defaultport);
            final TupleCentreId tid = new TupleCentreId(id.getName(),
                    id.getNode(), String.valueOf(id.getPort()));
            System.err
                    .println(" @@@@@@@@@@ TupleCentreContainer.defaultport = "
                            + TupleCentreContainer.defaultport);
            System.err.println(" @@@@@@@@@@ RespectTCContainer.getDefPort() = "
                    + RespectTCContainer.getDefPort());
            System.err.println(" @@@@@@@@@@ id.getName() = " + id.getName());
            System.err.println(" @@@@@@@@@@ id.getNode() = " + id.getNode());
            System.err.println(" @@@@@@@@@@ id.getPort() = " + id.getPort());
            System.err.println(" @@@@@@@@@@ tid.getName() = " + tid.getName());
            System.err.println(" @@@@@@@@@@ tid.getNode() = " + tid.getNode());
            System.err.println(" @@@@@@@@@@ tid.getPort() = " + tid.getPort());
            return rtcc.createRespectTC(tid, q);
        } catch (final InvalidTupleCentreIdException e) {
            throw new InvalidTupleCentreIdException();
        }
    }

    /**
     * 
     */
    public static synchronized void destroyTC() {
        /*
         * 
         */
    }

    /**
     * @param ttcid
     *            the id of the tuple centre to make persistent
     * @param persistencyPath
     *            the path where to store persistency information
     * 
     */
    public static synchronized void disablePersistency(
            final TucsonTupleCentreId ttcid, final String persistencyPath) {
        IManagementContext context = null;
        context = RespectTCContainer.getRespectTCContainer()
                .getManagementContext(ttcid.getInternalTupleCentreId());
        context.disablePersistency(persistencyPath, ttcid);
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param aid
     *            the identifier of the agent requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param o
     *            the tuple or tuples list argument of the operation
     * @return the tuple or tuples list result of the operation
     * @throws TucsonInvalidLogicTupleException
     *             if the given argument is not a valid tuple or tuples list
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     */
    public static Object doBlockingOperation(final int type,
            final TucsonAgentId aid, final TucsonTupleCentreId tid,
            final Object o) throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        IOrdinarySynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getOrdinarySynchInterface(tid.getInternalTupleCentreId());
            if (type == TucsonOperation.getCode()) {
                return context.get(aid.getAgentId());
            }
            if (type == TucsonOperation.setCode()) {
                return context.set(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.outCode()) {
                context.out(aid.getAgentId(), (LogicTuple) o);
                return o;
            }
            if (type == TucsonOperation.inCode()) {
                return context.in(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.outAllCode()) {
                return context.outAll(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.inAllCode()) {
                return context.inAll(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.rdAllCode()) {
                return context.rdAll(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.noAllCode()) {
                return context.noAll(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.uin(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uin(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uin(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.uin(aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.uin(aid.getAgentId(), (LogicTuple) o);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return null;
    }

    /**
     * SPAWN ADDITION
     * 
     * @param type
     *            the type code of the operation requested
     * @param aid
     *            the identifier of the tuple centre requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple or argument of the operation
     * @return the tuple or tuples list result of the operation
     * @throws TucsonInvalidLogicTupleException
     *             if the given argument is not a valid tuple or tuples list
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     */
    public static Object doBlockingOperation(final int type,
            final TucsonTupleCentreId aid, final TucsonTupleCentreId tid,
            final LogicTuple t) throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        IOrdinarySynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getOrdinarySynchInterface(tid.getInternalTupleCentreId());
            if (type == TucsonOperation.outCode()) {
                context.out(aid.getInternalTupleCentreId(), t);
                return t;
            }
            if (type == TucsonOperation.inCode()) {
                return context.in(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.outAllCode()) {
                return context.outAll(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.inAllCode()) {
                return context.inAll(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.rdAllCode()) {
                return context.rdAll(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.noAllCode()) {
                return context.noAll(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.uin(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uin(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uin(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.uin(aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.uin(aid.getInternalTupleCentreId(), t);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return null;
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param aid
     *            the identifier of the agent requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @return the tuple or tuples list result of the operation
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     * @throws TucsonInvalidSpecificationException
     *             if the given tuple is not a valid representation of a ReSpecT
     *             specification
     */
    public static Object doBlockingSpecOperation(final int type,
            final TucsonAgentId aid, final TucsonTupleCentreId tid,
            final LogicTuple t) throws TucsonOperationNotPossibleException,
            TucsonInvalidSpecificationException {
        final LogicTuple res = null;
        ISpecificationSynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getSpecificationSynchInterface(
                            tid.getInternalTupleCentreId());
            if (type == TucsonOperation.setSCode()) {
                if ("spec".equals(t.getName())) {
                    return ((SpecificationSynchInterface) context).setS(aid
                            .getAgentId(), new RespectSpecification(t.getArg(0)
                            .getName()));
                }
                return context.setS(aid.getAgentId(), t);
            }
            if (type == TucsonOperation.getSCode()) {
                return context.getS(aid.getAgentId());
            }
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            throw new TucsonInvalidSpecificationException();
        } catch (final InvalidOperationException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param tcid
     *            the identifier of the tuple centre requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @return the tuple or tuples list result of the operation
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     * @throws TucsonInvalidSpecificationException
     *             if the given tuple is not a valid representation of a ReSpecT
     *             specification
     */
    public static Object doBlockingSpecOperation(final int type,
            final TucsonTupleCentreId tcid, final TucsonTupleCentreId tid,
            final LogicTuple t) throws TucsonOperationNotPossibleException,
            TucsonInvalidSpecificationException {
        final LogicTuple res = null;
        ISpecificationSynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getSpecificationSynchInterface(
                            tid.getInternalTupleCentreId());
            if (type == TucsonOperation.setSCode()) {
                if ("spec".equals(t.getName())) {
                    return ((SpecificationSynchInterface) context).setS(tcid,
                            new RespectSpecification(t.getArg(0).getName()));
                }
                return context.setS(tcid, t);
            }
            if (type == TucsonOperation.getSCode()) {
                return context.getS(tcid);
            }
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            throw new TucsonInvalidSpecificationException();
        } catch (final InvalidOperationException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    /**
     * 
     * @param type
     *            the type codeof the ReSpecT operation to be executed
     * @param aid
     *            the identifier of the TuCSoN agent requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the Java object representing the tuple centre operation
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be performed for some
     *             reason
     * @throws UnreachableNodeException
     *             if the TuCSoN tuple centre target of the notification cannot
     *             be reached over the network
     * @throws OperationTimeOutException
     *             if the notification operation expires timeout
     */
    public static ITupleCentreOperation doEnvironmentalOperation(
            final int type, final TucsonAgentId aid,
            final TucsonTupleCentreId tid, final LogicTuple t,
            final OperationCompletionListener l)
            throws OperationTimeOutException,
            TucsonOperationNotPossibleException, UnreachableNodeException {
        IEnvironmentContext context = null;
        RespectOperation op = null;
        context = RespectTCContainer.getRespectTCContainer()
                .getEnvironmentContext(tid.getInternalTupleCentreId());
        if (type == TucsonOperation.getEnvCode()) {
            op = RespectOperation.makeGetEnv(t, l);
        } else if (type == TucsonOperation.setEnvCode()) {
            op = RespectOperation.makeSetEnv(t, l);
        }
        // Preparing the input event for the tuple centre.
        final HashMap<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("id", aid.toString());
        InputEvent event = null;
        final TransducersManager tm = TransducersManager.INSTANCE;
        TransducerStandardInterface transducer = tm.getTransducer(aid
                .getAgentName());
        if (t != null) {
            // It's an event performed by a transducer. In other words, it's an
            // environment event
            event = new InputEvent(transducer.getIdentifier(), op,
                    tid.getInternalTupleCentreId(), context.getCurrentTime(),
                    eventMap);
            // Sending the event
            event.setSource(transducer.getIdentifier());
            event.setTarget(tid.getInternalTupleCentreId());
            context.notifyInputEnvEvent(event);
        } else {
            // It's an agent request of environment properties
            event = new InputEvent(aid, op, tid.getInternalTupleCentreId(),
                    context.getCurrentTime(), eventMap);
            final InternalEvent internalEv = new InternalEvent(event,
                    InternalOperation.makeGetEnv(t));
            internalEv.setSource(tid.getInternalTupleCentreId()); // Set
            // the source of the event
            final TransducerId[] tIds = tm.getTransducerIds(tid
                    .getInternalTupleCentreId());
            for (final TransducerId tId2 : tIds) {
                internalEv.setTarget(tId2); // Set target resource
                transducer = tm.getTransducer(tId2.getAgentName());
                transducer.notifyOutput(internalEv);
            }
        }
        return op;
    }

    /**
     * 
     * @param type
     *            the type codeof the ReSpecT operation to be executed
     * @param aid
     *            the identifier of the tuple centre requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the Java object representing the tuple centre operation
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be performed for some
     *             reason
     * @throws UnreachableNodeException
     *             if the TuCSoN tuple centre target of the notification cannot
     *             be reached over the network
     * @throws OperationTimeOutException
     *             if the notification operation expires timeout
     */
    public static ITupleCentreOperation doEnvironmentalOperation(
            final int type, final TucsonTupleCentreId aid,
            final TucsonTupleCentreId tid, final LogicTuple t,
            final OperationCompletionListener l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        IEnvironmentContext context = null;
        RespectOperation op = null;
        context = RespectTCContainer.getRespectTCContainer()
                .getEnvironmentContext(tid.getInternalTupleCentreId());
        if (type == TucsonOperation.getEnvCode()) {
            op = RespectOperation.makeGetEnv(t, l);
        } else if (type == TucsonOperation.setEnvCode()) {
            op = RespectOperation.makeSetEnv(t, l);
        }
        // Preparing the input event for the tuple centre.
        final HashMap<String, String> eventMap = new HashMap<String, String>();
        eventMap.put("id", aid.toString());
        InputEvent event = new InputEvent(aid, op,
                tid.getInternalTupleCentreId(), context.getCurrentTime(),
                eventMap);
        TransducerStandardInterface transducer;
        event = new InputEvent(aid, op, tid.getInternalTupleCentreId(),
                context.getCurrentTime(), eventMap);
        final InternalEvent internalEv = new InternalEvent(event,
                InternalOperation.makeGetEnv(t));
        internalEv.setSource(tid.getInternalTupleCentreId()); // Set
        final TransducersManager tm = TransducersManager.INSTANCE;
        // the source of the event
        final TransducerId[] tIds = tm.getTransducerIds(tid
                .getInternalTupleCentreId());
        for (final TransducerId tId2 : tIds) {
            internalEv.setTarget(tId2); // Set target resource
            transducer = tm.getTransducer(tId2.getAgentName());
            transducer.notifyOutput(internalEv);
        }
        return op;
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param obj
     *            the argument of the management operation
     * @return the result of the operation
     */
    public static Object doManagementOperation(final int type,
            final TucsonTupleCentreId tid, final Object obj) {
        IManagementContext context = null;
        context = RespectTCContainer.getRespectTCContainer()
                .getManagementContext(tid.getInternalTupleCentreId());
        if (type == TucsonOperation.abortOpCode()) {
            return context.abortOperation((Long) obj);
        }
        if (type == TucsonOperation.setSCode()) {
            try {
                context.setSpec(new RespectSpecification(((LogicTuple) obj)
                        .getArg(0).getName()));
                return true;
            } catch (final InvalidSpecificationException e) {
                e.printStackTrace();
                return false;
            } catch (final InvalidOperationException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (type == TucsonOperation.getSCode()) {
            return new LogicTuple(context.getSpec().toString());
        }
        if (type == TucsonOperation.getTRSetCode()) {
            return context.getTRSet((LogicTuple) obj);
        }
        if (type == TucsonOperation.getTSetCode()) {
            return context.getTSet((LogicTuple) obj);
        }
        if (type == TucsonOperation.getWSetCode()) {
            return context.getWSet((LogicTuple) obj);
        }
        if (type == TucsonOperation.goCmdCode()) {
            try {
                context.goCommand();
                return true;
            } catch (final OperationNotPossibleException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (type == TucsonOperation.stopCmdCode()) {
            try {
                context.stopCommand();
                return true;
            } catch (final OperationNotPossibleException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (type == TucsonOperation.isStepModeCode()) {
            return context.isStepModeCommand();
        }
        if (type == TucsonOperation.stepModeCode()) {
            context.stepModeCommand();
            return true;
        }
        if (type == TucsonOperation.nextStepCode()) {
            try {
                context.nextStepCommand();
                return true;
            } catch (final OperationNotPossibleException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        /*
         * TODO must be delete... if (type == TucsonOperation.setMngModeCode())
         * { context.setManagementMode((Boolean) obj); return true; }
         */
        if (type == TucsonOperation.addObsCode()) {
            context.addObserver((ObservableEventListener) obj);
            return true;
        }
        if (type == TucsonOperation.rmvObsCode()) {
            context.removeObserver((ObservableEventListener) obj);
            return true;
        }
        if (type == TucsonOperation.hasObsCode()) {
            return context.hasObservers();
        }
        if (type == TucsonOperation.addInspCode()) {
            context.addInspector((InspectableEventListener) obj);
            return true;
        }
        if (type == TucsonOperation.rmvInspCode()) {
            context.removeInspector((InspectableEventListener) obj);
            return true;
        }
        if (type == TucsonOperation.getInspectorsCode()) {
            return context.getInspectors();
        }
        if (type == TucsonOperation.hasInspCode()) {
            return context.hasInspectors();
        }
        // I don't think this is finished...
        if (type == TucsonOperation.reset()) {
            return context.hasInspectors();
        }
        return null;
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param aid
     *            the identifier of the agent requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the Object representing operation invocation
     * @throws TucsonInvalidLogicTupleException
     *             if the given tuple is not a valid representation of a tuple
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     */
    public static ITupleCentreOperation doNonBlockingOperation(final int type,
            final TucsonAgentId aid, final TucsonTupleCentreId tid,
            final LogicTuple t, final OperationCompletionListener l)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        IOrdinaryAsynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getOrdinaryAsynchInterface(tid.getInternalTupleCentreId());
            if (type == TucsonOperation.spawnCode()) {
                return context.spawn(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.outCode()) {
                return context.out(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inCode()) {
                return context.in(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.getCode()) {
                return context.get(aid.getAgentId(), l);
            }
            if (type == TucsonOperation.setCode()) {
                return context.set(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.outAllCode()) {
                return context.outAll(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inAllCode()) {
                return context.inAll(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdAllCode()) {
                return context.rdAll(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.noAllCode()) {
                return context.noAll(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uinp(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.urd(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.urdp(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uno(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.unop(aid.getAgentId(), t, l);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param tcid
     *            the identifier of the tuple centre requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the Object representing operation invocation
     * @throws TucsonInvalidLogicTupleException
     *             if the given tuple is not a valid representation of a tuple
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     */
    public static ITupleCentreOperation doNonBlockingOperation(final int type,
            final TucsonTupleCentreId tcid, final TucsonTupleCentreId tid,
            final LogicTuple t, final OperationCompletionListener l)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        IOrdinaryAsynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getOrdinaryAsynchInterface(tid.getInternalTupleCentreId());
            if (type == TucsonOperation.spawnCode()) {
                return context.spawn(tcid, t, l);
            }
            if (type == TucsonOperation.outCode()) {
                return context.out(tcid, t, l);
            }
            if (type == TucsonOperation.inCode()) {
                return context.in(tcid, t, l);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp(tcid, t, l);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd(tcid, t, l);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp(tcid, t, l);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no(tcid, t, l);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop(tcid, t, l);
            }
            if (type == TucsonOperation.getCode()) {
                return context.get(tcid, l);
            }
            if (type == TucsonOperation.setCode()) {
                return context.set(tcid, t, l);
            }
            if (type == TucsonOperation.outAllCode()) {
                return context.outAll(tcid, t, l);
            }
            if (type == TucsonOperation.inAllCode()) {
                return context.inAll(tcid, t, l);
            }
            if (type == TucsonOperation.rdAllCode()) {
                return context.rdAll(tcid, t, l);
            }
            if (type == TucsonOperation.noAllCode()) {
                return context.noAll(tcid, t, l);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin(tcid, t, l);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uinp(tcid, t, l);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.urd(tcid, t, l);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.urdp(tcid, t, l);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uno(tcid, t, l);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.unop(tcid, t, l);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param aid
     *            the identifier of the agent requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the Object representing operation invocation
     * @throws TucsonInvalidLogicTupleException
     *             if the given tuple is not a valid representation of a tuple
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     */
    public static ITupleCentreOperation doNonBlockingSpecOperation(
            final int type, final TucsonAgentId aid,
            final TucsonTupleCentreId tid, final LogicTuple t,
            final OperationCompletionListener l)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        ISpecificationAsynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getSpecificationAsynchInterface(
                            tid.getInternalTupleCentreId());
            if (type == TucsonOperation.noSCode()) {
                return context.noS(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.nopSCode()) {
                return context.nopS(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.outSCode()) {
                return context.outS(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inSCode()) {
                return context.inS(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inpSCode()) {
                return context.inpS(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdSCode()) {
                return context.rdS(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdpSCode()) {
                return context.rdpS(aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.getSCode()) {
                return context.getS(aid.getAgentId(), l);
            }
            if (type == TucsonOperation.setSCode()) {
                return context.setS(aid.getAgentId(), new RespectSpecification(
                        t.toString()), l);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            // Caused by the String version of a LogicTuple
            throw new TucsonInvalidLogicTupleException();
        }
        return res;
    }

    /**
     * 
     * @param type
     *            the type code of the operation requested
     * @param tcid
     *            the identifier of the tuple centre requesting the operation
     * @param tid
     *            the identifier of the tuple centre target of the operation
     * @param t
     *            the tuple argument of the operation
     * @param l
     *            the listener for operation completion
     * @return the Object representing operation invocation
     * @throws TucsonInvalidLogicTupleException
     *             if the given tuple is not a valid representation of a tuple
     * @throws TucsonOperationNotPossibleException
     *             if the operation cannot be performed
     */
    public static ITupleCentreOperation doNonBlockingSpecOperation(
            final int type, final TucsonTupleCentreId tcid,
            final TucsonTupleCentreId tid, final LogicTuple t,
            final OperationCompletionListener l)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        ISpecificationAsynchInterface context = null;
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getSpecificationAsynchInterface(
                            tid.getInternalTupleCentreId());
            if (type == TucsonOperation.noSCode()) {
                return context.noS(tcid, t, l);
            }
            if (type == TucsonOperation.nopSCode()) {
                return context.nopS(tcid, t, l);
            }
            if (type == TucsonOperation.outSCode()) {
                return context.outS(tcid, t, l);
            }
            if (type == TucsonOperation.inSCode()) {
                return context.inS(tcid, t, l);
            }
            if (type == TucsonOperation.inpSCode()) {
                return context.inpS(tcid, t, l);
            }
            if (type == TucsonOperation.rdSCode()) {
                return context.rdS(tcid, t, l);
            }
            if (type == TucsonOperation.rdpSCode()) {
                return context.rdpS(tcid, t, l);
            }
            if (type == TucsonOperation.getSCode()) {
                return context.getS(tcid, l);
            }
            if (type == TucsonOperation.setSCode()) {
                return context.setS(tcid,
                        new RespectSpecification(t.toString()), l);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            // Caused by the String version of a LogicTuple
            throw new TucsonInvalidLogicTupleException();
        }
        return res;
    }

    /**
     * @param ttcid
     *            the id of the tuple centre to make persistent
     * @param persistencyPath
     *            the path where to store persistency information
     * 
     */
    public static synchronized void enablePersistency(
            final TucsonTupleCentreId ttcid, final String persistencyPath) {
        IManagementContext context = null;
        context = RespectTCContainer.getRespectTCContainer()
                .getManagementContext(ttcid.getInternalTupleCentreId());
        context.enablePersistency(persistencyPath, ttcid);
    }

    /**
     * @param ttcid
     *            the id of the tuple centre to make persistent
     * @param persistencyPath
     *            the path where to store persistency information
     * @param file
     *            the name of the file to recover
     * 
     */
    public static void recoveryPersistent(final TucsonTupleCentreId ttcid,
            final String persistencyPath, final String file) {
        IManagementContext context = null;
        context = RespectTCContainer.getRespectTCContainer()
                .getManagementContext(ttcid.getInternalTupleCentreId());
        context.recoveryPersistent(persistencyPath, file, ttcid);
    }

    private TupleCentreContainer() {
        /*
         * 
         */
    }
}
