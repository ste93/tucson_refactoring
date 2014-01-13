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

    // ANY string is parsed as literal (even with parentheses!) ?
    public JavaTupleValue(final String arg) {
        this.ta = new Value(arg);
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
        return this.ta.isDouble();
    }

    public boolean isFloat() {
        return this.ta.isFloat();
    }

    public boolean isInt() {
        return this.ta.isInt();
    }

    public boolean isLong() {
        return this.ta.isLong();
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
        return this.ta.doubleValue();
    }

    public float toFloat() throws InvalidOperationException {
        return this.ta.floatValue();
    }

    public int toInt() throws InvalidOperationException {
        return this.ta.intValue();
    }

    public long toLong() throws InvalidOperationException {
        return this.ta.longValue();
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
