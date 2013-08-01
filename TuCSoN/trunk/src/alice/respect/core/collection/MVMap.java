package alice.respect.core.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A MVmap (multi-value map) is similar to a {@link Map} but, unlike this, it
 * allows to map multiple values ​​for the same {@code key}. This value are
 * stored in a {@link List} associated whit the {@code key}. You can have
 * duplicate values ​​associated with the same key.
 * 
 */
public interface MVMap<K, V> extends Iterable<V> {

	/**
	 * Returns the number of value contains in this Multi-Value Map.
	 * 
	 * The max number of key permitted is {@code Integer.MAX_VALUE} and each key
	 * can contain {@code Integer.MAX_VALUE} value.
	 * 
	 * @return the number of values contained in this map
	 */
	public int size();

	/**
	 * Returns {@code true} if this map contains no key-value mappings.
	 * 
	 * @return {@code true} if this map contains no key-value mappings
	 */
	public boolean isEmpty();

	/**
	 * Returns {@code true} if the MVMap contain at least one value for the
	 * specified key.
	 * 
	 * @param {@code key} - key to search
	 * @return {@code true} - if the MVMap contain at least one value for the
	 *         specified key
	 */
	public boolean containsKey(K key);

	/**
	 * Returns {@code true} if the MVMap contains the specified value for any
	 * key.
	 * 
	 * @param {@code value} - value to search
	 * @return {@code true} - if the MVMap contains the specified value for any
	 *         key
	 */
	public boolean containsValue(V value);

	/**
	 * Returns a {@link List} that contain the value mapped at specified key. If
	 * the key does not contain values (or not existing) it return an empty List
	 * 
	 * Changes to the {@code List} are allowed and they are reflected on the
	 * MVMap and vice versa.
	 * 
	 * @param {@code key} - key to search
	 * @return {@code List<V>} whit the value mapped at specified key if any or
	 *         return an empty list
	 * */
	public List<V> get(K key);

	/**
	 * Associates the specified {@code value} with the specified {@code key} in
	 * this MVMap. Multiple value for each {@code key} are allowed.
	 * 
	 * @param {@code key} - the key
	 * @param {@code value} - the value
	 * @return {@code true} if the {@code value} are correctly added at the
	 *         MVMap. This depend of the specific implementation of List that is
	 *         used.
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
	 * Removes the first occurrence of the specified element from this MVMap.
	 * 
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(K key, V value);

	/**
	 * Removes the first occurrence of the specified element from this MVMap.
	 * 
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(Map.Entry<K, V> e);

	/** Remove all element from the MVMap */
	public void clear();

	/** Return a number of keys collected into MVMap */
	public int getKeysNumber();

	/**
	 * <p>
	 * Return a list of all values contained into MVMap
	 * </p>
	 * WARNING: the returned list should be used in read-only mode because it is
	 * not synchronized with the rest of the map
	 */
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
