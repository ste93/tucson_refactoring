/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.respect.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import alice.logictuple.LogicTuple;
import alice.logictuple.LogicTupleOpManager;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.ILinkContext;
import alice.respect.api.IRespectTC;
import alice.respect.api.RespectSpecification;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.respect.api.geolocation.PlatformUtils;
import alice.respect.api.geolocation.service.AbstractGeolocationService;
import alice.respect.api.geolocation.service.GeolocationServiceManager;
import alice.respect.core.tupleset.ITupleSet;
import alice.respect.core.tupleset.TupleSetCoord;
import alice.respect.core.tupleset.TupleSetSpec;
import alice.tucson.api.AbstractSpawnActivity;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.introspection.WSetEvent;
import alice.tucson.persistency.PersistencyData;
import alice.tucson.persistency.PersistencyXML;
import alice.tucson.service.Spawn2PLibrary;
import alice.tucson.service.Spawn2PSolver;
import alice.tuplecentre.api.AgentId;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.core.AbstractBehaviourSpecification;
import alice.tuplecentre.core.AbstractEvent;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import alice.tuplecentre.core.InputEvent;
import alice.tuplecentre.core.ObservableEventReactionFail;
import alice.tuplecentre.core.ObservableEventReactionOK;
import alice.tuplecentre.core.OperationCompletionListener;
import alice.tuplecentre.core.OutputEvent;
import alice.tuplecentre.core.TriggeredReaction;
import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Parser;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;

/**
 * This class defines a ReSpecT Context as a specialization of a tuple centre VM
 * context (defining VM specific structures)
 *
 * @see alice.tuplecentre.core.AbstractTupleCentreVMContext
 *
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * @author (contributor) Saverio Cicora
 * @author (contributor) Michele Bombardi (mailto:
 *         michele.bombardi@studio.unibo.it)
 */
public class RespectVMContext extends alice.tuplecentre.core.AbstractTupleCentreVMContext {

	/**
	 *
	 * @author Stefano Mariani (mailto: s.mariani@unibo.it)
	 *
	 */
	public enum ModType {
		ADD_PRED, ADD_SPEC, ADD_TUPLE, DEL_PRED, DEL_SPEC, DEL_TUPLE, EMPTY_PRED, EMPTY_SPEC, EMPTY_TUPLES
	}

	class CompletionListener implements OperationCompletionListener {

		private final OutputEvent oe;

		public CompletionListener(final OutputEvent o) {
			this.oe = o;
		}

		@Override
		public void operationCompleted(final AbstractTupleCentreOperation arg0) {
			arg0.removeListener();
			// oe.getTarget() == oeTarget by construction (loc 1201)!
			// 3rd arg is the target of the event,
			RespectVMContext.this.log("Completion op = " + arg0 + ", from = " + this.oe.getSource() + ", to = "
					+ this.oe.getTarget() + ", arg = " + arg0.getTupleResult() + " / " + arg0.getTupleListResult());
			final InputEvent res = new InputEvent(this.oe.getSource(), arg0, (TupleCentreId) this.oe.getTarget(),
					RespectVMContext.this.getCurrentTime(), RespectVMContext.this.getPosition());
			RespectVMContext.this.notifyInputEvent(res);
		}
	}

	/**
	 * the distance tollerance used in spatial observation predicates and guards
	 */
	private static final float METERS_DISTANCE_TOLLERANCE = 10f;

	/**
	 * Static services that checks if a source text contains a valid ReSpecT
	 * specification
	 *
	 * @param spec
	 *            the String representation of the ReSpecT specification to check
	 *            for syntactic correctness
	 * @return a logic tuple that provides information about the check: valid is the
	 *         specification is OK, or invalid(L) if there are errors (at line L).
	 *
	 */
	public static LogicTuple checkReactionSpec(final String spec) {
		Prolog core = new Prolog();
		try {
			final Struct co = new Struct(spec);
			if (co.isAtom()) {
				final alice.tuprolog.Theory thspec = new alice.tuprolog.Theory(co.getName());
				core.setTheory(thspec);
			} else if (co.isList()) {
				final alice.tuprolog.Theory thspec = new alice.tuprolog.Theory(co);
				core.setTheory(thspec);
			} else {
				core = null;
				return new LogicTuple("invalid", new alice.logictuple.Var());
			}
			core = null;
			return new LogicTuple("valid");
		} catch (final alice.tuprolog.InvalidTheoryException ex) {
			core = null;
			return new LogicTuple("invalid", new Value(ex.line));
		}
	}

	private static Term list2tuple(final List<LogicTuple> list) {
		final Term[] termArray = new Term[list.size()];
		final Iterator<LogicTuple> it = list.iterator();
		int i = 0;
		while (it.hasNext()) {
			termArray[i] = it.next().toTerm();
			i++;
		}
		return new Struct(termArray);
	}

	private final Prolog core;
	private AbstractEvent currentReactionEvent;
	private Struct currentReactionTerm;
	/** are we setting a specification from outside? */
	private boolean isExternalSetSpec;
	private boolean isPersistent;
	private final Prolog matcher = new Prolog();
	/** Used to keep trace of theory other than reactions */
	private Theory noReactionTh;
	/** multiset of Prolog predicates */
	private TupleSet prologPredicates;
	/** Persistency XML */
	private PersistencyXML pXML;
	private RespectSpecification reactionSpec;
	private final Object semaphore;
	/**
	 * list of temporary output event caused by linkability operation: they are
	 * added to the output queue (outputEventList only when the related reaction is
	 * successfully executed
	 */
	private final List<AbstractEvent> temporaryOutputEventList;
	/** List of timers scheduled for execution */
	private final List<Timer> timers;
	/** multiset of triggered timed reactions */
	private final TRSet timeSet;
	private boolean transaction;
	private Prolog trigCore;
	/** multiset of tuples T */
	private final ITupleSet tSet;
	/** multiset of specification tuple Sigma */
	private final ITupleSet tSpecSet;
	private final RespectVM vm;
	/** multiset of pending query set */
	private final PendingQuerySet wSet;
	/** multiset of triggered reactions Z */
	private final TRSet zSet;

	/**
	 *
	 * @param rvm
	 *            the ReSpecT VM this storage context is managed by
	 * @param tid
	 *            the identifier of the tuple centre managed
	 * @param queueSize
	 *            the maximum InQ size of the tuple centre
	 * @param respectTC
	 *            the ReSpecT tuple centres manager
	 */
	public RespectVMContext(final RespectVM rvm, final TupleCentreId tid, final int queueSize,
			final IRespectTC respectTC) {
		super(rvm, tid, queueSize, respectTC);
		this.timers = new ArrayList<Timer>();
		this.semaphore = new Object();
		this.tSet = new TupleSetCoord();
		this.tSpecSet = new TupleSetSpec();
		this.prologPredicates = new TupleSet();
		this.wSet = new PendingQuerySet();
		this.zSet = new TRSet();
		this.timeSet = new TRSet();
		this.vm = rvm;
		this.temporaryOutputEventList = new ArrayList<AbstractEvent>();
		this.core = new Prolog();
		final alice.tuprolog.event.OutputListener l = new alice.tuprolog.event.OutputListener() {

			@Override
			public void onOutput(final alice.tuprolog.event.OutputEvent ev) {
				System.out.print(ev.getMsg());
			}
		};
		this.core.addOutputListener(l);
		try {
			((alice.respect.api.Respect2PLibrary) this.core.loadLibrary("alice.respect.api.Respect2PLibrary"))
					.init(this);
		} catch (final InvalidLibraryException e) {
			e.printStackTrace();
		}
		try {
			this.trigCore = new Prolog();
			this.trigCore.loadLibrary("alice.respect.api.Respect2PLibrary");
			((alice.respect.api.Respect2PLibrary) this.trigCore.getLibrary("alice.respect.api.Respect2PLibrary"))
					.init(this);
		} catch (final InvalidLibraryException e) {
			e.printStackTrace();
		}
		this.reactionSpec = new RespectSpecification("");
		this.reset();
		this.isExternalSetSpec = false;
		this.isPersistent = false;
	}

	@Override
	public List<Tuple> addListTuple(final Tuple t) {
		final List<Tuple> list = new LinkedList<Tuple>();
		LogicTuple tuple = (LogicTuple) t;
		LogicTuple toAdd;
		while (!"[]".equals(tuple.toString())) {
			toAdd = new LogicTuple(tuple.getArg(0));
			this.tSet.add(toAdd);
			if (this.isPersistent) {
				this.writePersistencyUpdate(toAdd, ModType.ADD_TUPLE);
			}
			list.add(new LogicTuple(tuple.getArg(0)));
			tuple = new LogicTuple(tuple.getArg(1));
		}
		return list;
	}

	@Override
	public void addPendingQueryEvent(final InputEvent w) {
		this.wSet.add(w);
	}

