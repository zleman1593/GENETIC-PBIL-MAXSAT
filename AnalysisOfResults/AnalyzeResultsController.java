package AnalysisOfResults;
import java.io.IOException;
import java.util.HashMap;

import sun.java2d.loops.ProcessPath.EndSubPathHandler;
import Algorithms.TestController;

import com.sun.corba.se.impl.orb.ParserTable.TestContactInfoListFactory;

public class AnalyzeResultsController {
	// Used for comparing doubles.
	private static final double EPSILON = 0.00001;
	// Store the results.
	private static HashMap<String, HashMap<String, String>> results_GA;
	private static HashMap<String, HashMap<String, String>> results_PBIL;
	private static HashMap<String, HashMap<String, String>> results_GA_Parameters;
	private static HashMap<String, HashMap<String, String>> results_PBIL_Parameters;
	// Store the parameters and their respective values.
	private static HashMap<Integer, String> parameters_GA;
	private static HashMap<Integer, String> parameters_PBIL;
	

	public static void main(String[] args) throws IOException {
		/* Run general analysis. */
		AnalyzeResults analyzeResults = new AnalyzeResults(AnalyzeResults.NO_DATA, null);
		// Get structured results.
		results_GA = analyzeResults.getParsedResults_GA();
		results_PBIL = analyzeResults.getParsedResults_PBIL();
		// Write to all graph data files.
		new GenerateGraphData(results_GA, results_PBIL);
		
		
		/* Run parameter analysis. */
		AnalyzeResults analyzeResultsParameters = new AnalyzeResults(AnalyzeResults.NO_DATA, null);
		// Get structured results.
		results_GA_Parameters = analyzeResultsParameters.getParsedResults_GA();
		results_PBIL_Parameters = analyzeResultsParameters.getParsedResults_PBIL();
		// Write to all graph data files.
		new GenerateGraphData(results_GA_Parameters, results_PBIL_Parameters);
	}
	
	public static void initializeParameters_GA() {
		// Initialize HashMap.
		parameters_GA = new HashMap<Integer, String>();
		
		// Fixed values.
		int defaultPopSize = TestController.popSize[0];
		String defaultSelectionType = TestController.selectionType[0];
		String defaultCrossoverType = TestController.crossoverType[0];
		double defaultCrossoverProb = TestController.crossoverProb[0]; 
		double defaultMutationProb = TestController.mutationProb[0];
		
		// Find the values that varied.
		for (int i = 0; i < TestController.popSize.length; i++) {
			int popSize = TestController.popSize[i];
			String selectionType = TestController.selectionType[i];
			String crossoverType = TestController.crossoverType[i];
			double crossoverProb = TestController.crossoverProb[i];
			double mutationProb = TestController.mutationProb[i];
			
			if (popSize != defaultPopSize) {
				parameters_GA.put(LineNumberGA.POP_SIZE.getNumVal(), String.valueOf(popSize));
			} else if (!selectionType.equalsIgnoreCase(defaultSelectionType)) {
				parameters_GA.put(LineNumberGA.SELECTION_TYPE.getNumVal(), selectionType);
			} else if (!crossoverType.equalsIgnoreCase(defaultCrossoverType)) {
				parameters_GA.put(LineNumberGA.CROSSOVER_TYPE.getNumVal(), crossoverType);
			} else if (Math.abs(crossoverProb - defaultCrossoverProb) > EPSILON) {
				parameters_GA.put(LineNumberGA.CROSSOVER_PROB.getNumVal(), String.valueOf(crossoverProb));
			} else if (Math.abs(mutationProb - defaultMutationProb) > EPSILON) {
				parameters_GA.put(LineNumberGA.MUTATION_PROB.getNumVal(), String.valueOf(mutationProb));
			}
		}
			
		
		
		
		
		
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
