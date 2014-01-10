/**
 * JavaTuplesEngine.java
 */
package alice.tuples.javatuples.advanced;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.api.exceptions.InvalidVarNameException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 09/gen/2014
 * 
 */
public class JavaTuplesEngine {

    public static LogicTuple toLogicTuple(final IJavaTuple t) {
        try {
            return LogicTuple.parse(t.toString());
        } catch (InvalidTupleException e) {
            // cannot happen
            e.printStackTrace();
            return null;
        }
    }

    public static IJavaTuple toJavaTuple(final LogicTuple t) {
        String name = null;
        int nArgs;
        try {
            name = t.getName();
            nArgs = t.getArity();
        } catch (InvalidOperationException e) {
            nArgs = 0;
        }
        if (nArgs == 0) {
            // TODO if name is a number may cause problems
            return new JavaTupleValue(name);
        }
        try {
            IJavaTuple tuple = new JavaTuple(name, toJavaTuple(t.getArg(0)));
            for (int i = 1; i < nArgs; i++) {
                tuple.addArg(toJavaTuple(t.getArg(i)));
            }
            return tuple;
        } catch (InvalidTupleException e) {
            // cannot happen
        } catch (InvalidOperationException e) {
            // cannot happen
        } catch (NonCompositeException e) {
            // cannot happen
        }
        return null;
    }

    // TODO
    /**
     * @param arg
     * @return
     */
    private static IJavaTuple toJavaTuple(final TupleArgument arg) {
        if (arg.isAtomic() || arg.isVar()) {
            if (arg.isVar()) {
                try {
                    return new JavaTupleVar(arg.getName());
                } catch (InvalidVarNameException e) {
                    // cannot happen
                    return null;
                } catch (InvalidOperationException e) {
                    // cannot happen
                    return null;
                }
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
            } catch (InvalidOperationException e) {
                // cannot happen
            }
        }
        // list case
        if (arg.isList()) {
            TupleArgument[] a;
            try {
                a = arg.toArray();
                IJavaTuple list = new JavaTupleList(toJavaTuple(a[0]));
                for (int i = 1; i < a.length; i++) {
                    list.addArg(toJavaTuple(a[i]));
                }
                return list;
            } catch (InvalidTupleOperationException e) {
                // cannot happen
            } catch (NonCompositeException e) {
                // cannot happen
            }
        }
        // compound case
        if (arg.isStruct()) {
            try {
                int arity = arg.getArity();
                IJavaTuple tuple =
                        new JavaTuple(arg.getName(), toJavaTuple(arg.getArg(0)));
                for (int i = 1; i < arity; i++) {
                    tuple.addArg(toJavaTuple(arg.getArg(i)));
                }
                return tuple;
            } catch (InvalidOperationException e) {
                // cannot happen
            } catch (InvalidTupleException e) {
                // cannot happen
            } catch (NonCompositeException e) {
                // cannot happen
            }
        }
        return null;
    }

}
