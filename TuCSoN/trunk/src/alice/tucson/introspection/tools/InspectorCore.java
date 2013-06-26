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
package alice.tucson.introspection.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import alice.logictuple.LogicTuple;
import alice.respect.api.AgentId;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.introspection.WSetEvent;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.core.TriggeredReaction;

/**
 * 
 * 
 * @author s.mariani@unibo.it
 */
public class InspectorCore extends alice.tucson.introspection.Inspector {

    protected boolean loggingQueries = false;

    protected boolean loggingReactions = false;
    protected boolean loggingTuples = false;
    protected LogicTuple logOpFilter;
    protected String logQueryFilename;
    protected FileWriter logQueryWriter;

    protected String logReactionFilename;
    protected FileWriter logReactionWriter;
    protected String logTupleFilename;

    protected LogicTuple logTupleFilter;
    protected FileWriter logTupleWriter;
    /*
     * Used when logging tuples/operations/reactions.
     */
    private Calendar cal;

    private final Inspector form;

    /**
     * 
     * 
     * @param f
     * @param id_
     * @param tid_
     */
    public InspectorCore(final Inspector f, final TucsonAgentId id_,
            final TucsonTupleCentreId tid_) {
        super(id_, tid_);
        this.form = f;
        this.logTupleFilename = "inspector-tuples.log";
        this.logQueryFilename = "inspector-queries.log";
        this.logReactionFilename = "inspector-reactions.log";
        try {
            this.logTupleWriter = new FileWriter(this.logTupleFilename, true);
            this.logQueryWriter = new FileWriter(this.logQueryFilename, true);
            this.logReactionWriter =
                    new FileWriter(this.logReactionFilename, true);
        } catch (final IOException e) {
            // TODO Properly handle Exception
        }
    }

    public void changeLogQueryFile(final String name) {
        this.logQueryFilename = name;
        if (this.logQueryWriter != null) {
            try {
                this.logQueryWriter.close();
            } catch (final IOException e) {
                // TODO Properly handle Exception
            }
        }
        try {
            this.logQueryWriter = new FileWriter(this.logQueryFilename, true);
        } catch (final IOException e) {
            // TODO Properly handle Exception
        }
    }

    public void changeLogReactionFile(final String name) {
        this.logReactionFilename = name;
        if (this.logReactionWriter != null) {
            try {
                this.logReactionWriter.close();
            } catch (final IOException e) {
                // TODO Properly handle Exception
            }
        }
        try {
            this.logReactionWriter =
                    new FileWriter(this.logReactionFilename, true);
        } catch (final IOException e) {
            // TODO Properly handle Exception
        }
    }

    public void changeLogTupleFile(final String name) {
        this.logTupleFilename = name;
        if (this.logTupleWriter != null) {
            try {
                this.logTupleWriter.close();
            } catch (final IOException e) {
                // TODO Properly handle Exception
            }
        }
        try {
            this.logTupleWriter = new FileWriter(this.logTupleFilename, true);
        } catch (final IOException e) {
            // TODO Properly handle Exception
        }
    }

