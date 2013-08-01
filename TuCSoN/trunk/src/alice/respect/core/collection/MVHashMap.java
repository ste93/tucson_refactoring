package alice.respect.core.collection;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This is an implementation of MVMap based on HashMap and ArrayList.
 * 
 * This class are not Thread-safe
 * */
public class MVHashMap<K, V> implements MVMap<K, V> {

	private final static int INITIAL_CAPACITY_PER_KEY = 5;

	private Map<K, List<V>> map;
	private int totalSize = 0;

	public MVHashMap() {
		this.map = new HashMap<K, List<V>>();
	}

	private List<V> createNewList() {
		return new ArrayList<V>(INITIAL_CAPACITY_PER_KEY);
	}

	@Override
	public int getKeysNumber() {
		return map.size();
	}

	@Override
	public int size() {
		return totalSize;
	}

	@Override
	public boolean isEmpty() {
		return (totalSize == 0);
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(V value) {
		for (K k : map.keySet()) {
			if (map.get(k).contains(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<V> get(K key) {
		List<V> list = map.get(key);
		if (list == null) {
			list = createNewList();
		}
		return new Values(key, list, null);
	}

	@Override
	public boolean put(K key, V value) {
		List<V> list = map.get(key);
		if (list == null) {
			list = createNewList();
			if (list.add(value)) {
				map.put(key, list);
				totalSize++;
				return true;
			}
		} else if (list.add(value)) {
			totalSize++;
			return true;
		}
		return false;
	}

	@Override
	public boolean put(Map.Entry<K, V> e) {
		return put(e.getKey(), e.getValue());
	}

	@Override
	public boolean remove(K key, V value) {
		List<V> list = map.get(key);
		if (list == null) {
			return false;
		} else if (list.remove(value)) {
			totalSize--;
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Map.Entry<K, V> e) {
		return remove(e.getKey(), e.getValue());
	}

	@Override
	public void clear() {
		// For clear value for each key
		for (List<V> list : map.values()) {
			list.clear();
		}
		map.clear();
		totalSize = 0;
	}

	/**
	 * <p>
	 * Return a list of all values contained into MVMap.
	 * The list are wrapped whit a {@link Collections#unmodifiableList()}
	 * </p>
	 * WARNING: the returned list should be used in read-only mode because it is
	 * not synchronized with the rest of the map
	 */
	@Override
	public List<V> values() {
		List<V> list = createNewList();
		for (K k : map.keySet()) {
			list.addAll(get(k));
		}
		return Collections.unmodifiableList(list);
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof MVHashMap) {
			MVHashMap<?, ?> m = (MVHashMap<?, ?>) object;
			return this.map.equals(m.getMap());
		}
		return false;
	}

	@Override
	public Iterator<V> iterator() {
		return new MVmapIterator();
	}

	/** See {@link AbstractCollection#toArray()} */
	@Override
	@SuppressWarnings("unchecked")
	public V[] toArray(V[] v) {
		// Estimate size of array; be prepared to see more or fewer elements
		int size = totalSize;
		V[] ret = v.length >= size ? v : (V[]) java.lang.reflect.Array.newInstance(v.getClass().getComponentType(), size);
		Iterator<V> it = iterator();

		for (int i = 0; i < ret.length; i++) {
			if (!it.hasNext()) { // fewer elements than expected
				if (v != ret)
					return Arrays.copyOf(ret, i);
				ret[i] = null; // null-terminate
				return ret;
			}
			ret[i] = (V) it.next();
		}
		return ret;
	}

	private Map<K, List<V>> getMap() {
		return map;
	}

	private class Values extends AbstractList<V> implements List<V> {

		final K key;

		List<V> wrappedList;
		// This is the super-list when create a sub-list
		final Values parentList;

		Values(K key, List<V> list, Values parentList) {
			this.key = key;
			this.wrappedList = list;
			this.parentList = parentList;
		}

		private boolean isSubList() {
			return (parentList != null);
		}

		void checkKeyExistence() {
			// The get method of MVMap returns an empty list if the requested
			// key does not exist. If a value is added to this list I need to
			// create the key associated with the list in the map.
			if (parentList != null) {
				parentList.checkKeyExistence();
			} else if (wrappedList.isEmpty()) {
				List<V> list = map.get(key);
				if (list != null) {
					wrappedList = list;
				}
			}
		}

		void removeKeyIfEmpty() {
			if (parentList != null) {
				parentList.removeKeyIfEmpty();
			} else if (wrappedList.isEmpty()) {
				map.remove(key);
			}
		}

		K getKey() {
			return key;
		}

		void addToMap() {
			if (parentList != null) {
				parentList.addToMap();
			} else {
				map.put(key, wrappedList);
			}
		}

		@Override
		public int size() {
			checkKeyExistence();
			return wrappedList.size();
		}

		@Override
		public boolean contains(Object o) {
			checkKeyExistence();
			return wrappedList.contains(o);
		}

		@Override
		public Iterator<V> iterator() {
			checkKeyExistence();
			return new WrappedIterator();
		}

		@Override
		public boolean add(V value) {
			checkKeyExistence();
			boolean wasEmpty = wrappedList.isEmpty();
			boolean changed = wrappedList.add(value);
			if (changed) {
				totalSize++;
				if (wasEmpty) {
					addToMap();
				}
			}
			return changed;
		}

		@Override
		public boolean remove(Object o) {
			checkKeyExistence();
			boolean changed = wrappedList.remove(o);
			if (changed) {
				totalSize--;
				removeKeyIfEmpty();
			}
			return changed;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			checkKeyExistence();
			return wrappedList.containsAll(c);
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) {
				return true;
			}
			checkKeyExistence();
			return wrappedList.equals(object);
		}

		@Override
		public int hashCode() {
			checkKeyExistence();
			return wrappedList.hashCode();
		}

		@Override
		public String toString() {
			checkKeyExistence();
			return wrappedList.toString();
		}

		@Override
		public boolean addAll(Collection<? extends V> collection) {
			if (collection.isEmpty()) {
				return false;
			}
			int oldSize = size();
			boolean changed = wrappedList.addAll(collection);
			if (changed) {
				int newSize = wrappedList.size();
				totalSize += (newSize - oldSize);
				if (oldSize == 0) {
					addToMap();
				}
			}
			return changed;
		}

		@Override
		public boolean addAll(int index, Collection<? extends V> c) {
			if (c.isEmpty()) {
				return false;
			}
			int oldSize = size(); // calls refreshIfEmpty
			boolean changed = wrappedList.addAll(index, c);
			if (changed) {
				int newSize = wrappedList.size();
				totalSize += (newSize - oldSize);
				if (oldSize == 0) {
					addToMap();
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			if (c.isEmpty()) {
				return false;
			}
			int oldSize = size();
			boolean changed = wrappedList.removeAll(c);
			if (changed) {
				int newSize = wrappedList.size();
				totalSize += (newSize - oldSize);
				removeKeyIfEmpty();
			}
			return changed;
		}

		@Override
		public boolean retainAll(Collection<?> c) {

			if (c == null) {
				throw new NullPointerException();
			}
			int oldSize = size();
			boolean changed = wrappedList.retainAll(c);
			if (changed) {
				int newSize = wrappedList.size();
				totalSize += (newSize - oldSize);
				removeKeyIfEmpty();
			}
			return changed;
		}

		@Override
		public void clear() {
			int oldSize = size();
			if (oldSize == 0) {
				return;
			}
			wrappedList.clear();
			totalSize -= oldSize;
			removeKeyIfEmpty();
		}

		@Override
		public V get(int index) {
			checkKeyExistence();
			return wrappedList.get(index);
		}

		@Override
		public V set(int index, V element) {
			checkKeyExistence();
			return wrappedList.set(index, element);
		}

		@Override
		public void add(int index, V element) {
			checkKeyExistence();
			boolean wasEmpty = wrappedList.isEmpty();
			wrappedList.add(index, element);
			totalSize++;
			if (wasEmpty) {
				addToMap();
			}
		}

		@Override
		public V remove(int index) {
			checkKeyExistence();
			V value = wrappedList.remove(index);
			totalSize--;
			removeKeyIfEmpty();
			return value;
		}

		@Override
		public int indexOf(Object o) {
			checkKeyExistence();
			return wrappedList.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			checkKeyExistence();
			return wrappedList.lastIndexOf(o);
		}

		@Override
		public ListIterator<V> listIterator() {
			checkKeyExistence();
			return new WrappedListIterator();
		}

		@Override
		public ListIterator<V> listIterator(int index) {
			checkKeyExistence();
			return new WrappedListIterator(index);
		}

		@Override
		public List<V> subList(int fromIndex, int toIndex) {
			checkKeyExistence();
			return new Values(getKey(), wrappedList.subList(fromIndex, toIndex), isSubList() ? parentList : this);

		}

		private class WrappedIterator implements Iterator<V> {
			final Iterator<V> delegateIterator;
			final List<V> originalDelegate = wrappedList;

			WrappedIterator() {
				delegateIterator = wrappedList.listIterator();
			}

			WrappedIterator(Iterator<V> delegateIterator) {
				this.delegateIterator = delegateIterator;
			}

			/**
			 * If the delegate changed since the iterator was created, the
			 * iterator is no longer valid.
			 */
			void validateIterator() {
				checkKeyExistence();
				if (wrappedList != originalDelegate) {
					throw new ConcurrentModificationException();
				}
			}

			@Override
			public boolean hasNext() {
				validateIterator();
				return delegateIterator.hasNext();
			}

			@Override
			public V next() {
				validateIterator();
				return delegateIterator.next();
			}

			@Override
			public void remove() {
				delegateIterator.remove();
				totalSize--;
				removeKeyIfEmpty();
			}

			Iterator<V> getDelegateIterator() {
				validateIterator();
				return delegateIterator;
			}
		}

		private class WrappedListIterator extends WrappedIterator implements ListIterator<V> {

			WrappedListIterator() {
			}

			public WrappedListIterator(int index) {
				super(wrappedList.listIterator(index));
			}

			private ListIterator<V> getDelegateListIterator() {
				return (ListIterator<V>) getDelegateIterator();
			}

			@Override
			public boolean hasPrevious() {
				return getDelegateListIterator().hasPrevious();
			}

			@Override
			public V previous() {
				return getDelegateListIterator().previous();
			}

			@Override
			public int nextIndex() {
				return getDelegateListIterator().nextIndex();
			}

			@Override
			public int previousIndex() {
				return getDelegateListIterator().previousIndex();
			}

			@Override
			public void set(V value) {
				getDelegateListIterator().set(value);
			}

			@Override
			public void add(V value) {
				boolean wasEmpty = isEmpty();
				getDelegateListIterator().add(value);
				totalSize++;
				if (wasEmpty) {
					addToMap();
				}
			}
		}// END WrappedListIterator

	}// END WrappedList

	/**
	 * Iterator across all key-value pairs. This requires to manage two
	 * iterators: one on the map and one for the list associated at key.
	 */
	private class MVmapIterator implements Iterator<V> {

		final Iterator<Map.Entry<K, List<V>>> entryIterator;
		List<V> list;
		Iterator<V> valueIterator;

		protected MVmapIterator() {
			// Initialize the Entry iterator
			entryIterator = map.entrySet().iterator();
			if (entryIterator.hasNext()) {
				// if it has at least one element in the map, select the first
				advanceEntryIterator();
			} else {
				// else use an empty iterator
				valueIterator = Collections.emptyIterator();
			}
		}

		/**
		 * Select the next key in the map (if has any) and update the iterator
		 * of list associated with the key.
		 */
		private void advanceEntryIterator() {
			Map.Entry<K, List<V>> entry = entryIterator.next();
			list = entry.getValue();
			valueIterator = list.iterator();
		}

		/**
		 * Returns {@code true} if the iteration has more elements. (In other
		 * words, returns {@code true} if {@link #next} would return an element
		 * rather than throwing an exception.)
		 * 
		 * @return {@code true} if the iteration has more elements
		 */
		@Override
		public boolean hasNext() {
			return entryIterator.hasNext() || valueIterator.hasNext();
		}

		/**
		 * Returns the next element in the iteration.
		 * 
		 * @return the next element in the iteration
		 * @throws NoSuchElementException
		 *             if the iteration has no more elements
		 */
		@Override
		public V next() {
			if (!valueIterator.hasNext()) {
				advanceEntryIterator();
			}
			return valueIterator.next();
		}

		/**
		 * Removes from the underlying collection the last element returned by
		 * this iterator (optional operation). This method can be called only
		 * once per call to {@link #next}. The behavior of an iterator is
		 * unspecified if the underlying collection is modified while the
		 * iteration is in progress in any way other than by calling this
		 * method.
		 * 
		 * @throws IllegalStateException
		 *             if the {@code next} method has not yet been called, or
		 *             the {@code remove} method has already been called after
		 *             the last call to the {@code next} method
		 */
		@Override
		public void remove() {
			valueIterator.remove();
			// If the list contains no elements
			// then remove the key from map
			if (list.isEmpty()) {
				entryIterator.remove();
			}
			totalSize--;
		}
	}

}
