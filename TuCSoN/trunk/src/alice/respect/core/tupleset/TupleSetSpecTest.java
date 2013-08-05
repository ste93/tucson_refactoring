package alice.respect.core.tupleset;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;

public class TupleSetSpecTest {

	String[] t = { "reaction(out(hello),guard,body)", "reaction(in(X),guard,body)", "reaction(rd(hello(world)),true,in(a))", "reaction(rdp(a(b)),c,f)",
			"reaction(rd(pluto),event,rd(asd))", "reaction(inp(hello(world)),b,c)", "reaction(no(hello(a),b,m))",
			"reaction(out(pippo(pluto,paperino),guard,body))", "reaction(rdp([1,b,c]),g,b)", "reaction(nop(pippo(pluto,paperino)))",
			"reaction(inp(hello(A)),b,c)", "reaction(rd(pluto(event)),event,out(asd))", };

	@Test
	public void testGetKey() {
		TupleSetSpec ts = new TupleSetSpec();

		LogicTuple lt = null;
		String key1, key2;
		try {
			// Tuple ------------------------------------
			lt = LogicTuple.parse("reaction(out(world(10)), guard, body)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("out/1", key1);
			assertEquals("world/1", key2);

			lt = LogicTuple.parse("reaction(in(X),guard,body)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("in/1", key1);
			assertEquals("VAR", key2);

			lt = LogicTuple.parse("reaction(rd(a),true,in(A))");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("rd/1", key1);
			assertEquals("a/0", key2);

			lt = LogicTuple.parse("reaction(rdp([1,4]), b, c)");
			key1 = ts.getTupleKey1(lt);
			key2 = ts.getTupleKey2(lt);
			assertEquals("rdp/1", key1);
			assertEquals("./2", key2);

		} catch (InvalidLogicTupleException e) {
			fail("Undesiderated exception");
		}

	}

	@Test(expected = InvalidLogicTupleException.class)
	public void testTupleTemplateKeyException() throws InvalidLogicTupleException {
		TupleSetSpec ts = new TupleSetSpec();

		LogicTuple lt = null;
		try {
			lt = LogicTuple.parse("reaction(A, b, c)");
		} catch (InvalidLogicTupleException e) {
			fail("Undesiderated exception");
		}

		ts.getTupleKey1(lt);
	}

	@Test(expected = InvalidLogicTupleException.class)
	public void testTupleKeyException1() throws InvalidLogicTupleException {
		TupleSetSpec ts = new TupleSetSpec();

		LogicTuple lt = null;
		try {
			lt = LogicTuple.parse("tuple");
		} catch (InvalidLogicTupleException e) {
			fail("Undesiderated exception");
		}

		ts.getTupleKey1(lt);
	}

	@Test
	public void testAdd() {
		TupleSetSpec ts = new TupleSetSpec();

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

		TupleSetSpec ts = new TupleSetSpec();

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

		TupleSetSpec ts = new TupleSetSpec();

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
		TupleSetSpec ts = new TupleSetSpec();

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

			txt = "reaction(out(hello),guard,body)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "reaction(rd(hello(world)),true,in(a))";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "reaction(out(X),guard,body)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			String act = actual.toString();
			assertTrue(act.equals("reaction(out(hello),guard,body)") || act.equals("reaction(out(pippo(pluto,paperino),guard,body))"));

			txt = "reaction(out(pippo(A,paperino),guard,body))";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse("reaction(out(pippo(pluto,paperino),guard,body))").toString(), actual.toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Undesiderated exception");
		}

	}

	@Test
	public void testGetMatchingTuple() {
		TupleSetSpec ts = new TupleSetSpec();

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

			txt = "reaction(out(hello),guard,body)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "reaction(rd(hello(world)),true,in(a))";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse(txt).toString(), actual.toString());

			txt = "reaction(out(X),guard,body)";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			String act = actual.toString();
			assertTrue(act.equals("reaction(out(hello),guard,body)") || act.equals("reaction(out(pippo(pluto,paperino),guard,body))"));

			txt = "reaction(out(pippo(A,paperino),guard,body))";
			actual = ts.readMatchingTuple(LogicTuple.parse(txt));
			assertEquals(LogicTuple.parse("reaction(out(pippo(pluto,paperino),guard,body))").toString(), actual.toString());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Undesiderated exception");
		}
	}

	public void log(String s) {
		System.out.println(s);
	}

}
