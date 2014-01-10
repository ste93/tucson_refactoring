/**
 * IJavaTuple.java
 */
package alice.tuples.javatuples.advanced;

import alice.tuplecentre.api.TupleTemplate;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 09/gen/2014
 * 
 */
public interface IJavaTuple extends TupleTemplate {

    public void addArg(IJavaTuple t) throws NonCompositeException;

    public IJavaTuple getArg(int i);
    
    public int getArity();

    public String getName() throws NonCompositeException;

    public boolean isComposite();
    
    public boolean isList();

    public boolean isValue();

    public boolean isVar();

}
