package alice.respect.core.collection.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import alice.respect.core.collection.DoubleKeyMVMap;
import alice.respect.core.collection.MVMap;

public class DoubleKeyMVMapTest {

	@Test
	public void testSize() {
		DoubleKeyMVMap<String, String, String> map;
		map = new DoubleKeyMVMap<>();

		assertTrue(map.size() == 0);
		assertTrue(map.isEmpty());
		map.put("keep", "calm", "keep calm and make test case0");
		assertTrue(map.size() == 1);
		assertFalse(map.isEmpty());
		map.put("keep", "calm", "keep calm and make test case1");
		assertTrue(map.size() == 2);
		assertFalse(map.isEmpty());
		map.put("keep", "calm", "keep calm and make test case2");
		assertTrue(map.size() == 3);
		assertFalse(map.isEmpty());
		map.put("keep", "calm2", "keep calm2 and make test case");
		map.put("keep2", "calm", "keep2 calm and make test case");
		assertTrue(map.size() == 5);
		assertFalse(map.isEmpty());

		map.clear();
		assertTrue(map.size() == 0);
	}

	@Test
	public void testPut() {
		DoubleKeyMVMap<String, String, String> map;
		map = new DoubleKeyMVMap<>();

		boolean result;
		result = map.put("key 1", "key 2", "a b c d");
		assertTrue(result);
		result = map.put("key 1", "key 2", "a b c e");
		assertTrue(result);
		result = map.put("key 1", "key 2", "a b c f");
		assertTrue(result);
		result = map.put("key 1", "key 3", "a b c g");
		assertTrue(result);
		result = map.put(null, "key 3", "a b c d");
		assertTrue(result);
		result = map.put("key 1", null, "a b c d");
		assertTrue(result);
	}

	@Test
	public void testRemove() {
		DoubleKeyMVMap<String, String, String> map;
		map = new DoubleKeyMVMap<>();

		map.put("keep", "calm", "keep calm and make test0");
		map.put("keep", "calm", "keep calm and make test1");
		map.put("keep", "calm", "keep calm and make test2");
		map.put("keep", "calm2", "keep calm2 and make test");
		map.put("keep2", "calm", "keep2 calm and make test");
		assertTrue(map.size() == 5);

		assertTrue(map.remove("keep", "calm", "keep calm and make test0"));
		assertTrue(map.size() == 4);

		assertTrue(map.remove("keep2", "calm", "keep2 calm and make test"));
		assertTrue(map.size() == 3);

		assertFalse(map.remove("sav", "aa", "keep keep"));
		assertTrue(map.size() == 3);

		assertFalse(map.remove("keep", "aa", "keep keep"));
		assertTrue(map.size() == 3);

		assertFalse(map.remove("keep2", "aa", "keep keep"));
		assertTrue(map.size() == 3);

		assertFalse(map.remove("keep", "calm", "keep calm and make test"));
		assertTrue(map.size() == 3);

		assertTrue(map.remove("keep", "calm", "keep calm and make test1"));
		assertTrue(map.remove("keep", "calm", "keep calm and make test2"));
		assertTrue(map.remove("keep", "calm2", "keep calm2 and make test"));
		assertTrue(map.size() == 0);

		assertFalse(map.remove("keep", "calm", "keep calm and make test0"));
		assertFalse(map.remove("keep2", "calm", "keep2 calm and make test"));
		assertFalse(map.remove("keep", "calm", "keep calm and make test1"));
		assertFalse(map.remove("keep", "calm", "keep calm and make test2"));
		assertFalse(map.remove("keep", "calm2", "keep calm2 and make test"));
	}

	@Test
	public void testGet1() {
		DoubleKeyMVMap<String, String, String> map;
		map = new DoubleKeyMVMap<>();

		map.put("keep", "calm", "keep calm and make test0");
		map.put("keep", "calm", "keep calm and make test1");
		map.put("keep", "calm", "keep calm and make test2");
		map.put("keep", "calm2", "keep calm2 and make test");
		map.put("keep2", "calm", "keep2 calm and make test");

		MVMap<String, String> inner = map.get("keep");
		String[] s = new String[4];
		inner.toArray(s);

		String[] comp = { "keep calm and make test0", "keep calm and make test1", "keep calm and make test2", "keep calm2 and make test" };

		assertTrue(Arrays.equals(s, comp));
	}

	@Test
	public void testGet2() {

	}

