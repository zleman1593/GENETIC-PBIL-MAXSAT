import java.io.IOException;
import java.util.HashMap;

public class AnalyzeResultsController {
	private static HashMap<String, HashMap<String, String>> results_GA;
	private static HashMap<String, HashMap<String, String>> results_PBIL;

	public static void main(String[] args) throws IOException {
		AnalyzeResults analyzeResults = new AnalyzeResults();
		// Get structured results.
		results_GA = analyzeResults.getParsedResults_GA();
		results_PBIL = analyzeResults.getParsedResults_PBIL();
		
		System.out.println("LENGTH GA " + results_GA.keySet().size());
		System.out.println("LENGTH PBIL " + results_PBIL.keySet().size());
		
		// DEBUGGING
		System.out.println("GA");
		for (String problem : results_GA.keySet()) {
			System.out.println(problem);
		}
		
		System.out.println("PBIL");
		for (String problem : results_PBIL.keySet()) {
			System.out.println(problem);
		}
		
		// Print out results.
//		test("GA");
//		test("PBIL");
		// Check if any problem is unused.
		analyzeResults.printUnusedProblems();
		// Write to all graph data files.
		new GenerateGraphData(results_GA, results_PBIL);
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
