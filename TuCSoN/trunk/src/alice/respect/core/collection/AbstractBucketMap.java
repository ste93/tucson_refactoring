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
            this.entryIterator =
                    AbstractBucketMap.this.map.entrySet().iterator();
            if (this.entryIterator.hasNext()) {
                // if it has at least one element in the map, select the first
                this.advanceEntryIterator();
            } else {
                // else use an empty iterator
                this.valueIterator = Collections.emptyIterator();
            }
        }

        /**
         * Returns {@code true} if the iteration has more elements. (In other
         * words, returns {@code true} if {@link #next} would return an element
         * rather than throwing an exception.)
         * 
         * @return {@code true} if the iteration has more elements
         */
        public boolean hasNext() {
            return this.entryIterator.hasNext() || this.valueIterator.hasNext();
        }

        /**
         * Returns the next element in the iteration.
         * 
         * @return the next element in the iteration
         * @throws NoSuchElementException
         *             if the iteration has no more elements
         */
        public V next() {
            if (!this.valueIterator.hasNext()) {
                this.advanceEntryIterator();
            }
            return this.valueIterator.next();
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
        public void remove() {
            this.valueIterator.remove();
            // If the list contains no elements
            // then remove the key from map
            if (this.list.isEmpty()) {
                this.entryIterator.remove();
            }
            AbstractBucketMap.this.totalSize--;
        }

        /**
         * Select the next key in the map (if has any) and update the iterator
         * of list associated with the key.
         */
        private void advanceEntryIterator() {
            final Map.Entry<K, List<V>> entry = this.entryIterator.next();
            this.list = entry.getValue();
            this.valueIterator = this.list.iterator();
        }
    }

    private class WrappedList extends AbstractCollection<V> implements List<V> {

        /** Collection iterator for {@code WrappedCollection}. */
        private class WrappedIterator implements Iterator<V> {
            final Iterator<V> delegateIterator;
            final List<V> originalDelegate = WrappedList.this.delegate;

            WrappedIterator() {
                this.delegateIterator =
                        WrappedList.this.delegate.listIterator();
            }

            WrappedIterator(final Iterator<V> di) {
                this.delegateIterator = di;
            }

            public boolean hasNext() {
                this.validateIterator();
                return this.delegateIterator.hasNext();
            }

            public V next() {
                this.validateIterator();
                return this.delegateIterator.next();
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
            public void remove() {
                this.delegateIterator.remove();
                AbstractBucketMap.this.totalSize--;
                WrappedList.this.removeIfEmpty();
            }

            Iterator<V> getDelegateIterator() {
                this.validateIterator();
                return this.delegateIterator;
            }

            /**
             * If the delegate changed since the iterator was created, the
             * iterator is no longer valid.
             */
            void validateIterator() {
                WrappedList.this.refreshIfEmpty();
                if (WrappedList.this.delegate != this.originalDelegate) {
                    throw new ConcurrentModificationException();
                }
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
        private class WrappedListIterator extends WrappedIterator implements
                ListIterator<V> {

            public WrappedListIterator(final int index) {
                super(WrappedList.this.delegate.listIterator(index));
            }

            WrappedListIterator() {
            }

            public void add(final V value) {
                final boolean wasEmpty = WrappedList.this.isEmpty();
                this.getDelegateListIterator().add(value);
                AbstractBucketMap.this.totalSize++;
                if (wasEmpty) {
                    WrappedList.this.addToMap();
                }
            }

            public boolean hasPrevious() {
                return this.getDelegateListIterator().hasPrevious();
            }

            public int nextIndex() {
                return this.getDelegateListIterator().nextIndex();
            }

            public V previous() {
                return this.getDelegateListIterator().previous();
            }

            public int previousIndex() {
                return this.getDelegateListIterator().previousIndex();
            }

            public void set(final V value) {
                this.getDelegateListIterator().set(value);
            }

            private ListIterator<V> getDelegateListIterator() {
                return (ListIterator<V>) this.getDelegateIterator();
            }
        }// END WrappedListIterator

        final WrappedList ancestor;
        final Collection<V> ancestorDelegate;

        List<V> delegate;

        final K key;

        WrappedList(final K k, final List<V> d, final WrappedList a) {
            this.key = k;
            this.delegate = d;
            this.ancestor = a;
            this.ancestorDelegate = (a == null) ? null : a.getDelegate();
        }

        public void add(final int index, final V element) {
            this.refreshIfEmpty();
            final boolean wasEmpty = this.getDelegate().isEmpty();
            this.delegate.add(index, element);
            AbstractBucketMap.this.totalSize++;
            if (wasEmpty) {
                this.addToMap();
            }
        }

        @Override
        public boolean add(final V value) {
            this.refreshIfEmpty();
            final boolean wasEmpty = this.delegate.isEmpty();
            final boolean changed = this.delegate.add(value);
            if (changed) {
                AbstractBucketMap.this.totalSize++;
                if (wasEmpty) {
                    this.addToMap();
                }
            }
            return changed;
        }

        @Override
        public boolean addAll(final Collection<? extends V> collection) {
            if (collection.isEmpty()) {
                return false;
            }
            final int oldSize = this.size(); // calls refreshIfEmpty
            final boolean changed = this.delegate.addAll(collection);
            if (changed) {
                final int newSize = this.delegate.size();
                AbstractBucketMap.this.totalSize += (newSize - oldSize);
                if (oldSize == 0) {
                    this.addToMap();
                }
            }
            return changed;
        }

        public boolean addAll(final int index, final Collection<? extends V> c) {
            if (c.isEmpty()) {
                return false;
            }
            final int oldSize = this.size(); // calls refreshIfEmpty
            final boolean changed = this.delegate.addAll(index, c);
            if (changed) {
                final int newSize = this.delegate.size();
                AbstractBucketMap.this.totalSize += (newSize - oldSize);
                if (oldSize == 0) {
                    this.addToMap();
                }
            }
            return changed;
        }

        @Override
        public void clear() {
            final int oldSize = this.size(); // calls refreshIfEmpty
            if (oldSize == 0) {
                return;
            }
            this.delegate.clear();
            AbstractBucketMap.this.totalSize -= oldSize;
            this.removeIfEmpty(); // maybe shouldn't be removed if this is a
                                  // sublist
        }

        @Override
        public boolean contains(final Object o) {
            this.refreshIfEmpty();
            return this.delegate.contains(o);
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            this.refreshIfEmpty();
            return this.delegate.containsAll(c);
        }

        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            this.refreshIfEmpty();
            return this.delegate.equals(object);
        }

        public V get(final int index) {
            this.refreshIfEmpty();
            return this.delegate.get(index);
        }

        @Override
        public int hashCode() {
            this.refreshIfEmpty();
            return this.delegate.hashCode();
        }

        public int indexOf(final Object o) {
            this.refreshIfEmpty();
            return this.delegate.indexOf(o);
        }

        @Override
        public Iterator<V> iterator() {
            this.refreshIfEmpty();
            return new WrappedIterator();
        }

        public int lastIndexOf(final Object o) {
            this.refreshIfEmpty();
            return this.delegate.lastIndexOf(o);
        }

        public ListIterator<V> listIterator() {
            this.refreshIfEmpty();
            return new WrappedListIterator();
        }

        public ListIterator<V> listIterator(final int index) {
            this.refreshIfEmpty();
            return new WrappedListIterator(index);
        }

        public V remove(final int index) {
            this.refreshIfEmpty();
            final V value = this.delegate.remove(index);
            AbstractBucketMap.this.totalSize--;
            this.removeIfEmpty();
            return value;
        }

        @Override
        public boolean remove(final Object o) {
            this.refreshIfEmpty();
            final boolean changed = this.delegate.remove(o);
            if (changed) {
                AbstractBucketMap.this.totalSize--;
                this.removeIfEmpty();
            }
            return changed;
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            if (c.isEmpty()) {
                return false;
            }
            final int oldSize = this.size(); // calls refreshIfEmpty
            final boolean changed = this.delegate.removeAll(c);
            if (changed) {
                final int newSize = this.delegate.size();
                AbstractBucketMap.this.totalSize += (newSize - oldSize);
                this.removeIfEmpty();
            }
            return changed;
        }

        @Override
        public boolean retainAll(final Collection<?> c) {

            if (c == null) {
                throw new NullPointerException();
            }
            final int oldSize = this.size(); // calls refreshIfEmpty
            final boolean changed = this.delegate.retainAll(c);
            if (changed) {
                final int newSize = this.delegate.size();
                AbstractBucketMap.this.totalSize += (newSize - oldSize);
                this.removeIfEmpty();
            }
            return changed;
        }

        public V set(final int index, final V element) {
            this.refreshIfEmpty();
            return this.delegate.set(index, element);
        }

        @Override
        public int size() {
            this.refreshIfEmpty();
            return this.delegate.size();
        }

        public List<V> subList(final int fromIndex, final int toIndex) {
            this.refreshIfEmpty();
            return new WrappedList(this.getKey(), this.delegate.subList(
                    fromIndex, toIndex), (this.ancestor == null) ? this
                    : this.ancestor);
        }

        @Override
        public String toString() {
            this.refreshIfEmpty();
            return this.delegate.toString();
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
            if (this.ancestor != null) {
                this.ancestor.addToMap();
            } else {
                AbstractBucketMap.this.map.put(this.key, this.delegate);
            }
        }

        List<V> getDelegate() {
            return this.delegate;
        }

        K getKey() {
            return this.key;
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
            if (this.ancestor != null) {
                this.ancestor.refreshIfEmpty();
                if (this.ancestor.getDelegate() != this.ancestorDelegate) {
                    throw new ConcurrentModificationException();
                }
            } else if (this.delegate.isEmpty()) {

                final List<V> newDelegate =
                        AbstractBucketMap.this.map.get(this.key);
                if (newDelegate != null) {
                    this.delegate = newDelegate;
                }
            }
        }

        /**
         * If collection is empty, remove it from
         * {@code AbstractMapBasedMultimap.this.map}. For subcollections, check
         * whether the ancestor collection is empty.
         */
        void removeIfEmpty() {
            if (this.ancestor != null) {
                this.ancestor.removeIfEmpty();
            } else if (this.delegate.isEmpty()) {
                AbstractBucketMap.this.map.remove(this.key);
            }
        }

    }// END WrappedList

    private final Map<K, List<V>> map;

    private int totalSize;

    protected AbstractBucketMap(final Map<K, List<V>> m) {
        this.map = m;
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#clear()
     */
    public void clear() {
        // Clear each collection, to make previously returned collections empty.
        for (final List<V> list : this.map.values()) {
            list.clear();
        }
        this.map.clear();
        this.totalSize = 0;
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#containsKey(K)
     */
    public boolean containsKey(final K key) {
        return this.map.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#containsValue(V)
     */
    public boolean containsValue(final V value) {
        for (final K k : this.map.keySet()) {
            if (this.map.get(k).contains(value)) {
                return true;
            }
        }
        return false;
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
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractBucketMap) {
            final AbstractBucketMap<?, ?> m = (AbstractBucketMap<?, ?>) object;
            return this.map.equals(m.getMap());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#get(K)
     */
    public List<V> get(final K key) {
        List<V> list = this.map.get(key);
        if (list == null) {
            list = this.createList();
        }
        return this.wrapList(key, list);
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
        return this.map.hashCode();
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#isEmpty()
     */
    public boolean isEmpty() {
        return (this.totalSize == 0);
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#iterator()
     */
    public Iterator<V> iterator() {
        return new BucketMapIterator();
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#put(K, V)
     */
    public boolean put(final K key, final V value) {
        List<V> list = this.map.get(key);
        if (list == null) {
            list = this.createList();
            if (list.add(value)) {
                this.totalSize++;
                this.map.put(key, list);
                return true;
            }
            throw new AssertionError(
                    "New Collection violated the Collection spec");
        } else if (list.add(value)) {
            this.totalSize++;
            return true;
        } else {
            return false;
        }
    }

    public boolean put(final Map.Entry<K, V> e) {
        return this.put(e.getKey(), e.getValue());
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#remove(K, V)
     */
    public boolean remove(final K key, final V value) {
        final List<V> list = this.map.get(key);
        if (list == null) {
            return false;
        } else if (list.remove(value)) {
            this.totalSize--;
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(final Map.Entry<K, V> e) {
        return this.remove(e.getKey(), e.getValue());
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#size()
     */
    public int size() {
        return this.totalSize;
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#toArray(V[])
     */
    public V[] toArray(final V[] v) {
        // Estimate size of array; be prepared to see more or fewer elements
        final int size = this.totalSize;
        final V[] ret =
                v.length >= size ? v : (V[]) java.lang.reflect.Array
                        .newInstance(v.getClass().getComponentType(), size);
        final Iterator<V> it = this.iterator();

        for (int i = 0; i < ret.length; i++) {
            if (!it.hasNext()) { // fewer elements than expected
                if (v != ret) {
                    return Arrays.copyOf(ret, i);
                }
                ret[i] = null; // null-terminate
                return ret;
            }
            ret[i] = it.next();
        }
        return ret;
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
        return this.map.toString();
    }

    /*
     * (non-Javadoc)
     * @see collection.BucketMap#values()
     */
    public List<V> values() {
        final List<V> list = this.createList();
        for (final K k : this.map.keySet()) {
            list.addAll(this.get(k));
        }
        return list;
    }

    private Map<K, List<V>> getMap() {
        return this.map;
    }

    private List<V> wrapList(final K key, final List<V> list) {
        return new WrappedList(key, list, null);
    }

    abstract List<V> createList();

}
