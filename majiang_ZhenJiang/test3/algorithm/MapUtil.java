package algorithm;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
//http://blog.csdn.net/wangtao6791842/article/details/12904467#
public class MapUtil {

	public static void main(String[] args) {
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("A", 99.5);
		map.put("B", 67.4);
		map.put("C", 67.4);
		map.put("D", 67.3);
		
		Map<String, Double> sortByValue = sortByValue(map);
		System.out.println(sortByValue);
	}

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				if((o1.getValue()).compareTo(o2.getValue())>0){
					return -1;
				}else{
					return 1;
				}
			}
		});

		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}