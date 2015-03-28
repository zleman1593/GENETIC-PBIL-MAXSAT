package AnalysisOfResults;
import java.io.IOException;
import java.util.HashMap;
import Algorithms.TestController;

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
			} else if (differentDouble(crossoverProb, defaultCrossoverProb)) {
				parameters_GA.put(LineNumberGA.CROSSOVER_PROB.getNumVal(), String.valueOf(crossoverProb));
			} else if (differentDouble(mutationProb, defaultMutationProb)) {
				parameters_GA.put(LineNumberGA.MUTATION_PROB.getNumVal(), String.valueOf(mutationProb));
			}
		}
	}
	
	public static void initializeParameters_PBIL() {
		// Initialize HashMap.
		parameters_PBIL = new HashMap<Integer, String>();
		
		// Fixed values.
		int defaultSampleSize = TestController.PBIL_samples[0];
		double defaultLearningRate = TestController.PBIL_learningRate[0];
		double defaultNegLearningRate = TestController.PBIL_negLearningRate[0];
		double defaultMutationProb = TestController.PBIL_mutProb[0]; 
		double defaultMutationShift = TestController.PBIL_mutShift[0];
		
		// Find the values that varied.
		for (int i = 0; i < TestController.PBIL_samples.length; i++) {
			int sampleSize = TestController.PBIL_samples[i];
			double learningRate = TestController.PBIL_learningRate[i];
			double negLearningRate = TestController.PBIL_negLearningRate[i];
			double mutationProb = TestController.PBIL_mutProb[i];
			double mutationShift = TestController.PBIL_mutShift[i];
			
			if (sampleSize != defaultSampleSize) {
				parameters_PBIL.put(LineNumberPBIL.POP_SIZE.getNumVal(), String.valueOf(sampleSize));
			} else if (differentDouble(learningRate, defaultLearningRate)) {
				parameters_PBIL.put(LineNumberPBIL.LEARNING_RATE.getNumVal(), String.valueOf(learningRate));
			} else if (differentDouble(negLearningRate, defaultNegLearningRate)) {
				parameters_PBIL.put(LineNumberPBIL.NEG_LEARNING_RATE.getNumVal(), String.valueOf(negLearningRate));
			} else if (differentDouble(mutationProb, defaultMutationProb)) {
				parameters_PBIL.put(LineNumberPBIL.MUTATION_PROB.getNumVal(), String.valueOf(mutationProb));
			} else if (differentDouble(mutationShift, defaultMutationShift)) {
				parameters_PBIL.put(LineNumberPBIL.MUTATION_SHIFT.getNumVal(), String.valueOf(mutationShift));
			}
		}
	}
	
	// Returns true if the two doubles are different, false otherwise.
	private static boolean differentDouble(double a, double b) {
		return Math.abs(a - b) > EPSILON;
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
