/**
 * JTupleTemplate.java
 */
package alice.tuples.javatuples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public class JTupleTemplate implements Iterable<IJArg>, IJTupleTemplate {

    private static final int AVG_CAP = 5;
    private static final int AVG_CHARS = 1;
    private List<IJArg> args;

    public JTupleTemplate(final IJArg arg) throws InvalidTupleException {
        if (arg != null) {
            this.args = new ArrayList<IJArg>(JTupleTemplate.AVG_CAP);
            this.args.add(arg);
        } else {
            throw new InvalidTupleException();
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tuples.javatuples.IJTupleTemplate#addArg(alice.tuples.javatuples
     * .JVar)
     */
    @Override
    public void addArg(final IJArg arg) throws InvalidTupleException {
        if (arg != null) {
            this.args.add(arg);
        } else {
            throw new InvalidTupleException();
        }
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJTupleTemplate#getArg(int)
     */
    @Override
    public IJArg getArg(final int i) throws InvalidOperationException {
        if (i < this.args.size()) {
            return this.args.get(i);
        }
        throw new InvalidOperationException();
    }

    /*
     * (non-Javadoc)
     * @see alice.tuples.javatuples.IJTupleTemplate#getNArgs()
     */
    @Override
    public int getNArgs() {
        return this.args.size();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<IJArg> iterator() {
        return this.args.iterator();
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

    @Override
    public String toString() {
        final StringBuffer sb =
                new StringBuffer(JTupleTemplate.AVG_CAP
                        * JTupleTemplate.AVG_CHARS);
        sb.append("$javat(");
        for (final IJArg arg : this.args) {
            sb.append(arg.toString());
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(')');
        return sb.toString();
    }

}
