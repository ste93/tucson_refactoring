/**
 * TestJavaTuples.java
 */
package alice.tuples.javatuples;

import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.api.exceptions.InvalidVarNameException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 09/gen/2014
 * 
 */
public class TestJavaTuples {

    /**
     * @param args
     */
    public static void main(String[] args) {
        IJavaTuple t1;
        try {
            t1 = new JavaTuple("t", new JavaTupleValue("prova1"));
            System.out.println("t1 = " + t1);
            System.out.println("t1.getName() = " + t1.getName());
            System.out.println("t1.getArg(0) = " + t1.getArg(0));
            t1.addArg(new JavaTupleValue(1));
            System.out.println("t1 = " + t1);
            System.out.println("t1.getArg(1) = " + t1.getArg(1));
            t1.addArg(new JavaTuple("tt", new JavaTupleVar("var")));
            System.out.println("t1 = " + t1);
            System.out.println("t1.getArg(2) = " + t1.getArg(2));
            System.out.println("t1.getArg(2).getName() = "
                    + t1.getArg(2).getName());
            System.out.println("t1.getArg(2).getArg(0) = "
                    + t1.getArg(2).getArg(0));
            t1.addArg(new JavaTupleValue("t(prova2)"));
            System.out.println("t1 = " + t1);
            System.out.println("t1.getArg(3) = " + t1.getArg(3));
            t1.addArg(new JavaTuple("t", new JavaTupleVar("_1")));
            System.out.println("t1 = " + t1);
            System.out.println("t1.getArg(4) = " + t1.getArg(4));
        } catch (NonCompositeException e) {
            // cannot happen
        } catch (InvalidTupleException e) {
            // cannot happen
            e.printStackTrace();
        } catch (InvalidVarNameException e) {
            // cannot happen
            e.printStackTrace();
        }
    }

}
