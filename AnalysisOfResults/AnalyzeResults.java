package AnalysisOfResults;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.HashSet;
import java. util.HashMap;

import Algorithms.TestController;

public class AnalyzeResults {
	/* Names of factors we are considering for each problem. */
	// Number of literals this problem contains.
	static final String NUM_LITERALS = "number of literals";
	// Number of clauses this problem contains.
	static final String NUM_CLAUSES = "number of clauses";
	// The number of different parameters used for this problem.
	static final String NUM_EXPERIMENTS = "number of experiments"; 
	// Average number of timeouts per experiment.
	static final String AVG_NUM_TIMEOUTS = "average number of timeouts";
	// The fastest time the algorithm took to solve this problem.
	static final String BEST_EXECUTION_TIME = "best execution time"; 
	// The average time the algorithm took to solve this problem.
	static final String AVG_EXECUTION_TIME = "average execution time";
	// The generation the algorithm found the best solution.
	static final String BEST_GENERATION = "best generation";     
	// Average of best generations over all non-timed out trials.
	static final String AVG_BEST_GENERATION = "average best generation";  
	// Same as above but for timed out trials.
	static final String BEST_GENERATION_TIMEOUT = "best generation for timeouts"; 
	// Same as above but for timed out trials.
	static final String AVG_BEST_GENERATION_TIMEOUT = "average best generation for timeout"; 
	/* NOTE: 
	 * Percentage defined as: clauses solved by our algorithm divided by 
	 * 	clauses solved by best known algorithm. 
	 * */
	// The highest percentage of clauses solved / clauses solved by best known algorithm.
	static final String BEST_PERCENTAGE = "best percentage";
	// Same as above but for trials that timed out.
	static final String BEST_PERCENTAGE_TIMEOUT = "best percentage for timeouts";
	// The average percentage of clauses solved / clauses solved by best known algorithm.
	static final String AVG_PERCENTAGE = "average percentage";
	// Same as above but for trials that timed out.
	static final String AVG_PERCENTAGE_TIMEOUT = "average percentage for timeouts"; 
	/* A list of parameter settings, comma-separated. 
	 * GA order: population size, selection type, crossover type, 
	 * 					crossover probability, mutation probability.
	 * PBIL order: population size, learning rate, negative learning rate, 
	 * 					mutation probability, mutation shift.
	 * */
	// When the algorithm reached its maximum iteration.
	static final int MAX_ITERATION = Integer.MAX_VALUE;
	static final int NO_DATA = AnalyzeResultsController.NO_DATA;
	
	/* Files */
	// Path of source folder that contains all the results.
	String folderPath = "Combined_Results"; 				
	// A list of all the result files.
	File[] resultsFiles = new File(folderPath).listFiles(); 
	// A list of the names of MAXSAT problems used by both algorithms.
	String[] MAXSATProblems = TestController.files; 		
	// Unsatisfied clauses from currently known best algorithm.
	int[] MAXSATSolutions = TestController.maxValues;
	// Indices at the original TestController.problems array, of problems used by both algorithms. 
//	ArrayList<Integer> indicesOfProblemsUsed = indicesOfProblemsUsedByBothAlgorithms();
	
	/* HashMap<String, HashMap> to store information on each problem for quick lookup.
	 * KEY - String: Name of MAXSAT problem.
	 * VALUE - HashMap<String, String>: A HashMap where 
	 * 				KEY - String: a factor we're analyzing/considering (e.g. fastest time to solve the problem).
	 * 				VALUE - String: the value for this factor, after searching through all files.
	 * 
	 * NOTE: These maps may have different lengths because (after ~40 hours, on two computer, with multi-threading) 
	 * 		not all experiments were finished, so some problems were not used for some experiments on one algorithm. 
	 * */
	private HashMap<String, HashMap<String, String>> parsedResults_GA = new HashMap<String, HashMap<String, String>>();
	private HashMap<String, HashMap<String, String>> parsedResults_PBIL = new HashMap<String, HashMap<String, String>>(); 
	private HashMap<String, HashMap<String, String>> parsedResults_SA = new HashMap<String, HashMap<String, String>>(); 
	private HashMap<String, HashMap<String, String>> parsedResults_SAGA = new HashMap<String, HashMap<String, String>>(); 
	private HashMap<String, HashMap<String, String>> parsedResults_SAPBIL = new HashMap<String, HashMap<String, String>>(); 
	
