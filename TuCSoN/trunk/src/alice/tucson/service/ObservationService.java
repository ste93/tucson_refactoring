/*
 * Created on Dec 6, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package alice.tucson.service;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.TupleTemplate;

/**
 * 
 */
class ObservationService implements NodeServiceListener {

    private TucsonAgentId obsAid;
    private final TucsonTupleCentreId obsContext;

    public ObservationService(final TucsonTupleCentreId ctx) {
        this.obsContext = ctx;
        try {
            this.obsAid = new TucsonAgentId("obs_agent");
        } catch (final TucsonInvalidAgentIdException e) {
            System.err.println("[ObservationService]: " + e);
            e.printStackTrace();
        }
    }

    public void accEntered(final TucsonAgentId aid) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "new_agent", new Value(aid.toString()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void accQuit(final TucsonAgentId aid) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "exit_agent", new Value(aid.toString()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void getSpec_completed(final TupleCentreId tid, final IId id,
            final String spec) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "completed_getSpec", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new Value(spec))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void getSpec_requested(final TupleCentreId tid, final IId id) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "requested_getSpec", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void in_completed(final TupleCentreId tid, final IId id,
            final Tuple t) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "completed_in", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new TupleArgument(((LogicTuple) t).toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void in_requested(final TupleCentreId tid, final IId id,
            final TupleTemplate t) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "requested_in", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new TupleArgument(((LogicTuple) t).toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void inp_completed(final TupleCentreId tid, final IId id,
            final Tuple t) {
        try {
            if (t != null) {
                TupleCentreContainer.doBlockingOperation(TucsonOperation
                        .outCode(), this.obsAid, this.obsContext,
                        new LogicTuple("node_event", new Var(), new Value(
                                "completed_inp", new TupleArgument(
                                        ((TucsonTupleCentreId) tid).toTerm()),
                                new Value(((TucsonAgentId) id).toString()),
                                new Value("succeeded", new TupleArgument(
                                        ((LogicTuple) t).toTerm())))));
            } else {
                TupleCentreContainer.doBlockingOperation(TucsonOperation
                        .outCode(), this.obsAid, this.obsContext,
                        new LogicTuple("node_event", new Var(), new Value(
                                "completed_inp", new TupleArgument(
                                        ((TucsonTupleCentreId) tid).toTerm()),
                                new Value(((TucsonAgentId) id).toString()),
                                new Value("failed"))));
            }
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void inp_requested(final TupleCentreId tid, final IId id,
            final TupleTemplate t) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "requested_inp", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new TupleArgument(((LogicTuple) t).toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void out_requested(final TupleCentreId tid, final IId id,
            final Tuple t) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "done_out", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new TupleArgument(((LogicTuple) t).toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void rd_completed(final TupleCentreId tid, final IId id,
            final Tuple t) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "completed_rd", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new TupleArgument(((LogicTuple) t).toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void rd_requested(final TupleCentreId tid, final IId id,
            final TupleTemplate t) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "requested_rd", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new TupleArgument(((LogicTuple) t).toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void rdp_completed(final TupleCentreId tid, final IId id,
            final Tuple t) {
        try {
            if (t != null) {
                TupleCentreContainer.doBlockingOperation(TucsonOperation
                        .outCode(), this.obsAid, this.obsContext,
                        new LogicTuple("node_event", new Var(), new Value(
                                "completed_rdp", new TupleArgument(
                                        ((TucsonTupleCentreId) tid).toTerm()),
                                new Value(((TucsonAgentId) id).toString()),
                                new Value("succeeded", new TupleArgument(
                                        ((LogicTuple) t).toTerm())))));
            } else {
                TupleCentreContainer.doBlockingOperation(TucsonOperation
                        .outCode(), this.obsAid, this.obsContext,
                        new LogicTuple("node_event", new Var(), new Value(
                                "completed_rdp", new TupleArgument(
                                        ((TucsonTupleCentreId) tid).toTerm()),
                                new Value(((TucsonAgentId) id).toString()),
                                new Value("failed"))));
            }
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void rdp_requested(final TupleCentreId tid, final IId id,
            final TupleTemplate t) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "requested_rdp", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new TupleArgument(((LogicTuple) t).toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void setSpec_completed(final TupleCentreId tid, final IId id) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "completed_setSpec", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void setSpec_requested(final TupleCentreId tid, final IId id,
            final String spec) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext,
                    new LogicTuple("node_event", new Var(), new Value(
                            "requested_setSpec", new TupleArgument(
                                    ((TucsonTupleCentreId) tid).toTerm()),
                            new Value(((TucsonAgentId) id).toString()),
                            new Value(spec))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void tcCreated(final TucsonTupleCentreId tid) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext, new LogicTuple("node_event",
                            new Var(), new Value("new_tc", new TupleArgument(
                                    tid.toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

    public void tcDestroyed(final TucsonTupleCentreId tid) {
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.obsAid, this.obsContext, new LogicTuple("node_event",
                            new Var(), new Value("destoyed_tc",
                                    new TupleArgument(tid.toTerm()))));
        } catch (final Exception ex) {
            System.err.println("[ObservationService]: " + ex);
            ex.printStackTrace();
        }
    }

}
