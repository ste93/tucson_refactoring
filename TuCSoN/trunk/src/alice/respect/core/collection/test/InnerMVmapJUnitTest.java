package alice.respect.core.collection.test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import alice.respect.core.collection.DoubleKeyMVMap;
import alice.respect.core.collection.MVMap;

public class InnerMVmapJUnitTest {

    private static MVMap<String, Integer> create() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();
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

    private static MVMap<String, Integer> createEmpty() {
        return new DoubleKeyMVMap<String, String, Integer>().get(null);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testConcurrentModification() {

        final MVMap<String, Integer> map = InnerMVmapJUnitTest.create();

        final List<Integer> l1 = map.get("A");
        Assert.assertTrue(l1.size() == 6);

        final List<Integer> subL1 = l1.subList(0, 4);

        subL1.clear();

        map.put("A", 123);

        this.exception.expect(ConcurrentModificationException.class);
        subL1.isEmpty();

    }

    @Test
    public void testConsistency() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.create();

        final List<Integer> l = map.get("C");

        l.remove(0);

        Assert.assertTrue(l.size() == 0);

    }

    @Test
    public void testContainsKey() {

        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        map.put("A", 100);
        map.put("B", 1900);
        map.put("C", 190);
        map.put("AA", 11945);
        map.put("B", 15911);
        map.put("B", 1119);

        Assert.assertTrue(map.containsKey("A"));
        Assert.assertTrue(map.containsKey("AA"));
        Assert.assertTrue(map.containsKey("B"));
        Assert.assertTrue(map.containsKey("C"));
        Assert.assertFalse(map.containsKey("F"));

    }

    @Test
    public void testContainsValue() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();
        Assert.assertTrue(map.isEmpty());

        map.put("A", 100);
        map.put("B", 1900);
        map.put("C", 190);
        map.put("AA", 11945);
        map.put("B", 15911);
        map.put("B", 1119);

        Assert.assertTrue(map.containsValue(190));
        Assert.assertTrue(map.containsValue(1900));
        Assert.assertTrue(map.containsValue(100));
        Assert.assertTrue(map.containsValue(11945));
        Assert.assertFalse(map.containsValue(11000));
    }

    @Test
    public void testIsEmpty() {

        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        Assert.assertTrue(map.isEmpty());

        map.put("A", 10);
        map.put("B", 19);
        map.put("C", 190);
        map.put("D", 119);
        map.put("B", 159);
        map.put("B", 1119);

        map.clear();

        Assert.assertTrue(map.isEmpty());

    }

    @Test
    public void testListIterator() {

        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("D", 4);
        map.put("B", 5);
        map.put("A", 6);
        map.put("A", 7);
        List<Integer> list = map.get("A");

        Iterator<Integer> it = list.iterator();

        Assert.assertTrue(it.next() == 1);
        Assert.assertTrue(it.next() == 6);
        Assert.assertTrue(it.next() == 7);
        Assert.assertFalse(it.hasNext());

        // remove all element of the map from a test list.
        // If the final size of the test list is zero the test is passed
        int count = 0;
        for (Iterator<Integer> iterator = list.iterator(); iterator.hasNext();) {
            iterator.next();
            count++;
        }
        Assert.assertTrue(count == 3);

        it = list.iterator();
        count = 0;
        final int size = map.size();

        while (it.hasNext()) {
            it.next();
            it.remove();
            count++;
        }

        Assert.assertTrue(map.size() == (size - count));

        list = map.get("B");
        it = list.iterator();
        for (int i = 0; i < 2; i++) {
            it.next();
        }
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testListIteratorException1() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("D", 4);
        map.put("B", 5);
        map.put("A", 6);
        map.put("A", 7);
        final List<Integer> list = map.get("A");

        final Iterator<Integer> it = list.iterator();
        it.next();
        it.remove();

        this.exception.expect(IllegalStateException.class);
        it.remove();

    }

    @Test
    public void testListIteratorException2() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("D", 4);
        map.put("B", 5);
        map.put("A", 6);
        map.put("A", 7);
        final List<Integer> list = map.get("A");

        final Iterator<Integer> it = list.iterator();
        it.next();
        it.next();
        it.next();

        // A double consecutive remove() is not allowed
        this.exception.expect(NoSuchElementException.class);
        it.next();
    }

    @Test
    public void testMapIterator() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("D", 4);
        map.put("B", 5);
        map.put("A", 6);
        map.put("A", 7);

        Iterator<Integer> it = map.iterator();

        List<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(2);
        test.add(3);
        test.add(4);
        test.add(5);
        test.add(6);
        test.add(7);
        while (it.hasNext()) {
            Assert.assertTrue(test.remove(it.next()));
        }
        Assert.assertTrue(test.size() == 0);

        test = new ArrayList<Integer>();
        test.add(1);
        test.add(2);
        test.add(3);
        test.add(4);
        test.add(5);
        test.add(6);
        test.add(7);
        // remove all element of the map from a test list.
        // If the final size of the test list is zero the test is passed
        for (final Integer i : map) {
            Assert.assertTrue(test.remove(i));
        }
        Assert.assertTrue(test.size() == 0);

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

        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testMapIteratorException1() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("D", 4);
        map.put("B", 5);
        map.put("A", 6);
        map.put("A", 7);

        final Iterator<Integer> it = map.iterator();

        it.next();
        it.remove();
        it.next();
        it.remove();

        // A double consecutive remove() is not allowed
        this.exception.expect(IllegalStateException.class);
        it.remove();

    }

    @Test
    public void testMapIteratorException2() {
        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        final Iterator<Integer> it = map.iterator();
        it.next();
        it.remove();
        it.next();
        it.remove();
        it.next();
        it.remove();

        this.exception.expect(NoSuchElementException.class);
        it.next();

    }

    @Test
    public void testSize() {

        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();
        Assert.assertTrue(map.isEmpty());

        map.put("A", 10);
        map.put("B", 19);
        map.put("C", 190);
        map.put("AA", 119);
        map.put("B", 159);
        map.put("B", 1119);
        // Expected map size is 6
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(map.size() == 6);
        Assert.assertTrue(map.get("A").size() == 1);
        Assert.assertTrue(map.get("B").size() == 3);

        map.put("A", 12);
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(map.size() == 7);
        Assert.assertTrue(map.get("A").size() == 2);

        map.remove("B", 1119);
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(map.size() == 6);
        Assert.assertTrue(map.get("B").size() == 2);

        map.clear();
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.size() == 0);

    }

    @Test
    public void testSubListSize() {

        final MVMap<String, Integer> map = InnerMVmapJUnitTest.createEmpty();
        Assert.assertTrue(map.isEmpty());

        map.put("A", 100);
        map.put("B", 1900);
        map.put("C", 190);
        map.put("AA", 11945);
        map.put("B", 15911);
        map.put("B", 1119);
        // Expected map size is 6
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(map.size() == 6);

        final List<Integer> list = map.get("B");
        list.add(12);
        // list size = 4, map size = 7
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(list.size() == 4);
        Assert.assertTrue(map.size() == 7);
        Assert.assertTrue(map.get("B").size() == 4);

        list.remove(0);

        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(list.size() == 3);
        Assert.assertTrue(map.size() == 6);
        Assert.assertTrue(map.get("B").size() == 3);

        map.clear();
        Assert.assertTrue(list.size() == 0);
        Assert.assertTrue(map.isEmpty());
        Assert.assertTrue(map.size() == 0);

    }
}