	/* HashMap that groups the different files by problem name to allow faster operation for the above HashMaps.
	 * KEY - String: Name of MAXSAT problem.
	 * VALUE - ArrayList<String>: An ArrayList of file paths for this problem.
	 * */
	private HashMap<String, ArrayList<String>> filesGroupedByProblem = new HashMap<String, ArrayList<String>>();

	// Constructor.
	public AnalyzeResults(String algorithm, int paramLn, String paramVal) throws IOException {
		// Sort files by problem name for each algorithm.
		groupFilesByProblem();
		// Initialize parsed results.
		initializeHashMaps();
		// Parse results.
		for (String problem : filesGroupedByProblem.keySet()) {
			analyzeResults(problem, algorithm, paramLn, paramVal);
		}
	}
	
	// Getter method: Return parsed results for the specified algorithm in a HashMap.
	public HashMap<String, HashMap<String, String>>  getParsedResults(String algorithm) {
		if (algorithm.equalsIgnoreCase(AnalyzeResultsController.GA)) {
			return parsedResults_GA;
		} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.PBIL)) {
			return parsedResults_PBIL;
		} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.SA)) {
			return parsedResults_SA;
		} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.SAGA)) {
			return parsedResults_SAGA;
		} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.SAPBIL)) {
			return parsedResults_SAPBIL;
		} else {
			return null;
		}
	}
	
	// Initialize with problem names.
	private void initializeHashMaps() {
		for (String problem : filesGroupedByProblem.keySet()) {
			parsedResults_GA.put(problem, new HashMap<String, String>());
			parsedResults_PBIL.put(problem, new HashMap<String, String>());
			parsedResults_SA.put(problem, new HashMap<String, String>());
			parsedResults_SAGA.put(problem, new HashMap<String, String>());
			parsedResults_SAPBIL.put(problem, new HashMap<String, String>());
		}
	}

	// Helper method: Strip the word "File:" and extra spaces.
	private String getProblemName(String line) {
		int fileNameStartIndex = line.indexOf(":") + 1;
		String problemFileName = line.substring(fileNameStartIndex);
		return problemFileName.trim();
	}
	
	// Helper method: Return path to specified file.
	private String getFilePath(File file) {
		return folderPath + "/" + file.getName();
	}
	
	// Helper method: Print file not found error.
	private void printFileNotFound(String path) {
		System.out.println("Unable to open file '" + path + "'");
	}
			
	// Group all the result file paths by problem name for fast look up.
	private void groupFilesByProblem() throws IOException {
		for (File file : resultsFiles) {
			String filePath = "";
			if (!(file.getName().contains(".DS_Store") || file.getName().contains("delete.sh"))) {
				filePath = getFilePath(file);
				try {
					// Read problem name.
					BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
					String problemName = getProblemName(bufferedReader.readLine());
					bufferedReader.close();
					ArrayList<String> listOfFiles;
					if (filesGroupedByProblem.containsKey(problemName)) {
						listOfFiles = filesGroupedByProblem.get(problemName);
					} else {
						listOfFiles = new ArrayList<String>();
					}
					// Push to map.
					listOfFiles.add(filePath);
					filesGroupedByProblem.put(problemName, listOfFiles);
				} catch (FileNotFoundException e) {
					printFileNotFound(filePath);
				}
			}
		}
	}
	
	// Run analysis and fill in values for the HashMaps.
	private void analyzeResults(String prob, String algorithm, 
			int paramLineNum, String targetValue) throws IOException {
		ArrayList<String> files = filesGroupedByProblem.get(prob);
		
		/* Handling parameter queries:
		 * If we want to examine how the parameters affect performance,
		 * we only look at one experiment per problem (other parameters are fix.)
		 * Now need to loop through the experiments to find the one we need.
		 */
		if (paramLineNum != AnalyzeResultsController.NO_DATA) {
			boolean foundExperiment = false;
			for (int i = 0; i < files.size(); i++) {
				String file = files.get(i);
				try {
					BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
					String line = "";
					// Look for the experiment we care about.
					for (int l = 0; l < paramLineNum; l++) {
						line = bufferedReader.readLine();
					}
					if (line != null && line.trim().equalsIgnoreCase(targetValue)) {
						// Now the files ArrayList contains only the experiment we care about.
						foundExperiment = true;
						files.clear();
						files.add(file);
						break;
					}
					bufferedReader.close();
				} 
				catch(FileNotFoundException e) {
					printFileNotFound(file); 
				}
			}
			
			if (!foundExperiment) {
				System.out.println("Couldn't find experiment:");
				System.out.println("Algorithm: " + algorithm);
				System.out.println("Parameter Line Number: " + paramLineNum);
				System.out.println("Target Value: " + targetValue);
				return;
			}
		} 

		// Factors we are considering.
		int numExperimentsWithAllTrialsTimeOut = 0;
		int numExperiments = 0;
		int totalNumTimeOutTrials = 0;
		int numLiterals = 0;
		int numClauses = 0;
		long bestExecutionTime =  Long.MAX_VALUE;
		long totalExecutionTime = 0;
		int bestGeneration = Integer.MAX_VALUE;
		int bestGeneration_TimeOut = Integer.MAX_VALUE;
		int totalBestGeneration = 0;
		int totalBestGeneration_TimeOut = 0;
		int fewestUnsatClauses = Integer.MAX_VALUE;
		int fewestUnsatClauses_TimeOut = Integer.MAX_VALUE;
		int totalUnsatClauses = 0;
		int totalUnsatClauses_TimeOut = 0;
		double percentage = 0;
		double bestPercentage = 0;
		
		// Iterate through all files associated with this problem.
		for (int i = 0; i < files.size(); i++) {
			String file = files.get(i);
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line;
				int lineNum = 1;
				boolean isAlgorithm = true;

				while ((line = bufferedReader.readLine()) != null) {
					// Check if this is the algorithm we want to scan.
					if (lineNum == LineNumber.ALGORITHM_SETTING.getNumVal() &&
							!line.equalsIgnoreCase("Settings " + algorithm)) {
						isAlgorithm = false;
					}
					lineNum++;
				}
				bufferedReader.close();
				
				if (isAlgorithm) {
					numExperiments++;
					bufferedReader = new BufferedReader(new FileReader(file));
					lineNum = 1;
					
					while ((line = bufferedReader.readLine()) != null) {
						if (lineNum == LineNumber.NUM_LITERALS.getNumVal()) {
							numLiterals = Integer.parseInt(line);
						} else if (lineNum == LineNumber.NUM_CLAUSES.getNumVal()) {
							numClauses = Integer.parseInt(line);
						} else if (lineNum == LineNumber.AVG_BEST_GENERATION.getNumVal()) {
							int data = Integer.parseInt(line);
							if (data != NO_DATA) {
								totalBestGeneration += data;
							} else {
								numExperimentsWithAllTrialsTimeOut++;
							}
						} else if (lineNum == LineNumber.AVG_BEST_GENERATION_TIMEOUT.getNumVal()) {
							int data = Integer.parseInt(line);
							if (data != NO_DATA) {
								totalBestGeneration_TimeOut += data;
							}
						} else if (lineNum == LineNumber.BEST_GENERATION.getNumVal()) {
							int current = Integer.parseInt(line);
							if (current != MAX_ITERATION && current < bestGeneration) {
								bestGeneration = current;
							}
						} else if (lineNum == LineNumber.BEST_GENERATION_TIMEOUT.getNumVal()) {
							int current = Integer.parseInt(line);
							if (current != MAX_ITERATION && current < bestGeneration_TimeOut) {
								bestGeneration_TimeOut = current;
							} 
						} else if (lineNum == LineNumber.AVG_UNSAT_CLAUSES.getNumVal()) {
							int current = Integer.parseInt(line);
							if (current != NO_DATA) {
								totalUnsatClauses += current;
							}
						} else if (lineNum == LineNumber.AVG_UNSAT_CLAUSES_TIMEOUT.getNumVal()) {
							int current = Integer.parseInt(line);
							if (current != NO_DATA) {
								totalUnsatClauses_TimeOut += current;
							}
						} else if (lineNum == LineNumber.FEWEST_UNSAT_CLAUSES.getNumVal()) {
							int current = Integer.parseInt(line);
							if (current != MAX_ITERATION && current < fewestUnsatClauses) {
								fewestUnsatClauses = current;
							} 
						} else if (lineNum == LineNumber.FEWEST_UNSAT_CLAUSES_TIMEOUT.getNumVal()) {
							int current = Integer.parseInt(line);
							if (current != MAX_ITERATION && current < fewestUnsatClauses_TimeOut) {
								fewestUnsatClauses_TimeOut = current;
							}
						}  else if (lineNum == LineNumber.AVG_EXECUTION_TIME.getNumVal()) {
							long current = Long.parseLong(line);
							if ((int)current != AnalyzeResultsController.NO_DATA) {
								totalExecutionTime += Long.parseLong(line);
							}
						} else if (lineNum == LineNumber.BEST_EXECUTION_TIME.getNumVal()) {
							long current = Long.parseLong(line);
							if ((int)current != AnalyzeResultsController.NO_DATA && current < bestExecutionTime) {
								bestExecutionTime = current; 
							}
						} else if (lineNum == LineNumber.NUM_TIMEOUTS.getNumVal()) {
							int num = Integer.parseInt(line);
							totalNumTimeOutTrials += num;
						} else if (lineNum == LineNumber.AVG_PERCENT_SAT.getNumVal()) {
							percentage = Double.parseDouble(line);
							if (percentage > bestPercentage) {
								bestPercentage =  percentage;
							}
						}
						
						lineNum++;
					}
					bufferedReader.close();
					
					// Get values and other info.
					int avgNumTimeOuts = totalNumTimeOutTrials / numExperiments;
					int numExperimentsWithNonTimeOutTrials = numExperiments - numExperimentsWithAllTrialsTimeOut;
					long avgExecutionTime = totalExecutionTime / (long) numExperiments;
					int solutionIndex = Arrays.asList(MAXSATProblems).indexOf(prob);
					int bestKnownNumUnsatClauses = MAXSATSolutions[solutionIndex];
					int bestKnownSatClauses = numClauses - bestKnownNumUnsatClauses;
					// Initialize and avoid division by zero.
					int avgBestGeneration = NO_DATA;
					int avgBestGeneration_TimeOut = NO_DATA; 
					double avgPercentage = NO_DATA;
					double avgPercentage_TimeOut = NO_DATA;
//					double bestPercentage = NO_DATA;
//					double bestPercentage_TimeOut = NO_DATA;
					// Calculate.
					if (numExperimentsWithNonTimeOutTrials > 0) {
						avgBestGeneration = totalBestGeneration / numExperimentsWithNonTimeOutTrials;
					}
					if (totalNumTimeOutTrials > 0) {
						avgBestGeneration_TimeOut = totalBestGeneration_TimeOut / numExperiments;
					}
					if (numExperimentsWithNonTimeOutTrials > 0) {
						if (fewestUnsatClauses == MAX_ITERATION) {
							fewestUnsatClauses = NO_DATA;
							bestPercentage = NO_DATA;
						} else {
							double averageSatClauses = numClauses - totalUnsatClauses / numExperiments;
							avgPercentage = averageSatClauses / (double)bestKnownSatClauses;
//							bestPercentage = (double)(numClauses - fewestUnsatClauses)/ (double)bestKnownSatClauses;
						}
					}
					double averageSatClauses_TimeOut = numClauses - totalUnsatClauses_TimeOut / numExperiments;
					avgPercentage_TimeOut = averageSatClauses_TimeOut / (double)bestKnownSatClauses;
					
					// Push values to HashMap.
					HashMap<String, String> results = new HashMap<String, String>();
					results.put(NUM_LITERALS, String.valueOf(numLiterals));
					results.put(NUM_CLAUSES, String.valueOf(numClauses));
					results.put(NUM_EXPERIMENTS, String.valueOf(numExperiments));
					results.put(AVG_NUM_TIMEOUTS, String.valueOf(avgNumTimeOuts));
					results.put(BEST_EXECUTION_TIME, String.valueOf(bestExecutionTime));
					results.put(AVG_EXECUTION_TIME, String.valueOf(avgExecutionTime));
					results.put(BEST_GENERATION, String.valueOf(bestGeneration));
					results.put(BEST_GENERATION_TIMEOUT, String.valueOf(bestGeneration_TimeOut));
					results.put(AVG_BEST_GENERATION, String.valueOf(avgBestGeneration));
					results.put(AVG_BEST_GENERATION_TIMEOUT, String.valueOf(avgBestGeneration_TimeOut));
					results.put(BEST_PERCENTAGE, String.valueOf(bestPercentage));
					results.put(BEST_PERCENTAGE_TIMEOUT, String.valueOf(bestPercentage));
					results.put(AVG_PERCENTAGE, String.valueOf(avgPercentage));
					results.put(AVG_PERCENTAGE_TIMEOUT, String.valueOf(avgPercentage_TimeOut));
					
					// Push HashMaps to parsed results as a value.
					if (algorithm.equalsIgnoreCase(AnalyzeResultsController.GA)) {
						parsedResults_GA.put(prob, results);
					} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.PBIL)){
						parsedResults_PBIL.put(prob, results);
					} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.SA)){
						parsedResults_SA.put(prob, results);
					} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.SAGA)){
						parsedResults_SAGA.put(prob, results);
					} else if (algorithm.equalsIgnoreCase(AnalyzeResultsController.SAPBIL)){
						parsedResults_SAPBIL.put(prob, results);
					}
				}
			}
			catch(FileNotFoundException e) {
				printFileNotFound(file);
			}
		}
	}
	
