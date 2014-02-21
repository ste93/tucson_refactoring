/**
 * JavaTuplesTests.java
 */
package alice.tuples.javatuples;

import java.util.logging.Level;
import java.util.logging.Logger;

import alice.logictuple.LogicTuple;
import alice.tuplecentre.api.exceptions.InvalidTupleException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public class JavaTuplesTests {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            final IJTuple jt = new JTuple(new JVal("foo"));
            Logger.getLogger("JavaTuplesTests").log(Level.INFO, jt.toString());
            jt.addArg(new JVal(10));
            Logger.getLogger("JavaTuplesTests").log(Level.INFO, jt.toString());
            final IJTupleTemplate jtt =
                    new JTupleTemplate(new JVar(JArgType.LITERAL, "l"));
            Logger.getLogger("JavaTuplesTests").log(Level.INFO, jtt.toString());
            jtt.addArg(new JVar(JArgType.INT, "i"));
            Logger.getLogger("JavaTuplesTests").log(Level.INFO, jtt.toString());
            final LogicTuple lt = JTuplesEngine.toLogicTuple(jt);
            Logger.getLogger("JavaTuplesTests").log(Level.INFO, lt.toString());
            final LogicTuple ltt = JTuplesEngine.toLogicTuple(jtt);
            Logger.getLogger("JavaTuplesTests").log(Level.INFO, ltt.toString());
        } catch (final InvalidTupleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvalidJValException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvalidJVarException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
