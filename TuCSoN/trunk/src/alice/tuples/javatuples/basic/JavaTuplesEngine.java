/**
 * JavaTuplesEngine.java
 */
package alice.tuples.javatuples.basic;

import alice.logictuple.LogicMatchingEngine;
import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 09/gen/2014
 * 
 */
public class JavaTuplesEngine {

    public enum VarType {
        ANY, DOUBLE, FLOAT, INT, LITERAL, LONG
    }

    public static boolean match(final IJavaTuple arg1, final IJavaTuple arg2) {
        if (arg1.isComposite() && arg2.isComposite()) {
            if (arg1.getArity() == arg2.getArity()) {
                for (int i = 0; i < arg1.getArity(); i++) {
                    try {
                        if (JavaTuplesEngine.match(arg1.getArg(i),
                                arg2.getArg(i))) {
                            continue;
                        }
                        return false;
                    } catch (final NonCompositeException e) {
                        // cannot happen
                    }
                }
                return true;
            }
            return false;
        } else if (arg1.isComposite() || arg2.isComposite()) {
            return false;
        }
        try {
            return LogicMatchingEngine.match(LogicTuple.parse(arg1.toString()),
                    LogicTuple.parse(arg2.toString()));
        } catch (final InvalidTupleException e) {
            // cannot happen
            return false;
        }
    }

    public static IJavaTuple propagate(final IJavaTuple arg1,
            final IJavaTuple arg2) {

        return null;
    }

    public static IJavaTuple toJavaTuple(final LogicTuple t) {
        String name = null;
        int nArgs;
        try {
            name = t.getName();
            nArgs = t.getArity();
        } catch (final InvalidOperationException e) {
            nArgs = 0;
        }
        if (nArgs == 0) {
            // TODO if name is a number may cause problems
            return new JavaTupleValue(name);
        }
        try {
            final IJavaTuple tuple =
                    new JavaTuple(JavaTuplesEngine.toJavaTuple(t.getArg(0)));
            for (int i = 1; i < nArgs; i++) {
                tuple.addArg(JavaTuplesEngine.toJavaTuple(t.getArg(i)));
            }
            return tuple;
        } catch (final InvalidOperationException e) {
            // cannot happen
        } catch (final NonCompositeException e) {
            // cannot happen
        }
        return null;
    }

    public static LogicTuple toLogicTuple(final IJavaTuple t) {
        try {
            return LogicTuple.parse(t.toString());
        } catch (final InvalidTupleException e) {
            // cannot happen
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param arg
     * @return
     */
    private static IJavaTuple toJavaTuple(final TupleArgument arg) {
        if (arg.isAtomic() || arg.isVar()) {
            if (arg.isVar()) {
                // TODO what about bound vars?
                return new JavaTupleVar(VarType.ANY);
            }
            // non-var case
            try {
                if (arg.isDouble()) {
                    return new JavaTupleValue(arg.doubleValue());
                } else if (arg.isFloat()) {
                    return new JavaTupleValue(arg.floatValue());
                } else if (arg.isInt()) {
                    return new JavaTupleValue(arg.intValue());
                } else if (arg.isLong()) {
                    return new JavaTupleValue(arg.longValue());
                } else {
                    return new JavaTupleValue(arg.getName());
                }
            } catch (final InvalidOperationException e) {
                // cannot happen
            }
        }
        // list case
        if (arg.isList()) {
            // TODO how to translate lists having no lists? Maybe by introducing
            // arrays?
        }
        // compound case
        if (arg.isStruct()) {
            try {
                final int arity = arg.getArity();
                final IJavaTuple tuple =
                        new JavaTuple(JavaTuplesEngine.toJavaTuple(arg
                                .getArg(0)));
                for (int i = 1; i < arity; i++) {
                    tuple.addArg(JavaTuplesEngine.toJavaTuple(arg.getArg(i)));
                }
                return tuple;
            } catch (final InvalidOperationException e) {
                // cannot happen
            } catch (final NonCompositeException e) {
                // cannot happen
            }
        }
        return null;
    }

}
