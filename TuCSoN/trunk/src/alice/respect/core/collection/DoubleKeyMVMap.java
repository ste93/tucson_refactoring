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
import java.util.Map.Entry;
import java.util.NoSuchElementException;

public class DoubleKeyMVMap<K1, K2, V> {

	HashMap<K1, MVMap<K2, V>> outerMap = new HashMap<>();

	private int totalValuesSize = 0;

	private MVMap<K2, V> createMVMap(K1 key) {
		return new InnerMVMap(key);
	}

	/** Null value are allowed */
	public boolean put(K1 k1, K2 k2, V v) {
		MVMap<K2, V> innerMap = outerMap.get(k1);
		if (innerMap == null) {
			innerMap = createMVMap(k1);
			if (innerMap.put(k2, v)) {
				outerMap.put(k1, innerMap);
				return true;
			} else {
				return false;
			}
		} else {
			return innerMap.put(k2, v);
		}
	}

	/**
	 * Removes the first occurrence of the specified element from this MVMap.
	 * 
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(K1 k1, K2 k2, V v) {
		MVMap<K2, V> innerMap = outerMap.get(k1);
		if (innerMap != null) {
			if (innerMap.remove(k2, v)) {
				if (innerMap.getKeysNumber() == 0)
					outerMap.remove(k1);
				return true;
			}
		}
		return false;
	}

	public MVMap<K2, V> get(K1 k1) {
		MVMap<K2, V> innerMap = outerMap.get(k1);
		if (innerMap == null) {
			innerMap = createMVMap(k1);
		}
		return innerMap;
	}

	public List<V> get(K1 k1, K2 k2) {
		return get(k1).get(k2);
	}

	public int size() {
		return totalValuesSize;
	}

	public boolean isEmpty() {
		return (totalValuesSize == 0);
	}

	public void clear() {
		for (K1 k : outerMap.keySet()) {
			outerMap.get(k).clear();
		}
		outerMap.clear();
	}

	public Iterator<V> iterator() {
		return new DoubleKeyMVmapIterator();
	}

	@SuppressWarnings("unchecked")
	public V[] toArray(V[] v) {
		// Estimate size of array; be prepared to see more or fewer elements
		int size = totalValuesSize;
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

	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer("[");
		Iterator<V> it = iterator();
		if (it.hasNext())
			ret.append(" " + it.next().toString() + " ");
		while (it.hasNext()) {
			ret.append(", ");
			ret.append(it.next().toString() + " ");
		}
		ret.append("]");
		return ret.toString();
	}

	private class InnerMVMap implements MVMap<K2, V> {

		private int innerMapSize = 0;
		private final Map<K2, List<V>> innerMap;

		final K1 mapKey1;

		InnerMVMap(K1 mapK1) {
			this.innerMap = new HashMap<>();
			mapKey1 = mapK1;
		}

		private List<V> newList() {
			return new ArrayList<>();
		}

		@Override
		public int getKeysNumber() {
			return innerMap.size();
		}

		@Override
		public int size() {
			return innerMapSize;
		}

		@Override
		public boolean isEmpty() {
			return (innerMapSize == 0);
		}

		@Override
		public boolean containsKey(K2 key) {
			return innerMap.containsKey(key);
		}

		@Override
		public boolean containsValue(V value) {
			for (K2 k : innerMap.keySet()) {
				if (innerMap.get(k).contains(value)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public List<V> get(K2 key) {
			List<V> v = innerMap.get(key);
			if (v == null) {
				v = newList();
			}
			return new Values(mapKey1, key, v, null);
		}

		@Override
		public boolean put(K2 key, V value) {
			boolean wasEmpty = innerMap.isEmpty();
			List<V> l = innerMap.get(key);
			if (l == null) {
				l = newList();
				if (l.add(value)) {
					innerMap.put(key, l);
					innerMapSize++;
					totalValuesSize++;
					if (wasEmpty)
						outerMap.put(mapKey1, this);
					return true;
				}
			} else if (l.add(value)) {
				innerMapSize++;
				totalValuesSize++;
				return true;
			}
			return false;
		}

		@Override
		public boolean put(Entry<K2, V> e) {
			return put(e.getKey(), e.getValue());
		}

		@Override
		public boolean remove(K2 key, V value) {
			List<V> l = innerMap.get(key);
			if (l == null) {
				return false;
			} else if (l.remove(value)) {
				innerMapSize--;
				totalValuesSize--;
				if (l.size() == 0) {
					innerMap.remove(key);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean remove(Entry<K2, V> e) {
			return remove(e.getKey(), e.getValue());
		}

		@Override
		public void clear() {
			// Clear value for each key
			for (List<V> list : innerMap.values()) {
				list.clear();
			}
			innerMap.clear();
			totalValuesSize -= innerMapSize;
			innerMapSize = 0;
		}

		/**
		 * <p>
		 * Return a list of all values contained into MVMap. The list are
		 * wrapped whit a {@link Collections#unmodifiableList()}
		 * </p>
		 * WARNING: the returned list should be used in read-only mode because
		 * it is not synchronized with the rest of the map
		 */
		@Override
		public List<V> values() {
			List<V> list = newList();
			for (K2 k : innerMap.keySet()) {
				list.addAll(get(k));
			}
			return Collections.unmodifiableList(list);
		}

