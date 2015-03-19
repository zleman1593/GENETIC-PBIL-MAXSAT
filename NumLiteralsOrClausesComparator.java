import java.util.Comparator;
import java.util.HashMap;

public class NumLiteralsOrClausesComparator implements Comparator<String> {
		HashMap<String, HashMap<String, String>> map;
		String key;
	 
		public NumLiteralsOrClausesComparator(HashMap<String, HashMap<String, String>> map, 
				String key) {
			this.map = map;
			this.key = key;
		}
	 
		public int compare(String problemA, String problemB) {
			String valueA = map.get(problemA).get(key);
			String valueB = map.get(problemB).get(key);
			return Integer.parseInt(valueA) - (Integer.parseInt(valueB));
		}
}
