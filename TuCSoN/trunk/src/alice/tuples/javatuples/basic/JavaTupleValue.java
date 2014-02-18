/**
 * JavaTupleArgument.java
 */
package alice.tuples.javatuples.basic;

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
        this.ta = new Value("double", new Value(arg));
    }

    public JavaTupleValue(final float arg) {
        this.ta = new Value("float", new Value(arg));
    }

    public JavaTupleValue(final int arg) {
        this.ta = new Value("int", new Value(arg));
    }

    public JavaTupleValue(final long arg) {
        this.ta = new Value("long", new Value(arg));
    }

    // ANY string is parsed as literal (even with parentheses!) ?
    public JavaTupleValue(final String arg) {
        this.ta = new Value("literal", new Value(arg));
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
    public IJavaTuple getArg(final int i) throws NonCompositeException {
        throw new NonCompositeException();
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#getArity()
     */
    @Override
    public int getArity() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#isComposite()
     */
    @Override
    public boolean isComposite() {
        return false;
    }

    public boolean isDouble() {
        try {
            return this.ta.getArg(1).isDouble();
        } catch (final InvalidOperationException e) {
            // cannot happen
            return false;
        }
    }

    public boolean isFloat() {
        try {
            return this.ta.getArg(1).isFloat();
        } catch (final InvalidOperationException e) {
            // cannot happen
            return false;
        }
    }

    public boolean isInt() {
        try {
            return this.ta.getArg(1).isInt();
        } catch (final InvalidOperationException e) {
            // cannot happen
            return false;
        }
    }

    public boolean isLong() {
        try {
            return this.ta.getArg(1).isLong();
        } catch (final InvalidOperationException e) {
            // cannot happen
            return false;
        }
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

    public double toDouble() throws InvalidOperationException {
        return this.ta.getArg(1).doubleValue();
    }

    public float toFloat() throws InvalidOperationException {
        return this.ta.getArg(1).floatValue();
    }

    public int toInt() throws InvalidOperationException {
        return this.ta.getArg(1).intValue();
    }

    public String toLiteral() throws InvalidOperationException {
        return this.ta.getArg(1).toString();
    }

    public long toLong() throws InvalidOperationException {
        return this.ta.getArg(1).longValue();
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
