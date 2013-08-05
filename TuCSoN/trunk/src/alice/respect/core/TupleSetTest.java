package alice.respect.core;

import static org.junit.Assert.*;

import org.junit.Test;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;

public class TupleSetTest {

	@Test
	public void test() {

		TupleSet ts = new TupleSet();

		try {
			ts.add(LogicTuple.parse("a(b, c)"));
			ts.add(LogicTuple.parse("a(b, d)"));
			ts.add(LogicTuple.parse("a(b, e)"));
			ts.add(LogicTuple.parse("a(b, f)"));
			ts.add(LogicTuple.parse("a(b, g)"));
			ts.add(LogicTuple.parse("a(b, d)"));

			assertTrue(ts.size() == 6);

			LogicTuple[] array = ts.toArray();
			
			for (LogicTuple logicTuple : array) {
				System.out.println(logicTuple.toString());
			}
			ts.remove(LogicTuple.parse("a(b, c)"));
			ts.remove(LogicTuple.parse("a(b, d)"));
			ts.remove(LogicTuple.parse("a(b, g)"));
			ts.remove(LogicTuple.parse("a(b, d)"));
			
			System.out.println("");
			System.out.println("");
			array = ts.toArray();
			
			for (LogicTuple logicTuple : array) {
				System.out.println(logicTuple.toString());
			}

		} catch (InvalidLogicTupleException e) {
			e.printStackTrace();
		}

	}

}
