/**
 * TestBasicTuples.java
 */
package alice.tuples.javatuples.basic;

import alice.logictuple.LogicTuple;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 18/feb/2014
 *
 */
public class TestBasicTuples {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        IJavaTuple jt = new JavaTuple(new JavaTupleValue("try"));
        System.out.println("jt = " + jt);
        try {
            jt.addArg(new JavaTupleValue(1));
        } catch (NonCompositeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("jt = " + jt);
        LogicTuple lt = JavaTuplesEngine.toLogicTuple(jt);
        System.out.println("lt = " + lt);
        IJavaTuple jt2 = JavaTuplesEngine.toJavaTuple(lt);
        System.out.println("jt2 = " + jt2);
    }

}
