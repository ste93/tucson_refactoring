package alice.respect.core.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A BucketMap is similar to a {@link Map} but, unlike this, it allows to map
 * multiple values ​​for the same {@code key}. This value are stored in a
 * {@link List} associated whit the {@code key}.
 */
public interface BucketMap<K, V> extends Iterable<V> {

    /** Remove all element from the BucketMap */
    void clear();

    /**
     * Returns {@code true} if the BucketMap contain at least one value for the
     * specified key.
     * 
     * @param key
     *            - key to search
     * @return {@code true} - if the BucketMap contain at least one value for
     *         the specified key
     */
    boolean containsKey(K key);

    /**
     * Returns {@code true} if the BucketMap contains the specified value for
     * any key.
     * 
     * @param value
     *            - value to search
     * @return {@code true} - if the BucketMap contains the specified value for
     *         any key
     */
    boolean containsValue(V value);

    @Override
    boolean equals(Object object);

    /**
     * Returns a {@link List} that contain the value mapped at specified key. If
     * the key does not contain values (or not existing) it return an empty List
     * 
     * Changes to the {@code List} are allowed and they are reflected on the
     * BucketMap and vice versa.
     * 
     * @param key
     *            - key to search
     * @return {@code List<V>} whit the value mapped at specified key if any or
     *         return an empty list
     * */
    List<V> get(K key);

    @Override
    int hashCode();

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     * 
     * @return {@code true} if this map contains no key-value mappings
     */
    boolean isEmpty();

    Iterator<V> iterator();

    /**
     * Associates the specified {@code value} with the specified {@code key} in
     * this BucketMap. Multiple value for each {@code key} are allowed.
     * 
     * Note: some implementation of {@code List} does not allow duplicates. See
     * the specific implementation for detail.
     * 
     * @param key
     *            - the key
     * @param value
     *            - the value
     * @return {@code true} if the {@code value} are correctly added at the
     *         BucketMap. This depend of the specific implementation of List
     *         that is used.
     */
    boolean put(K key, V value);

    /**
     * Is the same of the {@link #put(Object key, Object value)} but the
     * key-value pair is provided by a {@link java.util.Map.Entry} object.
     * 
     * 
     * @return {@code true} if the {@code value} are correctly added at the
     *         BucketMap. This depend of the specific implementation of List
     *         that is used.
     */
    boolean put(Map.Entry<K, V> e);

    /**
     * Removes the first occurrence of the specified element from this
     * BucketMap.
     * 
     * @return <tt>true</tt> if this list contained the specified element
     */
    boolean remove(K key, V value);

    /**
     * Removes the first occurrence of the specified element from this
     * BucketMap.
     * 
     * @return <tt>true</tt> if this list contained the specified element
     */
    boolean remove(Map.Entry<K, V> e);

    /**
     * Returns the number of key-value pair in this BucketMap.
     * 
     * The max number of key permitted is {@code Integer.MAX_VALUE} and each key
     * can contain {@code Integer.MAX_VALUE} value.
     * 
     * @return the number of key-value mappings in this map
     */
    int size();

    V[] toArray(V[] v);

    @Override
    String toString();

    // TODO CICORA: vedere se è da rimuovere
    List<V> values();

}
