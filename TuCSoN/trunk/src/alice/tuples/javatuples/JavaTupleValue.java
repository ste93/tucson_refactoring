/**
 * JavaTupleArgument.java
 */
package alice.tuples.javatuples;

import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.exceptions.InvalidOperationException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 09/gen/2014
 * 
 */
public class JavaTupleValue implements IJavaTuple {

    private final TupleArgument ta;

    public JavaTupleValue(final double arg) {
        this.ta = new Value(arg);
    }

    public JavaTupleValue(final float arg) {
        this.ta = new Value(arg);
    }

    public JavaTupleValue(final int arg) {
        this.ta = new Value(arg);
    }

    public JavaTupleValue(final long arg) {
        this.ta = new Value(arg);
    }

    // ANY string is parsed as literal (even with parentheses!)
    public JavaTupleValue(final String arg) {
        this.ta = new Value(arg.toLowerCase());
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tuples.javatuples.IJavaTuple#addArg(alice.tuples.javatuples.IJavaTuple
     * )
     */
    @Override
    public void addArg(final IJavaTuple t) throws NonCompositeException {
        throw new NonCompositeException();
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#getArg(int)
     */
    @Override
    public IJavaTuple getArg(final int i) {
        try {
            if (this.ta.isInt()) {
                return new JavaTupleValue(this.ta.intValue());
            } else if (this.ta.isFloat()) {
                return new JavaTupleValue(this.ta.floatValue());
            } else if (this.ta.isLong()) {
                return new JavaTupleValue(this.ta.longValue());
            } else if (this.ta.isDouble()) {
                return new JavaTupleValue(this.ta.doubleValue());
            } else {
                return new JavaTupleValue(this.ta.toString());
            }
        } catch (final InvalidOperationException e) {
            // can't happen
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#getName()
     */
    @Override
    public String getName() throws NonCompositeException {
        throw new NonCompositeException();
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#isComposite()
     */
    @Override
    public boolean isComposite() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#isValue()
     */
    @Override
    public boolean isValue() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#isVar()
     */
    @Override
    public boolean isVar() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tuplecentre.api.TupleTemplate#match(alice.tuplecentre.api.Tuple)
     */
    @Override
    public boolean match(final Tuple t) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tuplecentre.api.TupleTemplate#propagate(alice.tuplecentre.api.Tuple
     * )
     */
    @Override
    public boolean propagate(final Tuple t) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.ta.toString();
    }

}
