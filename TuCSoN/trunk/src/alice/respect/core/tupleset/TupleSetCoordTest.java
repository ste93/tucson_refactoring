package alice.respect.core.tupleset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;

public class TupleSetCoordTest {

	String[] t = { "a", "b", "hello", "world", "pippo(a(b),c)", "pluto", "paperino", "foo", "bar", "hello(world)", "hello(a)", "pippo(pluto,paperino)",
			"aaaa(sa,sa,e)", "hello(paperino)", "lol(hello)", "world(hello)", "world(hello(a,c))", "world(hello3)", "world(hello2)", "a(b)", "pippo(10)",
			"pippo(10.1)", "varterm(V)" };

	@Test
	public void testGetKey() {
		TupleSetCoord ts = new TupleSetCoord();

		LogicTuple lt = null;
		String key1, key2;
		try {
			// Tuple ------------------------------------
			lt = LogicTuple.parse("hello");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/0", key1);
			assertEquals("", key2);

			lt = LogicTuple.parse("hello(world)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/1", key1);
			assertEquals("world/0", key2);

			lt = LogicTuple.parse("hello(world(foo))");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/1", key1);
			assertEquals("world/1", key2);

			lt = LogicTuple.parse("hello(world(foo, bar), b, c)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/3", key1);
			assertEquals("world/2", key2);

			lt = LogicTuple.parse("hello(0)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/1", key1);
			assertEquals("0", key2);

			lt = LogicTuple.parse("hello(0.10)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/1", key1);
			assertEquals("0.1", key2);
			
			lt = LogicTuple.parse("hello([a,b,c])");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/1", key1);
			assertEquals("./2", key2);

			lt = LogicTuple.parse("[a(f,g,h),b,c]");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("./2", key1);
			assertEquals("a/3", key2);

			lt = LogicTuple.parse("hello(A)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/1", key1);
			assertEquals("VAR", key2);

			lt = LogicTuple.parse("hello(world(A))");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/1", key1);
			assertEquals("world/1", key2);

			lt = LogicTuple.parse("hello(world(foo, bar), A, c)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("hello/3", key1);
			assertEquals("world/2", key2);

		} catch (InvalidLogicTupleException e) {
			fail("Undesiderated exception");
		}

	}

	@Test(expected = InvalidLogicTupleException.class)
	public void testTupleTemplateKeyException() throws InvalidLogicTupleException {
		TupleSetCoord ts = new TupleSetCoord();

		LogicTuple lt = null;
		try {
			lt = LogicTuple.parse("A");
		} catch (InvalidLogicTupleException e) {
			fail("Undesiderated exception");
		}

		ts.getTupleKey1(lt);
	}

	@Test(expected = InvalidLogicTupleException.class)
	public void testTupleKeyException1() throws InvalidLogicTupleException {
		TupleSetCoord ts = new TupleSetCoord();

		LogicTuple lt = null;
		try {
			lt = LogicTuple.parse("A");
		} catch (InvalidLogicTupleException e) {
			fail("Undesiderated exception");
		}

		ts.getTupleKey1(lt);
	}

	@Test
	public void testAdd() {
		TupleSetCoord ts = new TupleSetCoord();

		List<String> l = Arrays.asList(t);
		Collections.shuffle(l);

		LogicTuple lt;

		try {
			for (String string : l) {
				lt = LogicTuple.parse(string);
				ts.add(lt);
			}

			LogicTuple[] tuple = ts.toArray();
			ArrayList<String> actual = new ArrayList<>();
			for (int i = 0; i < tuple.length; i++) {
				actual.add(tuple[i].toString());
			}
			Collections.sort(l);
			Collections.sort(actual);

			assertEquals(l, actual);

		} catch (Exception e) {
			fail("Undesiderated exception");
		}

	}

	@Test
	public void testRemove() {

		TupleSetCoord ts = new TupleSetCoord();

		List<String> l = Arrays.asList(t);
		Collections.shuffle(l);

		LogicTuple lt;

		try {
			for (String string : l) {
				lt = LogicTuple.parse(string);
				ts.add(lt);
			}

			Collections.shuffle(l);
			List<String> expected = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				String s = l.get(i);
				expected.add(s);
				lt = LogicTuple.parse(s);
				ts.remove(lt);
			}

			LogicTuple[] tuple = ts.toArray();
			ArrayList<String> actual = new ArrayList<>();
			for (int i = 0; i < tuple.length; i++) {
				actual.add(tuple[i].toString());
			}
			Collections.sort(expected);
			Collections.sort(actual);

			assertEquals(expected, actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Undesiderated exception");
		}
	}

	@Test
	public void testIsEmpty() {

		TupleSetCoord ts = new TupleSetCoord();

		assertTrue(ts.isEmpty());
		assertTrue(ts.toArray().length == 0);

		List<String> l = Arrays.asList(t);
		Collections.shuffle(l);

		LogicTuple lt;

		try {
			for (String string : l) {
				lt = LogicTuple.parse(string);
				ts.add(lt);
			}

			assertTrue(!ts.isEmpty());
			assertTrue(ts.toArray().length == t.length);

			ts.empty();
			assertTrue(ts.isEmpty());
			assertTrue(ts.toArray().length == 0);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Undesiderated exception");
		}
	}

	@Test
	public void testReadMatchingTuple() {
		TupleSetCoord ts = new TupleSetCoord();

		assertTrue(ts.isEmpty());
		assertTrue(ts.toArray().length == 0);

		List<String> l = Arrays.asList(t);
		Collections.shuffle(l);

		LogicTuple lt;

		try {
			for (String string : l) {
				lt = LogicTuple.parse(string);
				ts.add(lt);
			}

			LogicTuple actual;
			String txt;

			txt = "a";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "a(b)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "a(A)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse("a(b)").toString(), actual.toString());

			txt = "world(hello(a,c))";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "world(hello(A,c))";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse("world(hello(a,c))").toString(), actual.toString());

			txt = "world(A)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			String act = actual.toString();
			assertTrue(act.equals("world(hello)") || act.equals("world(hello3)") || act.equals("world(hello2)") || act.equals("world(hello(a,c))"));

			txt = "pippo(10.10)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "varterm(a)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertTrue(actual.getName().equals("varterm"));
			assertTrue(actual.getArg(0).isVar());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Undesiderated exception");
		}

	}

	@Test
	public void testGetMatchingTuple() {
		TupleSetCoord ts = new TupleSetCoord();

		assertTrue(ts.isEmpty());
		assertTrue(ts.toArray().length == 0);

		List<String> l = Arrays.asList(t);
		Collections.shuffle(l);

		LogicTuple lt;

		try {
			for (String string : l) {
				lt = LogicTuple.parse(string);
				ts.add(lt);
			}

			LogicTuple actual;
			String txt;

			txt = "a";
			actual = ts.getMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "a(b)";
			actual = ts.getMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "a(A)";
			actual = ts.getMatchingTuple(LogicTuple.parse(txt));
			assertNull(actual);

			txt = "world(hello(A,c))";
			actual = ts.getMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse("world(hello(a,c))").toString(), actual.toString());

			txt = "world(hello(a,c))";
			actual = ts.getMatchingTuple(LogicTuple.parse(txt));
			assertNull(actual);

			txt = "world(A)";
			actual = ts.getMatchingTuple(LogicTuple.parse(txt));
			String act = actual.toString();
			assertTrue(act.equals("world(hello)") || act.equals("world(hello3)") || act.equals("world(hello2)"));

		} catch (Exception e) {
			e.printStackTrace();
			fail("Undesiderated exception");
		}
	}

	public void log(String s) {
		System.out.println(s);
	}

}
