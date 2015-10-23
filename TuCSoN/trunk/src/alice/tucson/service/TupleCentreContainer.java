package alice.tucson.service;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidLogicTupleOperationException;
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
import alice.respect.core.RespectTC;
import alice.respect.core.RespectTCContainer;
import alice.respect.core.SpecificationSynchInterface;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidSpecificationException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.api.ObservableEventListener;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.core.InputEvent;

/**
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * @author (contributor) Michele Bombardi (mailto:
 *         michele.bombardi@studio.unibo.it)
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
     * 
     */
    public static synchronized void disablePersistence() {
        /*
         * 
         */
    }

    public static Object doBlockingOperation(final InputEvent ev)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        IOrdinarySynchInterface context = null;
        final int type = ev.getSimpleTCEvent().getType();
        final TucsonTupleCentreId tid = (TucsonTupleCentreId) ev.getTarget();
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getOrdinarySynchInterface(tid.getInternalTupleCentreId());
            if (type == TucsonOperation.getCode()) {
                return context.get(ev);
            }
            if (type == TucsonOperation.setCode()) {
                return context.set(ev);
            }
            if (type == TucsonOperation.outCode()) {
                context.out(ev);
                return ev.getSimpleTCEvent().getTupleArgument();
            }
            if (type == TucsonOperation.inCode()) {
                return context.in(ev);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp(ev);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd(ev);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp(ev);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no(ev);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop(ev);
            }
            if (type == TucsonOperation.outAllCode()) {
                return context.outAll(ev);
            }
            if (type == TucsonOperation.inAllCode()) {
                return context.inAll(ev);
            }
            if (type == TucsonOperation.rdAllCode()) {
                return context.rdAll(ev);
            }
            if (type == TucsonOperation.noAllCode()) {
                return context.noAll(ev);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin(ev);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.uin(ev);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uin(ev);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uin(ev);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.uin(ev);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.uin(ev);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return null;
    }

    public static Object doBlockingSpecOperation(final InputEvent ev)
            throws TucsonOperationNotPossibleException,
            TucsonInvalidSpecificationException { // FIXME still useful?
        final LogicTuple res = null;
        ISpecificationSynchInterface context = null;
        final int type = ev.getSimpleTCEvent().getType();
        final TucsonTupleCentreId tid = (TucsonTupleCentreId) ev.getTarget();
        final LogicTuple t = (LogicTuple) ev.getTuple();
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getSpecificationSynchInterface(
                            tid.getInternalTupleCentreId());
            if (type == TucsonOperation.setSCode()) {
                if ("spec".equals(t.getName())) {
                    return ((SpecificationSynchInterface) context)
                            .setS(new RespectSpecification(t.getArg(0)
                                    .getName()), ev);
                }
                return context.setS(t, ev);
            }
            if (type == TucsonOperation.getSCode()) {
                return context.getS(ev);
            }
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            throw new TucsonInvalidSpecificationException();
        }
        return res;
    }

    public static Object doBlockingSpecOperation(final InputEvent ev,
            final LogicTuple t) throws TucsonOperationNotPossibleException,
            TucsonInvalidSpecificationException {
        final LogicTuple res = null;
        ISpecificationSynchInterface context = null;
        final int type = ev.getSimpleTCEvent().getType();
        final TucsonTupleCentreId tid = (TucsonTupleCentreId) ev.getTarget();
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getSpecificationSynchInterface(
                            tid.getInternalTupleCentreId());
            if (type == TucsonOperation.setSCode()) {
                if ("spec".equals(t.getName())) {
                    return ((SpecificationSynchInterface) context)
                            .setS(new RespectSpecification(t.getArg(0)
                                    .getName()), ev);
                }
                return context.setS(t, ev);
            }
            if (type == TucsonOperation.getSCode()) {
                return context.getS(ev);
            }
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            throw new TucsonInvalidSpecificationException();
        } 
        return res;
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

    public static ITupleCentreOperation doNonBlockingOperation(
            final InputEvent ev) throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        IOrdinaryAsynchInterface context = null;
        final int type = ev.getSimpleTCEvent().getType();
        final TucsonTupleCentreId tid = (TucsonTupleCentreId) ev.getTarget();
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getOrdinaryAsynchInterface(tid.getInternalTupleCentreId());
            
            if (type == TucsonOperation.spawnCode()) {
                return context.spawn(ev);
            }
            if (type == TucsonOperation.getEnvCode()) {
                return context.getEnv(ev);
            }
            if (type == TucsonOperation.setEnvCode()) {
                return context.setEnv(ev);
            }
            if (type == TucsonOperation.outCode()) {
                return context.out(ev);
            }
            if (type == TucsonOperation.inCode()) {
                return context.in(ev);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp(ev);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd(ev);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp(ev);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no(ev);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop(ev);
            }
            if (type == TucsonOperation.getCode()) {
                return context.get(ev);
            }
            if (type == TucsonOperation.setCode()) {
                return context.set(ev);
            }
            if (type == TucsonOperation.outAllCode()) {
                return context.outAll(ev);
            }
            if (type == TucsonOperation.inAllCode()) {
                return context.inAll(ev);
            }
            if (type == TucsonOperation.rdAllCode()) {
                return context.rdAll(ev);
            }
            if (type == TucsonOperation.noAllCode()) {
                return context.noAll(ev);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin(ev);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uinp(ev);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.urd(ev);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.urdp(ev);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uno(ev);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.unop(ev);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    public static ITupleCentreOperation doNonBlockingSpecOperation(
            final InputEvent ev) throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        ISpecificationAsynchInterface context = null;
        final int type = ev.getSimpleTCEvent().getType();
        final TucsonTupleCentreId tid = (TucsonTupleCentreId) ev.getTarget();
        final LogicTuple t = (LogicTuple) ev.getTuple();
        try {
            context = RespectTCContainer.getRespectTCContainer()
                    .getSpecificationAsynchInterface(
                            tid.getInternalTupleCentreId());
            if (type == TucsonOperation.noSCode()) {
                return context.noS(ev);
            }
            if (type == TucsonOperation.nopSCode()) {
                return context.nopS(ev);
            }
            if (type == TucsonOperation.outSCode()) {
                return context.outS(ev);
            }
            if (type == TucsonOperation.inSCode()) {
                return context.inS(ev);
            }
            if (type == TucsonOperation.inpSCode()) {
                return context.inpS(ev);
            }
            if (type == TucsonOperation.rdSCode()) {
                return context.rdS(ev);
            }
            if (type == TucsonOperation.rdpSCode()) {
                return context.rdpS(ev);
            }
            if (type == TucsonOperation.getSCode()) {
                return context.getS(ev);
            }
            if (type == TucsonOperation.setSCode()) {
                return context.setS(new RespectSpecification(t.toString()), ev);
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
     */
    // why are these methods not implemented yet?
    public static synchronized void enablePersistence() {
        /*
         * 
         */
    }

    /**
     * 
     */
    public static void loadPersistentInformation() {
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
