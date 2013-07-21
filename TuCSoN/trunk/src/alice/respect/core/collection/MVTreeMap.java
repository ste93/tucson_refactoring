package alice.respect.core.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MVTreeMap<K, V> extends AbstractMVmap<K, V> {

	private final static int INITIAL_CAPACITY_PER_KEY = 2;

	public MVTreeMap() {
		super(new TreeMap<K, List<V>>());
	}

	@Override
	List<V> createList() {
		return new ArrayList<V>(INITIAL_CAPACITY_PER_KEY);
	}

}
