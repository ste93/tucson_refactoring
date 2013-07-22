package alice.respect.core.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BucketHashMap<K, V> extends AbstractBucketMap<K, V> {

    private final static int INITIAL_CAPACITY_PER_KEY = 2;

    public BucketHashMap() {
        super(new HashMap<K, List<V>>());
    }

    @Override
    public List<V> createList() {
        return new ArrayList<V>(BucketHashMap.INITIAL_CAPACITY_PER_KEY);
    }

}
