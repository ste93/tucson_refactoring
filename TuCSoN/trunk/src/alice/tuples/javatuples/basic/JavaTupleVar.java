/**
 * JavaTupleVar.java
 */
package alice.tuples.javatuples.basic;

import alice.logictuple.TupleArgument;
import alice.logictuple.Var;
import alice.tuplecentre.api.Tuple;
import alice.tuples.javatuples.basic.JavaTuplesEngine.VarType;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 09/gen/2014
 * 
 */
public class JavaTupleVar implements IJavaTuple {

    private final TupleArgument ta;
    private final VarType type;

    public JavaTupleVar(final VarType t) {
        this.type = t;
        switch (this.type) {
            case ANY:
                this.ta = new Var("_");
                break;
            case DOUBLE:
                this.ta = new Var("double(_)");
                break;
            case FLOAT:
                this.ta = new Var("float(_)");
                break;
            case INT:
                this.ta = new Var("int(_)");
                break;
            case LONG:
                this.ta = new Var("long(_)");
                break;
            case LITERAL:
                this.ta = new Var("literal(_)");
                break;
            default:
                this.ta = new Var("_");
                break;
        }
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

    /**
     * @return the type
     */
    public VarType getType() {
        return this.type;
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
