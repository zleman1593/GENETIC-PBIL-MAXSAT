import java.io.IOException;
import java.util.HashMap;

public class AnalyzeResultsController {
	public static HashMap<String, HashMap<String, String>> results_GA;
	public static HashMap<String, HashMap<String, String>> results_PBIL;
	
	public static void main(String[] args) throws IOException {		
		AnalyzeResults analyzeResults = new AnalyzeResults();

		results_GA = analyzeResults.getParsedResults_GA();
		results_PBIL = analyzeResults.getParsedResults_PBIL();
		
		test("GA");
		test("PBIL");
		
		// Check if any problem is unused.
		analyzeResults.printUnusedProblems();
		
	}
	
	public static void test(String algorithm) {
		HashMap<String, HashMap<String, String>> results; 
		if (algorithm.equalsIgnoreCase("GA")) {
			results = results_GA;
		} else {
			results = results_PBIL;
		}
		
		for (String problem : results.keySet()) {
			HashMap<String, String> map = results.get(problem);
			for (String key : map.keySet()) {
				System.out.println(key);
				System.out.println(map.get(key));
			}
		}
	}

}
