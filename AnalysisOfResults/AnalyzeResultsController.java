package AnalysisOfResults;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Algorithms.TestController;

public class AnalyzeResultsController {
	// Used for comparing doubles.
	private static final double EPSILON = 0.00001;
	// Store the results.
	private static HashMap<String, HashMap<String, String>> results_GA;
	private static HashMap<String, HashMap<String, String>> results_PBIL;
	private static HashMap<String, HashMap<String, String>> results_Parameters_GA;
	private static HashMap<String, HashMap<String, String>> results_Parameters_PBIL;
	// Store the parameters and their respective values.
	private static HashMap<Integer, ArrayList<String>> parameters_GA;
	private static HashMap<Integer, ArrayList<String>> parameters_PBIL;
	

	public static void main(String[] args) throws IOException {
		/* Run general analysis. */
//		AnalyzeResults analyzeResults_GA = new AnalyzeResults("GA", AnalyzeResults.NO_DATA, null);
//		results_GA = analyzeResults_GA.getParsedResults_GA();
//		AnalyzeResults analyzeResults_PBIL = new AnalyzeResults("PBIL", AnalyzeResults.NO_DATA, null);
//		results_PBIL = analyzeResults_PBIL.getParsedResults_PBIL();
//		// Write to all graph data files.
//		new GenerateGraphData(results_GA, results_PBIL);
//		
		
		initializeParameters_GA();
		/* Run parameter analysis. */
		for (Integer lineNum : parameters_GA.keySet()) {
			
			// DEBUGGING
			System.out.println("Line number " + lineNum);
			System.out.println("Value " + parameters_GA.get(lineNum));
			
			AnalyzeResults analyzeResultsParameters_GA = new AnalyzeResults("GA", lineNum, 
					parameters_GA.get(lineNum));
			results_Parameters_GA = analyzeResultsParameters_GA.getParsedResults_GA();
			
		}
		
		// DEBUGGING
		test("GA");
		
		
		initializeParameters_PBIL();
		for (Integer lineNum : parameters_PBIL.keySet()) {
			AnalyzeResults analyzeResultsParameters_PBIL = new AnalyzeResults("PBIL", lineNum, 
					parameters_PBIL.get(lineNum));
			results_Parameters_PBIL = analyzeResultsParameters_PBIL.getParsedResults_PBIL();
		}
		// Write to all graph data files.
//		new GenerateGraphData(results_Parameters_GA, results_Parameters_PBIL);
	}
	
	public static void initializeParameters_GA() {
		// Initialize HashMap.
		parameters_GA = new HashMap<Integer, ArrayList<String>>();
		
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
			
			ArrayList<String> values = new ArrayList<String>();
			if (popSize != defaultPopSize) {
				addValueToMapGA(LineNumberPBIL.POP_SIZE.getNumVal(), String.valueOf(popSize), values);
				parameters_GA.put(LineNumberGA.POP_SIZE.getNumVal(), values);
			} else if (!selectionType.equalsIgnoreCase(defaultSelectionType)) {
				addValueToMapGA(LineNumberGA.SELECTION_TYPE.getNumVal(), selectionType, values);
				parameters_GA.put(LineNumberGA.SELECTION_TYPE.getNumVal(), values);
			} else if (!crossoverType.equalsIgnoreCase(defaultCrossoverType)) {
				addValueToMapGA(LineNumberGA.CROSSOVER_TYPE.getNumVal(), crossoverType, values);
				parameters_GA.put(LineNumberGA.CROSSOVER_TYPE.getNumVal(), values);
			} else if (differentDouble(crossoverProb, defaultCrossoverProb)) {
				addValueToMapGA(LineNumberGA.CROSSOVER_PROB.getNumVal(), String.valueOf(crossoverProb), values);
				parameters_GA.put(LineNumberGA.CROSSOVER_PROB.getNumVal(), values);
			} else if (differentDouble(mutationProb, defaultMutationProb)) {
				addValueToMapGA(LineNumberGA.MUTATION_PROB.getNumVal(), String.valueOf(mutationProb), values);
				parameters_GA.put(LineNumberGA.MUTATION_PROB.getNumVal(), values);
			}
		}
	}
	
	public static void initializeParameters_PBIL() {
		// Initialize HashMap.
		parameters_PBIL = new HashMap<Integer, ArrayList<String>>();
		
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
			
			ArrayList<String> values = new ArrayList<String>();
			if (sampleSize != defaultSampleSize) {
				addValueToMapPBIL(LineNumberPBIL.POP_SIZE.getNumVal(), String.valueOf(sampleSize), values);
				parameters_PBIL.put(LineNumberPBIL.POP_SIZE.getNumVal(), values);
			} else if (differentDouble(learningRate, defaultLearningRate)) {
				addValueToMapPBIL(LineNumberPBIL.LEARNING_RATE.getNumVal(), String.valueOf(learningRate), values);
				parameters_PBIL.put(LineNumberPBIL.LEARNING_RATE.getNumVal(), values);
			} else if (differentDouble(negLearningRate, defaultNegLearningRate)) {
				addValueToMapPBIL(LineNumberPBIL.NEG_LEARNING_RATE.getNumVal(), String.valueOf(negLearningRate), values);
				parameters_PBIL.put(LineNumberPBIL.NEG_LEARNING_RATE.getNumVal(), values);
			} else if (differentDouble(mutationProb, defaultMutationProb)) {
				addValueToMapPBIL(LineNumberPBIL.MUTATION_PROB.getNumVal(), String.valueOf(mutationProb), values);
				parameters_PBIL.put(LineNumberPBIL.MUTATION_PROB.getNumVal(), values);
			} else if (differentDouble(mutationShift, defaultMutationShift)) {
				addValueToMapPBIL(LineNumberPBIL.MUTATION_SHIFT.getNumVal(), String.valueOf(mutationShift), values);
				parameters_PBIL.put(LineNumberPBIL.MUTATION_SHIFT.getNumVal(), values);
			}
		}
	}
	
	
	private static void addValueToMapPBIL(Integer key, String val, ArrayList<String> values) {
		if (parameters_PBIL.containsKey(key)) {
			values = parameters_PBIL.get(key);
		}
		values.add(String.valueOf(val));
	}
	
	private static void addValueToMapGA(Integer key, String val, ArrayList<String> values) {
		if (parameters_GA.containsKey(key)) {
			values = parameters_GA.get(key);
		}
		values.add(String.valueOf(val));
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
