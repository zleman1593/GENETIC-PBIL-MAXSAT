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
		// GA
//		AnalyzeResults analyzeResults_GA = new AnalyzeResults("GA", AnalyzeResults.NO_DATA, null);
//		results_GA = analyzeResults_GA.getParsedResults_GA();
//		new GenerateGraphData(results_GA, "GA");
//		// PBIL
//		AnalyzeResults analyzeResults_PBIL = new AnalyzeResults("PBIL", AnalyzeResults.NO_DATA, null);
//		results_PBIL = analyzeResults_PBIL.getParsedResults_PBIL();
//		new GenerateGraphData(results_PBIL, "PBIL");
		
		/* Run parameter analysis. */
		// GA
		initializeParameters_GA();
		for (Integer lineNum : parameters_GA.keySet()) {
			for (String value: parameters_GA.get(lineNum)) {
				AnalyzeResults analyzeResultsParameters_GA = new AnalyzeResults("GA", lineNum, value);
				results_Parameters_GA = analyzeResultsParameters_GA.getParsedResults_GA();
				new GenerateGraphData(results_Parameters_GA, "GA");
			}
		}
		
		// DEBUGGING
		test(results_Parameters_GA);
		
		// PBIL
		initializeParameters_PBIL();
		for (Integer lineNum : parameters_PBIL.keySet()) {
			for (String value: parameters_PBIL.get(lineNum)) {
				AnalyzeResults analyzeResultsParameters_PBIL = new AnalyzeResults("PBIL", lineNum, value);
				results_Parameters_PBIL = analyzeResultsParameters_PBIL.getParsedResults_PBIL();
				new GenerateGraphData(results_Parameters_PBIL, "PBIL");
			}
		}
		
		// DEBUGGING
		test(results_Parameters_PBIL);
		
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
				if (parameters_GA.containsKey(LineNumberGA.POP_SIZE.getNumVal())) {
					values = parameters_GA.get(LineNumberGA.POP_SIZE.getNumVal());
				}
				values.add(String.valueOf(popSize));
				parameters_GA.put(LineNumberGA.POP_SIZE.getNumVal(), values);
			} else if (!selectionType.equalsIgnoreCase(defaultSelectionType)) {
				if (parameters_GA.containsKey(LineNumberGA.SELECTION_TYPE.getNumVal())) {
					values = parameters_GA.get(LineNumberGA.SELECTION_TYPE.getNumVal());
				}
				values.add(selectionType);
				parameters_GA.put(LineNumberGA.SELECTION_TYPE.getNumVal(), values);
			} else if (!crossoverType.equalsIgnoreCase(defaultCrossoverType)) {
				if (parameters_GA.containsKey(LineNumberGA.CROSSOVER_TYPE.getNumVal())) {
					values = parameters_GA.get(LineNumberGA.CROSSOVER_TYPE.getNumVal());
				}
				values.add(crossoverType);
				parameters_GA.put(LineNumberGA.CROSSOVER_TYPE.getNumVal(), values);
			} else if (differentDouble(crossoverProb, defaultCrossoverProb)) {
				if (parameters_GA.containsKey(LineNumberGA.CROSSOVER_PROB.getNumVal())) {
					values = parameters_GA.get(LineNumberGA.CROSSOVER_PROB.getNumVal());
				}
				values.add(String.valueOf(crossoverProb));
				parameters_GA.put(LineNumberGA.CROSSOVER_PROB.getNumVal(), values);
			} else if (differentDouble(mutationProb, defaultMutationProb)) {
				if (parameters_GA.containsKey(LineNumberGA.MUTATION_PROB.getNumVal())) {
					values = parameters_GA.get(LineNumberGA.MUTATION_PROB.getNumVal());
				}
				values.add(String.valueOf(mutationProb));
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
				if (parameters_PBIL.containsKey(LineNumberPBIL.POP_SIZE.getNumVal())) {
					values = parameters_PBIL.get(LineNumberPBIL.POP_SIZE.getNumVal());
				}
				values.add(String.valueOf(sampleSize));
				parameters_PBIL.put(LineNumberPBIL.POP_SIZE.getNumVal(), values);
			} else if (differentDouble(learningRate, defaultLearningRate)) {
				if (parameters_PBIL.containsKey(LineNumberPBIL.LEARNING_RATE.getNumVal())) {
					values = parameters_PBIL.get(LineNumberPBIL.LEARNING_RATE.getNumVal());
				}
				values.add(String.valueOf(learningRate));
				parameters_PBIL.put(LineNumberPBIL.LEARNING_RATE.getNumVal(), values);
			} else if (differentDouble(negLearningRate, defaultNegLearningRate)) {
				if (parameters_PBIL.containsKey(LineNumberPBIL.NEG_LEARNING_RATE.getNumVal())) {
					values = parameters_PBIL.get(LineNumberPBIL.NEG_LEARNING_RATE.getNumVal());
				}
				values.add(String.valueOf(negLearningRate));
				parameters_PBIL.put(LineNumberPBIL.NEG_LEARNING_RATE.getNumVal(), values);
			} else if (differentDouble(mutationProb, defaultMutationProb)) {
				if (parameters_PBIL.containsKey(LineNumberPBIL.MUTATION_PROB.getNumVal())) {
					values = parameters_PBIL.get(LineNumberPBIL.MUTATION_PROB.getNumVal());
				}
				values.add(String.valueOf(mutationProb));
				parameters_PBIL.put(LineNumberPBIL.MUTATION_PROB.getNumVal(), values);
			} else if (differentDouble(mutationShift, defaultMutationShift)) {
				if (parameters_PBIL.containsKey(LineNumberPBIL.MUTATION_SHIFT.getNumVal())) {
					values = parameters_PBIL.get(LineNumberPBIL.MUTATION_SHIFT.getNumVal());
				}
				values.add(String.valueOf(mutationShift));				
				parameters_PBIL.put(LineNumberPBIL.MUTATION_SHIFT.getNumVal(), values);
			}
		}
	}
	
	// Returns true if the two doubles are different, false otherwise.
	private static boolean differentDouble(double a, double b) {
		return Math.abs(a - b) > EPSILON;
	}

	// Print out the fields in the results objects.
	public static void test(HashMap<String, HashMap<String, String>> results) {
		for (String problem : results.keySet()) {
			HashMap<String, String> map = results.get(problem);
			for (String key : map.keySet()) {
				System.out.println("KEY " + key);
				System.out.println("VALUE " + map.get(key));
			}
		}
	}
}
