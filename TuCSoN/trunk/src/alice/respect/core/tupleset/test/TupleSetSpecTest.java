package alice.respect.core.tupleset.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.core.tupleset.TupleSetSpec;

/**
 * 
 * @author Saverio Cicora
 *
 */
public class TupleSetSpecTest {

    private String[] t = { "reaction(out(hello),guard,body)",
            "reaction(in(X),guard,body)",
            "reaction(rd(hello(world)),true,in(a))", "reaction(rdp(a(b)),c,f)",
            "reaction(rd(pluto),event,rd(asd))",
            "reaction(inp(hello(world)),b,c)", "reaction(no(hello(a),b,m))",
            "reaction(out(pippo(pluto,paperino),guard,body))",
            "reaction(rdp([1,b,c]),g,b)",
            "reaction(nop(pippo(pluto,paperino)))",
            "reaction(inp(hello(A)),b,c)",
            "reaction(rd(pluto(event)),event,out(asd))", };

    public void log(final String s) {
        System.out.println(s);
    }

    @Test
    public void testAdd() {
        final TupleSetSpec ts = new TupleSetSpec();

        final List<String> l = Arrays.asList(this.t);
        Collections.shuffle(l);

        LogicTuple lt;

        try {
            for (final String string : l) {
                lt = LogicTuple.parse(string);
                ts.add(lt);
            }

            final LogicTuple[] tuple = ts.toArray();
            final ArrayList<String> actual = new ArrayList<String>();
            for (final LogicTuple element : tuple) {
                actual.add(element.toString());
            }
            Collections.sort(l);
            Collections.sort(actual);

            Assert.assertEquals(l, actual);

        } catch (final Exception e) {
            Assert.fail("Undesiderated exception");
        }

    }

    @Test
    public void testGetKey() {
        final TupleSetSpec ts = new TupleSetSpec();

        LogicTuple lt = null;
        String key1, key2;
        try {
            // Tuple ------------------------------------
            lt = LogicTuple.parse("reaction(out(world(10)), guard, body)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("out/1", key1);
            Assert.assertEquals("world/1", key2);

            lt = LogicTuple.parse("reaction(in(X),guard,body)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("in/1", key1);
            Assert.assertEquals("VAR", key2);

            lt = LogicTuple.parse("reaction(rd(a),true,in(A))");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("rd/1", key1);
            Assert.assertEquals("a/0", key2);

            lt = LogicTuple.parse("reaction(rdp([1,4]), b, c)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("rdp/1", key1);
            Assert.assertEquals("./2", key2);

        } catch (final InvalidLogicTupleException e) {
            Assert.fail("Undesiderated exception");
        }

    }

    @Test
    public void testGetMatchingTuple() {
        final TupleSetSpec ts = new TupleSetSpec();

        Assert.assertTrue(ts.isEmpty());
        Assert.assertTrue(ts.toArray().length == 0);

        final List<String> l = Arrays.asList(this.t);
        Collections.shuffle(l);

        LogicTuple lt;

        try {
            for (final String string : l) {
                lt = LogicTuple.parse(string);
                ts.add(lt);
            }

            LogicTuple actual;
            String txt;

            txt = "reaction(out(hello),guard,body)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "reaction(rd(hello(world)),true,in(a))";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "reaction(out(X),guard,body)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            final String act = actual.toString();
            Assert.assertTrue("reaction(out(hello),guard,body)".equals(act)
                    || "reaction(out(pippo(pluto,paperino),guard,body))"
                            .equals(act));

            txt = "reaction(out(pippo(A,paperino),guard,body))";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(
                    LogicTuple.parse(
                            "reaction(out(pippo(pluto,paperino),guard,body))")
                            .toString(), actual.toString());

        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("Undesiderated exception");
        }
    }

    @Test
    public void testIsEmpty() {

        final TupleSetSpec ts = new TupleSetSpec();

        Assert.assertTrue(ts.isEmpty());
        Assert.assertTrue(ts.toArray().length == 0);

        final List<String> l = Arrays.asList(this.t);
        Collections.shuffle(l);

        LogicTuple lt;

        try {
            for (final String string : l) {
                lt = LogicTuple.parse(string);
                ts.add(lt);
            }

            Assert.assertTrue(!ts.isEmpty());
            Assert.assertTrue(ts.toArray().length == this.t.length);

            ts.empty();
            Assert.assertTrue(ts.isEmpty());
            Assert.assertTrue(ts.toArray().length == 0);

        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("Undesiderated exception");
        }
    }

    @Test
    public void testReadMatchingTuple() {
        final TupleSetSpec ts = new TupleSetSpec();

        Assert.assertTrue(ts.isEmpty());
        Assert.assertTrue(ts.toArray().length == 0);

        final List<String> l = Arrays.asList(this.t);
        Collections.shuffle(l);

        LogicTuple lt;

        try {
            for (final String string : l) {
                lt = LogicTuple.parse(string);
                ts.add(lt);
            }

            LogicTuple actual;
            String txt;

            txt = "reaction(out(hello),guard,body)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "reaction(rd(hello(world)),true,in(a))";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "reaction(out(X),guard,body)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            final String act = actual.toString();
            Assert.assertTrue("reaction(out(hello),guard,body)".equals(act)
                    || "reaction(out(pippo(pluto,paperino),guard,body))"
                            .equals(act));

            txt = "reaction(out(pippo(A,paperino),guard,body))";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(
                    LogicTuple.parse(
                            "reaction(out(pippo(pluto,paperino),guard,body))")
                            .toString(), actual.toString());

        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("Undesiderated exception");
        }

    }

    @Test
    public void testRemove() {

        final TupleSetSpec ts = new TupleSetSpec();

        final List<String> l = Arrays.asList(this.t);
        Collections.shuffle(l);

        LogicTuple lt;

        try {
            for (final String string : l) {
                lt = LogicTuple.parse(string);
                ts.add(lt);
            }

            Collections.shuffle(l);
            final List<String> expected = new ArrayList<String>();
            for (int i = 0; i < 10; i++) {
                final String s = l.get(i);
                expected.add(s);
                lt = LogicTuple.parse(s);
                ts.remove(lt);
            }

            final LogicTuple[] tuple = ts.toArray();
            final ArrayList<String> actual = new ArrayList<String>();
            for (final LogicTuple element : tuple) {
                actual.add(element.toString());
            }
            Collections.sort(expected);
            Collections.sort(actual);

            Assert.assertEquals(expected, actual);

        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("Undesiderated exception");
        }
    }

    @Test(expected = InvalidLogicTupleException.class)
    public void testTupleKeyException1() throws InvalidLogicTupleException {
        final TupleSetSpec ts = new TupleSetSpec();

        LogicTuple lt = null;
        try {
            lt = LogicTuple.parse("tuple");
        } catch (final InvalidLogicTupleException e) {
            Assert.fail("Undesiderated exception");
        }

        ts.getTupleKey1(lt);
    }

    @Test(expected = InvalidLogicTupleException.class)
    public void testTupleTemplateKeyException()
            throws InvalidLogicTupleException {
        final TupleSetSpec ts = new TupleSetSpec();

        LogicTuple lt = null;
        try {
            lt = LogicTuple.parse("reaction(A, b, c)");
        } catch (final InvalidLogicTupleException e) {
            Assert.fail("Undesiderated exception");
        }

        ts.getTupleKey1(lt);
    }

}
