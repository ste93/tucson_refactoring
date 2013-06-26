package alice.tucson.service;

import java.util.Iterator;
import java.util.List;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuplecentre.api.ITupleCentreOperation;
import alice.tuplecentre.api.Tuple;
import alice.tuprolog.Library;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * 
 * 
 * @author ste
 */
public class Spawn2PLibrary extends Library {

    private static final long serialVersionUID = 3019036120338145017L;

    /**
     * Utility to convert a list of tuple into a tuple list of tuples
     * 
     * @param list
     *            the list of tuples to convert
     * 
     * @return the tuple list of tuples result of the conversion
     */
    private static Term list2tuple(final List<Tuple> list) {
        final Term[] termArray = new Term[list.size()];
        final Iterator<Tuple> it = list.iterator();
        int i = 0;
        while (it.hasNext()) {
            termArray[i] = ((LogicTuple) it.next()).toTerm();
            i++;
        }
        return new Struct(termArray);
    }

    private TucsonAgentId aid;
    private TucsonTupleCentreId target;

    private TucsonTupleCentreId tcid;

    /**
     * Both agents and the coordination medium itself can spawn() a computation,
     * hence we need to handle both.
     * 
     * @return the "spawner" id (actually, a generic wrapper hosting either a
     *         TucsonAgentId or a TucsonTupleCentreId, accessible with method
     *         <code> getId() </code>)
     */
    public final TucsonIdWrapper<?> getSpawnerId() {
        if (this.aid == null) {
            return new TucsonIdWrapper<TucsonTupleCentreId>(this.tcid);
        }
        return new TucsonIdWrapper<TucsonAgentId>(this.aid);
    }

    public final TucsonTupleCentreId getTargetTC() {
        return this.target;
    }

    /**
	 * 
	 */
    @Override
    public String getTheory() {
        return "out(T). \n" + "in(T). \n" + "inp(T). \n" + "rd(T). \n"
                + "rdp(T). \n" + "no(T). \n" + "nop(T). \n" + "out_all(L). \n"
                + "in_all(T,L). \n" + "rd_all(T,L). \n" + "no_all(T,L). \n"
                + "uin(T). \n" + "uinp(T). \n" + "urd(T). \n" + "urdp(T). \n"
                + "uno(T). \n" + "unop(T). \n";
    }

    public boolean in_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.inCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.inCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

    public boolean in_all_2(final Term arg0, final Term arg1) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.in_allCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.in_allCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg1,
                    Spawn2PLibrary.list2tuple(op.getTupleListResult()));
        }
        return false;
    }

    public boolean inp_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.inpCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.inpCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

    public boolean no_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.noCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.noCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return true;
        }
        this.unify(arg0, (Term) op.getTupleResult());
        return false;
    }

    public boolean no_all_2(final Term arg0, final Term arg1) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.no_allCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.no_allCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg1,
                    Spawn2PLibrary.list2tuple(op.getTupleListResult()));
        }
        return false;
    }

    public boolean nop_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.nopCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.nopCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return true;
        }
        this.unify(arg0, (Term) op.getTupleResult());
        return false;
    }

    public boolean out_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.outCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.outCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        return true;
    }

    public boolean out_all_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.out_allCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.out_allCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        return true;
    }

    public boolean rd_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.rdCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.rdCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

    public boolean rd_all_2(final Term arg0, final Term arg1) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.rd_allCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.rd_allCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg1,
                    Spawn2PLibrary.list2tuple(op.getTupleListResult()));
        }
        return false;
    }

    public boolean rdp_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.rdpCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.rdpCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

    public final void setSpawnerId(final TucsonAgentId id) {
        this.aid = id;
        this.tcid = null;
    }

    public final void setSpawnerId(final TucsonTupleCentreId id) {
        this.aid = null;
        this.tcid = id;
    }

    public final void setTargetTC(final TucsonTupleCentreId id) {
        this.target = id;
    }

    public boolean uin_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.uinCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.uinCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

    public boolean uinp_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.uinpCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.uinpCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

    public boolean uno_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.unoCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.unoCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return true;
        }
        this.unify(arg0, (Term) op.getTupleResult());
        return false;
    }

    public boolean unop_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.unopCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.unopCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return true;
        }
        this.unify(arg0, (Term) op.getTupleResult());
        return false;
    }

    public boolean urd_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.urdCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.urdCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

    public boolean urdp_1(final Term arg0) {
        ITupleCentreOperation op = null;
        final LogicTuple arg = new LogicTuple(arg0);
        if (this.aid != null) {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.urdpCode(), this.aid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        } else {
            try {
                op =
                        TupleCentreContainer.doNonBlockingOperation(
                                TucsonOperation.urdpCode(), this.tcid,
                                this.target, arg, null);
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[Spawn2PLibrary]: " + e);
                // TODO Properly handle Exception
                return false;
            }
        }
        op.waitForOperationCompletion();
        if (op.isResultSuccess()) {
            return this.unify(arg0, (Term) op.getTupleResult());
        }
        return false;
    }

}
