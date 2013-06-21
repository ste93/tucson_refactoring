/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.tucson.service.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.parsing.MyOpManager;
import alice.tucson.parsing.TucsonOpParser;
import alice.tucson.service.TucsonCmd;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuprolog.Parser;

/* MODIFIED BY <s.mariani@unibo.it> */

/**
 * Command Line Interpreter TuCSoN agent. Waits for user input, properly parses
 * the issued command, then process it calling the corresponding method on the
 * ACC (gained through constructor). It blocks until TuCSoN reply comes.
 */
public class CLIAgent extends alice.util.Automaton {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private static void busy() {
        System.out.print("[CLI]: ... ");
    }

    private static void error(final String s) {
        System.err.println("[CLI]: Unknown command <" + s + ">");
    }

    private static void input() {
        System.out.print("[CLI]: ?> ");
    }

    private static void log(final String s) {
        System.out.println("[CLI]: " + s);
    }

    private static void prompt(final String s) {
        System.out.println(" -> " + s);
    }

    private final EnhancedACC context;

    private final String node;

    private final int port;

    private final BufferedReader stdin;

    public CLIAgent(final EnhancedACC ctx, final String n, final int p) {
        this.context = ctx;
        this.node = n;
        this.port = p;
        this.stdin = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void boot() {
        CLIAgent.log("CLI agent listening to user...");
        this.become("goalRequest");
    }

    /**
     * Main reasoning cycle
     */
    public void goalRequest() {

        String st = "";
        while (st.equals("")) {
            CLIAgent.input();
            try {
                st = this.stdin.readLine();
            } catch (final IOException ex) {
                System.err.println("[CLI]: " + ex);
                new CLIAgent(this.context, this.node, this.port).boot();
            }
        }

        final TucsonOpParser parser =
                new TucsonOpParser(st, this.node, this.port);
        try {
            parser.parse();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            System.err.println("[CLI]: " + e);
            e.printStackTrace();
        }
        final TucsonCmd cmd = parser.getCmd();
        final TucsonTupleCentreId tid = parser.getTid();

        if ((cmd == null) || (tid == null)) {
            CLIAgent.error(st);
        } else if (cmd.getPrimitive().equals("quit")
                || cmd.getPrimitive().equals("exit")) {
            try {
                CLIAgent.log("Releasing ACC held (if any)...");
                this.context.exit();
            } catch (final TucsonOperationNotPossibleException ex) {
                System.err.println("[CLI]: " + ex);
            } finally {
                CLIAgent.log("I'm done, have a nice day :)");
                this.become("end");
            }
        } else {

            /**
             * Tokenize TuCSoN primitive & involved argument
             */
            final String methodName = cmd.getPrimitive();
            final String tuple = cmd.getArg();

            try {

                /**
                 * Admissible Ordinary primitives
                 */
                // what about timeout? it returns null too...how to discriminate
                // inp/rdp failure?
                if (methodName.equals("spawn")) {
                    final LogicTuple t = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.spawn(tid, t, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success");
                    } else {
                        CLIAgent.prompt("failure");
                    }
                } else if (methodName.equals("out")) {
                    final LogicTuple t = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.out(tid, t, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("in")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.in(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("rd")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.rd(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("inp")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.inp(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("rdp")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.rdp(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("no")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.no(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("nop")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.nop(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("set")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.set(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                } else if (methodName.equals("get")) {
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.get(tid, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                }

                /* MODIFIED BY <s.mariani@unibo.it> */
                else if (methodName.equals("out_all")) {
                    final LogicTuple t = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.out_all(tid, t, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                } else if (methodName.equals("rd_all")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.rd_all(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                } else if (methodName.equals("no_all")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.no_all(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                } else if (methodName.equals("in_all")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.in_all(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                } else if (methodName.equals("urd")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.urd(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("uno")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.uno(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("urdp")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.urdp(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("unop")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.unop(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("uin")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.uin(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("uinp")) {
                    final LogicTuple templ = LogicTuple.parse(tuple);
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.uinp(tid, templ, (Long) null);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                }

                /**
                 * Admissible Specification primitives
                 */

                else if (methodName.equals("out_s")) {
                    final LogicTuple t =
                            new LogicTuple(Parser.parseSingleTerm("reaction("
                                    + tuple + ")", new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context
                                    .out_s(tid, new LogicTuple(t.getArg(0)),
                                            new LogicTuple(t.getArg(1)),
                                            new LogicTuple(t.getArg(2)),
                                            Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("in_s")) {
                    final LogicTuple templ =
                            new LogicTuple(Parser.parseSingleTerm("reaction("
                                    + tuple + ")", new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.in_s(tid,
                                    new LogicTuple(templ.getArg(0)),
                                    new LogicTuple(templ.getArg(1)),
                                    new LogicTuple(templ.getArg(2)),
                                    Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("rd_s")) {
                    final LogicTuple templ =
                            new LogicTuple(Parser.parseSingleTerm("reaction("
                                    + tuple + ")", new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.rd_s(tid,
                                    new LogicTuple(templ.getArg(0)),
                                    new LogicTuple(templ.getArg(1)),
                                    new LogicTuple(templ.getArg(2)),
                                    Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("inp_s")) {
                    final LogicTuple templ =
                            new LogicTuple(Parser.parseSingleTerm("reaction("
                                    + tuple + ")", new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.inp_s(tid,
                                    new LogicTuple(templ.getArg(0)),
                                    new LogicTuple(templ.getArg(1)),
                                    new LogicTuple(templ.getArg(2)),
                                    Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("rdp_s")) {
                    final LogicTuple templ =
                            new LogicTuple(Parser.parseSingleTerm("reaction("
                                    + tuple + ")", new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.rdp_s(tid,
                                    new LogicTuple(templ.getArg(0)),
                                    new LogicTuple(templ.getArg(1)),
                                    new LogicTuple(templ.getArg(2)),
                                    Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("no_s")) {
                    final LogicTuple templ =
                            new LogicTuple(Parser.parseSingleTerm("reaction("
                                    + tuple + ")", new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.no_s(tid,
                                    new LogicTuple(templ.getArg(0)),
                                    new LogicTuple(templ.getArg(1)),
                                    new LogicTuple(templ.getArg(2)),
                                    Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("nop_s")) {
                    final LogicTuple templ =
                            new LogicTuple(Parser.parseSingleTerm("reaction("
                                    + tuple + ")", new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.nop_s(tid,
                                    new LogicTuple(templ.getArg(0)),
                                    new LogicTuple(templ.getArg(1)),
                                    new LogicTuple(templ.getArg(2)),
                                    Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: " + op.getLogicTupleResult());
                    } else {
                        CLIAgent.prompt("failure: " + op.getLogicTupleResult());
                    }
                } else if (methodName.equals("set_s")) {
                    final LogicTuple templ =
                            new LogicTuple(Parser.parseSingleTerm(tuple,
                                    new MyOpManager()));
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.set_s(tid, templ, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                } else if (methodName.equals("get_s")) {
                    CLIAgent.busy();
                    final ITucsonOperation op =
                            this.context.get_s(tid, Long.MAX_VALUE);
                    if (op.isResultSuccess()) {
                        CLIAgent.prompt("success: "
                                + op.getLogicTupleListResult());
                    } else {
                        CLIAgent.prompt("failure: "
                                + op.getLogicTupleListResult());
                    }
                }

                else if (methodName.equals("help") || methodName.equals("man")
                        || methodName.equals("syntax")) {
                    CLIAgent.log("--------------------------------------------------------------------------------");
                    CLIAgent.log("TuCSoN CLI Syntax:");
                    CLIAgent.log("");
                    CLIAgent.log("\t\ttcName@ipAddress:port ? CMD");
                    CLIAgent.log("");
                    CLIAgent.log("where CMD can be:");
                    CLIAgent.log("");
                    CLIAgent.log("\t\tout(Tuple)");
                    CLIAgent.log("\t\tin(TupleTemplate)");
                    CLIAgent.log("\t\trd(TupleTemplate)");
                    CLIAgent.log("\t\tno(TupleTemplate)");
                    CLIAgent.log("\t\tinp(TupleTemplate)");
                    CLIAgent.log("\t\trdp(TupleTemplate)");
                    CLIAgent.log("\t\tnop(TupleTemplate)");
                    CLIAgent.log("\t\tget()");
                    CLIAgent.log("\t\tset([Tuple1, ..., TupleN])");
                    CLIAgent.log("\t\tspawn(exec('Path.To.Java.Class.class')) | spawn(solve('Path/To/Prolog/Theory.pl', Goal))");
                    CLIAgent.log("\t\tin_all(TupleTemplate, TupleList)");
                    CLIAgent.log("\t\trd_all(TupleTemplate, TupleList)");
                    CLIAgent.log("\t\tno_all(TupleTemplate, TupleList)");
                    CLIAgent.log("\t\tuin(TupleTemplate)");
                    CLIAgent.log("\t\turd(TupleTemplate)");
                    CLIAgent.log("\t\tuno(TupleTemplate)");
                    CLIAgent.log("\t\tuinp(TupleTemplate)");
                    CLIAgent.log("\t\turdp(TupleTemplate)");
                    CLIAgent.log("\t\tunop(TupleTemplate)");
                    CLIAgent.log("\t\tout_s(Event,Guard,Reaction)");
                    CLIAgent.log("\t\tin_s(EventTemplate, GuardTemplate, ReactionTemplate)");
                    CLIAgent.log("\t\trd_s(EventTemplate, GuardTemplate, ReactionTemplate)");
                    CLIAgent.log("\t\tinp_s(EventTemplate, GuardTemplate ,ReactionTemplate)");
                    CLIAgent.log("\t\trdp_s(EventTemplate, GuardTemplate, ReactionTemplate)");
                    CLIAgent.log("\t\tno_s(EventTemplate, GuardTemplate, ReactionTemplate)");
                    CLIAgent.log("\t\tnop_s(EventTemplate, GuardTemplate, ReactionTemplate)");
                    CLIAgent.log("\t\tget_s()");
                    CLIAgent.log("\t\tset_s([(Event1,Guard1,Reaction1), ..., (EventN,GuardN,ReactionN)])");
                    CLIAgent.log("--------------------------------------------------------------------------------");
                }

                else if (methodName.equals("o/")) {
                    CLIAgent.prompt("\\o");
                } else if (methodName.equals("\\o")) {
                    CLIAgent.prompt("o/");
                } else if (methodName.equalsIgnoreCase("hi")) {
                    CLIAgent.prompt("Hi there!");
                } else {
                    CLIAgent.error(methodName);
                }

            } catch (final InvalidLogicTupleException ex1) {
                System.err.println("[CLI]: " + ex1);
                ex1.printStackTrace();
            } catch (final TucsonOperationNotPossibleException ex2) {
                System.err.println("[CLI]: " + ex2);
            } catch (final UnreachableNodeException ex3) {
                System.err.println("[CLI]: " + ex3);
            } catch (final OperationTimeOutException ex5) {
                System.err.println("[CLI]: " + ex5);
            } catch (final InvalidTupleOperationException ex6) {
                System.err.println("[CLI]: " + ex6);
            }

        }

        this.become("goalRequest");

    }

}