		@Override
		public Iterator<V> iterator() {
			return new MVmapIterator();
		}

		@Override
		public String toString() {
			StringBuffer ret = new StringBuffer("[");
			Iterator<V> it = iterator();
			if (it.hasNext())
				ret.append(" " + it.next().toString() + " ");
			while (it.hasNext()) {
				ret.append(", ");
				ret.append(it.next().toString() + " ");
			}
			ret.append("]");
			return ret.toString();
		}

		/** See {@link AbstractCollection#toArray()} */
		@Override
		@SuppressWarnings("unchecked")
		public V[] toArray(V[] v) {
			// Estimate size of array; be prepared to see more or fewer elements
			int size = innerMapSize;
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

		private class Values extends AbstractList<V> implements List<V> {

			// This is the super-list when create a sub-list
			final List<V> wrappedList;
			final Values parentList;
			final K1 listKey1;
			final K2 listKey2;

			Values(K1 k1, K2 k2, List<V> listToWrap, Values parentList) {
				super();
				listKey1 = k1;
				listKey2 = k2;
				this.wrappedList = listToWrap;
				this.parentList = parentList;
			}

			private void addKeysToMap() {
				if (parentList != null) {
					parentList.addKeysToMap();
				} else {
					innerMap.put(listKey2, wrappedList);
				}
			}

			private void removeInnerKeyIfEmpty() {
				if (parentList != null) {
					parentList.removeInnerKeyIfEmpty();
				} else if (wrappedList.isEmpty()) {
					innerMap.remove(listKey2);
				}
			}

			private boolean isSublist() {
				return (parentList != null);
			}

			@Override
			public int size() {
				return wrappedList.size();
			}

			@Override
			public boolean isEmpty() {
				return wrappedList.isEmpty();
			}

			@Override
			public boolean contains(Object o) {
				return wrappedList.contains(o);
			}

			@Override
			public int indexOf(Object o) {
				return wrappedList.indexOf(o);
			}

			@Override
			public int lastIndexOf(Object o) {
				return wrappedList.lastIndexOf(o);
			}

			@Override
			public Object[] toArray() {
				return wrappedList.toArray();
			}

			@Override
			public <T> T[] toArray(T[] a) {
				return wrappedList.toArray(a);
			}

			@Override
			public V get(int index) {
				return wrappedList.get(index);
			}

			@Override
			public V set(int index, V element) {
				return wrappedList.set(index, element);
			}

			@Override
			public boolean add(V e) {
				boolean wasEmpty = wrappedList.isEmpty();
				// The ArrayList.add() return always true
				wrappedList.add(e);
				innerMapSize++;
				totalValuesSize++;
				if (wasEmpty)
					addKeysToMap();
				return true;
			}

			@Override
			public void add(int index, V element) {
				boolean wasEmpty = wrappedList.isEmpty();
				wrappedList.add(index, element);
				innerMapSize++;
				totalValuesSize++;
				if (wasEmpty)
					addKeysToMap();
			}

			@Override
			public V remove(int index) {
				V v = wrappedList.remove(index);
				innerMapSize--;
				totalValuesSize--;
				removeInnerKeyIfEmpty();
				return v;
			}

			@Override
			public boolean remove(Object o) {
				if (wrappedList.remove(o)) {
					innerMapSize--;
					totalValuesSize--;
					removeInnerKeyIfEmpty();
					return true;
				}
				return false;
			}

			@Override
			public void clear() {
				int oldSize = size();
				if (oldSize == 0)
					return;

				wrappedList.clear();
				innerMapSize -= oldSize;
				totalValuesSize -= oldSize;
				removeInnerKeyIfEmpty();
			}

			@Override
			public boolean addAll(Collection<? extends V> c) {
				int oldSize = wrappedList.size();
				if (wrappedList.addAll(c)) {
					int newSize = wrappedList.size();
					innerMapSize += (newSize - oldSize);
					totalValuesSize += (newSize - oldSize);
					if (oldSize == 0)
						addKeysToMap();
					return true;
				}
				return false;
			}

			@Override
			public boolean addAll(int index, Collection<? extends V> c) {
				int oldSize = wrappedList.size();
				if (wrappedList.addAll(index, c)) {
					int newSize = wrappedList.size();
					innerMapSize += (newSize - oldSize);
					totalValuesSize += (newSize - oldSize);
					if (oldSize == 0)
						addKeysToMap();
					return true;
				}
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				int oldSize = wrappedList.size();
				if (wrappedList.removeAll(c)) {
					int newSize = wrappedList.size();
					innerMapSize += (newSize - oldSize);
					totalValuesSize += (newSize - oldSize);
					if (newSize == 0)
						removeInnerKeyIfEmpty();
					return true;
				}
				return false;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				int oldSize = wrappedList.size();
				if (wrappedList.retainAll(c)) {
					int newSize = wrappedList.size();
					innerMapSize += (newSize - oldSize);
					totalValuesSize += (newSize - oldSize);
					removeInnerKeyIfEmpty();
					return true;
				}
				return false;
			}

			@Override
			public ListIterator<V> listIterator(int index) {
				return new WrappedListIterator(index);
			}

			@Override
			public ListIterator<V> listIterator() {
				return new WrappedListIterator();
			}

			@Override
			public Iterator<V> iterator() {
				return new WrappedIterator();
			}

			@Override
			public List<V> subList(int fromIndex, int toIndex) {
				Values p = isSublist() ? parentList : this;
				List<V> l = wrappedList.subList(fromIndex, toIndex);
				return new Values(listKey1, listKey2, l, p);
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
					innerMapSize--;
					totalValuesSize--;
					removeInnerKeyIfEmpty();
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
					innerMapSize++;
					totalValuesSize++;
					if (wasEmpty) {
						addKeysToMap();
					}
				}
			}// END WrappedListIterator

		} // END OF Values