	@Override
	public void addSpecTuple(final Tuple t) {
		Tuple tuple = null;
		if (",".equals(((LogicTuple) t).getName())) {
			tuple = new LogicTuple("reaction", ((LogicTuple) t).getArg(0), ((LogicTuple) t).getArg(1).getArg(0),
					((LogicTuple) t).getArg(1).getArg(1));
		} else {
			tuple = t;
		}
		// FIXME LogicTuple > Tuple in all Cicora's API
		this.tSpecSet.add((LogicTuple) tuple);
		if (this.isPersistent) {
			this.writePersistencyUpdate((LogicTuple) tuple, ModType.ADD_SPEC);
		}
		this.setReactionSpecHelper(new alice.respect.api.RespectSpecification(this.tSpecSet.toString()));
	}

	/**
	 *
	 * @param out
	 *            the out-link event to be remembered
	 */
	public void addTemporaryOutputEvent(final InputEvent out) {
		synchronized (this.temporaryOutputEventList) {
			this.temporaryOutputEventList.add(out);
		}
	}

	@Override
	public void addTuple(final Tuple t, final boolean update) {
		this.tSet.add((LogicTuple) t);
		if (this.isPersistent && update) {
			this.writePersistencyUpdate((LogicTuple) t, ModType.ADD_TUPLE);
		}
	}

	/*
	 * TODO: delete useless
	 */
	public void closePersistencyUpdates() {
		if (this.isPersistent) {
			/*
			 * final File f = new File(this.pPath, "tc_" + this.pFileName + "_" + this.pDate
			 * + ".dat"); final long now = System.currentTimeMillis(); final Date d = new
			 * Date(now); final SimpleDateFormat sdf = new SimpleDateFormat(
			 * "yyyy-MM-dd HH:mm:ss"); final String ds = sdf.format(d); PrintWriter pw =
			 * null; try { pw = new PrintWriter(new FileWriter(f, true), true);
			 * pw.printf("</updates time=%s>%n", ds); pw.flush(); pw.close(); } catch (final
			 * IOException e) { e.printStackTrace(); } finally { if (pw != null) {
			 * pw.close(); } }
			 */
		}
	}

	/**
	 * @param path
	 *            the path where persistency information is stored
	 * @param fileName
	 *            the name of the file where persistency information is stored
	 *
	 */
	public void disablePersistency(final String path, final TucsonTupleCentreId fileName) {
		this.closePersistencyUpdates();
		this.isPersistent = false;
	}

	@Override
	public void emptyTupleSet() {
		this.tSet.empty();
		if (this.isPersistent) {
			this.writePersistencyUpdate(null, ModType.EMPTY_TUPLES);
		}
	}

	/**
	 * @param path
	 *            the path where to store persistency information
	 * @param fileName
	 *            the name of the file to create for storing persistency information
	 *
	 */
	public void enablePersistency(final String path, final TucsonTupleCentreId fileName) {
		this.isPersistent = true;
		/*
		 * this.pPath = path; this.pFileName = fileName.getName() + "_at_" +
		 * fileName.getNode() + "_at_" + fileName.getPort(); long now =
		 * System.currentTimeMillis(); Date d = new Date(now); final SimpleDateFormat
		 * sdf = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss"); String date =
		 * sdf.format(d); this.pDate = date; final File f = new File(path, "tc_" +
		 * this.pFileName + "_" + date + ".dat"); PrintWriter pw = null;
		 * this.log(">>> Taking persistency snapshot..."); try { pw = new
		 * PrintWriter(new FileWriter(f, true), true);
		 * pw.printf("<snapshot tc=%s time=%s>%n", fileName, date);
		 * pw.printf("\t<tuples>%n"); final Iterator<LogicTuple> it =
		 * this.tSet.getIterator(); while (it.hasNext()) { pw.println("\t\t" +
		 * it.next().toString()); } pw.printf("\t</tuples>%n");
		 * pw.printf("\t<specTuples>%n"); final Iterator<LogicTuple> itS =
		 * this.tSpecSet.getIterator(); while (itS.hasNext()) { pw.println("\t\t" +
		 * itS.next().toString()); } pw.printf("\t</specTuples>%n");
		 * pw.printf("\t<predicates>%n"); final Iterator<LogicTuple> itP =
		 * this.prologPredicates .getIterator(); while (itP.hasNext()) {
		 * pw.println("\t\t" + itP.next().toString()); } pw.printf("\t</predicates>%n");
		 * now = System.currentTimeMillis(); d = new Date(now); date = sdf.format(d);
		 * pw.printf("</snapshot tc=%s time=%s>%n", fileName, date);
		 * pw.printf("<updates time=%s>%n", date); pw.flush(); pw.close();
		 */
		final PersistencyData pData = new PersistencyData(this.tSet, this.tSpecSet, this.prologPredicates, null);
		this.pXML = new PersistencyXML(path, fileName);
		this.pXML.write(pData);
		/*
		 * } catch (final IOException e) { e.printStackTrace(); } finally { if (pw !=
		 * null) { pw.close(); } }
		 */
	}

