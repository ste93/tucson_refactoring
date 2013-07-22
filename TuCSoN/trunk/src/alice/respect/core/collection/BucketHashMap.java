package alice.respect.core.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 22/lug/2013
 * 
 * @param <K>
 *            the type of the key
 * @param <V>
 *            the type of the value
 */
public class BucketHashMap<K, V> extends AbstractBucketMap<K, V> {

    private static final int INITIAL_CAPACITY_PER_KEY = 2;

    /**
     * 
     */
    public BucketHashMap() {
        super(new HashMap<K, List<V>>());
    }

    @Override
    public List<V> createList() {
        return new ArrayList<V>(BucketHashMap.INITIAL_CAPACITY_PER_KEY);
    }

}
