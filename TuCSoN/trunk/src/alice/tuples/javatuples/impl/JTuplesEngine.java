/**
 * JTuplesEngine.java
 */
package alice.tuples.javatuples.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import alice.logictuple.LogicMatchingEngine;
import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuples.javatuples.api.IJArg;
import alice.tuples.javatuples.api.IJTuple;
import alice.tuples.javatuples.api.IJTupleTemplate;
import alice.tuples.javatuples.api.IJVal;
import alice.tuples.javatuples.api.IJVar;
import alice.tuples.javatuples.api.JArgType;
import alice.tuples.javatuples.exceptions.InvalidJValException;
import alice.tuples.javatuples.exceptions.InvalidJVarException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 *
 */
public final class JTuplesEngine {

    /**
     *
     * @param lt
     *            the tuProlog LogicTuple to be checked
     * @return wether the given tuProlog LogicTuple represents a template
     *         (namely, wether it contains any variable)
     * @throws InvalidTupleException
     *             if the given tuProlog LogicTuple is not convertible into a
     *             JTuple (too much expressive)
     */
    public static boolean isTemplate(final LogicTuple lt)
            throws InvalidTupleException {
        if ("javat".equals(lt.getName())) {
            final int a = lt.getArity();
            for (int i = 0; i < a; i++) {
                final TupleArgument ta = lt.getArg(i);
                if (ta.getArity() == 0) {
                    if (ta.isVar()) {
                        return true;
                    }
                    throw new InvalidTupleException();
                } else if (ta.getArity() == 1) {
                    final TupleArgument ta2 = ta.getArg(0);
                    if (ta2.isVar()) {
                        return true;
                    }
                }
            }
            return false;
        }
        throw new InvalidTupleException("Error occurred while converting '"
                + lt.toString() + "' into JTuple");
    }

    /**
     * Tests if the given arguments matches according to tuProlog matching rules
     * for LogicTuples
     *
     * No unification (a la tuProlog) is performed
     *
     * @param template
     *            the JTupleTemplate to match
     * @param tuple
     *            the JTuple to match
     * @return wether the given JTupleTemplate and the given JTuple match
     *         (according to tuProlog matching rules for LogicTuples)
     */
    public static boolean match(final IJTupleTemplate template,
            final IJTuple tuple) {
        return LogicMatchingEngine.match(JTuplesEngine.toLogicTuple(template),
                JTuplesEngine.toLogicTuple(tuple));
    }

    /**
     * Tries to perform unification (a la tuProlog)
     *
     * @param template
     *            the JTupleTemplate to match
     * @param tuple
     *            the JTuple to match
     * @return wether the given JTupleTemplate and the given JTuple match
     *         (according to tuProlog matching rules for LogicTuples)
     */
    public static boolean propagate(final IJTupleTemplate template,
            final IJTuple tuple) {
        return LogicMatchingEngine.propagate(
                JTuplesEngine.toLogicTuple(template),
                JTuplesEngine.toLogicTuple(tuple));
    }

    /**
     *
     * @param tuple
     *            the tuProlog LogicTuple to convert into a JTuple
     * @return the obtained JTuple
     * @throws InvalidTupleException
     *             if the given tuProlog LogicTuple is more expressive w.r.t.
     *             JTuple language, hence, not convertible
     */
    public static IJTuple toJavaTuple(final LogicTuple tuple)
            throws InvalidTupleException {
        TupleArgument ta;
        IJVal[] vals = null;
        IJTuple jt;
        try {
            if ("javat".equals(tuple.getName())) {
                final int a = tuple.getArity();
                vals = new JVal[a];
                for (int i = 0; i < a; i++) {
                    ta = tuple.getArg(i);
                    if (ta.getArity() == 1) {
                        if ("double".equals(ta.getName())) {
                            vals[i] = new JVal(ta.getArg(0).doubleValue());
                        } else if ("float".equals(ta.getName())) {
                            vals[i] = new JVal(ta.getArg(0).floatValue());
                        } else if ("int".equals(ta.getName())) {
                            vals[i] = new JVal(ta.getArg(0).intValue());
                        } else if ("literal".equals(ta.getName())) {
                            vals[i] = new JVal(ta.getArg(0).toString());
                        } else if ("long".equals(ta.getName())) {
                            vals[i] = new JVal(ta.getArg(0).longValue());
                        }
                    } else {
                        throw new InvalidTupleException();
                    }
                }
            } else {
                throw new InvalidTupleException(
                        "Error occurred while converting '" + tuple.toString()
                                + "' into JTuple");
            }
        } catch (final InvalidJValException e) {
            // cannot happen
            Logger.getLogger("JTuplesEngine").log(Level.FINEST,
                    "Error: InvalidJValException");
        }
        jt = new JTuple(vals[0]);
        for (int i = 1; i < vals.length; i++) {
            jt.addArg(vals[i]);
        }
        return jt;
    }

