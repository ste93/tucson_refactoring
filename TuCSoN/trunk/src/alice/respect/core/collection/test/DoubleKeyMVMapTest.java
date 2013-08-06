package alice.respect.core.collection.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import alice.respect.core.collection.DoubleKeyMVMap;
import alice.respect.core.collection.MVMap;

public class DoubleKeyMVMapTest {

    @Test
    public void testGet1() {
        DoubleKeyMVMap<String, String, String> map;
        map = new DoubleKeyMVMap<String, String, String>();

        map.put("keep", "calm", "keep calm and make test0");
        map.put("keep", "calm", "keep calm and make test1");
        map.put("keep", "calm", "keep calm and make test2");
        map.put("keep", "calm2", "keep calm2 and make test");
        map.put("keep2", "calm", "keep2 calm and make test");

        final MVMap<String, String> inner = map.get("keep");
        final String[] s = new String[4];
        inner.toArray(s);

        final String[] comp =
                { "keep calm and make test0", "keep calm and make test1",
                        "keep calm and make test2", "keep calm2 and make test" };

        Assert.assertTrue(Arrays.equals(s, comp));
    }

    @Test
    public void testGet2() {
        /*
	     * 
	     */
    }

    @Test
    public void testInnerSize() {
        DoubleKeyMVMap<String, String, String> map;
        map = new DoubleKeyMVMap<String, String, String>();

        map.put("keep", "calm", "keep calm and make test0");
        map.put("keep", "calm2", "keep calm and make test1");
        map.put("keep", "calm", "keep calm and make test2");
        map.put("keep", "calm2", "keep calm2 and make test");
        map.put("keep2", "calm", "keep2 calm and make test");
        map.put("keep2", "calm2", "keep2 calm and make test");
        map.put("keep2", "calm2", "keep2 calm and make test");

        final MVMap<String, String> inner1 = map.get("keep");
        Assert.assertTrue(inner1.size() == 4);
        Assert.assertTrue(map.size() == 7);

        final MVMap<String, String> inner2 = map.get("keep2");
        Assert.assertTrue(inner2.size() == 3);
        Assert.assertTrue(map.size() == 7);

        inner1.remove("calm", "keep calm and make test0");
        inner1.remove("calm", "keep calm and make test2");
        Assert.assertTrue(inner1.size() == 2);
        Assert.assertTrue(inner2.size() == 3);
        Assert.assertTrue(map.size() == 5);

        final List<String> listInner1 = inner2.get("calm");
        Assert.assertTrue(listInner1.size() == 1);

        listInner1.add("hello world");
        Assert.assertTrue(listInner1.size() == 2);
        Assert.assertTrue(inner2.size() == 4);
        Assert.assertTrue(inner1.size() == 2);
        Assert.assertTrue(map.size() == 6);

        listInner1.add(2, "foo");
        Assert.assertTrue(listInner1.size() == 3);
        Assert.assertTrue(inner2.size() == 5);
        Assert.assertTrue(inner1.size() == 2);
        Assert.assertTrue(map.size() == 7);

        listInner1.remove(1);
        Assert.assertTrue(listInner1.size() == 2);
        Assert.assertTrue(inner2.size() == 4);
        Assert.assertTrue(inner1.size() == 2);
        Assert.assertTrue(map.size() == 6);

        final String[] comp =
                { "keep calm and make test0", "keep calm and make test1",
                        "keep calm and make test2", "keep calm2 and make test" };
        listInner1.addAll(Arrays.asList(comp));
        Assert.assertTrue(listInner1.size() == 6);
        Assert.assertTrue(inner2.size() == 8);
        Assert.assertTrue(inner1.size() == 2);
        Assert.assertTrue(map.size() == 10);

        listInner1.clear();
        Assert.assertTrue(listInner1.size() == 0);
        Assert.assertTrue(inner2.size() == 2);
        Assert.assertTrue(inner1.size() == 2);
        Assert.assertTrue(map.size() == 4);

        listInner1.addAll(Arrays.asList(comp));
        Assert.assertTrue(listInner1.size() == 4);
        Assert.assertTrue(inner2.size() == 6);
        Assert.assertTrue(inner1.size() == 2);
        inner2.clear();
        Assert.assertTrue(inner2.size() == 0);
        Assert.assertTrue(map.size() == 2);

    }

    @Test
    public void testNewInnerMap() {
        DoubleKeyMVMap<String, String, String> map;
        map = new DoubleKeyMVMap<String, String, String>();

        map.put("keep", "calm", "keep calm and make test0");
        map.put("keep", "calm2", "keep calm and make test1");
        map.put("keep", "calm", "keep calm and make test2");
        map.put("keep", "calm2", "keep calm2 and make test");
        map.put("keep2", "calm", "keep2 calm and make test");
        map.put("keep2", "calm2", "keep2 calm and make test");
        map.put("keep2", "calm2", "keep2 calm and make test");

        final MVMap<String, String> inner = map.get("keep3");
        inner.put("foo", "abcdefg");
    }