	@Test
	public void testInnerSize() {
		DoubleKeyMVMap<String, String, String> map;
		map = new DoubleKeyMVMap<>();

		map.put("keep", "calm", "keep calm and make test0");
		map.put("keep", "calm2", "keep calm and make test1");
		map.put("keep", "calm", "keep calm and make test2");
		map.put("keep", "calm2", "keep calm2 and make test");
		map.put("keep2", "calm", "keep2 calm and make test");
		map.put("keep2", "calm2", "keep2 calm and make test");
		map.put("keep2", "calm2", "keep2 calm and make test");

		MVMap<String, String> inner1 = map.get("keep");
		assertTrue(inner1.size() == 4);
		assertTrue(map.size() == 7);

		MVMap<String, String> inner2 = map.get("keep2");
		assertTrue(inner2.size() == 3);
		assertTrue(map.size() == 7);

		inner1.remove("calm", "keep calm and make test0");
		inner1.remove("calm", "keep calm and make test2");
		assertTrue(inner1.size() == 2);
		assertTrue(inner2.size() == 3);
		assertTrue(map.size() == 5);

		List<String> listInner1 = inner2.get("calm");
		assertTrue(listInner1.size() == 1);

		listInner1.add("hello world");
		assertTrue(listInner1.size() == 2);
		assertTrue(inner2.size() == 4);
		assertTrue(inner1.size() == 2);
		assertTrue(map.size() == 6);

		listInner1.add(2, "foo");
		assertTrue(listInner1.size() == 3);
		assertTrue(inner2.size() == 5);
		assertTrue(inner1.size() == 2);
		assertTrue(map.size() == 7);

		listInner1.remove(1);
		assertTrue(listInner1.size() == 2);
		assertTrue(inner2.size() == 4);
		assertTrue(inner1.size() == 2);
		assertTrue(map.size() == 6);

		String[] comp = { "keep calm and make test0", "keep calm and make test1", "keep calm and make test2", "keep calm2 and make test" };
		listInner1.addAll(Arrays.asList(comp));
		assertTrue(listInner1.size() == 6);
		assertTrue(inner2.size() == 8);
		assertTrue(inner1.size() == 2);
		assertTrue(map.size() == 10);

		listInner1.clear();
		assertTrue(listInner1.size() == 0);
		assertTrue(inner2.size() == 2);
		assertTrue(inner1.size() == 2);
		assertTrue(map.size() == 4);

		listInner1.addAll(Arrays.asList(comp));
		assertTrue(listInner1.size() == 4);
		assertTrue(inner2.size() == 6);
		assertTrue(inner1.size() == 2);
		inner2.clear();
		assertTrue(inner2.size() == 0);
		assertTrue(map.size() == 2);

	}

	@Test
	public void testToString() {
		DoubleKeyMVMap<String, String, String> map;
		map = new DoubleKeyMVMap<>();

		map.put("keep", "calm", "keep calm and make test0");
		map.put("keep", "calm2", "keep calm and make test1");
		map.put("keep", "calm", "keep calm and make test2");
		map.put("keep", "calm2", "keep calm2 and make test");
		map.put("keep2", "calm", "keep2 calm and make test");
		map.put("keep2", "calm2", "keep2 calm and make test");
		map.put("keep2", "calm2", "keep2 calm and make test");

		String s = map.toString();
		String desiderata = "[ keep2 calm and make test , keep2 calm and make test , keep2 calm and make test , keep calm and make test0 , keep calm and make test2 , keep calm and make test1 , keep calm2 and make test ]";
		assertTrue(s.compareTo(desiderata) == 0);

		MVMap<String, String> inner = map.get("keep");
		s = inner.toString();
		desiderata = "[ keep calm and make test0 , keep calm and make test2 , keep calm and make test1 , keep calm2 and make test ]";
		assertTrue(s.compareTo(desiderata) == 0);
		
		inner.clear();
		s = inner.toString();
		desiderata = "[]";
		assertTrue(s.compareTo(desiderata) == 0);
		

		map.clear();
		s = map.toString();
		desiderata = "[]";
		assertTrue(s.compareTo(desiderata) == 0);
		
	}
	
	@Test
	public void testNewInnerMap(){
		DoubleKeyMVMap<String, String, String> map;
		map = new DoubleKeyMVMap<>();

		map.put("keep", "calm", "keep calm and make test0");
		map.put("keep", "calm2", "keep calm and make test1");
		map.put("keep", "calm", "keep calm and make test2");
		map.put("keep", "calm2", "keep calm2 and make test");
		map.put("keep2", "calm", "keep2 calm and make test");
		map.put("keep2", "calm2", "keep2 calm and make test");
		map.put("keep2", "calm2", "keep2 calm and make test");
		
		MVMap<String, String> inner = map.get("keep3");
		inner.put("foo", "abcdefg");
	}

}