    @Override
    public void onContextEvent(
            final alice.tucson.introspection.InspectorContextEvent msg) {

        if (msg.tuples != null) {

            final TupleViewer viewer = this.form.getTupleForm();
            final StringBuffer st = new StringBuffer();
            Iterator<? extends Tuple> it = msg.tuples.iterator();
            int n = 0;

            while (it.hasNext()) {
                st.append(it.next().toString()).append('\n');
                n++;
            }
            viewer.setNItems(n);
            viewer.setText(st.toString());

            if (this.loggingTuples) {

                try {

                    this.cal = Calendar.getInstance();
                    st.delete(0, st.length());
                    this.logTupleWriter.write("snapshot(\n" + "localtime(date("
                            + this.cal.get(Calendar.DAY_OF_MONTH) + "-"
                            + this.cal.get(Calendar.MONTH) + "-"
                            + this.cal.get(Calendar.YEAR) + "), time("
                            + this.cal.get(Calendar.HOUR_OF_DAY) + ":"
                            + this.cal.get(Calendar.MINUTE) + ":"
                            + this.cal.get(Calendar.SECOND) + ")"
                            + "),\n\ttuples([\n");

                    it = msg.tuples.iterator();
                    if (this.logTupleFilter == null) {
                        if (it.hasNext()) {
                            st.append("\t\t").append(it.next().toString())
                                    .append(",\n");
                            while (it.hasNext()) {
                                st.append("\t\t").append(it.next().toString())
                                        .append(",\n");
                            }
                            st.substring(0, st.length() - 2);
                        }
                    } else {
                        if (it.hasNext()) {
                            LogicTuple tuple = (LogicTuple) it.next();
                            if (this.logTupleFilter.match(tuple)) {
                                st.append("\t\t").append(tuple.toString())
                                        .append(",\n");
                            }
                            while (it.hasNext()) {
                                tuple = (LogicTuple) it.next();
                                if (this.logTupleFilter.match(tuple)) {
                                    st.append("\t\t").append(tuple.toString())
                                            .append(",\n");
                                }
                            }
                            if (!(st.length() == 0)) {
                                st.substring(0, st.length() - 2);
                            }
                        }
                    }

                    this.logTupleWriter.write(st + "\n\t])\n).\n");
                    this.logTupleWriter.flush();

                } catch (final IOException e) {
                    // TODO Properly handle Exception
                }

            }

        }

        if (msg.wnEvents != null) {

            final EventViewer viewer = this.form.getQueryForm();
            final StringBuffer st = new StringBuffer();
            Iterator<WSetEvent> it = msg.wnEvents.iterator();
            int n = 0;
            WSetEvent ev;

            while (it.hasNext()) {
                ev = it.next();
                st.append(ev.getOp()).append(" from <")
                        .append(((AgentId) ev.getSource()).getLocalName())
                        .append("> to <").append(ev.getTarget()).append(">\n");
                n++;
            }
            viewer.setNItems(n);
            viewer.setText(st.toString());

            if (this.loggingQueries) {
                try {
                    this.cal = Calendar.getInstance();
                    st.delete(0, st.length());
                    this.logQueryWriter.write("snapshot(\n" + "localtime(date("
                            + this.cal.get(Calendar.DAY_OF_MONTH) + "-"
                            + this.cal.get(Calendar.MONTH) + "-"
                            + this.cal.get(Calendar.YEAR) + "), time("
                            + this.cal.get(Calendar.HOUR_OF_DAY) + ":"
                            + this.cal.get(Calendar.MINUTE) + ":"
                            + this.cal.get(Calendar.SECOND) + "))"
                            + "),\n\toperations([\n");
                    it = msg.wnEvents.iterator();
                    if (this.logOpFilter == null) {
                        if (it.hasNext()) {
                            ev = it.next();
                            st.append("\t\top(what(")
                                    .append(ev.getOp())
                                    .append("),\n\t\t\twho(")
                                    .append(((AgentId) ev.getSource())
                                            .getLocalName())
                                    .append("),\n\t\t\twhere(")
                                    .append(ev.getTarget())
                                    .append(")\n\t\t),\n");
                            while (it.hasNext()) {
                                ev = it.next();
                                st.append("\t\top(what(")
                                        .append(ev.getOp())
                                        .append("),\n\t\t\twho(")
                                        .append(((AgentId) ev.getSource())
                                                .getLocalName())
                                        .append("),\n\t\t\twhere(")
                                        .append(ev.getTarget())
                                        .append(")\n\t\t),\n");
                            }
                            st.substring(0, st.length() - 2);
                        }
                    } else {
                        if (it.hasNext()) {
                            ev = it.next();
                            LogicTuple tuple = ev.getOp();
                            if (this.logOpFilter.match(tuple)) {
                                st.append("\t\top(what(")
                                        .append(ev.getOp())
                                        .append("),\n\t\t\twho(")
                                        .append(((AgentId) ev.getSource())
                                                .getLocalName())
                                        .append("),\n\t\t\twhere(")
                                        .append(ev.getTarget())
                                        .append(")\n\t\t),\n");
                            }
                            while (it.hasNext()) {
                                ev = it.next();
                                tuple = ev.getOp();
                                if (this.logOpFilter.match(tuple)) {
                                    st.append("\t\top(what(")
                                            .append(ev.getOp())
                                            .append("),\n\t\t\twho(")
                                            .append(((AgentId) ev.getSource())
                                                    .getLocalName())
                                            .append("),\n\t\t\twhere(")
                                            .append(ev.getTarget())
                                            .append(")\n\t\t),\n");
                                }
                            }
                            if (!(st.length() == 0)) {
                                st.substring(0, st.length() - 2);
                            }
                        }
                    }
                    this.logQueryWriter.write(st + "\n\t])\n).\n");
                    this.logQueryWriter.flush();
                } catch (final IOException e) {
                    // TODO Properly handle Exception
                }
            }

        }

        if (msg.reactionOk != null) {

            this.cal = Calendar.getInstance();
            final ReactionViewer viewer = this.form.getReactionForm();
            final TriggeredReaction tr = msg.reactionOk;
            viewer.appendText("reaction < " + tr.getReaction()
                    + " > SUCCEEDED @ " + this.cal.get(Calendar.HOUR_OF_DAY)
                    + ":" + this.cal.get(Calendar.MINUTE) + ":"
                    + this.cal.get(Calendar.SECOND) + ".\n");
            if (this.loggingReactions) {
                try {
                    this.logReactionWriter.write("snapshot(\n"
                            + "\tlocaltime(date("
                            + this.cal.get(Calendar.DAY_OF_MONTH) + "-"
                            + this.cal.get(Calendar.MONTH) + "-"
                            + this.cal.get(Calendar.YEAR) + "), time("
                            + this.cal.get(Calendar.HOUR_OF_DAY) + ":"
                            + this.cal.get(Calendar.MINUTE) + ":"
                            + this.cal.get(Calendar.SECOND) + ")"
                            + "),\n\t\tsucceeded( " + tr.getReaction()
                            + " ).\n");
                    this.logReactionWriter.flush();
                } catch (final IOException e) {
                    // TODO Properly handle Exception
                }
            }

        } else if (msg.reactionFailed != null) {

            this.cal = Calendar.getInstance();
            final ReactionViewer viewer = this.form.getReactionForm();
            final TriggeredReaction tr = msg.reactionFailed;
            viewer.appendText("reaction < " + tr.getReaction() + " > FAILED @ "
                    + this.cal.get(Calendar.HOUR_OF_DAY) + ":"
                    + this.cal.get(Calendar.MINUTE) + ":"
                    + this.cal.get(Calendar.SECOND) + ".\n");
            if (this.loggingReactions) {
                try {
                    this.logReactionWriter.write("snapshot(\n"
                            + "\tlocaltime(date("
                            + this.cal.get(Calendar.DAY_OF_MONTH) + "-"
                            + this.cal.get(Calendar.MONTH) + "-"
                            + this.cal.get(Calendar.YEAR) + "), time("
                            + this.cal.get(Calendar.HOUR_OF_DAY) + ":"
                            + this.cal.get(Calendar.MINUTE) + ":"
                            + this.cal.get(Calendar.SECOND) + ")"
                            + "),\n\t\tfailed( " + tr.getReaction() + " ).\n");
                    this.logReactionWriter.flush();
                } catch (final IOException e) {
                    // TODO Properly handle Exception
                }
            }

        }

    }

}
