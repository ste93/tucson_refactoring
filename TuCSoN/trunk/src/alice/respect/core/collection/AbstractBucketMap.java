package alice.respect.core.collection;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class AbstractBucketMap<K, V> implements BucketMap<K, V> {

	private Map<K, List<V>> map;
	private int totalSize;

	protected AbstractBucketMap(Map<K, List<V>> map) {
		this.map = map;
	}

	abstract List<V> createList();

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#size()
	 */
	@Override
	public int size() {
		return totalSize;
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return (totalSize == 0);
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#containsKey(K)
	 */
	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#containsValue(V)
	 */
	@Override
	public boolean containsValue(V value) {
		for (K k : map.keySet()) {
			if (map.get(k).contains(value)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#get(K)
	 */
	@Override
	public List<V> get(K key) {
		List<V> list = map.get(key);
		if (list == null) {
			list = createList();
		}
		return wrapList(key, list);
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#put(K, V)
	 */
	@Override
	public boolean put(K key, V value) {
		List<V> list = map.get(key);
		if (list == null) {
			list = createList();
			if (list.add(value)) {
				totalSize++;
				map.put(key, list);
				return true;
			} else {
				throw new AssertionError("New Collection violated the Collection spec");
			}
		} else if (list.add(value)) {
			totalSize++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean put(Map.Entry<K, V> e) {
		return put(e.getKey(), e.getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#remove(K, V)
	 */
	@Override
	public boolean remove(K key, V value) {
		List<V> list = map.get(key);
		if (list == null) {
			return false;
		} else if (list.remove(value)) {
			totalSize--;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(Map.Entry<K, V> e) {
		return remove(e.getKey(), e.getValue());
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#clear()
	 */
	@Override
	public void clear() {
		// Clear each collection, to make previously returned collections empty.
		for (List<V> list : map.values()) {
			list.clear();
		}
		map.clear();
		totalSize = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#values()
	 */
	@Override
	public List<V> values() {
		List<V> list = createList();
		for (K k : map.keySet()) {
			list.addAll(get(k));
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#toString()
	 */
	@Override
	public String toString() {
		return map.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#hashCode()
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof AbstractBucketMap) {
			AbstractBucketMap<?, ?> m = (AbstractBucketMap<?, ?>) object;
			return this.map.equals(m.getMap());
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#iterator()
	 */
	@Override
	public Iterator<V> iterator() {
		return new BucketMapIterator();
	}

	/*
	 * (non-Javadoc)
	 * @see collection.BucketMap#toArray(V[])
	 */
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

	private List<V> wrapList(K key, List<V> list) {
		return new WrappedList(key, list, null);
	}

	private class WrappedList extends AbstractCollection<V> implements List<V> {

		final K key;
		List<V> delegate;
		final WrappedList ancestor;
		final Collection<V> ancestorDelegate;

		WrappedList(K key, List<V> delegate, WrappedList ancestor) {
			this.key = key;
			this.delegate = delegate;
			this.ancestor = ancestor;
			this.ancestorDelegate = (ancestor == null) ? null : ancestor.getDelegate();
		}

		/**
		 * If the delegate collection is empty, but the multimap has values for
		 * the key, replace the delegate with the new collection for the key.
		 * 
		 * <p>
		 * For a subcollection, refresh its ancestor and validate that the
		 * ancestor delegate hasn't changed.
		 */
		void refreshIfEmpty() {
			// If the delegate list is empty it's possible that the key
			// associated
			// to it do not exist in the map but it's possible that the
			// key was subsequently added. This code grant the consistency of
			// the map.
			if (ancestor != null) {
				ancestor.refreshIfEmpty();
				if (ancestor.getDelegate() != ancestorDelegate) {
					throw new ConcurrentModificationException();
				}
			} else if (delegate.isEmpty()) {

				List<V> newDelegate = map.get(key);
				if (newDelegate != null) {
					delegate = newDelegate;
				}
			}
		}

		/**
		 * If collection is empty, remove it from
		 * {@code AbstractMapBasedMultimap.this.map}. For subcollections, check
		 * whether the ancestor collection is empty.
		 */
		void removeIfEmpty() {
			if (ancestor != null) {
				ancestor.removeIfEmpty();
			} else if (delegate.isEmpty()) {
				map.remove(key);
			}
		}

		K getKey() {
			return key;
		}

		/**
		 * Add the delegate to the map. Other {@code WrappedCollection} methods
		 * should call this method after adding elements to a previously empty
		 * collection.
		 * 
		 * <p>
		 * Subcollection add the ancestor's delegate instead.
		 */
		void addToMap() {
			if (ancestor != null) {
				ancestor.addToMap();
			} else {
				map.put(key, delegate);
			}
		}

		@Override
		public int size() {
			refreshIfEmpty();
			return delegate.size();
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) {
				return true;
			}
			refreshIfEmpty();
			return delegate.equals(object);
		}

		@Override
		public int hashCode() {
			refreshIfEmpty();
			return delegate.hashCode();
		}

		@Override
		public String toString() {
			refreshIfEmpty();
			return delegate.toString();
		}

		List<V> getDelegate() {
			return delegate;
		}

		@Override
		public Iterator<V> iterator() {
			refreshIfEmpty();
			return new WrappedIterator();
		}

		@Override
		public boolean add(V value) {
			refreshIfEmpty();
			boolean wasEmpty = delegate.isEmpty();
			boolean changed = delegate.add(value);
			if (changed) {
				totalSize++;
				if (wasEmpty) {
					addToMap();
				}
			}
			return changed;
		}

		@Override
		public boolean addAll(Collection<? extends V> collection) {
			if (collection.isEmpty()) {
				return false;
			}
			int oldSize = size(); // calls refreshIfEmpty
			boolean changed = delegate.addAll(collection);
			if (changed) {
				int newSize = delegate.size();
				totalSize += (newSize - oldSize);
				if (oldSize == 0) {
					addToMap();
				}
			}
			return changed;
		}

		@Override
		public boolean contains(Object o) {
			refreshIfEmpty();
			return delegate.contains(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			refreshIfEmpty();
			return delegate.containsAll(c);
		}

		@Override
		public void clear() {
			int oldSize = size(); // calls refreshIfEmpty
			if (oldSize == 0) {
				return;
			}
			delegate.clear();
			totalSize -= oldSize;
			removeIfEmpty(); // maybe shouldn't be removed if this is a sublist
		}

		@Override
		public boolean remove(Object o) {
			refreshIfEmpty();
			boolean changed = delegate.remove(o);
			if (changed) {
				totalSize--;
				removeIfEmpty();
			}
			return changed;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			if (c.isEmpty()) {
				return false;
			}
			int oldSize = size(); // calls refreshIfEmpty
			boolean changed = delegate.removeAll(c);
			if (changed) {
				int newSize = delegate.size();
				totalSize += (newSize - oldSize);
				removeIfEmpty();
			}
			return changed;
		}

		@Override
		public boolean retainAll(Collection<?> c) {

			if (c == null) {
				throw new NullPointerException();
			}
			int oldSize = size(); // calls refreshIfEmpty
			boolean changed = delegate.retainAll(c);
			if (changed) {
				int newSize = delegate.size();
				totalSize += (newSize - oldSize);
				removeIfEmpty();
			}
			return changed;
		}

		@Override
		public boolean addAll(int index, Collection<? extends V> c) {
			if (c.isEmpty()) {
				return false;
			}
			int oldSize = size(); // calls refreshIfEmpty
			boolean changed = delegate.addAll(index, c);
			if (changed) {
				int newSize = delegate.size();
				totalSize += (newSize - oldSize);
				if (oldSize == 0) {
					addToMap();
				}
			}
			return changed;
		}

		@Override
		public V get(int index) {
			refreshIfEmpty();
			return delegate.get(index);
		}

		@Override
		public V set(int index, V element) {
			refreshIfEmpty();
			return delegate.set(index, element);
		}

		@Override
		public void add(int index, V element) {
			refreshIfEmpty();
			boolean wasEmpty = getDelegate().isEmpty();
			delegate.add(index, element);
			totalSize++;
			if (wasEmpty) {
				addToMap();
			}
		}

		@Override
		public V remove(int index) {
			refreshIfEmpty();
			V value = delegate.remove(index);
			totalSize--;
			removeIfEmpty();
			return value;
		}

		@Override
		public int indexOf(Object o) {
			refreshIfEmpty();
			return delegate.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			refreshIfEmpty();
			return delegate.lastIndexOf(o);
		}

		@Override
		public ListIterator<V> listIterator() {
			refreshIfEmpty();
			return new WrappedListIterator();
		}

		@Override
		public ListIterator<V> listIterator(int index) {
			refreshIfEmpty();
			return new WrappedListIterator(index);
		}

		@Override
		public List<V> subList(int fromIndex, int toIndex) {
			refreshIfEmpty();
			return new WrappedList(getKey(), delegate.subList(fromIndex, toIndex), (ancestor == null) ? this : ancestor);
		}

		/** Collection iterator for {@code WrappedCollection}. */
		private class WrappedIterator implements Iterator<V> {
			final Iterator<V> delegateIterator;
			final List<V> originalDelegate = delegate;

			WrappedIterator() {
				delegateIterator = delegate.listIterator();
			}

			WrappedIterator(Iterator<V> delegateIterator) {
				this.delegateIterator = delegateIterator;
			}

			/**
			 * If the delegate changed since the iterator was created, the
			 * iterator is no longer valid.
			 */
			void validateIterator() {
				refreshIfEmpty();
				if (delegate != originalDelegate) {
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
				delegateIterator.remove();
				totalSize--;
				removeIfEmpty();
			}

			Iterator<V> getDelegateIterator() {
				validateIterator();
				return delegateIterator;
			}
		}

		/**
		 * {@code ListIterator} decorator. This is necessary for grant the
		 * consistency of {@code totalSize} value.
		 * 
		 * Note that the {@link #remove} and {@link #set(Object)} methods are
		 * <i>not</i> defined in terms of the cursor position; they are defined
		 * to operate on the last element returned by a call to {@link #next} or
		 * {@link #previous()}.
		 */
		private class WrappedListIterator extends WrappedIterator implements ListIterator<V> {

			WrappedListIterator() {
			}

			public WrappedListIterator(int index) {
				super(delegate.listIterator(index));
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
	private class BucketMapIterator implements Iterator<V> {

		final Iterator<Map.Entry<K, List<V>>> entryIterator;
		List<V> list;
		Iterator<V> valueIterator;

		protected BucketMapIterator() {
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
