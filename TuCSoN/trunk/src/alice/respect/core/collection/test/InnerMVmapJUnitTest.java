package alice.respect.core.collection.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import alice.respect.core.collection.DoubleKeyMVMap;
import alice.respect.core.collection.MVHashMap;
import alice.respect.core.collection.MVMap;

public class InnerMVmapJUnitTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private MVMap<String, Integer> createEmpty() {
		return new DoubleKeyMVMap<String, String, Integer>().get(null);
	}

	private MVMap<String, Integer> create() {
		MVMap<String, Integer> map = createEmpty();
		map.put("A", 10);
		map.put("A", 11);
		map.put("A", 12);
		map.put("A", 13);
		map.put("A", 14);
		map.put("A", 15);
		map.put("B", 19);
		map.put("C", 190);
		map.put("AA", 119);
		map.put("B", 159);
		map.put("B", 1119);

		return map;
	}

	@Test
	public void testConsistency() {
		MVMap<String, Integer> map = create();

		List<Integer> l = map.get("C");

		l.remove(0);

		assertTrue(l.size() == 0);

	}

	@Test
	public void testConcurrentModification() {

		MVMap<String, Integer> map = create();

		List<Integer> l1 = map.get("A");
		assertTrue(l1.size() == 6);

		List<Integer> subL1 = l1.subList(0, 4);

		subL1.clear();

		map.put("A", 123);

		exception.expect(ConcurrentModificationException.class);
		subL1.isEmpty();

	}

	@Test
	public void testSize() {

		MVMap<String, Integer> map = createEmpty();
		assertTrue(map.isEmpty());

		map.put("A", 10);
		map.put("B", 19);
		map.put("C", 190);
		map.put("AA", 119);
		map.put("B", 159);
		map.put("B", 1119);
		// Expected map size is 6
		assertFalse(map.isEmpty());
		assertTrue(map.size() == 6);
		assertTrue(map.get("A").size() == 1);
		assertTrue(map.get("B").size() == 3);

		map.put("A", 12);
		assertFalse(map.isEmpty());
		assertTrue(map.size() == 7);
		assertTrue(map.get("A").size() == 2);

		map.remove("B", 1119);
		assertFalse(map.isEmpty());
		assertTrue(map.size() == 6);
		assertTrue(map.get("B").size() == 2);

		map.clear();
		assertTrue(map.isEmpty());
		assertTrue(map.size() == 0);

	}

	@Test
	public void testSubListSize() {

		MVMap<String, Integer> map = createEmpty();
		assertTrue(map.isEmpty());

		map.put("A", 100);
		map.put("B", 1900);
		map.put("C", 190);
		map.put("AA", 11945);
		map.put("B", 15911);
		map.put("B", 1119);
		// Expected map size is 6
		assertFalse(map.isEmpty());
		assertTrue(map.size() == 6);

		List<Integer> list = map.get("B");
		list.add(12);
		// list size = 4, map size = 7
		assertFalse(map.isEmpty());
		assertTrue(list.size() == 4);
		assertTrue(map.size() == 7);
		assertTrue(map.get("B").size() == 4);

		list.remove(0);

		assertFalse(map.isEmpty());
		assertTrue(list.size() == 3);
		assertTrue(map.size() == 6);
		assertTrue(map.get("B").size() == 3);

		map.clear();
		assertTrue(list.size() == 0);
		assertTrue(map.isEmpty());
		assertTrue(map.size() == 0);

	}

	@Test
	public void testIsEmpty() {

		MVMap<String, Integer> map = createEmpty();

		assertTrue(map.isEmpty());

		map.put("A", 10);
		map.put("B", 19);
		map.put("C", 190);
		map.put("D", 119);
		map.put("B", 159);
		map.put("B", 1119);

		map.clear();

		assertTrue(map.isEmpty());

	}

	@Test
	public void testContainsKey() {

		MVMap<String, Integer> map = createEmpty();

		map.put("A", 100);
		map.put("B", 1900);
		map.put("C", 190);
		map.put("AA", 11945);
		map.put("B", 15911);
		map.put("B", 1119);

		assertTrue(map.containsKey("A"));
		assertTrue(map.containsKey("AA"));
		assertTrue(map.containsKey("B"));
		assertTrue(map.containsKey("C"));
		assertFalse(map.containsKey("F"));

	}

	@Test
	public void testContainsValue() {
		MVMap<String, Integer> map = createEmpty();
		assertTrue(map.isEmpty());

		map.put("A", 100);
		map.put("B", 1900);
		map.put("C", 190);
		map.put("AA", 11945);
		map.put("B", 15911);
		map.put("B", 1119);

		assertTrue(map.containsValue(190));
		assertTrue(map.containsValue(1900));
		assertTrue(map.containsValue(100));
		assertTrue(map.containsValue(11945));
		assertFalse(map.containsValue(11000));
	}

	@Test
	public void testMapIterator() {
		MVMap<String, Integer> map = createEmpty();

		map.put("A", 1);
		map.put("B", 2);
		map.put("C", 3);
		map.put("D", 4);
		map.put("B", 5);
		map.put("A", 6);
		map.put("A", 7);

		Iterator<Integer> it = map.iterator();

		List<Integer> test = new ArrayList<>();
		test.add(1);
		test.add(2);
		test.add(3);
		test.add(4);
		test.add(5);
		test.add(6);
		test.add(7);
		while (it.hasNext()) {
			assertTrue(test.remove(it.next()));
		}
		assertTrue(test.size() == 0);

		test = new ArrayList<>();
		test.add(1);
		test.add(2);
		test.add(3);
		test.add(4);
		test.add(5);
		test.add(6);
		test.add(7);
		// remove all element of the map from a test list.
		// If the final size of the test list is zero the test is passed
		for (Integer i : map) {
			assertTrue(test.remove(i));
		}
		assertTrue(test.size() == 0);

		it = map.iterator();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();

		assertFalse(it.hasNext());
	}

	@Test
	public void testMapIteratorException1() {
		MVMap<String, Integer> map = createEmpty();

		map.put("A", 1);
		map.put("B", 2);
		map.put("C", 3);
		map.put("D", 4);
		map.put("B", 5);
		map.put("A", 6);
		map.put("A", 7);

		Iterator<Integer> it = map.iterator();

		it.next();
		it.remove();
		it.next();
		it.remove();

		// A double consecutive remove() is not allowed
		exception.expect(IllegalStateException.class);
		it.remove();

	}

	@Test
	public void testMapIteratorException2() {
		MVMap<String, Integer> map = createEmpty();

		map.put("A", 1);
		map.put("B", 2);
		map.put("C", 3);

		Iterator<Integer> it = map.iterator();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();

		exception.expect(NoSuchElementException.class);
		it.next();

	}

	@Test
	public void testListIteratorException1() {
		MVMap<String, Integer> map = createEmpty();

		map.put("A", 1);
		map.put("B", 2);
		map.put("C", 3);
		map.put("D", 4);
		map.put("B", 5);
		map.put("A", 6);
		map.put("A", 7);
		List<Integer> list = map.get("A");

		Iterator<Integer> it = list.iterator();
		it.next();
		it.remove();

		exception.expect(IllegalStateException.class);
		it.remove();

	}

	@Test
	public void testListIteratorException2() {
		MVMap<String, Integer> map = createEmpty();

		map.put("A", 1);
		map.put("B", 2);
		map.put("C", 3);
		map.put("D", 4);
		map.put("B", 5);
		map.put("A", 6);
		map.put("A", 7);
		List<Integer> list = map.get("A");

		Iterator<Integer> it = list.iterator();
		it.next();
		it.next();
		it.next();

		// A double consecutive remove() is not allowed
		exception.expect(NoSuchElementException.class);
		it.next();
	}

	@Test
	public void testListIterator() {

		MVMap<String, Integer> map = createEmpty();

		map.put("A", 1);
		map.put("B", 2);
		map.put("C", 3);
		map.put("D", 4);
		map.put("B", 5);
		map.put("A", 6);
		map.put("A", 7);
		List<Integer> list = map.get("A");

		Iterator<Integer> it = list.iterator();

		assertTrue(it.next() == 1);
		assertTrue(it.next() == 6);
		assertTrue(it.next() == 7);
		assertFalse(it.hasNext());

		// remove all element of the map from a test list.
		// If the final size of the test list is zero the test is passed
		int count = 0;
		for (Integer i : list) {
			count++;
		}
		assertTrue(count == 3);

		it = list.iterator();
		count = 0;
		int size = map.size();

		while (it.hasNext()) {
			it.next();
			it.remove();
			count++;
		}

		assertTrue(map.size() == (size - count));

		list = map.get("B");
		it = list.iterator();
		for (int i = 0; i < 2; i++) {
			it.next();
		}
		assertFalse(it.hasNext());
	}

	private void print(String s) {
		System.out.println(s);
	}
}