		/**
		 * Iterator across all key-value pairs. This requires to manage two
		 * iterators: one on the map and one for the list associated at key.
		 */
		private class MVmapIterator implements Iterator<V> {

			final Iterator<Map.Entry<K2, List<V>>> entryIterator;
			List<V> list;
			Iterator<V> valueIterator;

			protected MVmapIterator() {
				// Initialize the Entry iterator
				entryIterator = innerMap.entrySet().iterator();
				if (entryIterator.hasNext()) {
					// if it has at least one element in the map, select the
					// first
					advanceEntryIterator();
				} else {
					// else use an empty iterator
					valueIterator = Collections.emptyIterator();
				}
			}

			/**
			 * Select the next key in the map (if has any) and update the
			 * iterator of list associated with the key.
			 */
			private void advanceEntryIterator() {
				Map.Entry<K2, List<V>> entry = entryIterator.next();
				list = entry.getValue();
				valueIterator = list.iterator();
			}

			/**
			 * Returns {@code true} if the iteration has more elements. (In
			 * other words, returns {@code true} if {@link #next} would return
			 * an element rather than throwing an exception.)
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
			 * Removes from the underlying collection the last element returned
			 * by this iterator (optional operation). This method can be called
			 * only once per call to {@link #next}. The behavior of an iterator
			 * is unspecified if the underlying collection is modified while the
			 * iteration is in progress in any way other than by calling this
			 * method.
			 * 
			 * @throws IllegalStateException
			 *             if the {@code next} method has not yet been called,
			 *             or the {@code remove} method has already been called
			 *             after the last call to the {@code next} method
			 */
			@Override
			public void remove() {
				valueIterator.remove();
				// If the list contains no elements
				// then remove the key from map
				if (list.isEmpty()) {
					entryIterator.remove();
				}
				totalValuesSize--;
			}
		}

	} // InnerMap

	/**
	 * Iterator across all key-value pairs. This requires to manage two
	 * iterators: one on the map and one for the map associated at key.
	 */
	private class DoubleKeyMVmapIterator implements Iterator<V> {

		final Iterator<Map.Entry<K1, MVMap<K2, V>>> entryIterator;
		MVMap<K2, V> innerMap;
		Iterator<V> valueIterator;

		protected DoubleKeyMVmapIterator() {
			// Initialize the Entry iterator
			entryIterator = outerMap.entrySet().iterator();
			if (entryIterator.hasNext()) {
				// if it has at least one element in the map, select the
				// first
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
			Map.Entry<K1, MVMap<K2, V>> entry = entryIterator.next();
			innerMap = entry.getValue();
			valueIterator = innerMap.iterator();
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
			if (innerMap.isEmpty()) {
				entryIterator.remove();
			}
			totalValuesSize--;
		}
	}

}
