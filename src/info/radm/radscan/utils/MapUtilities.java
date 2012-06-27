package info.radm.radscan.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Based on one solution to the discussion:
 * http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
 *  
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 *
 */
public class MapUtilities {

	public static <K, V extends Comparable<V>> List<Entry<K, V>> sortByValue(Map<K, V> map) {
	        List<Entry<K, V>> entries = new ArrayList<Entry<K, V>>(map.entrySet());
	        Collections.sort(entries, new ByValue<K, V>());
	        return entries;
	}
	
	private static class ByValue<K, V extends Comparable<V>> implements Comparator<Entry<K, V>> {
	        public int compare(Entry<K, V> o1, Entry<K, V> o2) {
	                return o2.getValue().compareTo(o1.getValue());
	        }
	}
}