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

	// TODO correggere l'estenzione ITERABLE: aggiungere i metodi in questa
	// interfaccia
	// Valutare se è il caso di inserire come interfaccia Collections

	/**
	 * Returns the number of key-value pair in this BucketMap.
	 * 
	 * The max number of key permitted is {@code Integer.MAX_VALUE} and each key
	 * can contain {@code Integer.MAX_VALUE} value.
	 * 
	 * @return the number of key-value mappings in this map
	 */
	public int size();

	/**
	 * Returns {@code true} if this map contains no key-value mappings.
	 * 
	 * @return {@code true} if this map contains no key-value mappings
	 */
	public boolean isEmpty();

	/**
	 * Returns {@code true} if the BucketMap contain at least one value for the
	 * specified key.
	 * 
	 * @param {@code key} - key to search
	 * @return {@code true} - if the BucketMap contain at least one value for
	 *         the specified key
	 */
	public boolean containsKey(K key);

	/**
	 * Returns {@code true} if the BucketMap contains the specified value for
	 * any key.
	 * 
	 * @param {@code value} - value to search
	 * @return {@code true} - if the BucketMap contains the specified value for
	 *         any key
	 */
	public boolean containsValue(V value);

	/**
	 * Returns a {@link List} that contain the value mapped at specified key. If
	 * the key does not contain values (or not existing) it return an empty List
	 * 
	 * Changes to the {@code List} are allowed and they are reflected on the
	 * BucketMap and vice versa.
	 * 
	 * @param {@code key} - key to search
	 * @return {@code List<V>} whit the value mapped at specified key if any or
	 *         return an empty list
	 * */
	public List<V> get(K key);

	/**
	 * Associates the specified {@code value} with the specified {@code key} in
	 * this BucketMap. Multiple value for each {@code key} are allowed.
	 * 
	 * Note: some implementation of {@code List} does not allow duplicates. See
	 * the specific implementation for detail.
	 * 
	 * @param {@code key} - the key
	 * @param {@code value} - the value
	 * @return {@code true} if the {@code value} are correctly added at the
	 *         BucketMap. This depend of the specific implementation of List
	 *         that is used.
	 */
	public boolean put(K key, V value);

	/**
	 * Is the same of the {@link #put(K key, V value)} but the key-value pair is
	 * provided by a {@link Map.Entry} object.
	 * 
	 * @param {@code Map.Entry<K, V>} the key-value pair
	 * @return {@code true} if the {@code value} are correctly added at the
	 *         BucketMap. This depend of the specific implementation of List
	 *         that is used.
	 */
	public boolean put(Map.Entry<K, V> e);

	/**
	 * Removes the first occurrence of the specified element from this
	 * BucketMap.
	 * 
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(K key, V value);

	/**
	 * Removes the first occurrence of the specified element from this
	 * BucketMap.
	 * 
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(Map.Entry<K, V> e);

	/** Remove all element from the BucketMap */
	public void clear();

	public List<V> values();

	@Override
	public String toString();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object object);

	public Iterator<V> iterator();

	public V[] toArray(V[] v);

}
