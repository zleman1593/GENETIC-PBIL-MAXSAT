package AnalysisOfResults;
import java.io.IOException;
import java.util.HashMap;

public class AnalyzeResultsController {
	private static HashMap<String, HashMap<String, String>> results_GA;
	private static HashMap<String, HashMap<String, String>> results_PBIL;
	private static HashMap<String, HashMap<String, String>> results_GA_Parameters;
	private static HashMap<String, HashMap<String, String>> results_PBIL_Parameters;

	public static void main(String[] args) throws IOException {
		/* Run general analysis. */
		AnalyzeResults analyzeResults = new AnalyzeResults(null, null);
		// Get structured results.
		results_GA = analyzeResults.getParsedResults_GA();
		results_PBIL = analyzeResults.getParsedResults_PBIL();
		// Write to all graph data files.
		new GenerateGraphData(results_GA, results_PBIL);
		
		
		/* Run parameter analysis. */
		AnalyzeResults analyzeResultsParameters = new AnalyzeResults(null, null);
		// Get structured results.
		results_GA_Parameters = analyzeResultsParameters.getParsedResults_GA();
		results_PBIL_Parameters = analyzeResultsParameters.getParsedResults_PBIL();
		// Write to all graph data files.
		new GenerateGraphData(results_GA_Parameters, results_PBIL_Parameters);
	}

	// Print out the fields in the results objects.
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