	@Override
	public void evalReaction(final TriggeredReaction z) {
		this.transaction = true;
		this.tSet.beginTransaction();
		this.tSpecSet.beginTransaction();
		this.wSet.beginTransaction();
		this.zSet.beginTransaction();
		this.timeSet.beginTransaction();
		this.temporaryOutputEventList.clear();
		final Term goalList = ((LogicReaction) z.getReaction()).getStructReaction().getTerm(1);
		this.currentReactionEvent = z.getEvent();
		final SolveInfo info = this.core.solve(goalList);
		this.core.solveEnd();
		this.log("reaction evaluation success = " + info.isSuccess());
		if (info.isSuccess()) {
			if (this.vm.hasInspectors()) {
				/* Dradi */
				try {
					Struct sol = (Struct) info.getSolution();
					sol.resolveTerm();
					this.vm.notifyInspectableEvent(new ObservableEventReactionOK(this,
							new TriggeredReaction(z.getEvent(), new LogicReaction(sol))));
				} catch (NoSolutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/* Dradi */
			}
			final int n = this.temporaryOutputEventList.size();
			for (int i = 0; i < n; i++) {
				final InputEvent curr = (alice.tuplecentre.core.InputEvent) this.temporaryOutputEventList.get(i);
				this.log("outgoing link: " + curr);
				this.addPendingQueryEvent(curr);
			}
		} else {
			if (this.vm.hasInspectors()) {
				this.vm.notifyInspectableEvent(new ObservableEventReactionFail(this, z));
			}
		}
		final boolean success = info.isSuccess();
		final boolean specModified = this.tSpecSet.operationsPending();
		this.temporaryOutputEventList.clear();
		this.zSet.endTransaction(success);
		this.wSet.endTransaction(success);
		this.tSet.endTransaction(success);
		this.tSpecSet.endTransaction(success);
		this.timeSet.endTransaction(success);
		this.transaction = false;
		if (specModified) {
			this.setReactionSpecHelper(new RespectSpecification(this.tSpecSet.toString()));
		}
	}

	@Override
	public void fetchTimedReactions(final AbstractEvent ev) {
		if (((RespectOperation) ev.getSimpleTCEvent()).getLogicTupleArgument() != null) {
			try {
				final Term timed = ((RespectOperation) ev.getSimpleTCEvent()).getLogicTupleArgument().toTerm();
				final Struct tev = new Struct("reaction", timed, new alice.tuprolog.Var("G"),
						new alice.tuprolog.Var("R"));
				SolveInfo info = this.trigCore.solve(tev);
				alice.tuprolog.Term guard = null;
				while (info.isSuccess()) {
					guard = info.getVarValue("G");
					this.currentReactionEvent = ev;
					if (this.evalGuard(guard)) {
						final Term reactions = info.getVarValue("R");
						final Struct trigReaction = new Struct("reaction", timed, reactions);
						final TriggeredReaction tr = new TriggeredReaction(ev, new LogicReaction(trigReaction));
						this.timeSet.add(tr);
						this.core.solve("retract(reaction( " + timed + ", (G),(" + reactions + "))) .");
						this.core.solveEnd();
					}
					if (this.trigCore.hasOpenAlternatives()) {
						info = this.trigCore.solveNext();
					} else {
						break;
					}
					this.trigCore.solveEnd();
					this.trigCore.solve("retract(reaction( " + timed + ", (G),(" + info.getVarValue("R") + "))) .");
					this.trigCore.solveEnd();
				}
			} catch (final NoMoreSolutionException e) {
				this.trigCore.solveEnd();
			} catch (final NoSolutionException e) {
				this.trigCore.solveEnd();
			} catch (final MalformedGoalException e) {
				this.notifyException("INTERNAL ERROR: fetchTimedReactions " + ev);
				this.trigCore.solveEnd();
			}
		}
	}

	@Override
	public void fetchTriggeredReactions(final AbstractEvent ev) {
		synchronized (this.semaphore) {
			try {
				this.currentReactionTerm = null;
				if (ev.isInput()) {
					final InputEvent ie = (InputEvent) ev;
					this.log("INVOCATION phase: " + ie);
					final RespectOperation op = (RespectOperation) ev.getSimpleTCEvent();
					if (op.isSpawn()) {
						this.currentReactionTerm = new Struct("spawn", op.getLogicTupleArgument().toTerm());
					} else if (op.isOut()) {
						this.currentReactionTerm = new Struct("out", op.getLogicTupleArgument().toTerm());
					} else if (op.isIn()) {
						this.currentReactionTerm = new Struct("in", op.getLogicTupleArgument().toTerm());
					} else if (op.isRd()) {
						this.currentReactionTerm = new Struct("rd", op.getLogicTupleArgument().toTerm());
					} else if (op.isInp()) {
						this.currentReactionTerm = new Struct("inp", op.getLogicTupleArgument().toTerm());
					} else if (op.isRdp()) {
						this.currentReactionTerm = new Struct("rdp", op.getLogicTupleArgument().toTerm());
					} else if (op.isNo()) {
						this.currentReactionTerm = new Struct("no", op.getLogicTupleArgument().toTerm());
					} else if (op.isNop()) {
						this.currentReactionTerm = new Struct("nop", op.getLogicTupleArgument().toTerm());
					} else if (op.isOutS()) {
						this.currentReactionTerm = new Struct("out_s", op.getLogicTupleArgument().toTerm());
					} else if (op.isRdS()) {
						this.currentReactionTerm = new Struct("rd_s", op.getLogicTupleArgument().toTerm());
					} else if (op.isInS()) {
						this.currentReactionTerm = new Struct("in_s", op.getLogicTupleArgument().toTerm());
					} else if (op.isRdpS()) {
						this.currentReactionTerm = new Struct("rdp_s", op.getLogicTupleArgument().toTerm());
					} else if (op.isInpS()) {
						this.currentReactionTerm = new Struct("inp_s", op.getLogicTupleArgument().toTerm());
					} else if (op.isNoS()) {
						this.currentReactionTerm = new Struct("no_s", op.getLogicTupleArgument().toTerm());
					} else if (op.isNopS()) {
						this.currentReactionTerm = new Struct("nop_s", op.getLogicTupleArgument().toTerm());
					} else if (op.isGetEnv()) {
						this.currentReactionTerm = new Struct("getEnv", op.getLogicTupleArgument().getArg(0).toTerm(),
								op.getLogicTupleArgument().getArg(1).toTerm());
					} else if (op.isSetEnv()) {
						this.currentReactionTerm = new Struct("setEnv", op.getLogicTupleArgument().getArg(0).toTerm(),
								op.getLogicTupleArgument().getArg(1).toTerm());
					} else if (op.isTime()) {
						this.currentReactionTerm = new Struct("time", op.getLogicTupleArgument().toTerm());
					} else if (op.isUrd()) {
						this.currentReactionTerm = new Struct("urd", op.getLogicTupleArgument().toTerm());
					} else if (op.isUno()) {
						this.currentReactionTerm = new Struct("uno", op.getLogicTupleArgument().toTerm());
					} else if (op.isUin()) {
						this.currentReactionTerm = new Struct("uin", op.getLogicTupleArgument().toTerm());
					} else if (op.isUrdp()) {
						this.currentReactionTerm = new Struct("urdp", op.getLogicTupleArgument().toTerm());
					} else if (op.isUnop()) {
						this.currentReactionTerm = new Struct("unop", op.getLogicTupleArgument().toTerm());
					} else if (op.isUinp()) {
						this.currentReactionTerm = new Struct("uinp", op.getLogicTupleArgument().toTerm());
					} else if (op.isOutAll()) {
						this.currentReactionTerm = new Struct("out_all", op.getLogicTupleArgument().toTerm());
					} else if (op.isInAll()) {
						if (op.getLogicTupleListResult() == null) {
							this.currentReactionTerm = new Struct("in_all",
									op.getLogicTupleArgument().toTerm(),
									new Struct());
						} else {
							this.currentReactionTerm = new Struct("in_all",
									op.getLogicTupleArgument().toTerm(),
									RespectVMContext.list2tuple(op.getLogicTupleListResult()));
						}
					} else if (op.isRdAll()) {
						if (op.getLogicTupleListResult() == null) {
							this.currentReactionTerm = new Struct("rd_all",
									op.getLogicTupleArgument().toTerm(),
									new Struct());
						} else {
							this.currentReactionTerm = new Struct("rd_all",
									op.getLogicTupleArgument().toTerm(),
									RespectVMContext.list2tuple(op.getLogicTupleListResult()));
						}
					} else if (op.isNoAll()) {
						if (op.getLogicTupleListResult() == null) {
							this.currentReactionTerm = new Struct("no_all",
									op.getLogicTupleArgument().toTerm(),
									new Struct());
						} else {
							this.currentReactionTerm = new Struct("no_all",
									op.getLogicTupleArgument().toTerm(),
									RespectVMContext.list2tuple(op.getLogicTupleListResult()));
						}
					}
				} else if (ev.isOutput()) {
					final alice.tuplecentre.core.OutputEvent oe = (alice.tuplecentre.core.OutputEvent) ev;
					final RespectOperation op = (RespectOperation) ev.getSimpleTCEvent();
					if (((OutputEvent) ev).isLinking()) {
						this.log("linking event processing: " + oe);
						if (op.isSpawn()) {
							this.currentReactionTerm = new Struct("spawn", op.getLogicTupleArgument().toTerm());
						} else if (op.isOut()) {
							this.currentReactionTerm = new Struct("out", op.getLogicTupleArgument().toTerm());
						} else if (op.isIn()) {
							this.currentReactionTerm = new Struct("in", op.getLogicTupleArgument().toTerm());
						} else if (op.isRd()) {
							this.currentReactionTerm = new Struct("rd", op.getLogicTupleArgument().toTerm());
						} else if (op.isInp()) {
							this.currentReactionTerm = new Struct("inp", op.getLogicTupleArgument().toTerm());
						} else if (op.isRdp()) {
							this.currentReactionTerm = new Struct("rdp", op.getLogicTupleArgument().toTerm());
						} else if (op.isNo()) {
							this.currentReactionTerm = new Struct("no", op.getLogicTupleArgument().toTerm());
						} else if (op.isNop()) {
							this.currentReactionTerm = new Struct("nop", op.getLogicTupleArgument().toTerm());
						} else if (op.isOutS()) {
							this.currentReactionTerm = new Struct("out_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isInS()) {
							this.currentReactionTerm = new Struct("in_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isRdS()) {
							this.currentReactionTerm = new Struct("rd_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isInpS()) {
							this.currentReactionTerm = new Struct("inp_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isRdpS()) {
							this.currentReactionTerm = new Struct("rdp_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isNoS()) {
							this.currentReactionTerm = new Struct("no_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isNopS()) {
							this.currentReactionTerm = new Struct("nop_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isUrd()) {
							this.currentReactionTerm = new Struct("urd", op.getLogicTupleArgument().toTerm());
						} else if (op.isUno()) {
							this.currentReactionTerm = new Struct("uno", op.getLogicTupleArgument().toTerm());
						} else if (op.isUin()) {
							this.currentReactionTerm = new Struct("uin", op.getLogicTupleArgument().toTerm());
						} else if (op.isUrdp()) {
							this.currentReactionTerm = new Struct("urdp", op.getLogicTupleArgument().toTerm());
						} else if (op.isUnop()) {
							this.currentReactionTerm = new Struct("unop", op.getLogicTupleArgument().toTerm());
						} else if (op.isUinp()) {
							this.currentReactionTerm = new Struct("uinp", op.getLogicTupleArgument().toTerm());
						} else if (op.isOutAll()) {
							this.currentReactionTerm = new Struct("out_all", op.getLogicTupleArgument().toTerm());
						} else if (op.isInAll()) {
							if (op.getLogicTupleListResult() == null) {
								this.currentReactionTerm = new Struct("in_all",
										op.getLogicTupleArgument().toTerm(),
										new Struct());
							} else {
								this.currentReactionTerm = new Struct("in_all",
										op.getLogicTupleArgument().toTerm(),
										RespectVMContext.list2tuple(op.getLogicTupleListResult()));
							}
						} else if (op.isRdAll()) {
							// TODO correct all
							if (op.getLogicTupleListResult() == null) {
								this.currentReactionTerm = new Struct("rd_all",
										op.getLogicTupleArgument().toTerm(),
										new Struct());
							} else {
								this.currentReactionTerm = new Struct("rd_all",
										op.getLogicTupleArgument().toTerm(),
										RespectVMContext.list2tuple(op.getLogicTupleListResult()));
							}
						} else if (op.isNoAll()) {
							if (op.getLogicTupleListResult() == null) {
								this.currentReactionTerm = new Struct("no_all",
										op.getLogicTupleArgument().toTerm(),
										new Struct());
							} else {
								this.currentReactionTerm = new Struct("no_all",
										op.getLogicTupleArgument().toTerm(),
										RespectVMContext.list2tuple(op.getLogicTupleListResult()));
							}
						}
					} else {
						this.log("COMPLETION phase: " + oe);
						if (op.isSpawn()) {
							this.currentReactionTerm = new Struct("spawn", op.getLogicTupleResult().toTerm());
						} else if (op.isOut()) {
							this.currentReactionTerm = new Struct("out", op.getLogicTupleResult().toTerm());
						} else if (op.isIn()) {
							this.currentReactionTerm = new Struct("in", op.getLogicTupleResult().toTerm());
						} else if (op.isRd()) {
							this.currentReactionTerm = new Struct("rd", op.getLogicTupleResult().toTerm());
						} else if (op.isInp()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("inp", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("inp", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isRdp()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("rdp", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("rdp", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isNo()) {
							this.currentReactionTerm = new Struct("no", op.getLogicTupleArgument().toTerm());
						} else if (op.isNop()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("nop", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("nop", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isOutS()) {
							this.currentReactionTerm = new Struct("out_s", op.getLogicTupleResult().toTerm());
						} else if (op.isInS()) {
							this.currentReactionTerm = new Struct("in_s", op.getLogicTupleResult().toTerm());
						} else if (op.isRdS()) {
							this.currentReactionTerm = new Struct("rd_s", op.getLogicTupleResult().toTerm());
						} else if (op.isInpS()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("inp_s", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("inp_s", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isRdpS()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("rdp_s", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("rdp_s", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isNoS()) {
							this.currentReactionTerm = new Struct("no_s", op.getLogicTupleArgument().toTerm());
						} else if (op.isNopS()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("nop_s", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("nop_s", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isUrd()) {
							this.currentReactionTerm = new Struct("urd", op.getLogicTupleArgument().toTerm());
						} else if (op.isUno()) {
							this.currentReactionTerm = new Struct("uno", op.getLogicTupleArgument().toTerm());
						} else if (op.isUin()) {
							this.currentReactionTerm = new Struct("uin", op.getLogicTupleArgument().toTerm());
						} else if (op.isUrdp()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("urdp", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("urdp", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isUnop()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("unop", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("unop", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isUinp()) {
							final LogicTuple result = op.getLogicTupleResult();
							if (result != null) {
								this.currentReactionTerm = new Struct("uinp", result.toTerm());
							} else {
								this.currentReactionTerm = new Struct("uinp", op.getLogicTupleArgument().toTerm());
							}
						} else if (op.isOutAll()) {
							this.currentReactionTerm = new Struct("out_all", op.getLogicTupleArgument().toTerm());
						} else if (op.isInAll()) {
							if (op.getLogicTupleListResult() == null) {
								this.currentReactionTerm = new Struct("in_all",
										op.getLogicTupleArgument().toTerm(),
										new Struct());
							} else {
								this.currentReactionTerm = new Struct("in_all",
										op.getLogicTupleArgument().toTerm(),
										RespectVMContext.list2tuple(op.getLogicTupleListResult()));
							}
						} else if (op.isRdAll()) {
							if (op.getLogicTupleListResult() == null) {
								this.currentReactionTerm = new Struct("rd_all",
										op.getLogicTupleArgument().toTerm(),
										new Struct());
							} else {
								this.currentReactionTerm = new Struct("rd_all",
										op.getLogicTupleArgument().toTerm(),
										RespectVMContext.list2tuple(op.getLogicTupleListResult()));
							}
						} else if (op.isNoAll()) {
							if (op.getLogicTupleListResult() == null) {
								this.currentReactionTerm = new Struct("no_all",
										op.getLogicTupleArgument().toTerm(),
										new Struct());
							} else {
								this.currentReactionTerm = new Struct("no_all",
										op.getLogicTupleArgument().toTerm(),
										RespectVMContext.list2tuple(op.getLogicTupleListResult()));
							}
						} else if (op.isGetEnv()) {
							this.currentReactionTerm = new Struct("getEnv",
									op.getLogicTupleArgument().toTerm(),
									op.getLogicTupleArgument().toTerm());
						} else if (op.isSetEnv()) {
							this.currentReactionTerm = new Struct("setEnv",
									op.getLogicTupleArgument().toTerm(),
									op.getLogicTupleArgument().toTerm());
						}
					}
				} else if (ev.isInternal()) {
					final InternalEvent ev1 = (InternalEvent) ev;
					this.log("internal event processing: " + ev1);
					final InternalOperation rop = ev1.getInternalOperation();
					if (rop.isSpawnR()) {
						this.currentReactionTerm = new Struct("spawn", rop.getArgument().toTerm());
					} else if (rop.isOutR()) {
						this.currentReactionTerm = new Struct("out", rop.getArgument().toTerm());
					} else if (rop.isInR()) {
						this.currentReactionTerm = new Struct("in", rop.getArgument().toTerm());
					} else if (rop.isRdR()) {
						this.currentReactionTerm = new Struct("rd", rop.getArgument().toTerm());
					} else if (rop.isNoR()) {
						this.currentReactionTerm = new Struct("no", rop.getArgument().toTerm());
					} else if (rop.isOutSR()) {
						this.currentReactionTerm = new Struct("out_s", rop.getArgument().toTerm());
					} else if (rop.isInSR()) {
						this.currentReactionTerm = new Struct("in_s", rop.getArgument().toTerm());
					} else if (rop.isRdSR()) {
						this.currentReactionTerm = new Struct("rd_s", rop.getArgument().toTerm());
					} else if (rop.isNoSR()) {
						this.currentReactionTerm = new Struct("no_s", rop.getArgument().toTerm());
					} else if (rop.isGetEnv()) {
						this.currentReactionTerm = new Struct("getEnv", rop.getArgument().getArg(0).toTerm(),
								rop.getArgument().getArg(1).toTerm());
					} else if (rop.isSetEnv()) {
						this.currentReactionTerm = new Struct("setEnv", rop.getArgument().getArg(0).toTerm(),
								rop.getArgument().getArg(1).toTerm());
					} else if (rop.isUrdR()) {
						this.currentReactionTerm = new Struct("urd", rop.getArgument().toTerm());
					} else if (rop.isUnoR()) {
						this.currentReactionTerm = new Struct("uno", rop.getArgument().toTerm());
					} else if (rop.isUinR()) {
						this.currentReactionTerm = new Struct("uin", rop.getArgument().toTerm());
					} else if (rop.isOutAllR()) {
						this.currentReactionTerm = new Struct("out_all", rop.getArgument().toTerm());
					} else if (rop.isInAllR()) {
						this.currentReactionTerm = new Struct("in_all", rop.getArgument().toTerm());
					} else if (rop.isRdAllR()) {
						this.currentReactionTerm = new Struct("rd_all", rop.getArgument().toTerm());
					} else if (rop.isNoAllR()) {
						this.currentReactionTerm = new Struct("no_all", rop.getArgument().toTerm());
					}
				}
				if (this.currentReactionTerm != null) {
					final AbstractMap<Var, Var> v = new LinkedHashMap<Var, Var>();
					this.currentReactionTerm = (Struct) this.currentReactionTerm.copyGoal(v, 0);
					final Struct tev = new Struct("reaction", this.currentReactionTerm, new alice.tuprolog.Var("G"),
							new alice.tuprolog.Var("R"));
					SolveInfo info = this.trigCore.solve(tev);
					alice.tuprolog.Term guard = null;
					while (info.isSuccess()) {
						guard = info.getVarValue("G");
						this.currentReactionEvent = ev;
						if (this.evalGuard(guard)) {
							final Struct trigReaction = new Struct("reaction", this.currentReactionTerm,
									info.getVarValue("R"));
							final TriggeredReaction tr = new TriggeredReaction(ev, new LogicReaction(trigReaction));
							this.zSet.add(tr);
							this.log("triggered reaction = " + tr.getReaction());
						}
						if (this.trigCore.hasOpenAlternatives()) {
							info = this.trigCore.solveNext();
						} else {
							break;
						}
						this.trigCore.solveEnd();
					}
				}
			} catch (final NoSolutionException e) {
				this.trigCore.solveEnd();
			} catch (final NoMoreSolutionException e) {
				this.trigCore.solveEnd();
			}
		}
	}

	/**
	 * 
	 * @return a Java iterator through the list of spatial from reactions possibly
	 *         found
	 */
	public Iterator<Term> findFromReactions() {
		final List<Term> foundReactions = new ArrayList<Term>();
		try {
			final Struct from = new Struct("from", new alice.tuprolog.Var("S"), new alice.tuprolog.Var("P"));
			final Struct fev = new Struct("reaction", from, new alice.tuprolog.Var("G"), new alice.tuprolog.Var("R"));
			SolveInfo info = this.trigCore.solve(fev);
			while (info.isSuccess()) {
				foundReactions.add(from);
				if (this.trigCore.hasOpenAlternatives()) {
					info = this.trigCore.solveNext();
				} else {
					break;
				}
				this.trigCore.solveEnd();
			}
		} catch (final NoMoreSolutionException e) {
			this.notifyException("INTERNAL ERROR: fetchFromReactions ");
			this.trigCore.solveEnd();
		}
		return foundReactions.iterator();
	}

	/**
	 *
	 * @return a Java iterator through the list of timed reactions possibly found
	 */
	public Iterator<Term> findTimeReactions() {
		final List<Term> foundReactions = new ArrayList<Term>();
		try {
			final Struct timed = new Struct("time", new alice.tuprolog.Var("Time"));
			final Struct tev = new Struct("reaction", timed, new alice.tuprolog.Var("G"), new alice.tuprolog.Var("R"));
			// log("theory = " + this.trigCore.getTheory());
			SolveInfo info = this.trigCore.solve(tev);
			while (info.isSuccess()) {
				foundReactions.add(info.getVarValue("Time"));
				if (this.trigCore.hasOpenAlternatives()) {
					info = this.trigCore.solveNext();
				} else {
					break;
				}
				this.trigCore.solveEnd();
			}
		} catch (final NoMoreSolutionException e) {
			this.notifyException("INTERNAL ERROR: fetchTimedReactions ");
			this.trigCore.solveEnd();
		} catch (final NoSolutionException e) {
			this.notifyException("INTERNAL ERROR: fetchTimedReactions ");
			this.trigCore.solveEnd();
		}
		return foundReactions.iterator();
	}

	/**
	 * 
	 * @return a Java iterator through the list of spatial to reactions possibly
	 *         found
	 */
	public Iterator<Term> findToReactions() {
		final List<Term> foundReactions = new ArrayList<Term>();
		try {
			final Struct to = new Struct("to", new alice.tuprolog.Var("S"), new alice.tuprolog.Var("P"));
			final Struct tev = new Struct("reaction", to, new alice.tuprolog.Var("G"), new alice.tuprolog.Var("R"));
			SolveInfo info = this.trigCore.solve(tev);
			while (info.isSuccess()) {
				foundReactions.add(to);
				if (this.trigCore.hasOpenAlternatives()) {
					info = this.trigCore.solveNext();
				} else {
					break;
				}
				this.trigCore.solveEnd();
			}
		} catch (final NoMoreSolutionException e) {
			this.notifyException("INTERNAL ERROR: fetchToReactions ");
			this.trigCore.solveEnd();
		}
		return foundReactions.iterator();
	}

	@Override
	public List<Tuple> getAllTuples() {
		final List<Tuple> tl = new LinkedList<Tuple>();
		final Iterator<LogicTuple> it = this.tSet.getIterator();
		while (it.hasNext()) {
			tl.add(it.next());
		}
		return tl;
	}

	/**
	 *
	 * @return the event currently under processing
	 */
	public AbstractEvent getCurrentReactionEvent() {
		return this.currentReactionEvent;
	}

	/**
	 *
	 * @return the reaction term currently under processing
	 */
	public Struct getCurrentReactionTerm() {
		return this.currentReactionTerm;
	}

	@Override
	public Iterator<? extends AbstractEvent> getPendingQuerySetIterator() {
		return this.wSet.getIterator();
	}

	/**
	 *
	 * @return the tuProlog engine responsible for matching triggering events with
	 *         event templates
	 */
	public Prolog getPrologCore() {
		return this.matcher;
	}

	/**
	 *
	 * @return a Java iterator through tuProlog predicates used in ReSpecT
	 *         specification
	 */
	public Iterator<? extends Tuple> getPrologPredicatesIterator() {
		return this.prologPredicates.getIterator();
	}

	@Override
	public AbstractBehaviourSpecification getReactionSpec() {
		return this.reactionSpec;
	}

	/**
	 *
	 * @return the ReSpecT VM this storage context is managed by
	 */
	public RespectVM getRespectVM() {
		return this.vm;
	}

	@Override
	public Iterator<LogicTuple> getSpecTupleSetIterator() {
		return this.tSpecSet.getIterator();
	}

	@Override
	public Iterator<? extends TriggeredReaction> getTriggeredReactionSetIterator() {
		return this.zSet.getIterator();
	}

	/**
	 *
	 * @return the list of tuples representing triggered reactions
	 */
	public LogicTuple[] getTRSet() {
		final TriggeredReaction[] trig = this.zSet.toArray();
		final LogicTuple[] tuples = new LogicTuple[trig.length];
		for (int i = 0; i < tuples.length; i++) {
			final Term term = ((LogicReaction) trig[i].getReaction()).getStructReaction().getTerm();
			tuples[i] = new LogicTuple(term);
		}
		return tuples;
	}

	/**
	 *
	 * @param filter
	 *            the tuple template to be used in filtering stored tuples
	 * @return the list of tuples currently stored
	 */
	public LogicTuple[] getTSet(final LogicTuple filter) {
		final LogicTuple[] ltSet = this.tSet.toArray();
		final ArrayList<LogicTuple> supportList = new ArrayList<LogicTuple>();
		if (filter == null) {
			return ltSet;
		}
		for (final LogicTuple tuple : ltSet) {
			if (filter.match(tuple)) {
				supportList.add(tuple);
			}
		}
		return supportList.toArray(new LogicTuple[supportList.size()]);
	}

	@Override
	public Iterator<LogicTuple> getTupleSetIterator() {
		return this.tSet.getIterator();
	}

	/**
	 *
	 * @param filter
	 *            the tuple template to be used in filtering InQ events
	 * @return the list of tuples representing InQ events currently stored
	 */
	public WSetEvent[] getWSet(final LogicTuple filter) {
		final AbstractEvent[] ev = this.wSet.toArray();
		final ArrayList<WSetEvent> events = new ArrayList<WSetEvent>();
		if (filter == null) {
			for (final AbstractEvent e : ev) {
				events.add(new WSetEvent(((RespectOperation) e.getSimpleTCEvent()).toTuple(), e.getSource(),
						e.getTarget()));
			}
			return events.toArray(new WSetEvent[events.size()]);
		}
		final LogicTuple[] tuples = new LogicTuple[this.wSet.size()];
		for (int i = 0; i < tuples.length; i++) {
			tuples[i] = ((RespectOperation) ev[i].getSimpleTCEvent()).toTuple();
		}
		int i = 0;
		for (final LogicTuple tuple : tuples) {
			if (filter.match(tuple)) {
				events.add(new WSetEvent(((RespectOperation) ev[i].getSimpleTCEvent()).toTuple(), ev[i].getSource(),
						ev[i].getTarget()));
			}
			i++;
		}
		return events.toArray(new WSetEvent[events.size()]);
	}

	@Override
	public List<Tuple> inAllTuples(final TupleTemplate t) {
		final List<Tuple> tl = new LinkedList<Tuple>();
		TupleTemplate t2 = t;
		Tuple tuple = this.removeMatchingTuple(t2, true);
		while (tuple != null) {
			if (this.isPersistent) {
				this.writePersistencyUpdate((LogicTuple) tuple, ModType.DEL_TUPLE);
			}
			t2 = t;
			tl.add(tuple);
			tuple = this.removeMatchingTuple(t2, true);
		}
		return tl;
	}

	@Override
	public void linkOperation(final OutputEvent oe) {
		this.log(" ### ### ### OE = " + oe);
		final TupleCentreId target = (TupleCentreId) oe.getTarget();
		try {
			final AbstractTupleCentreOperation op = oe.getSimpleTCEvent();
			op.addListener(new CompletionListener(oe));
			final ILinkContext link = RespectTCContainer.getRespectTCContainer().getLinkContext(target);
			// link.doOperation((TupleCentreId) oe.getSource(), op);
			TupleCentreId source;
			if (oe.getSource() instanceof TucsonTupleCentreId) {
				source = ((TucsonTupleCentreId) oe.getSource()).getInternalTupleCentreId();
			} else {
				source = (TupleCentreId) oe.getSource();
			}
			link.doOperation(source, op);
		} catch (final OperationNotPossibleException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param in
	 *            the environmental input event to notify
	 */
	public void notifyInputEnvEvent(final InputEvent in) {
		this.addEnvInputEvent(in);
		this.vm.notifyNewInputEvent();
	}

	/**
	 *
	 * @param in
	 *            the input event to notify
	 */
	public void notifyInputEvent(final InputEvent in) {
		this.addInputEvent(in);
		this.notifyNewInputEvent();
	}

	/**
	 *
	 */
	public void notifyNewInputEvent() {
		this.vm.notifyNewInputEvent();
	}

	@Override
	public List<Tuple> readAllTuples(final TupleTemplate t) {
		final List<Tuple> tl = new LinkedList<Tuple>();
		TupleTemplate t2 = t;
		Tuple tuple = this.removeMatchingTuple(t2, false);
		while (tuple != null) {
			t2 = t;
			tl.add(tuple);
			tuple = this.removeMatchingTuple(t2, false);
		}
		final List<Tuple> tl2 = tl;
		final Iterator<Tuple> it = tl2.iterator();
		while (it.hasNext()) {
			this.addTuple(it.next(), false);
		}
		return tl;
	}

	@Override
	public Tuple readMatchingSpecTuple(final TupleTemplate t) {
		return this.tSpecSet.readMatchingTuple((alice.logictuple.LogicTuple) t);
	}

	@Override
	public Tuple readMatchingTuple(final TupleTemplate t) {
		return this.tSet.readMatchingTuple((alice.logictuple.LogicTuple) t);
	}

	@Override
	public Tuple readUniformTuple(final TupleTemplate t) {
		List<Tuple> tl = new LinkedList<Tuple>();
		tl = this.readAllTuples(t);
		if (tl == null || tl.isEmpty()) {
			return null;
		}
		final int extracted = new Random().nextInt(tl.size());
		return tl.get(extracted);
	}

	/**
	 * @param path
	 *            the path where persistency information is stored
	 * @param file
	 *            the name of the file where persistency information is stored
	 * @param tcName
	 *            the name of the tuple centre to be recovered
	 *
	 */
	public void recoveryPersistent(final String path, final String file, final TucsonTupleCentreId tcName) {
		// BufferedReader br = null;
		try {
			final File f = new File(path.concat(file));
			List<String> tuples = null;
			List<String> specs = null;
			List<String> predicates = null;
			List<String> updates = null;
			/*
			 * br = new BufferedReader(new FileReader(f)); String line = br.readLine();
			 * final long now = System.currentTimeMillis(); final Date d = new Date(now);
			 * final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd_HH.mm.ss");
			 * final String date = sdf.format(d); final String logFileName =
			 * path.concat(date + ".log"); // read snapshot if (line != null &&
			 * line.startsWith("<snapshot")) { this.log(">>> Snapshot begins!"); line =
			 * br.readLine(); // read tuples if (line != null &&
			 * line.startsWith("\t<tuples>")) { this.log(">>> Tuples begin!"); tuples = new
			 * LinkedList<String>(); line = br.readLine(); while (line != null &&
			 * !line.startsWith("\t</tuples>")) { tuples.add(line.trim()); line =
			 * br.readLine(); } this.log(">>> Tuples end!"); line = br.readLine(); // skip
			 * "\t</tuples>" line } // read specs if (line != null &&
			 * line.startsWith("\t<specTuples>")) { this.log(">>> Specs begin!"); specs =
			 * new LinkedList<String>(); line = br.readLine(); while (line != null &&
			 * !line.startsWith("\t</specTuples>")) { specs.add(line.trim()); line =
			 * br.readLine(); } this.log(">>> Specs end!"); line = br.readLine(); // skip
			 * "\t</specTuples>" line } // read predicates if (line != null &&
			 * line.startsWith("\t<predicates>")) { this.log(">>> Predicates begin!");
			 * predicates = new LinkedList<String>(); line = br.readLine(); while (line !=
			 * null && !line.startsWith("\t</predicates>")) { predicates.add(line.trim());
			 * line = br.readLine(); } this.log(">>> Predicates end!"); line =
			 * br.readLine(); // skip "\t</predicates>" line }
			 * this.log(">>> Snapshot end!"); line = br.readLine(); // skip
			 * "</snapshot ...>" line // read updates while (line != null &&
			 * line.startsWith("<updates")) { this.log(">>> Updates begin!"); updates = new
			 * LinkedList<String>(); line = br.readLine(); while (line != null &&
			 * !line.startsWith("</updates")) { updates.add(line.trim()); line =
			 * br.readLine(); } this.log(">>> Updates end!"); } } br.close();
			 */
			this.pXML = new PersistencyXML(path.concat(file));
			final PersistencyData recoveredData = this.pXML.parse();
			tuples = recoveredData.getTuples();
			specs = recoveredData.getSpecTuples();
			predicates = recoveredData.getPredicates();
			updates = recoveredData.getUpdates();
			// recover tuples
			if (tuples != null && !tuples.isEmpty()) {
				this.log(">>> Recovering tuples...");
				for (final String t : tuples) {
					if (!t.startsWith("is_persistent")) {
						this.addTuple(LogicTuple.parse(t), true);
					}
				}
				this.log(">>> ...tuples recovered!");
			}
			// recover specs
			if (specs != null && !specs.isEmpty()) {
				this.log(">>> Recovering specs...");
				for (final String s : specs) {
					this.addSpecTuple(LogicTuple.parse(s));
				}
				this.log(">>> ...specs recovered!");
			}
			// recover predicates
			if (predicates != null && !predicates.isEmpty()) {
				this.log(">>> Recovering predicates...");
				for (final String p : predicates) {
					this.prologPredicates.add(LogicTuple.parse(p));
				}
				this.log(">>> ...predicates recovered!");
			}
			// recover updates
			if (updates != null && !updates.isEmpty()) {
				this.log(">>> Recovering updates...");
				String[] split = null;
				for (final String p : updates) {
					split = p.trim().split(" ");
					// this.log("split[0] = " + split[0]);
					// if (split.length == 2) {
					// this.log("split[1] = " + split[1]);
					// }
					if ("(+t)".equals(split[0])) {
						if (!split[1].startsWith("is_persistent")) {
							this.addTuple(LogicTuple.parse(split[1]), true);
						}
					} else if ("(-t)".equals(split[0])) {
						if (!split[1].startsWith("is_persistent")) {
							this.removeMatchingTuple(LogicTuple.parse(split[1]), true);
						}
					} else if ("(+s)".equals(split[0])) {
						this.addSpecTuple(LogicTuple.parse(split[1]));
					} else if ("(-s)".equals(split[0])) {
						this.removeMatchingSpecTuple(LogicTuple.parse(split[1]));
					} else if ("(+p)".equals(split[0])) {
						this.prologPredicates.add(LogicTuple.parse(split[1]));
					} else if ("(-p)".equals(split[0])) {
						this.prologPredicates.getMatchingTuple(LogicTuple.parse(split[1]));
					} else if ("(et)".equals(split[0])) {
						this.emptyTupleSet();
					} else if ("(ep)".equals(split[0])) {
						this.prologPredicates.empty();
					} else if ("(es)".equals(split[0])) {
						this.removeReactionSpec();
					}
				}
				this.log(">>> ...updates recovered!");
			}
			if (!f.delete()) {
				this.log(">>> Old persistency file could NOT be deleted!");
			}
			this.enablePersistency(path, tcName);
		} catch (final InvalidLogicTupleException e) {
			e.printStackTrace();
		}
		/*
		 * catch (final FileNotFoundException e) { e.printStackTrace(); } catch (final
		 * IOException e) { e.printStackTrace(); } finally { if (br != null) { try {
		 * br.close(); } catch (final IOException e) { e.printStackTrace(); } } }
		 */
	}

	@Override
	public Tuple removeMatchingSpecTuple(final TupleTemplate t) {
		final Tuple tuple = this.tSpecSet.getMatchingTuple((LogicTuple) t);
		if (tuple != null) {
			if (this.isPersistent) {
				this.writePersistencyUpdate((LogicTuple) tuple, ModType.DEL_SPEC);
			}
			this.setReactionSpecHelper(new alice.respect.api.RespectSpecification(this.tSpecSet.toString()));
		}
		return tuple;
	}

	@Override
	public Tuple removeMatchingTuple(final TupleTemplate t, final boolean update) {
		final Tuple tuple = this.tSet.getMatchingTuple((LogicTuple) t);
		if (this.isPersistent && update) {
			this.writePersistencyUpdate((LogicTuple) tuple, ModType.DEL_TUPLE);
		}
		return tuple;
	}

	/**
	 * Removes the event related to a specific executed operation
	 *
	 * @param operationId
	 *            identifier of the operation
	 * @return wether the event has been successfully removed or not
	 */
	public boolean removePendingQueryEvent(final long operationId) {
		return this.wSet.removeEventOfOperation(operationId);
	}

	/**
	 * Removes all events of specified agent
	 *
	 * @param id
	 *            the identifier of the agent whose events must be removed
	 */
	@Override
	public void removePendingQueryEventsOf(final alice.tuplecentre.api.AgentId id) {
		this.wSet.removeEventsOf(id);
	}

	/**
	 *
	 */
	public void removeReactionSpec() {
		this.core.clearTheory();
		this.trigCore.clearTheory();
		this.tSpecSet.empty();
		if (this.isPersistent) {
			this.writePersistencyUpdate(null, ModType.EMPTY_SPEC);
			this.writePersistencyUpdate(null, ModType.EMPTY_PRED);
		}
	}

	@Override
	public TriggeredReaction removeTimeTriggeredReaction() {
		if (this.timeSet.isEmpty()) {
			return null;
		}
		return this.timeSet.get();
	}

	@Override
	public TriggeredReaction removeTriggeredReaction() {
		if (this.zSet.isEmpty()) {
			return null;
		}
		return this.zSet.get();
	}

	@Override
	public Tuple removeUniformTuple(final TupleTemplate t) {
		List<Tuple> tl = new LinkedList<Tuple>();
		tl = this.readAllTuples(t);
		if (tl == null || tl.isEmpty()) {
			return null;
		}
		final int extracted = new Random().nextInt(tl.size());
		final Tuple toRemove = tl.get(extracted);
		this.tSet.getMatchingTuple((LogicTuple) toRemove);
		if (this.isPersistent) {
			this.writePersistencyUpdate((LogicTuple) toRemove, ModType.DEL_TUPLE);
		}
		return toRemove;
	}

	/**
	 * resets the virtual machine to boot state
	 */
	@Override
	public final void reset() {
		this.tSet.empty();
		this.wSet.empty();
		this.zSet.empty();
		this.timeSet.empty();
		this.setBootTime();
		this.setPosition();
		this.setDistanceTollerance(RespectVMContext.METERS_DISTANCE_TOLLERANCE);
	}

	@Override
	public void setAllSpecTuples(final List<Tuple> tupleList) {
		this.tSpecSet.empty();
		if (this.isPersistent) {
			this.writePersistencyUpdate(null, ModType.EMPTY_SPEC);
			this.writePersistencyUpdate(null, ModType.EMPTY_PRED);
		}
		for (final Tuple t : tupleList) {
			this.addSpecTuple(t);
			if (this.isPersistent) {
				this.writePersistencyUpdate((LogicTuple) t, ModType.ADD_SPEC);
			}
		}
	}

	@Override
	public void setAllTuples(final List<Tuple> tupleList) {
		this.tSet.empty();
		if (this.isPersistent) {
			this.writePersistencyUpdate(null, ModType.EMPTY_TUPLES);
		}
		for (final Tuple t : tupleList) {
			this.addTuple(t, true);
		}
	}

	@Override
	public boolean setReactionSpec(final AbstractBehaviourSpecification spec) {
		this.isExternalSetSpec = true;
		this.noReactionTh = null;
		this.prologPredicates = new TupleSet();
		if (this.isPersistent) {
			this.writePersistencyUpdate(null, ModType.EMPTY_PRED);
		}
		final Prolog engine = new Prolog();
		try {
			engine.solve("retractall(reaction(X,Y,Z)).");
			engine.solveEnd();
			final Parser parser = new Parser(new LogicTupleOpManager(), spec.toString());
			Term term = parser.nextTerm(true);
			LogicTuple pp;
			while (term != null) {
				engine.solve("assert(" + term + ").");
				if (!term.match(Term.createTerm("reaction(E,G,R)"))) {
					pp = new LogicTuple(term);
					this.prologPredicates.add(pp);
					if (this.isPersistent) {
						this.writePersistencyUpdate(pp, ModType.ADD_PRED);
					}
				}
				term = parser.nextTerm(true);
			}
			engine.solveEnd();
			final Term[] preds = new Term[this.prologPredicates.size()];
			int i = 0;
			for (final LogicTuple p : this.prologPredicates.toArray()) {
				preds[i] = p.toTerm();
				i++;
			}
			this.noReactionTh = new Theory(new Struct(preds));
		} catch (final MalformedGoalException e) {
			e.printStackTrace();
		} catch (final InvalidTheoryException e) {
			e.printStackTrace();
			this.log("clause: " + e.clause + ", l: " + e.line + ", p: " + e.pos);
		}
		final boolean result = this.setReactionSpecHelper(spec);
		if (result) {
			this.tSpecSet.empty();
			if (this.isPersistent) {
				this.writePersistencyUpdate(null, ModType.EMPTY_SPEC);
			}
			try {
				alice.tuprolog.SolveInfo info = this.core.solve("reaction(X,Y,Z).");
				LogicTuple st;
				while (true) {
					final alice.tuprolog.Term solution = info.getSolution();
					st = new LogicTuple(solution);
					this.tSpecSet.add(st);
					if (this.isPersistent) {
						this.writePersistencyUpdate(st, ModType.ADD_SPEC);
					}
					info = this.core.solveNext();
				}
			} catch (final alice.tuprolog.NoMoreSolutionException e) {
				this.log("No more solutions.");
			} catch (final alice.tuprolog.NoSolutionException e) {
				this.log("No solution.");
			} catch (final MalformedGoalException e) {
				e.printStackTrace();
			}
		}
		this.isExternalSetSpec = false;
		return result;
	}

	/**
	 *
	 * @param set
	 *            the list of tuple representing InQ events to overwrite this InQ
	 *            with
	 */
	public void setWSet(final List<LogicTuple> set) {
		this.wSet.empty();
		for (final LogicTuple t : set) {
			final String operation = t.toString();
			final String opKind = operation.substring(0, 2);
			if ("rd".equals(opKind)) {
				final String tupla = operation.substring(3, operation.length() - 1);
				LogicTuple logicTuple = null;
				try {
					logicTuple = LogicTuple.parse(tupla);
					final RespectOperation op = RespectOperation.makeRd(logicTuple, null);
					this.vm.doOperation(null, op);
				} catch (final InvalidLogicTupleException e) {
					e.printStackTrace();
				} catch (final OperationNotPossibleException e) {
					e.printStackTrace();
				}
			} else if ("in".equals(opKind)) {
				final String tupla = operation.substring(3, operation.length() - 1);
				LogicTuple logicTuple = null;
				try {
					logicTuple = LogicTuple.parse(tupla);
					final RespectOperation op = RespectOperation.makeIn(logicTuple, null);
					this.vm.doOperation(null, op);
				} catch (final InvalidLogicTupleException e) {
					e.printStackTrace();
				} catch (final OperationNotPossibleException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean spawnActivity(final Tuple tuple, final IId owner, final IId targetTC) {
		try {
			final ClassLoader cl = Thread.currentThread().getContextClassLoader();
			// final URL[] urls = ((URLClassLoader) cl).getURLs();
			// this.log("Known paths:");
			// for (final URL url : urls) {
			// System.out.println("\t" + url.getFile());
			// }
			final LogicTuple t = (LogicTuple) tuple;
			if (!("exec".equals(t.getName()) || "solve".equals(t.getName()))) {
				this.log("spawn argument must be a tuple with functor name 'exec' or 'solve'");
				return false;
			}
			if (t.getArity() == 2) {
				this.log("Prolog theory expected");
				if (!"solve".equals(t.getName())) {
					this.log("Prolog spawn argument must be a tuple with functor name 'solve'");
					return false;
				}
				final String theoryPath = alice.util.Tools.removeApices(t.getArg(0).toString());
				final Term goal = t.getArg(1).toTerm();
				if (theoryPath.endsWith(".pl")) {
					final Prolog solver = new Prolog();
					final Spawn2PLibrary s2pLib = new Spawn2PLibrary();
					if (owner.isAgent()) {
						final TucsonAgentId aid = new TucsonAgentId(((AgentId) owner).toString());
						this.log("spawnActivity.aid = " + aid);
						s2pLib.setSpawnerId(aid);
					} else {
						final TucsonTupleCentreId tcid = new TucsonTupleCentreId(((TupleCentreId) owner).getName(),
								((TupleCentreId) owner).getNode(), String.valueOf(((TupleCentreId) owner).getPort()));
						this.log("spawnActivity.tcid = " + tcid);
						s2pLib.setSpawnerId(tcid);
					}
					TucsonTupleCentreId target;
					if (targetTC instanceof TucsonTupleCentreId) {
						target = (TucsonTupleCentreId) targetTC;
					} else {
						target = new TucsonTupleCentreId(((TupleCentreId) targetTC).getName(),
								((TupleCentreId) targetTC).getNode(),
								String.valueOf(((TupleCentreId) targetTC).getPort()));
					}
					this.log("spawnActivity.target = " + target);
					s2pLib.setTargetTC(target);
					solver.loadLibrary(s2pLib);
					// theoryPath should be a pathname but it is not now!!
					final InputStream is = cl.getResourceAsStream(theoryPath);
					final Theory toSpawn = new Theory(new BufferedInputStream(is));
					solver.setTheory(toSpawn);
					// final String[] libs = solver.getCurrentLibraries();
					// this.log("Known libs:");
					// for (final String lib : libs) {
					// System.out.println("\t" + lib);
					// }
					new Spawn2PSolver(solver, goal).start();
					return true;
				}
				this.log("Prolog theory file must end with .pl extension");
				return false;
			} else if (t.getArity() == 1) {
				this.log("Java class expected");
				if (!"exec".equals(t.getName())) {
					this.log("Java spawn argument must be a tuple with functor name 'exec'");
					return false;
				}
				final String className = alice.util.Tools.removeApices(t.getArg(0).toString());
				if (className.endsWith(".class")) {
					final Class<?> toSpawn = cl.loadClass(className.substring(0, className.length() - 6));
					if (AbstractSpawnActivity.class.isAssignableFrom(toSpawn)) {
						final AbstractSpawnActivity instance = (AbstractSpawnActivity) toSpawn.newInstance();
						if (owner.isAgent()) {
							final TucsonAgentId aid = new TucsonAgentId(((AgentId) owner).toString());
							this.log("spawnActivity.aid = " + aid);
							instance.setSpawnerId(aid);
						} else {
							final TucsonTupleCentreId tcid = new TucsonTupleCentreId(((TupleCentreId) owner).getName(),
									((TupleCentreId) owner).getNode(),
									String.valueOf(((TupleCentreId) owner).getPort()));
							this.log("spawnActivity.tcid = " + tcid);
							instance.setSpawnerId(tcid);
						}
						TucsonTupleCentreId target;
						if (targetTC instanceof TucsonTupleCentreId) {
							target = (TucsonTupleCentreId) targetTC;
						} else {
							target = new TucsonTupleCentreId(((TupleCentreId) targetTC).getName(),
									((TupleCentreId) targetTC).getNode(),
									String.valueOf(((TupleCentreId) targetTC).getPort()));
						}
						this.log("spawnActivity.target = " + target);
						instance.setTargetTC(target);
						if (instance.checkInstantiation()) {
							new Thread(instance).start();
							return true;
						}
					} else {
						this.log("Java class to spawn must be assignable from SpawnActivity.class");
						return false;
					}
				} else {
					this.log("Java class file must end with .class extension");
					return false;
				}
			} else {
				this.log(
						"Prolog predicate arity must be 1 (Java class name) or 2 (Prolog theory filepath, goal to solve)");
				return false;
			}
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (final InstantiationException e) {
			e.printStackTrace();
			return false;
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (final TucsonInvalidTupleCentreIdException e) {
			e.printStackTrace();
			return false;
		} catch (final TucsonInvalidAgentIdException e) {
			e.printStackTrace();
			return false;
		} catch (final InvalidLibraryException e) {
			e.printStackTrace();
			return false;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		} catch (final InvalidTheoryException e) {
			System.err.println(
					"[RespectVMContext]: InvalidTheoryException @ c: " + e.clause + ", l: " + e.line + ", p: " + e.pos);
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public boolean timeTriggeredReaction() {
		return !this.timeSet.isEmpty();
	}

	@Override
	public boolean triggeredReaction() {
		return !this.zSet.isEmpty();
	}

	@Override
	public void updateSpecAfterTimedReaction(final TriggeredReaction tr) {
		final LogicReaction lr = (LogicReaction) tr.getReaction();
		final Struct rStruct = lr.getStructReaction();
		final Struct rg = new Struct(rStruct.getName(), rStruct.getArg(0), new Var(), rStruct.getArg(1));
		this.removeMatchingSpecTuple(new LogicTuple(rg));
	}

	private boolean evalGuard(final Term g) {
		this.log("guard = " + g);
		final SolveInfo info = this.core.solve(g);
		this.core.solveEnd();
		this.log("evaluation = " + info.isSuccess());
		return info.isSuccess();
	}

	private void log(final String s) {
		System.out.println("....[RespectVMContext (" + ((alice.respect.api.TupleCentreId) this.getId()).getName() + "@"
				+ ((alice.respect.api.TupleCentreId) this.getId()).getNode() + ":"
				+ ((alice.respect.api.TupleCentreId) this.getId()).getPort() + ")]: " + s);
	}

	/**
	 * @param toAdd
	 * @param addition2
	 */
	private void writePersistencyUpdate(final LogicTuple update, final ModType mode) {
		// this.log("update = " + update + ", mode = " + mode);
		this.pXML.writeUpdate(update, mode);
		/*
		 * final File f = new File(this.pPath, "tc_" + this.pFileName + "_" + this.pDate
		 * + ".dat"); PrintWriter pw = null; try { pw = new PrintWriter(new
		 * FileWriter(f, true), true); switch (mode) { case ADD_TUPLE:
		 * pw.println("\t(+t) " + update); break; case ADD_SPEC: pw.println("\t(+s) " +
		 * update); break; case ADD_PRED: pw.println("\t(+p) " + update); break; case
		 * DEL_TUPLE: pw.println("\t(t) " + update); break; case DEL_SPEC:
		 * pw.println("\t(s) " + update); break; case DEL_PRED: pw.println("\t(p) " +
		 * update); break; case EMPTY_TUPLES: pw.println("\t(et)"); break; case
		 * EMPTY_SPEC: pw.println("\t(es)"); break; case EMPTY_PRED:
		 * pw.println("\t(ep)"); break; default: break; } pw.flush(); pw.close(); }
		 * catch (final IOException e) { e.printStackTrace(); } finally { if (pw !=
		 * null) { pw.close(); } }
		 */
	}

	/**
	 * 
	 * @param spec
	 *            the ReSpecT specification to be added to this ReSpecT VM storage
	 *            context
	 * @return wether the ReSpecT specification has been succesfully added or not
	 */
	protected boolean addReactionSpecHelper(final AbstractBehaviourSpecification spec) {
		if (this.transaction) {
			return false;
		}
		try {
			this.timers.clear();
			final Struct co = new Struct(spec.toString());
			if (co.isAtom()) {
				final alice.tuprolog.Theory thspec = new alice.tuprolog.Theory(co.getName());
				this.core.addTheory(thspec);
				this.trigCore.addTheory(thspec);
			} else if (co.isList()) {
				final alice.tuprolog.Theory thspec = new alice.tuprolog.Theory(co);
				this.core.addTheory(thspec);
				this.trigCore.addTheory(thspec);
			} else {
				this.notifyException("Invalid reaction spec:\n" + co);
				return false;
			}
			this.reactionSpec = (RespectSpecification) spec;
			Iterator<Term> it;
			it = this.findTimeReactions();
			while (it.hasNext()) {
				final Term current = it.next();
				final Timer currTimer = new Timer();
				final long timeValue = ((alice.tuprolog.Number) current).longValue();
				final long currLocalTime = this.getCurrentTime();
				long delay;
				if (timeValue > currLocalTime) {
					delay = timeValue - currLocalTime;
				} else {
					delay = 0;
				}
				currTimer.schedule(
						new RespectTimerTask(this,
								RespectOperation.makeTime(new LogicTuple("time", new TupleArgument(current)), null)),
						delay);
			}
			/** SPATIAL EXTENSION - Interfacing with geolocation service **/
			final GeolocationServiceManager geolocationManager = GeolocationServiceManager.getGeolocationManager();
			if (geolocationManager.getServices().size() > 0) {
				final int platform = PlatformUtils.getPlatform();
				final AbstractGeolocationService geoService = GeolocationServiceManager.getGeolocationManager()
						.getAppositeService(platform);
				if (geoService != null) {
					final Iterator<Term> fit = this.findFromReactions();
					final Iterator<Term> tit = this.findToReactions();
					if (fit.hasNext() || tit.hasNext()) {
						if (!geoService.isRunning()) {
							geoService.start();
						}
						geoService.generateSpatialEvents(true);
						while (fit.hasNext()) {
							fit.next();
						}
						while (tit.hasNext()) {
							tit.next();
						}
					} else {
						geoService.generateSpatialEvents(false);
					}
				}
			}
			return true;
		} catch (final alice.tuprolog.InvalidTheoryException ex) {
			this.notifyException("Invalid reaction spec. " + ex.line + " " + ex.pos);
			this.notifyException(spec.toString());
			return false;
		}
	}

	/**
	 * 
	 * @param spec
	 *            the ReSpecT specification to overwrite this ReSpecT VM one with
	 * @return wether the ReSpecT specification has been succesfully overwritten or
	 *         not
	 */
	protected boolean setReactionSpecHelper(final AbstractBehaviourSpecification spec) {
		// log("spec = " + spec);
		if (this.transaction) {
			return false;
		}
		try {
			this.timers.clear();
			final Struct co = new Struct(spec.toString());
			if (co.isAtom()) {
				final alice.tuprolog.Theory thspec = new alice.tuprolog.Theory(co.getName());
				// int i = 0;
				// for (Iterator<? extends Term> iterator =
				// thspec.iterator(this.trigCore); iterator.hasNext();) {
				// Term term = iterator.next();
				// log("thspec term " + i++ + " = " + term);
				// }
				// log("ATOM 1 > " + thspec);
				this.core.setTheory(thspec);
				this.trigCore.setTheory(thspec);
				// i = 0;
				// for (Iterator<? extends Term> iterator =
				// this.trigCore.getTheory().iterator(this.trigCore); iterator
				// .hasNext();) {
				// Term term = iterator.next();
				// log("term " + i++ + " = " + term);
				// }
				// log("ATOM 2 > " + this.trigCore.getTheory());
			} else if (co.isList()) {
				final alice.tuprolog.Theory thspec = new alice.tuprolog.Theory(co);
				// log("LIST > " + thspec);
				this.core.setTheory(thspec);
				this.trigCore.setTheory(thspec);
			} else {
				this.notifyException("Invalid reaction spec:\n" + co);
				return false;
			}
			if (this.noReactionTh != null && !this.isExternalSetSpec) {
				// log("noReactionTh = " + this.noReactionTh);
				this.core.addTheory(this.noReactionTh);
				this.trigCore.addTheory(this.noReactionTh);
			}
			this.reactionSpec = (RespectSpecification) spec;
			final Iterator<Term> it = this.findTimeReactions();
			while (it.hasNext()) {
				final Term current = it.next();
				// log("timed = " + current);
				final Timer currTimer = new Timer();
				final long timeValue = ((alice.tuprolog.Number) current).longValue();
				final long currLocalTime = this.getCurrentTime();
				long delay;
				if (timeValue > currLocalTime) {
					delay = timeValue - currLocalTime;
				} else {
					delay = 0;
				}
				currTimer.schedule(
						new RespectTimerTask(this,
								RespectOperation.makeTime(new LogicTuple("time", new TupleArgument(current)), null)),
						delay);
			}
			/** SPATIAL EXTENSION - Interfacing with geolocation service **/
			final GeolocationServiceManager geolocationManager = GeolocationServiceManager.getGeolocationManager();
			if (geolocationManager.getServices().size() > 0) {
				final int platform = PlatformUtils.getPlatform();
				final AbstractGeolocationService geoService = GeolocationServiceManager.getGeolocationManager()
						.getAppositeService(platform);
				if (geoService != null) {
					final Iterator<Term> fit = this.findFromReactions();
					final Iterator<Term> tit = this.findToReactions();
					if (fit.hasNext() || tit.hasNext()) {
						if (!geoService.isRunning()) {
							geoService.start();
						}
						geoService.generateSpatialEvents(true);
						while (fit.hasNext()) {
							fit.next();
						}
						while (tit.hasNext()) {
							tit.next();
						}
					} else {
						geoService.generateSpatialEvents(false);
					}
				}
			}
			return true;
		} catch (final alice.tuprolog.InvalidTheoryException ex) {
			// FIXME Check correctness
			this.notifyException("<!> Invalid reaction spec: " + ex.line + " " + ex.pos + "<!>");
			return false;
		}
	}
}
