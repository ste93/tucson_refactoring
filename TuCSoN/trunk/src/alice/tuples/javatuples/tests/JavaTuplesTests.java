/**
 * JavaTuplesTests.java
 */
package alice.tuples.javatuples.tests;

import java.util.logging.Level;
import java.util.logging.Logger;

import alice.logictuple.LogicTuple;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuples.javatuples.api.IJTuple;
import alice.tuples.javatuples.api.IJTupleTemplate;
import alice.tuples.javatuples.api.JArgType;
import alice.tuples.javatuples.exceptions.InvalidJValException;
import alice.tuples.javatuples.exceptions.InvalidJVarException;
import alice.tuples.javatuples.impl.JTuple;
import alice.tuples.javatuples.impl.JTupleTemplate;
import alice.tuples.javatuples.impl.JTuplesEngine;
import alice.tuples.javatuples.impl.JVal;
import alice.tuples.javatuples.impl.JVar;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 21/feb/2014
 * 
 */
public class JavaTuplesTests {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Logger logger = Logger.getLogger("JavaTuplesTests");
        logger.setLevel(Level.INFO);
        try {
            final IJTuple jt = new JTuple(new JVal("foo"));
            logger.log(Level.INFO, "1) " + jt.toString());
            jt.addArg(new JVal(10));
            logger.log(Level.INFO, "2) " + jt.toString());
            final IJTupleTemplate jtt =
                    new JTupleTemplate(new JVar(JArgType.LITERAL, "l"));
            logger.log(Level.INFO, "3) " + jtt.toString());
            jtt.addArg(new JVar(JArgType.INT, "i"));
            logger.log(Level.INFO, "4) " + jtt.toString());
            final LogicTuple lt = JTuplesEngine.toLogicTuple(jt);
            logger.log(Level.INFO, "5) " + lt.toString());
            final LogicTuple ltt = JTuplesEngine.toLogicTuple(jtt);
            logger.log(Level.INFO, "6) " + ltt.toString());
            if (jtt.match(jt)) {
                logger.log(Level.INFO, "7) " + jtt.toString());
                logger.log(Level.INFO, "7.1) " + jtt.getArg(0).toString());
                logger.log(Level.INFO, "7.2) " + jtt.getArg(1).toString());
                if (jtt.propagate(jt)) {
                    logger.log(Level.INFO, "8) " + jtt.toString());
                    logger.log(Level.INFO, "8.1) " + jtt.getArg(0).toString());
                    logger.log(Level.INFO, "8.2) " + jtt.getArg(1).toString());
                } else {
                    logger.log(Level.INFO, "Can't unify!");
                }
            } else {
                logger.log(Level.INFO, "No match!");
            }
            final IJTuple jt2 = JTuplesEngine.toJavaTuple(lt);
            logger.log(Level.INFO, "9) " + jt2.toString());
            final IJTupleTemplate jtt2 = JTuplesEngine.toJavaTupleTemplate(ltt);
            logger.log(Level.INFO, "10) " + jtt2.toString());
        } catch (final InvalidTupleException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvalidJValException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvalidJVarException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvalidOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
