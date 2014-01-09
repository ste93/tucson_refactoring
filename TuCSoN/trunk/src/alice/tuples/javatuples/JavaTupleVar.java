/**
 * JavaTupleVar.java
 */
package alice.tuples.javatuples;

import alice.logictuple.TupleArgument;
import alice.logictuple.Var;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.exceptions.InvalidVarNameException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 09/gen/2014
 * 
 */
public class JavaTupleVar implements IJavaTuple {

    private final TupleArgument ta;

    public JavaTupleVar(final String varName) throws InvalidVarNameException {
        // should begin with a letter or _, then letters or numbers (no numbers
        // alone, no letters after numbers, no _ after anything)
        if (!varName.matches("_?[a-zA-Z]*[0-9]*")) {
            throw new InvalidVarNameException();
        }
        this.ta = new Var(varName.toUpperCase());
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
            return new JavaTupleVar(this.ta.toString());
        } catch (InvalidVarNameException e) {
            // cannot happen
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
        return false;
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJavaTuple#isVar()
     */
    @Override
    public boolean isVar() {
        return true;
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
