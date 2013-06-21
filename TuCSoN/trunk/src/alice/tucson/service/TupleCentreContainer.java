package alice.tucson.service;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.api.AgentId;
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
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidSpecificationException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.api.ObservableEventListener;
import alice.tuplecentre.core.OperationCompletionListener;

/**
 * 
 */
public class TupleCentreContainer {

    private static int defaultport;

    public static RespectTC createTC(final TucsonTupleCentreId id, final int q,
            final int def_port) throws InvalidTupleCentreIdException {
        TupleCentreContainer.defaultport = def_port;
        try {
            final RespectTCContainer rtcc =
                    RespectTCContainer.getRespectTCContainer();
            rtcc.setDefPort(TupleCentreContainer.defaultport);
            final TupleCentreId tid =
                    new TupleCentreId(id.getName(), id.getNode(), ""
                            + id.getPort());
            return rtcc.createRespectTC(tid, q);
        } catch (final InvalidTupleCentreIdException e) {
            throw new InvalidTupleCentreIdException();
        }
    }

    public static synchronized void destroyTC() {
        /*
         * 
         */
    }

    public static synchronized void disablePersistence() {
        /*
         * 
         */
    }

    /**
     * 
     * @param type
     * @param aid
     * @param tid
     * @param o
     * @return
     * @throws TucsonInvalidLogicTupleException
     * @throws TucsonOperationNotPossibleException
     */
    public static Object doBlockingOperation(final int type,
            final TucsonAgentId aid, final TucsonTupleCentreId tid,
            final Object o) throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        IOrdinarySynchInterface context = null;
        try {
            context =
                    (RespectTCContainer.getRespectTCContainer())
                            .getOrdinarySynchInterface((TupleCentreId) tid
                                    .getInternalTupleCentreId());
            if (type == TucsonOperation.get_Code()) {
                return context.get((AgentId) aid.getAgentId());
            }
            if (type == TucsonOperation.set_Code()) {
                return context.set((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.outCode()) {
                context.out((AgentId) aid.getAgentId(), (LogicTuple) o);
                return o;
            }
            if (type == TucsonOperation.inCode()) {
                return context.in((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.out_allCode()) {
                return context.out_all((AgentId) aid.getAgentId(),
                        (LogicTuple) o);
            }
            if (type == TucsonOperation.in_allCode()) {
                return context.in_all((AgentId) aid.getAgentId(),
                        (LogicTuple) o);
            }
            if (type == TucsonOperation.rd_allCode()) {
                return context.rd_all((AgentId) aid.getAgentId(),
                        (LogicTuple) o);
            }
            if (type == TucsonOperation.no_allCode()) {
                return context.no_all((AgentId) aid.getAgentId(),
                        (LogicTuple) o);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.uin((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uin((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uin((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.uin((AgentId) aid.getAgentId(), (LogicTuple) o);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.uin((AgentId) aid.getAgentId(), (LogicTuple) o);
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
     * @param aid
     * @param tid
     * @param t
     * 
     * @return
     * 
     * @throws TucsonInvalidLogicTupleException
     * @throws TucsonOperationNotPossibleException
     */
    public static Object doBlockingOperation(final int type,
            final TucsonTupleCentreId aid, final TucsonTupleCentreId tid,
            final LogicTuple t) throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        IOrdinarySynchInterface context = null;
        try {
            context =
                    RespectTCContainer.getRespectTCContainer()
                            .getOrdinarySynchInterface(
                                    (TupleCentreId) tid
                                            .getInternalTupleCentreId());
            if (type == TucsonOperation.outCode()) {
                context.out((TupleCentreId) aid.getInternalTupleCentreId(), t);
                return t;
            }
            if (type == TucsonOperation.inCode()) {
                return context.in(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.out_allCode()) {
                return context.out_all(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.in_allCode()) {
                return context.in_all(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.rd_allCode()) {
                return context.rd_all(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.no_allCode()) {
                return context.no_all(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.uin(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uin(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uin(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.uin(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.uin(
                        (TupleCentreId) aid.getInternalTupleCentreId(), t);
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
     * @param aid
     * @param tid
     * @param t
     * @return
     * @throws TucsonInvalidLogicTupleException
     * @throws TucsonOperationNotPossibleException
     * @throws TucsonInvalidSpecificationException
     */
    public static Object doBlockingSpecOperation(final int type,
            final TucsonAgentId aid, final TucsonTupleCentreId tid,
            final LogicTuple t) throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException,
            TucsonInvalidSpecificationException {
        final LogicTuple res = null;
        ISpecificationSynchInterface context = null;
        try {
            context =
                    (RespectTCContainer.getRespectTCContainer())
                            .getSpecificationSynchInterface((TupleCentreId) tid
                                    .getInternalTupleCentreId());
            if (type == TucsonOperation.set_sCode()) {
                if (t.getName().equals("spec")) {
                    return ((SpecificationSynchInterface) context).set_s(
                            (AgentId) aid.getAgentId(),
                            new RespectSpecification(t.getArg(0).getName()));
                }
                return ((SpecificationSynchInterface) context).set_s(
                        (AgentId) aid.getAgentId(), t);
            }
            if (type == TucsonOperation.get_sCode()) {
                return ((SpecificationSynchInterface) context)
                        .get_s((AgentId) aid.getAgentId());
            }
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            throw new TucsonInvalidSpecificationException();
        } catch (final InvalidTupleOperationException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    public static Object doBlockingSpecOperation(final int type,
            final TucsonTupleCentreId tcid, final TucsonTupleCentreId tid,
            final LogicTuple t) throws TucsonOperationNotPossibleException,
            TucsonInvalidSpecificationException {
        final LogicTuple res = null;
        ISpecificationSynchInterface context = null;
        try {
            context =
                    (RespectTCContainer.getRespectTCContainer())
                            .getSpecificationSynchInterface((TupleCentreId) tid
                                    .getInternalTupleCentreId());
            if (type == TucsonOperation.set_sCode()) {
                if (t.getName().equals("spec")) {
                    return ((SpecificationSynchInterface) context).set_s(tcid,
                            new RespectSpecification(t.getArg(0).getName()));
                }
                return ((SpecificationSynchInterface) context).set_s(tcid, t);
            }
            if (type == TucsonOperation.get_sCode()) {
                return ((SpecificationSynchInterface) context).get_s(tcid);
            }
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        } catch (final InvalidSpecificationException e) {
            throw new TucsonInvalidSpecificationException();
        } catch (final InvalidTupleOperationException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    /**
     * 
     * @param type
     * @param tid
     * @param obj
     * @return
     * @throws TucsonOperationNotPossibleException
     * @throws TucsonInvalidLogicTupleException
     */
    public static Object doManagementOperation(final int type,
            final TucsonTupleCentreId tid, final Object obj)
            throws TucsonOperationNotPossibleException,
            TucsonInvalidLogicTupleException {

        IManagementContext context = null;
        context =
                (RespectTCContainer.getRespectTCContainer())
                        .getManagementContext((TupleCentreId) tid
                                .getInternalTupleCentreId());

        if (type == TucsonOperation.abortOpCode()) {
            return context.abortOperation((Long) obj);
        }

        if (type == TucsonOperation.set_sCode()) {
            try {
                context.setSpec(new RespectSpecification(((LogicTuple) obj)
                        .getArg(0).getName()));
                return true;
            } catch (final InvalidSpecificationException e) {
                System.err.println("[TupleCentreContainer]: " + e);
                e.printStackTrace();
                return false;
            } catch (final InvalidTupleOperationException e) {
                System.err.println("[TupleCentreContainer]: " + e);
                e.printStackTrace();
                return false;
            }
        }
        if (type == TucsonOperation.get_sCode()) {
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
                System.err.println("[TupleCentreContainer]: " + e);
                e.printStackTrace();
                return false;
            }
        }
        if (type == TucsonOperation.stopCmdCode()) {
            try {
                context.stopCommand();
                return true;
            } catch (final OperationNotPossibleException e) {
                System.err.println("[TupleCentreContainer]: " + e);
                e.printStackTrace();
                return false;
            }
        }
        if (type == TucsonOperation.nextStepCode()) {
            try {
                context.nextStepCommand();
                return true;
            } catch (final OperationNotPossibleException e) {
                System.err.println("[TupleCentreContainer]: " + e);
                e.printStackTrace();
                return false;
            }
        }

        if (type == TucsonOperation.setMngModeCode()) {
            context.setManagementMode((Boolean) obj);
            return true;
        }

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
     * @param aid
     * @param tid
     * @param t
     * @param l
     * @return
     * @throws TucsonInvalidLogicTupleException
     * @throws TucsonOperationNotPossibleException
     */
    public static ITupleCentreOperation doNonBlockingOperation(final int type,
            final TucsonAgentId aid, final TucsonTupleCentreId tid,
            final LogicTuple t, final OperationCompletionListener l)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        IOrdinaryAsynchInterface context = null;
        try {
            context =
                    (RespectTCContainer.getRespectTCContainer())
                            .getOrdinaryAsynchInterface((TupleCentreId) tid
                                    .getInternalTupleCentreId());
            if (type == TucsonOperation.spawnCode()) {
                return context.spawn((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.outCode()) {
                return context.out((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inCode()) {
                return context.in((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inpCode()) {
                return context.inp((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdCode()) {
                return context.rd((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdpCode()) {
                return context.rdp((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.noCode()) {
                return context.no((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.nopCode()) {
                return context.nop((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.get_Code()) {
                return context.get((AgentId) aid.getAgentId(), l);
            }
            if (type == TucsonOperation.set_Code()) {
                return context.set((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.out_allCode()) {
                return context.out_all((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.in_allCode()) {
                return context.in_all((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rd_allCode()) {
                return context.rd_all((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.no_allCode()) {
                return context.no_all((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.uinCode()) {
                return context.uin((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.uinpCode()) {
                return context.uinp((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.urdCode()) {
                return context.urd((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.urdpCode()) {
                return context.urdp((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.unoCode()) {
                return context.uno((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.unopCode()) {
                return context.unop((AgentId) aid.getAgentId(), t, l);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    public static ITupleCentreOperation doNonBlockingOperation(final int type,
            final TucsonTupleCentreId tcid, final TucsonTupleCentreId tid,
            final LogicTuple t, final OperationCompletionListener l)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        IOrdinaryAsynchInterface context = null;
        try {
            context =
                    (RespectTCContainer.getRespectTCContainer())
                            .getOrdinaryAsynchInterface((TupleCentreId) tid
                                    .getInternalTupleCentreId());
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
            if (type == TucsonOperation.get_Code()) {
                return context.get(tcid, l);
            }
            if (type == TucsonOperation.set_Code()) {
                return context.set(tcid, t, l);
            }
            if (type == TucsonOperation.out_allCode()) {
                return context.out_all(tcid, t, l);
            }
            if (type == TucsonOperation.in_allCode()) {
                return context.in_all(tcid, t, l);
            }
            if (type == TucsonOperation.rd_allCode()) {
                return context.rd_all(tcid, t, l);
            }
            if (type == TucsonOperation.no_allCode()) {
                return context.no_all(tcid, t, l);
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
     * @param aid
     * @param tid
     * @param t
     * @param l
     * @return
     * @throws TucsonInvalidLogicTupleException
     * @throws TucsonOperationNotPossibleException
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
            context =
                    (RespectTCContainer.getRespectTCContainer())
                            .getSpecificationAsynchInterface((TupleCentreId) tid
                                    .getInternalTupleCentreId());
            if (type == TucsonOperation.no_sCode()) {
                return context.no_s((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.nop_sCode()) {
                return context.nop_s((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.out_sCode()) {
                return context.out_s((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.in_sCode()) {
                return context.in_s((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.inp_sCode()) {
                return context.inp_s((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rd_sCode()) {
                return context.rd_s((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.rdp_sCode()) {
                return context.rdp_s((AgentId) aid.getAgentId(), t, l);
            }
            if (type == TucsonOperation.get_sCode()) {
                return context.get_s((AgentId) aid.getAgentId(), l);
            }
            if (type == TucsonOperation.set_sCode()) {
                return context.set_s((AgentId) aid.getAgentId(),
                        new RespectSpecification(t.toString()), l);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    public static ITupleCentreOperation doNonBlockingSpecOperation(
            final int type, final TucsonTupleCentreId tcid,
            final TucsonTupleCentreId tid, final LogicTuple t,
            final OperationCompletionListener l)
            throws TucsonInvalidLogicTupleException,
            TucsonOperationNotPossibleException {
        final ITupleCentreOperation res = null;
        ISpecificationAsynchInterface context = null;
        try {
            context =
                    (RespectTCContainer.getRespectTCContainer())
                            .getSpecificationAsynchInterface((TupleCentreId) tid
                                    .getInternalTupleCentreId());
            if (type == TucsonOperation.no_sCode()) {
                return context.no_s(tcid, t, l);
            }
            if (type == TucsonOperation.nop_sCode()) {
                return context.nop_s(tcid, t, l);
            }
            if (type == TucsonOperation.out_sCode()) {
                return context.out_s(tcid, t, l);
            }
            if (type == TucsonOperation.in_sCode()) {
                return context.in_s(tcid, t, l);
            }
            if (type == TucsonOperation.inp_sCode()) {
                return context.inp_s(tcid, t, l);
            }
            if (type == TucsonOperation.rd_sCode()) {
                return context.rd_s(tcid, t, l);
            }
            if (type == TucsonOperation.rdp_sCode()) {
                return context.rdp_s(tcid, t, l);
            }
            if (type == TucsonOperation.get_sCode()) {
                return context.get_s(tcid, l);
            }
            if (type == TucsonOperation.set_sCode()) {
                return context.set_s(tcid,
                        new RespectSpecification(t.toString()), l);
            }
        } catch (final InvalidLogicTupleException e) {
            throw new TucsonInvalidLogicTupleException();
        } catch (final OperationNotPossibleException e) {
            throw new TucsonOperationNotPossibleException();
        }
        return res;
    }

    // why are these methods not implemented yet?
    public static synchronized void enablePersistence() {
        /*
         * 
         */
    }

    public static void loadPersistentInformation() {
        /*
         * 
         */
    }

}
