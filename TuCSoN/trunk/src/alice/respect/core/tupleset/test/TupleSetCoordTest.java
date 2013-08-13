package alice.respect.core.tupleset.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.core.tupleset.TupleSetCoord;

/**
 * 
 * @author Saverio Cicora
 *
 */
public class TupleSetCoordTest {

    private String[] t = { "a", "b", "hello", "world", "pippo(a(b),c)",
            "pluto", "paperino", "foo", "bar", "hello(world)", "hello(a)",
            "pippo(pluto,paperino)", "aaaa(sa,sa,e)", "hello(paperino)",
            "lol(hello)", "world(hello)", "world(hello(a,c))", "world(hello3)",
            "world(hello2)", "a(b)", "pippo(10)", "pippo(10.1)", "varterm(V)" };

    public void log(final String s) {
        System.out.println(s);
    }

    @Test
    public void testAdd() {
        final TupleSetCoord ts = new TupleSetCoord();

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
        final TupleSetCoord ts = new TupleSetCoord();

        LogicTuple lt = null;
        String key1, key2;
        try {
            // Tuple ------------------------------------
            lt = LogicTuple.parse("hello");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/0", key1);
            Assert.assertEquals("", key2);

            lt = LogicTuple.parse("hello(world)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/1", key1);
            Assert.assertEquals("world/0", key2);

            lt = LogicTuple.parse("hello(world(foo))");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/1", key1);
            Assert.assertEquals("world/1", key2);

            lt = LogicTuple.parse("hello(world(foo, bar), b, c)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/3", key1);
            Assert.assertEquals("world/2", key2);

            lt = LogicTuple.parse("hello(0)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/1", key1);
            Assert.assertEquals("0", key2);

            lt = LogicTuple.parse("hello(0.10)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/1", key1);
            Assert.assertEquals("0.1", key2);

            lt = LogicTuple.parse("hello([a,b,c])");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/1", key1);
            Assert.assertEquals("./2", key2);

            lt = LogicTuple.parse("[a(f,g,h),b,c]");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("./2", key1);
            Assert.assertEquals("a/3", key2);

            lt = LogicTuple.parse("hello(A)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/1", key1);
            Assert.assertEquals("VAR", key2);

            lt = LogicTuple.parse("hello(world(A))");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/1", key1);
            Assert.assertEquals("world/1", key2);

            lt = LogicTuple.parse("hello(world(foo, bar), A, c)");
            key1 = ts.getTupleKey1(lt);
            key2 = ts.getTupleKey2(lt);
            Assert.assertEquals("hello/3", key1);
            Assert.assertEquals("world/2", key2);

        } catch (final InvalidLogicTupleException e) {
            Assert.fail("Undesiderated exception");
        }

    }

    @Test
    public void testGetMatchingTuple() {
        final TupleSetCoord ts = new TupleSetCoord();

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

            txt = "a";
            actual = ts.getMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "a(b)";
            actual = ts.getMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "a(A)";
            actual = ts.getMatchingTuple(LogicTuple.parse(txt));
            Assert.assertNull(actual);

            txt = "world(hello(A,c))";
            actual = ts.getMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse("world(hello(a,c))")
                    .toString(), actual.toString());

            txt = "world(hello(a,c))";
            actual = ts.getMatchingTuple(LogicTuple.parse(txt));
            Assert.assertNull(actual);

            txt = "world(A)";
            actual = ts.getMatchingTuple(LogicTuple.parse(txt));
            final String act = actual.toString();
            Assert.assertTrue("world(hello)".equals(act)
                    || "world(hello3)".equals(act)
                    || "world(hello2)".equals(act));

        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("Undesiderated exception");
        }
    }

    @Test
    public void testIsEmpty() {

        final TupleSetCoord ts = new TupleSetCoord();

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
        final TupleSetCoord ts = new TupleSetCoord();

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

            txt = "a";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "a(b)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "a(A)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse("a(b)").toString(),
                    actual.toString());

            txt = "world(hello(a,c))";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "world(hello(A,c))";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse("world(hello(a,c))")
                    .toString(), actual.toString());

            txt = "world(A)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            final String act = actual.toString();
            Assert.assertTrue("world(hello)".equals(act)
                    || "world(hello3)".equals(act)
                    || "world(hello2)".equals(act)
                    || "world(hello(a,c))".equals(act));

            txt = "pippo(10.10)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertEquals(LogicTuple.parse(txt).toString(),
                    actual.toString());

            txt = "varterm(a)";
            actual = ts.readMatchingTuple(LogicTuple.parse(txt));
            Assert.assertTrue("varterm".equals(actual.getName()));
            Assert.assertTrue(actual.getArg(0).isVar());

        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail("Undesiderated exception");
        }

    }

    @Test
    public void testRemove() {

        final TupleSetCoord ts = new TupleSetCoord();

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
        final TupleSetCoord ts = new TupleSetCoord();

        LogicTuple lt = null;
        try {
            lt = LogicTuple.parse("A");
        } catch (final InvalidLogicTupleException e) {
            Assert.fail("Undesiderated exception");
        }

        ts.getTupleKey1(lt);
    }

    @Test(expected = InvalidLogicTupleException.class)
    public void testTupleTemplateKeyException()
            throws InvalidLogicTupleException {
        final TupleSetCoord ts = new TupleSetCoord();

        LogicTuple lt = null;
        try {
            lt = LogicTuple.parse("A");
        } catch (final InvalidLogicTupleException e) {
            Assert.fail("Undesiderated exception");
        }

        ts.getTupleKey1(lt);
    }

}
