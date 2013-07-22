package alice.respect.core.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 22/lug/2013
 * 
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 */
public class BucketTreeMap<K, V> extends AbstractBucketMap<K, V> {

    private static final int INITIAL_CAPACITY_PER_KEY = 2;

    /**
     * 
     */
    public BucketTreeMap() {
        super(new TreeMap<K, List<V>>());
    }

    @Override
    public List<V> createList() {
        return new ArrayList<V>(BucketTreeMap.INITIAL_CAPACITY_PER_KEY);
    }

}