//	/* Find out which problems are unused, if any, because of some unfinished experiments.
//	 * NOTE: When all problems are used, it means they are used for both algorithms combined,
//	 * 		 so each of the algorithms may not have used all the problems. 
//	 */
//	public void printUnusedProblems() throws IOException {
//		HashSet<String> setOfUsedProblems = new HashSet<String>();
//				
//		for (File file : resultsFiles) {
//			String resultFilePath = getFilePath(file);
//			
//			if (!file.getName().equalsIgnoreCase(".DS_Store")) {
//				try {
//					BufferedReader bufferReader = new BufferedReader(new FileReader(resultFilePath));
//					String problemFileName = getProblemName(bufferReader.readLine());
//					// Add to set.
//					setOfUsedProblems.add(problemFileName.trim());
//					bufferReader.close();
//				} 
//				catch(FileNotFoundException e) {
//					printFileNotFound(resultFilePath);
//				}
//			} 
//		}
//		
//		boolean hasUnused = false;
//		for (int i = 0; i < MAXSATProblems.length; i++) {
//			String problem = MAXSATProblems[i];
//			if (!setOfUsedProblems.contains(problem)) {
//				hasUnused = true;
//				System.out.println(problem + " is unused");
//			}
//		}
//		if (!hasUnused) {
//			System.out.println("All MAXSAT problems are used.");
//		}
//	}
	