    /**
     *
     * @param template
     *            the tuProlog LogicTuple template to convert into a
     *            JTupleTemplate
     * @return the obtained JTupleTemplate
     * @throws InvalidTupleException
     *             if the given tuProlog LogicTuple template is more expressive
     *             w.r.t. JTupleTemplate language, hence, not convertible
     */
    public static IJTupleTemplate toJavaTupleTemplate(final LogicTuple template)
            throws InvalidTupleException {
        TupleArgument ta;
        TupleArgument ta2;
        List<IJArg> args = null;
        IJTupleTemplate jtt;
        try {
            if ("javat".equals(template.getName())) {
                final int a = template.getArity();
                args = new ArrayList<IJArg>(a);
                for (int i = 0; i < a; i++) {
                    ta = template.getArg(i);
                    if (ta.getArity() == 0) {
                        if (ta.isVar()) {
                            args.add(new JVar(JArgType.ANY));
                        } else {
                            throw new InvalidTupleException();
                        }
                    } else if (ta.getArity() == 1) {
                        ta2 = ta.getArg(0);
                        if (ta2.isVar()) {
                            if ("double".equals(ta.getName())) {
                                args.add(new JVar(JArgType.DOUBLE));
                            } else if ("float".equals(ta.getName())) {
                                args.add(new JVar(JArgType.FLOAT));
                            } else if ("int".equals(ta.getName())) {
                                args.add(new JVar(JArgType.INT));
                            } else if ("literal".equals(ta.getName())) {
                                args.add(new JVar(JArgType.LITERAL));
                            } else if ("long".equals(ta.getName())) {
                                args.add(new JVar(JArgType.LONG));
                            }
                        } else {
                            if ("double".equals(ta.getName())) {
                                args.add(new JVal(ta2.doubleValue()));
                            } else if ("float".equals(ta.getName())) {
                                args.add(new JVal(ta2.floatValue()));
                            } else if ("int".equals(ta.getName())) {
                                args.add(new JVal(ta2.intValue()));
                            } else if ("literal".equals(ta.getName())) {
                                args.add(new JVal(ta2.toString()));
                            } else if ("long".equals(ta.getName())) {
                                args.add(new JVal(ta2.longValue()));
                            }
                        }
                    } else {
                        throw new InvalidTupleException();
                    }
                }
            } else {
                throw new InvalidTupleException(
                        "Error occurred while converting '"
                                + template.toString() + "' into JTuple");
            }
        } catch (final InvalidJValException e) {
            // cannot happen
            Logger.getLogger("JTuplesEngine").log(Level.FINEST,
                    "Error: InvalidJValException");
        } catch (final InvalidJVarException e) {
            // cannot happen
            Logger.getLogger("JTuplesEngine").log(Level.FINEST,
                    "Error: InvalidJVarException");
        }
        jtt = new JTupleTemplate(args.get(0));
        for (int i = 1; i < args.size(); i++) {
            jtt.addArg(args.get(i));
        }
        return jtt;
    }

    /**
     *
     * @param tuple
     *            the JTuple to convert into a tuProlog LogicTuple
     * @return the obtained tuProlog LogicTuple
     */
    public static LogicTuple toLogicTuple(final IJTuple tuple) {
        final JTuple jt = (JTuple) tuple;
        final TupleArgument[] tas = new TupleArgument[jt.getNArgs()];
        int i = 0;
        for (final IJVal val : jt) {
            if (val.isDouble()) {
                tas[i] = new Value("double", new Value(val.toDouble()));
            } else if (val.isFloat()) {
                tas[i] = new Value("float", new Value(val.toFloat()));
            } else if (val.isInt()) {
                tas[i] = new Value("int", new Value(val.toInt()));
            } else if (val.isLiteral()) {
                tas[i] = new Value("literal", new Value(val.toLiteral()));
            } else if (val.isLong()) {
                tas[i] = new Value("long", new Value(val.toLong()));
            }
            i++;
        }
        return new LogicTuple("javat", tas);
    }

    /**
     *
     * @param template
     *            the JTupleTemplate to convert into a tuProlog LogicTuple
     * @return the obtained tuProlog LogicTuple
     */
    public static LogicTuple toLogicTuple(final IJTupleTemplate template) {
        final JTupleTemplate jt = (JTupleTemplate) template;
        final TupleArgument[] tas = new TupleArgument[jt.getNArgs()];
        int i = 0;
        for (final IJArg arg : jt) {
            if (arg.isVal()) {
                final IJVal val = (IJVal) arg;
                if (val.isDouble()) {
                    tas[i] = new Value("double", new Value(val.toDouble()));
                } else if (val.isFloat()) {
                    tas[i] = new Value("float", new Value(val.toFloat()));
                } else if (val.isInt()) {
                    tas[i] = new Value("int", new Value(val.toInt()));
                } else if (val.isLiteral()) {
                    tas[i] = new Value("literal", new Value(val.toLiteral()));
                } else if (val.isLong()) {
                    tas[i] = new Value("long", new Value(val.toLong()));
                } else {
                    Logger.getLogger("JTuplesEngine").log(Level.FINEST,
                            "Error: Invalid JVal type");
                }
            } else if (arg.isVar()) {
                final IJVar var = (IJVar) arg;
                switch (var.getType()) {
                    case ANY:
                        tas[i] = new Var();
                        break;
                    case DOUBLE:
                        tas[i] = new Value("double", new Var());
                        break;
                    case FLOAT:
                        tas[i] = new Value("float", new Var());
                        break;
                    case INT:
                        tas[i] = new Value("int", new Var());
                        break;
                    case LITERAL:
                        tas[i] = new Value("literal", new Var());
                        break;
                    case LONG:
                        tas[i] = new Value("long", new Var());
                        break;
                    default:
                        // cannot happen
                        Logger.getLogger("JTuplesEngine").log(Level.FINEST,
                                "Error: Invalid JVar type");
                        break;
                }
            }
            i++;
        }
        return new LogicTuple("javat", tas);
    }

    private JTuplesEngine() {
        // to prevent instantiation
    }
}
