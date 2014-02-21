/**
 * IJVar.java
 */
package alice.tuples.javatuples;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public interface IJVar extends IJArg {

    IJVal bind(IJVal v) throws BindingNullJValException;

    IJVal getBoundVal();

    String getName();

    JArgType getType();

}