//	// There are 5 problems used by PBIL but not GA. 
//	private ArrayList<Integer> indicesOfProblemsUsedByBothAlgorithms() {
//		ArrayList<Integer> usedIndices = new ArrayList<Integer>();
//		for (int i = 0; i < TestController.files.length; i++) {
//			String problem = TestController.files[i];
//			if (!problem.equals("140v/s2v140c1600-10.cnf") &&
//					!problem.equals("5SAT/HG-5SAT-V100-C1800-100.cnf") &&
//					!problem.equals("60v/s3v60c1000-1.cnf") &&
//					!problem.equals("5SAT/HG-5SAT-V50-C900-5.cnf") &&
//					!problem.equals("maxcut-140-630-0.8/maxcut-140-630-0.8-9.cnf")) {
//				usedIndices.add(i);
//			}
//		}
//		return usedIndices;
//	}
//	
//	// We want to delete the problems used by PBIL but not GA.
//	private String[] deleteProblemsUnusedByGA() {
//		String[] problemsUsedByBothAlgorithms = new String[indicesOfProblemsUsed.size()];
//		int index = 0;
//		for (Integer i: indicesOfProblemsUsed) {
//			problemsUsedByBothAlgorithms[index] = TestController.files[i];
//			index++;
//		}
//		return problemsUsedByBothAlgorithms;
//	}
//	
//	// We also want to delete the solutions corresponding to the deleted problems.
//	private int[] deleteSolutionsUnusedByGA() {
//		int[] solutionsUsedByBothAlgorithms = new int[indicesOfProblemsUsed.size()];
//		int index = 0;
//		for (Integer i: indicesOfProblemsUsed) {
//			solutionsUsedByBothAlgorithms[index] = TestController.maxValues[i];
//			index++;
//		}
//		return solutionsUsedByBothAlgorithms;
//	}
}
