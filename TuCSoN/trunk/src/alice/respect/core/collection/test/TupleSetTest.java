package alice.respect.core.collection.test;

import org.junit.Assert;
import org.junit.Test;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.core.TupleSet;

public class TupleSetTest {

    @Test
    public void test() {

        final TupleSet ts = new TupleSet();

        try {
            ts.add(LogicTuple.parse("a(b, c)"));
            ts.add(LogicTuple.parse("a(b, d)"));
            ts.add(LogicTuple.parse("a(b, e)"));
            ts.add(LogicTuple.parse("a(b, f)"));
            ts.add(LogicTuple.parse("a(b, g)"));
            ts.add(LogicTuple.parse("a(b, d)"));

            Assert.assertTrue(ts.size() == 6);

            LogicTuple[] array = ts.toArray();

            for (final LogicTuple logicTuple : array) {
                System.out.println(logicTuple.toString());
            }
            // ts.remove(LogicTuple.parse("a(b, c)"));
            // ts.remove(LogicTuple.parse("a(b, d)"));
            // ts.remove(LogicTuple.parse("a(b, g)"));
            // ts.remove(LogicTuple.parse("a(b, d)"));

            System.out.println("");
            System.out.println("");
            array = ts.toArray();

            for (final LogicTuple logicTuple : array) {
                System.out.println(logicTuple.toString());
            }

        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
        }

    }

}