    @Test
    public void testPut() {
        DoubleKeyMVMap<String, String, String> map;
        map = new DoubleKeyMVMap<String, String, String>();

        boolean result;
        result = map.put("key 1", "key 2", "a b c d");
        Assert.assertTrue(result);
        result = map.put("key 1", "key 2", "a b c e");
        Assert.assertTrue(result);
        result = map.put("key 1", "key 2", "a b c f");
        Assert.assertTrue(result);
        result = map.put("key 1", "key 3", "a b c g");
        Assert.assertTrue(result);
        result = map.put(null, "key 3", "a b c d");
        Assert.assertTrue(result);
        result = map.put("key 1", null, "a b c d");
        Assert.assertTrue(result);
    }

    @Test
    public void testRemove() {
        DoubleKeyMVMap<String, String, String> map;
        map = new DoubleKeyMVMap<String, String, String>();

        map.put("keep", "calm", "keep calm and make test0");
        map.put("keep", "calm", "keep calm and make test1");
        map.put("keep", "calm", "keep calm and make test2");
        map.put("keep", "calm2", "keep calm2 and make test");
        map.put("keep2", "calm", "keep2 calm and make test");
        Assert.assertTrue(map.size() == 5);

        Assert.assertTrue(map
                .remove("keep", "calm", "keep calm and make test0"));
        Assert.assertTrue(map.size() == 4);

        Assert.assertTrue(map.remove("keep2", "calm",
                "keep2 calm and make test"));
        Assert.assertTrue(map.size() == 3);

        Assert.assertFalse(map.remove("sav", "aa", "keep keep"));
        Assert.assertTrue(map.size() == 3);

        Assert.assertFalse(map.remove("keep", "aa", "keep keep"));
        Assert.assertTrue(map.size() == 3);

        Assert.assertFalse(map.remove("keep2", "aa", "keep keep"));
        Assert.assertTrue(map.size() == 3);

        Assert.assertFalse(map
                .remove("keep", "calm", "keep calm and make test"));
        Assert.assertTrue(map.size() == 3);

        Assert.assertTrue(map
                .remove("keep", "calm", "keep calm and make test1"));
        Assert.assertTrue(map
                .remove("keep", "calm", "keep calm and make test2"));
        Assert.assertTrue(map.remove("keep", "calm2",
                "keep calm2 and make test"));
        Assert.assertTrue(map.size() == 0);

        Assert.assertFalse(map.remove("keep", "calm",
                "keep calm and make test0"));
        Assert.assertFalse(map.remove("keep2", "calm",
                "keep2 calm and make test"));
        Assert.assertFalse(map.remove("keep", "calm",
                "keep calm and make test1"));
        Assert.assertFalse(map.remove("keep", "calm",
                "keep calm and make test2"));
        Assert.assertFalse(map.remove("keep", "calm2",
                "keep calm2 and make test"));
    }

    @Test
    public void testSize() {
        DoubleKeyMVMap<String, String, String> map;
        map = new DoubleKeyMVMap<String, String, String>();

        Assert.assertTrue(map.size() == 0);
        Assert.assertTrue(map.isEmpty());
        map.put("keep", "calm", "keep calm and make test case0");
        Assert.assertTrue(map.size() == 1);
        Assert.assertFalse(map.isEmpty());
        map.put("keep", "calm", "keep calm and make test case1");
        Assert.assertTrue(map.size() == 2);
        Assert.assertFalse(map.isEmpty());
        map.put("keep", "calm", "keep calm and make test case2");
        Assert.assertTrue(map.size() == 3);
        Assert.assertFalse(map.isEmpty());
        map.put("keep", "calm2", "keep calm2 and make test case");
        map.put("keep2", "calm", "keep2 calm and make test case");
        Assert.assertTrue(map.size() == 5);
        Assert.assertFalse(map.isEmpty());

        map.clear();
        Assert.assertTrue(map.size() == 0);
    }

    @Test
    public void testToString() {
        DoubleKeyMVMap<String, String, String> map;
        map = new DoubleKeyMVMap<String, String, String>();

        map.put("keep", "calm", "keep calm and make test0");
        map.put("keep", "calm2", "keep calm and make test1");
        map.put("keep", "calm", "keep calm and make test2");
        map.put("keep", "calm2", "keep calm2 and make test");
        map.put("keep2", "calm", "keep2 calm and make test");
        map.put("keep2", "calm2", "keep2 calm and make test");
        map.put("keep2", "calm2", "keep2 calm and make test");

        String s = map.toString();
        String desiderata =
                "[ keep2 calm and make test , keep2 calm and make test , keep2 calm and make test , keep calm and make test0 , keep calm and make test2 , keep calm and make test1 , keep calm2 and make test ]";
        Assert.assertTrue(s.compareTo(desiderata) == 0);

        final MVMap<String, String> inner = map.get("keep");
        s = inner.toString();
        desiderata =
                "[ keep calm and make test0 , keep calm and make test2 , keep calm and make test1 , keep calm2 and make test ]";
        Assert.assertTrue(s.compareTo(desiderata) == 0);

        inner.clear();
        s = inner.toString();
        desiderata = "[]";
        Assert.assertTrue(s.compareTo(desiderata) == 0);

        map.clear();
        s = map.toString();
        desiderata = "[]";
        Assert.assertTrue(s.compareTo(desiderata) == 0);

    }

}
