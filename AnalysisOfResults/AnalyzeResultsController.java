package AnalysisOfResults;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

import Algorithms.TestController;

public class AnalyzeResultsController {
	// Algorithm names.
	public static final String GA = "GA";
	public static final String PBIL = "PBIL";
	public static final String SA = "SA";	
	public static final String SAGA = "SAGA";	
	public static final String SAPBIL = "SAPBIL";	
	// Parameters.
	public static final int NO_DATA = -1;
	public static final int DEFAULT = -2;
	// Folder name.
	static final String folderPath = "Graph_Data";
	// Used for comparing doubles.
	private static final double EPSILON = 0.00001;
	// Store the results.
	private static HashMap<String, HashMap<String, String>> results_GA;
	private static HashMap<String, HashMap<String, String>> results_PBIL;
	private static HashMap<String, HashMap<String, String>> results_SA;
	private static HashMap<String, HashMap<String, String>> results_SAGA;
	private static HashMap<String, HashMap<String, String>> results_SAPBIL;
	private static HashMap<String, HashMap<String, String>> results_Parameters_GA;
	private static HashMap<String, HashMap<String, String>> results_Parameters_PBIL;
	private static HashMap<String, HashMap<String, String>> results_Parameters_SA;
	private static HashMap<String, HashMap<String, String>> results_Parameters_SAGA;
	private static HashMap<String, HashMap<String, String>> results_Parameters_SAPBIL;
	// Store the parameters and their respective values.
	private static HashMap<Integer, ArrayList<String>> parameters_GA;
	private static HashMap<Integer, ArrayList<String>> parameters_PBIL;
	private static HashMap<Integer, ArrayList<String>> parameters_SA;
	private static HashMap<Integer, ArrayList<String>> parameters_SAGA;
	private static HashMap<Integer, ArrayList<String>> parameters_SAPBIL;
	
	public static void main(String[] args) throws IOException {
		// Delete all files in the directory.
		File folder = new File(folderPath);
		for (File file : folder.listFiles()) {
			try {
			    file.delete();
			} catch (Exception e) {
				System.out.println("Cannot delete file: " + file.getName());
			}
		}
		
		/* Run general analysis. */
		// GA.
		long startTime = System.currentTimeMillis();
		AnalyzeResults analyzeResults_GA = new AnalyzeResults(GA, NO_DATA, null);
		results_GA = analyzeResults_GA.getParsedResults(GA);
		new GenerateGraphData(results_GA, GA, false, null, null);
		// PBIL.
		AnalyzeResults analyzeResults_PBIL = new AnalyzeResults(PBIL, NO_DATA, null);
		results_PBIL = analyzeResults_PBIL.getParsedResults(PBIL);
		new GenerateGraphData(results_PBIL, PBIL, false, null, null);
		// SA.
		AnalyzeResults analyzeResults_SA = new AnalyzeResults(SA, NO_DATA, null);
		results_PBIL = analyzeResults_SA.getParsedResults(SA);
		new GenerateGraphData(results_SA, SA, false, null, null);
		// SA.
		AnalyzeResults analyzeResults_SAGA = new AnalyzeResults(SAGA, NO_DATA, null);
		results_PBIL = analyzeResults_SAGA.getParsedResults(SAGA);
		new GenerateGraphData(results_SAGA, SAGA, false, null, null);
		// SA.
		AnalyzeResults analyzeResults_SAPBIL = new AnalyzeResults(SAPBIL, NO_DATA, null);
		results_PBIL = analyzeResults_SAPBIL.getParsedResults(SAPBIL);
		new GenerateGraphData(results_SAPBIL, SAPBIL, false, null, null);
		
		// End time calculation.
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Finished writing all graph data for general analysis ");
		System.out.println("Time: " + duration / 1000.0 + " second(s)");
		/* End general analysis*/
		
		
		/* Run parameter analysis. */
		long startTime_p = System.currentTimeMillis();
		
		// GA.
		initializeParameters_GA();
		generateParamGraph(GA, results_Parameters_GA, parameters_GA);
		// PBIL.
		initializeParameters_PBIL();
		generateParamGraph(PBIL, results_Parameters_PBIL, parameters_PBIL);
		// SA.
		initializeParameters_SA();
		generateParamGraph(SA, results_Parameters_SA, parameters_SA);
		// SAGA.
		initializeParameters_SAGA();
		generateParamGraph(SAGA, results_Parameters_SAGA, parameters_SAGA);
		// SAPBIL.
		initializeParameters_SAPBIL();
		generateParamGraph(SAPBIL, results_Parameters_SAPBIL, parameters_SAPBIL);
		
		// End time calculation.
		long duration_p = System.currentTimeMillis() - startTime_p;
		System.out.println("Finished writing all graph data for parameter analysis.");
		System.out.println("Time: " + duration_p / 1000.0 + " second(s)");
		/* End parameter analysis*/
	}
	
