/**
 * JTuplesEngine.java
 */
package alice.tuples.javatuples;

import java.util.logging.Level;
import java.util.logging.Logger;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public class JTuplesEngine {

    public static boolean match(final IJTupleTemplate template,
            final IJTuple tuple) {

        return false;

    }

    public static boolean propagate(final IJTupleTemplate template,
            final IJTuple tuple) {

        return false;

    }

    public static IJTuple toJavaTuple(final LogicTuple tuple)
            throws InvalidTupleException {
        return new JTuple(null);
    }

    public static IJTupleTemplate
            toJavaTupleTemplate(final LogicTuple template)
                    throws InvalidTupleException {
        return new JTupleTemplate(null);
    }

    public static LogicTuple toLogicTuple(final IJTuple tuple) {
        final JTuple jt = (JTuple) tuple;
        TupleArgument[] tas = new TupleArgument[jt.getNArgs()];
        int i = 0;
        try {
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
        } catch (final InvalidOperationException e) {
            // cannot happen
            Logger.getLogger("JTuplesEngine").log(Level.FINEST, "wtf");
        }
        return new LogicTuple("javat", tas);
    }

    public static LogicTuple toLogicTuple(final IJTupleTemplate template) {
        final JTupleTemplate jt = (JTupleTemplate) template;
        TupleArgument[] tas = new TupleArgument[jt.getNArgs()];
        int i = 0;
        try {
            for (final IJArg arg : jt) {
                if (arg.isVal()) {
                    final IJVal val = (IJVal) arg;
                    if (val.isDouble()) {
                        tas[i] =
                                new Value("double", new Value(val.toDouble()));
                    } else if (val.isFloat()) {
                        tas[i] = new Value("float", new Value(val.toFloat()));
                    } else if (val.isInt()) {
                        tas[i] = new Value("int", new Value(val.toInt()));
                    } else if (val.isLiteral()) {
                        tas[i] =
                                new Value("literal",
                                        new Value(val.toLiteral()));
                    } else if (val.isLong()) {
                        tas[i] = new Value("long", new Value(val.toLong()));
                    }
                } else if (arg.isVar()) {
                    final IJVar var = (IJVar) arg;
                    switch (var.getType()) {
                        case ANY:
                            tas[i] = new Var(var.getName().toUpperCase());
                            break;
                        case DOUBLE:
                            tas[i] =
                                    new Value("double", new Var(var.getName()
                                            .toUpperCase()));
                            break;
                        case FLOAT:
                            tas[i] =
                                    new Value("float", new Var(var.getName()
                                            .toUpperCase()));
                            break;
                        case INT:
                            tas[i] =
                                    new Value("int", new Var(var.getName()
                                            .toUpperCase()));
                            break;
                        case LITERAL:
                            tas[i] =
                                    new Value("literal", new Var(var.getName()
                                            .toUpperCase()));
                            break;
                        case LONG:
                            tas[i] =
                                    new Value("long", new Var(var.getName()
                                            .toUpperCase()));
                            break;
                        default:
                            // cannot happen
                            Logger.getLogger("JTuplesEngine").log(Level.FINEST,
                                    "wtf");
                            break;
                    }
                }
                i++;
            }
        } catch (final InvalidOperationException e) {
            // cannot happen
            Logger.getLogger("JTuplesEngine").log(Level.FINEST, "wtf");
        }
        return new LogicTuple("javat", tas);
    }

}