	public static void generateParamGraph(String algorithm, HashMap<String, HashMap<String, String>> results_Parameters, 
			HashMap<Integer, ArrayList<String>> parameters) throws IOException {
		AnalyzeResults analyzeResultsDefaultParameters = new AnalyzeResults(algorithm, DEFAULT, "parameter values");
		results_Parameters = analyzeResultsDefaultParameters.getParsedResults(algorithm);
		new GenerateGraphData(results_Parameters, algorithm, true, "Default", "parameter values");

		// Get graph data for the rest of the experiments.
		for (Integer lineNum : parameters.keySet()) {
			for (String value: parameters.get(lineNum)) {
				AnalyzeResults analyzeResultsParameters = new AnalyzeResults(algorithm, lineNum, value);
				results_Parameters = analyzeResultsParameters.getParsedResults(algorithm);
				String param = getParameterName(algorithm, lineNum);
				new GenerateGraphData(results_Parameters, algorithm, true, param, value);
			}
		}
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
	
	public static void initializeParameters_SAPBIL() {
		// Initialize HashMap.
		parameters_SAPBIL = new HashMap<Integer, ArrayList<String>>();
		
		// Fixed values.
		int defaultSampleSize = TestController.SAPBIL_samples[0];
		double defaultLearningRate = TestController.SAPBIL_learningRate[0];
		double defaultNegLearningRate = TestController.SAPBIL_negLearningRate[0];
		double defaultMutationProb = TestController.SAPBIL_mutProb[0]; 
		double defaultMutationShift = TestController.SAPBIL_mutShift[0];
		int defaultSAFrequency = TestController.SAPBIL_howOftenToIntroduceSA[0];
		
		// Find the values that varied.
		for (int i = 0; i < TestController.SAPBIL_samples.length; i++) {
			int sampleSize = TestController.SAPBIL_samples[i];
			double learningRate = TestController.SAPBIL_learningRate[i];
			double negLearningRate = TestController.SAPBIL_negLearningRate[i];
			double mutationProb = TestController.SAPBIL_mutProb[i];
			double mutationShift = TestController.SAPBIL_mutShift[i];
			double SAFrequency = TestController.SAPBIL_howOftenToIntroduceSA[i];
			
			ArrayList<String> values = new ArrayList<String>();
			if (sampleSize != defaultSampleSize) {
				if (parameters_SAPBIL.containsKey(LineNumberSAPBIL.SAMPLES.getNumVal())) {
					values = parameters_SAPBIL.get(LineNumberSAPBIL.SAMPLES.getNumVal());
				}
				values.add(String.valueOf(sampleSize));
				parameters_SAPBIL.put(LineNumberSAPBIL.SAMPLES.getNumVal(), values);
			} else if (differentDouble(learningRate, defaultLearningRate)) {
				if (parameters_SAPBIL.containsKey(LineNumberSAPBIL.LEARNING_RATE.getNumVal())) {
					values = parameters_SAPBIL.get(LineNumberSAPBIL.LEARNING_RATE.getNumVal());
				}
				values.add(String.valueOf(learningRate));
				parameters_SAPBIL.put(LineNumberSAPBIL.LEARNING_RATE.getNumVal(), values);
			} else if (differentDouble(negLearningRate, defaultNegLearningRate)) {
				if (parameters_SAPBIL.containsKey(LineNumberSAPBIL.NEG_LEARNING_RATE.getNumVal())) {
					values = parameters_SAPBIL.get(LineNumberSAPBIL.NEG_LEARNING_RATE.getNumVal());
				}
				values.add(String.valueOf(negLearningRate));
				parameters_SAPBIL.put(LineNumberSAPBIL.NEG_LEARNING_RATE.getNumVal(), values);
			} else if (differentDouble(mutationProb, defaultMutationProb)) {
				if (parameters_SAPBIL.containsKey(LineNumberSAPBIL.MUTATION_PROB.getNumVal())) {
					values = parameters_SAPBIL.get(LineNumberSAPBIL.MUTATION_PROB.getNumVal());
				}
				values.add(String.valueOf(mutationProb));
				parameters_SAPBIL.put(LineNumberSAPBIL.MUTATION_PROB.getNumVal(), values);
			} else if (differentDouble(mutationShift, defaultMutationShift)) {
				if (parameters_SAPBIL.containsKey(LineNumberSAPBIL.MUTATION_SHIFT.getNumVal())) {
					values = parameters_SAPBIL.get(LineNumberSAPBIL.MUTATION_SHIFT.getNumVal());
				}
				values.add(String.valueOf(mutationShift));				
				parameters_SAPBIL.put(LineNumberSAPBIL.MUTATION_SHIFT.getNumVal(), values);
			} else if (SAFrequency == defaultSAFrequency){
				if (parameters_SAPBIL.containsKey(LineNumberSAPBIL.SA_FREQUENCY.getNumVal())) {
					values = parameters_SAPBIL.get(LineNumberSAPBIL.SA_FREQUENCY.getNumVal());
				}
				values.add(String.valueOf(SAFrequency));				
				parameters_SAPBIL.put(LineNumberSAPBIL.SA_FREQUENCY.getNumVal(), values);
			}
		}
	}
	
	public static void initializeParameters_SA() {
		// Initialize HashMap.
		parameters_GA = new HashMap<Integer, ArrayList<String>>();
		
		// Fixed values.
		double defaultMinTemp = TestController.SA_minTemp[0];
		double defaultMaxTemp = TestController.SA_maxTemp[0];
		
		// Find the values that varied.
		for (int i = 0; i < TestController.SA_minTemp.length; i++) {
			double minTemp = TestController.SA_minTemp[i];
			double maxTemp = TestController.SA_maxTemp[i];
			
			ArrayList<String> values = new ArrayList<String>();
			if (minTemp!= defaultMinTemp) {
				if (parameters_SA.containsKey(LineNumberSA.MIN_TEMP.getNumVal())) {
					values = parameters_SA.get(LineNumberSA.MIN_TEMP.getNumVal());
				}
				values.add(String.valueOf(minTemp));
				parameters_SA.put(LineNumberSA.MIN_TEMP.getNumVal(), values);
			} else if (maxTemp != defaultMaxTemp) {
				if (parameters_SA.containsKey(LineNumberSA.MAX_TEMP.getNumVal())) {
					values = parameters_SA.get(LineNumberSA.MAX_TEMP.getNumVal());
				}
				values.add(String.valueOf(maxTemp));
				parameters_SA.put(LineNumberSA.MAX_TEMP.getNumVal(), values);
			}
		}
	}
	
	
	// Returns true if the two doubles are different, false otherwise.
	private static boolean differentDouble(double a, double b) {
		return Math.abs(a - b) > EPSILON;
	}
	
	// Returns the name of the parameter given the algorithm and its line number in results file.
	public static String getParameterName(String algorithm, int line) 
		throws InvalidParameterException {
		if (algorithm.equalsIgnoreCase("GA")) {
			if (line == LineNumberGA.POP_SIZE.getNumVal()) {
				return "Population Size";
			} else if (line == LineNumberGA.SELECTION_TYPE.getNumVal()) {
				return "Selection Type";
			} else if (line == LineNumberGA.CROSSOVER_TYPE.getNumVal()) {
				return "Crossover Type";
			} else if (line == LineNumberGA.CROSSOVER_PROB.getNumVal()) {
				return "Crossover Probability";
			} else if (line == LineNumberGA.MUTATION_PROB.getNumVal()) {
				return "Mutation Probability";
			} else {
				throw new InvalidParameterException("Invalid line number for " + algorithm);
			}
		} else {
			if (line == LineNumberPBIL.POP_SIZE.getNumVal()) {
				return "Population Size";
			} else if (line == LineNumberPBIL.LEARNING_RATE.getNumVal()) {
				return "Learning Rate";
			} else if (line == LineNumberPBIL.NEG_LEARNING_RATE.getNumVal()) {
				return "Negative Learning Rate";
			} else if (line == LineNumberPBIL.MUTATION_PROB.getNumVal()) {
				return "Mutation Probability";
			} else if (line == LineNumberPBIL.MUTATION_SHIFT.getNumVal()) {
				return "Mutation Shift";
			} else {
				throw new InvalidParameterException("Invalid line number for " + algorithm);
			}
		} 
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